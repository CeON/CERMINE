package pl.edu.icm.cermine.tools.classification.svm;

import java.io.File;
import java.io.IOException;
import java.util.List;
import libsvm.svm_parameter;
import org.apache.commons.cli.*;
import pl.edu.icm.cermine.evaluation.tools.EvaluationUtils.DocumentsIterator;
import pl.edu.icm.cermine.evaluation.tools.PenaltyCalculator;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.structure.SVMInitialZoneClassifier;
import pl.edu.icm.cermine.structure.model.BxPage;
import pl.edu.icm.cermine.structure.model.BxZone;
import pl.edu.icm.cermine.structure.model.BxZoneLabel;
import pl.edu.icm.cermine.tools.classification.features.FeatureVectorBuilder;
import pl.edu.icm.cermine.tools.classification.general.BxDocsToTrainingSamplesConverter;
import pl.edu.icm.cermine.tools.classification.general.TrainingSample;

public class SVMInitialBuilder {

    protected static SVMZoneClassifier getZoneClassifier(List<TrainingSample<BxZoneLabel>> trainingSamples,
            Integer kernelType, Double gamma, Double C, Integer degree) throws IOException {
        // Filter the training documents
        // so that in the learning examples all classes are
        // represented equally

        PenaltyCalculator pc = new PenaltyCalculator(trainingSamples);
        int[] intClasses = new int[pc.getClasses().size()];
        double[] classesWeights = new double[pc.getClasses().size()];

        Integer labelIdx = 0;
        for (BxZoneLabel label : pc.getClasses()) {
            intClasses[labelIdx] = label.ordinal();
            classesWeights[labelIdx] = pc.getPenaltyWeigth(label);
            ++labelIdx;
        }

        //SampleSelector<BxZoneLabel> selector = new UndersamplingSelector<BxZoneLabel>(1.3);
        //trainingSamples = selector.pickElements(trainingSamples);

        FeatureVectorBuilder<BxZone, BxPage> featureVectorBuilder = SVMInitialZoneClassifier.getFeatureVectorBuilder();
        SVMZoneClassifier zoneClassifier = new SVMZoneClassifier(featureVectorBuilder);
        svm_parameter param = SVMZoneClassifier.getDefaultParam();
        param.svm_type = svm_parameter.C_SVC;
        param.gamma = gamma;
        param.C = C;
        param.degree = degree;
        param.kernel_type = kernelType;
        param.weight_label = intClasses;
        param.weight = classesWeights;

        zoneClassifier.setParameter(param);
        zoneClassifier.buildClassifier(trainingSamples);
        zoneClassifier.printWeigths(featureVectorBuilder);
        zoneClassifier.saveModel("svm_initial_classifier");
        return zoneClassifier;
    }

// sample parameters: -input /home/pawel/icm/xmls_v2 -output initial_svm_classifier_test -degree 3 -g 0.0325 -C 256
    public static void main(String[] args) throws ParseException, AnalysisException, IOException, CloneNotSupportedException  {
        Options options = new Options();
        options.addOption("input", true, "input path");
        options.addOption("output", true, "output model path");
        options.addOption("kernel", true, "kernel type");
        options.addOption("g", true, "gamma");
        options.addOption("C", true, "C");
        options.addOption("degree", true, "degree");

        CommandLineParser parser = new GnuParser();
        CommandLine line = parser.parse(options, args);
        if (!(line.hasOption("input") && line.hasOption("output") && line.hasOption("kernel") && line.hasOption("g") && line.hasOption("C") )) {
            System.err.println("Usage: SVMInitialBuilder -input input_directory -output output_model_file -kernel K -gamma G -C c [-degree d]");
            System.exit(1);
        }
        Double C = Double.valueOf(line.getOptionValue("C"));
        Double gamma = Double.valueOf(line.getOptionValue("g"));
        String inDir = line.getOptionValue("input");
        String outFile = line.getOptionValue("output");
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
        if (kernelType == svm_parameter.POLY && degree == null) {
            System.err.println("Polynomial kernel requires the -degree option to be specified");
            System.exit(1);
        }
        File input = new File(inDir);
        if (input.isDirectory()) {
            DocumentsIterator it = new DocumentsIterator(inDir);
            FeatureVectorBuilder<BxZone, BxPage> featureVectorBuilder = SVMInitialZoneClassifier.getFeatureVectorBuilder();
            List<TrainingSample<BxZoneLabel>> trainingSamples =
                BxDocsToTrainingSamplesConverter.getZoneTrainingSamples(it.iterator(), featureVectorBuilder,
                    BxZoneLabel.getLabelToGeneralMap());
            SVMZoneClassifier classifier = getZoneClassifier(trainingSamples, kernelType, gamma, C, degree);
            classifier.saveModel(outFile);
        } else {
            List<TrainingSample<BxZoneLabel>> trainingSamples = SVMZoneClassifier.loadProblem(inDir, SVMInitialZoneClassifier.getFeatureVectorBuilder());
            for (TrainingSample<BxZoneLabel> sample : trainingSamples) {
                sample.setLabel(sample.getLabel().getGeneralLabel());
            }
            SVMZoneClassifier classifier = getZoneClassifier(trainingSamples, kernelType, gamma, C, degree);
            classifier.saveModel(outFile);
        }
    }
}
