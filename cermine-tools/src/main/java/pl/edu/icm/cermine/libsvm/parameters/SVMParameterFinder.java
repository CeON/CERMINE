/**
 * This file is part of CERMINE project.
 * Copyright (c) 2011-2016 ICM-UW
 *
 * CERMINE is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * CERMINE is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with CERMINE. If not, see <http://www.gnu.org/licenses/>.
 */

package pl.edu.icm.cermine.libsvm.parameters;

import java.io.IOException;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import libsvm.svm_parameter;
import org.apache.commons.cli.*;
import pl.edu.icm.cermine.evaluation.tools.ClassificationResults;
import pl.edu.icm.cermine.evaluation.tools.DividedEvaluationSet;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.structure.model.BxPage;
import pl.edu.icm.cermine.structure.model.BxZone;
import pl.edu.icm.cermine.structure.model.BxZoneLabel;
import pl.edu.icm.cermine.tools.classification.general.FeatureVectorBuilder;
import pl.edu.icm.cermine.tools.classification.general.TrainingSample;
import pl.edu.icm.cermine.tools.classification.svm.SVMZoneClassifier;

/**
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public abstract class SVMParameterFinder {

    private static final EnumMap<BxZoneLabel, BxZoneLabel> DEFAULT_LABEL_MAP = new EnumMap<BxZoneLabel, BxZoneLabel>(BxZoneLabel.class);

    static {
        for (BxZoneLabel label : BxZoneLabel.values()) {
            DEFAULT_LABEL_MAP.put(label, label);
        }
    }

    protected int foldness;
    private final Map<BxZoneLabel, BxZoneLabel> labelMap = DEFAULT_LABEL_MAP.clone();

    public static void main(String[] args, SVMParameterFinder evaluator) 
            throws ParseException, AnalysisException, InterruptedException, ExecutionException{
        Options options = new Options();
        options.addOption("fold", true, "foldness of cross-validation");
        options.addOption("kernel", true, "kernel type");
        options.addOption("degree", true, "degree");
        options.addOption("ext", true, "file extension");
        options.addOption("threads", true, "number of threads");
        options.addOption("minc", true, "");
        options.addOption("maxc", true, "");
        options.addOption("ming", true, "");
        options.addOption("maxg", true, "");
        
        CommandLineParser parser = new DefaultParser();
        CommandLine line = parser.parse(options, args);

        if (line.hasOption("help")) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(args[0] + " [-options] input-file",
                    options);
        } else {
            String[] remaining = line.getArgs();

            if (remaining.length != 1) {
                throw new ParseException("Input file is missing!");
            }

            if (!line.hasOption("fold")) {
                evaluator.foldness = 5;
            } else {
                evaluator.foldness = Integer.valueOf(line.getOptionValue("fold"));
            }
            String inputFile = remaining[0];

            String degreeStr = line.getOptionValue("degree");
            Integer degree = -1;
            if (degreeStr != null && !degreeStr.isEmpty()) {
                degree = Integer.valueOf(degreeStr);
            }
            Integer kernelType;
            switch(Integer.valueOf(line.getOptionValue("kernel"))) {
                case 0: kernelType = svm_parameter.LINEAR; break;
                case 1: kernelType = svm_parameter.POLY; break;
                case 2: kernelType = svm_parameter.RBF; break;
                case 3: kernelType = svm_parameter.SIGMOID; break;
                default:
                    throw new IllegalArgumentException("Invalid kernel value provided");
            }

            int threads = 3;
            String threadsStr = line.getOptionValue("threads");
            if (threadsStr != null && !threadsStr.isEmpty()) {
                threads = Integer.valueOf(threadsStr);
            }
            
            String ext = "cxml";
            String extStr = line.getOptionValue("ext");
            if (extStr != null && !extStr.isEmpty()) {
                ext = extStr;
            }

            int minc = -5;
            if (line.hasOption("minc")) {
                minc = Integer.valueOf(line.getOptionValue("minc"));
            }
            int maxc = 15;
            if (line.hasOption("maxc")) {
                maxc = Integer.valueOf(line.getOptionValue("maxc"));
            }
            int ming = -15;
            if (line.hasOption("ming")) {
                ming = Integer.valueOf(line.getOptionValue("ming"));
            }
            int maxg = 3;
            if (line.hasOption("maxg")) {
                maxg = Integer.valueOf(line.getOptionValue("maxg"));
            }
            
            evaluator.setLabelMap(BxZoneLabel.getLabelToGeneralMap());
            evaluator.run(inputFile, ext, threads, kernelType, degree, minc, maxc, ming, maxg);
        }
    }

    protected abstract List<TrainingSample<BxZoneLabel>> getSamples(String inputFile, String ext) throws AnalysisException;
    
    public void run(String inputFile, String ext, int threads, int kernel, int degree, int minc, int maxc, int ming, int maxg) 
            throws AnalysisException, InterruptedException, ExecutionException {
        List<TrainingSample<BxZoneLabel>> samples = getSamples(inputFile, ext);

        ExecutorService executor = Executors.newFixedThreadPool(3);
        CompletionService<EvaluationParams> completionService = new ExecutorCompletionService<EvaluationParams>(executor);
        
        double bestRate = 0;
        int bestclog = 0;
        int bestglog = 0;

        int submitted = 0;

        for (int clog = minc; clog <= maxc; clog++) {
            for (int glog = maxg; glog >= ming; glog--) {
                completionService.submit(new Evaluator(samples, new EvaluationParams(clog, glog), kernel, degree));
                submitted++;
            }
        }
        
        while (submitted > 0) {
            Future<EvaluationParams> f1 = completionService.take();
            EvaluationParams p = f1.get();
            if (p.rate > bestRate) {
                bestRate = p.rate;
                bestclog = p.clog;
                bestglog = p.glog;
            }
            System.out.println("Gamma: "+p.glog+", C: "+p.clog+", rate: "+p.rate+" (Best: "+bestglog+" "+bestclog+" "+bestRate+")");
            submitted--;
        }    
        
        executor.shutdown();
    }

    private static class EvaluationParams {
        int clog;
        int glog;
        double rate;

        public EvaluationParams(int clog, int glog) {
            this.clog = clog;
            this.glog = glog;
        }
        
    }
    
    private class Evaluator implements Callable<EvaluationParams> {

        private final List<TrainingSample<BxZoneLabel>> samples;
        private final EvaluationParams params;
        private final int kernel;
        private final int degree;

        public Evaluator(List<TrainingSample<BxZoneLabel>> samples, EvaluationParams params, int kernel, int degree) {
            this.samples = samples;
            this.params = params;
            this.kernel = kernel;
            this.degree = degree;
        }

        @Override
        public EvaluationParams call() throws AnalysisException, IOException, CloneNotSupportedException {
            double gamma = Math.pow(2, params.glog);
            double c = Math.pow(2, params.clog);
            double rate = evaluate(samples, kernel, gamma, c, degree);
            params.rate = rate;
            return params;
        }
    }
    
    private double evaluate(List<TrainingSample<BxZoneLabel>> samples, int kernelType, double gamma, double C, int degree) throws AnalysisException, IOException, CloneNotSupportedException {
        ClassificationResults summary = new ClassificationResults();
        
        List<DividedEvaluationSet> sampleSets = DividedEvaluationSet.build(samples, foldness);
        
        for (int fold = 0; fold < foldness; ++fold) {
        	List<TrainingSample<BxZoneLabel>> trainingSamples = sampleSets.get(fold).getTrainingDocuments();
        	List<TrainingSample<BxZoneLabel>> testSamples = sampleSets.get(fold).getTestDocuments();

            ClassificationResults iterationResults = new ClassificationResults();

            SVMZoneClassifier zoneClassifier = getZoneClassifier(trainingSamples, kernelType, gamma, C, degree);

            for (TrainingSample<BxZoneLabel> testSample : testSamples) {
            	BxZoneLabel expectedClass = testSample.getLabel();
                BxZoneLabel inferedClass = zoneClassifier.predictLabel(testSample);
                ClassificationResults documentResults = compareItems(expectedClass, inferedClass);
                iterationResults.add(documentResults);
            }
            summary.add(iterationResults);
        }
        
        return summary.getMeanF1Score();
    }

    protected ClassificationResults compareItems(BxZoneLabel expected, BxZoneLabel actual) {
        ClassificationResults pageResults = new ClassificationResults();
        pageResults.addOneZoneResult(expected, actual);
        return pageResults;
    }

    public void setLabelMap(Map<BxZoneLabel, BxZoneLabel> value) {
        labelMap.putAll(DEFAULT_LABEL_MAP);
        labelMap.putAll(value);
    }

    protected abstract SVMZoneClassifier getZoneClassifier(List<TrainingSample<BxZoneLabel>> trainingSamples, int kernelType, double gamma, double C, int degree)
            throws AnalysisException, IOException, CloneNotSupportedException;
    protected abstract FeatureVectorBuilder<BxZone, BxPage> getFeatureVectorBuilder();
}
