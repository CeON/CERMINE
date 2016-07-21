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

package pl.edu.icm.cermine.evaluation;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import libsvm.svm_parameter;
import org.apache.commons.cli.*;
import pl.edu.icm.cermine.evaluation.tools.ClassificationResults;
import pl.edu.icm.cermine.evaluation.tools.DividedEvaluationSet;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.exception.TransformationException;
import pl.edu.icm.cermine.structure.model.BxDocument;
import pl.edu.icm.cermine.structure.model.BxPage;
import pl.edu.icm.cermine.structure.model.BxZone;
import pl.edu.icm.cermine.structure.model.BxZoneLabel;
import pl.edu.icm.cermine.structure.transformers.BxDocumentToTrueVizWriter;
import pl.edu.icm.cermine.structure.transformers.TrueVizToBxDocumentReader;
import pl.edu.icm.cermine.tools.classification.general.FeatureVectorBuilder;
import pl.edu.icm.cermine.tools.classification.general.TrainingSample;
import pl.edu.icm.cermine.tools.classification.svm.SVMZoneClassifier;

/**
 * Class for performing cross-validating classifier performance in zone classification task
 *
 * @author Pawel Szostek
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public abstract class CrossvalidatingZoneClassificationEvaluator {

    private static final EnumMap<BxZoneLabel, BxZoneLabel> DEFAULT_LABEL_MAP = new EnumMap<BxZoneLabel, BxZoneLabel>(BxZoneLabel.class);

    static {
        for (BxZoneLabel label : BxZoneLabel.values()) {
            DEFAULT_LABEL_MAP.put(label, label);
        }
    }

    protected int foldness;
    private final Map<BxZoneLabel, BxZoneLabel> labelMap = DEFAULT_LABEL_MAP.clone();
    private final TrueVizToBxDocumentReader reader = new TrueVizToBxDocumentReader();
    private final BxDocumentToTrueVizWriter writer = new BxDocumentToTrueVizWriter();

    public static void main(String[] args, CrossvalidatingZoneClassificationEvaluator evaluator)
            throws ParseException, AnalysisException, IOException, TransformationException, CloneNotSupportedException {
        Options options = new Options();
        options.addOption("compact", false, "do not print results for pages");
        options.addOption("fold", true, "foldness of cross-validation");
        options.addOption("help", false, "print this help message");
        options.addOption("minimal", false, "print only final summary");
        options.addOption("full", false, "print all possible messages");

        options.addOption("kernel", true, "kernel type");
        options.addOption("g", true, "gamma");
        options.addOption("C", true, "C");
        options.addOption("degree", true, "degree");
        
        options.addOption("ext", true, "ext");
        
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
                throw new ParseException("Foldness of cross-validation is not given!");
            } else {
                evaluator.foldness = Integer.valueOf(line.getOptionValue("fold"));
            }
            String inputFile = remaining[0];

            Double C = Double.valueOf(line.getOptionValue("C"));
            Double gamma = Double.valueOf(line.getOptionValue("g"));
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
            
            evaluator.setLabelMap(BxZoneLabel.getLabelToGeneralMap());
            evaluator.run(inputFile, line.getOptionValue("ext"), kernelType, gamma, C, degree);

        }
    }

    protected abstract List<TrainingSample<BxZoneLabel>> getSamples(String inputFile, String ext) throws AnalysisException;
    
    public void run(String inputFile, String ext, int kernelType, double gamma, double C, int degree) throws AnalysisException, IOException, TransformationException, CloneNotSupportedException {
        ClassificationResults summary = newResults();

        List<TrainingSample<BxZoneLabel>> samples = getSamples(inputFile, ext);
        List<DividedEvaluationSet> sampleSets = DividedEvaluationSet.build(samples, foldness);
        System.out.println("All training elements: " +  samples.size());
        for (int fold = 0; fold < foldness; ++fold) {
        	List<TrainingSample<BxZoneLabel>> trainingSamples = sampleSets.get(fold).getTrainingDocuments();
        	List<TrainingSample<BxZoneLabel>> testSamples = sampleSets.get(fold).getTestDocuments();
            System.out.println("Fold number " + fold);
        	System.out.println("Training elements " + trainingSamples.size());
            System.out.println("Test elements  " + testSamples.size());

            ClassificationResults iterationResults = newResults();

            SVMZoneClassifier zoneClassifier = getZoneClassifier(trainingSamples, kernelType, gamma, C, degree);

            for (TrainingSample<BxZoneLabel> testSample : testSamples) {
            	BxZoneLabel expectedClass = testSample.getLabel();
                BxZoneLabel inferedClass = zoneClassifier.predictLabel(testSample);
                ClassificationResults documentResults = compareItems(expectedClass, inferedClass);
                iterationResults.add(documentResults);
                if (!expectedClass.equals(inferedClass)) {
                    System.out.println("Expected " + expectedClass + ", got " + inferedClass);
                    System.out.println(testSample.getData() + "\n");
                }
            }
            summary.add(iterationResults);
            System.out.println("=== Single iteration summary (" + (fold + 1) + "/" + this.foldness + ")");
            printFinalResults(iterationResults);
           
        }
        System.out.println("=== General summary (" + this.foldness + " iterations)");
        printFinalResults(summary);
        System.out.println("F score "+summary.getMeanF1Score());
    }

    protected ClassificationResults newResults() {
        return new ClassificationResults();
    }

    protected ClassificationResults compareItems(BxZoneLabel expected, BxZoneLabel actual) {
        ClassificationResults pageResults = newResults();
        pageResults.addOneZoneResult(expected, actual);
        return pageResults;
    }

    protected BxDocument readDocument(Reader input) throws TransformationException {
        List<BxPage> pages = reader.read(input);
        BxDocument ret = new BxDocument();
        for (BxPage page : pages) {
            page.setParent(ret);
        }
        return ret.setPages(pages);
    }

    public void setLabelMap(Map<BxZoneLabel, BxZoneLabel> value) {
        labelMap.putAll(DEFAULT_LABEL_MAP);
        labelMap.putAll(value);
    }

    protected void writeDocument(BxDocument document, Writer output) throws TransformationException {
        writer.write(output, Lists.newArrayList(document));
    }

    protected void printItemResults(BxZone expected, BxZone actual) {
        if (expected.getLabel() != actual.getLabel()) {
            System.out.println("Expected " + expected.getLabel() + ", got " + actual.getLabel());
            System.out.println(expected.toText() + "\n");
        }
    }

    protected void printDocumentResults(ClassificationResults results) {
        results.printLongSummary();
        results.printShortSummary();
    }

    protected void printFinalResults(ClassificationResults results) {
        results.printMatrix();
        results.printLongSummary();
        results.printShortSummary();
        results.printQualityMeasures();
    }

    protected abstract SVMZoneClassifier getZoneClassifier(List<TrainingSample<BxZoneLabel>> trainingSamples, int kernelType, double gamma, double C, int degree)
            throws AnalysisException, IOException, CloneNotSupportedException;
    protected abstract FeatureVectorBuilder<BxZone, BxPage> getFeatureVectorBuilder();
}
