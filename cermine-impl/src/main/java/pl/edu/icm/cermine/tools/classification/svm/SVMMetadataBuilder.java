package pl.edu.icm.cermine.tools.classification.svm;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import libsvm.svm_parameter;
import org.apache.commons.cli.*;
import pl.edu.icm.cermine.evaluation.tools.EvaluationUtils;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.exception.TransformationException;
import pl.edu.icm.cermine.structure.SVMInitialZoneClassifier;
import pl.edu.icm.cermine.structure.SVMMetadataZoneClassifier;
import pl.edu.icm.cermine.structure.model.*;
import pl.edu.icm.cermine.tools.classification.features.FeatureVectorBuilder;
import pl.edu.icm.cermine.tools.classification.general.BxDocsToTrainingSamplesConverter;
import pl.edu.icm.cermine.tools.classification.general.ClassificationUtils;
import pl.edu.icm.cermine.tools.classification.general.TrainingSample;
import pl.edu.icm.cermine.tools.classification.sampleselection.OversamplingSampler;
import pl.edu.icm.cermine.tools.classification.sampleselection.SampleSelector;

public class SVMMetadataBuilder {
	protected static SVMZoneClassifier getZoneClassifier(List<TrainingSample<BxZoneLabel>> trainingSamplesUnrevised, Integer kernelType, Double gamma, Double C, Integer degree) throws IOException, AnalysisException, CloneNotSupportedException
	{
        trainingSamplesUnrevised = ClassificationUtils.filterElements(trainingSamplesUnrevised, BxZoneLabelCategory.CAT_METADATA);

        SampleSelector<BxZoneLabel> selector = new OversamplingSampler<BxZoneLabel>(0.7);
        List<TrainingSample<BxZoneLabel>> trainingSamples = selector.pickElements(trainingSamplesUnrevised);

        FeatureVectorBuilder<BxZone, BxPage> featureVectorBuilder = SVMMetadataZoneClassifier.getFeatureVectorBuilder();
        SVMZoneClassifier zoneClassifier = new SVMZoneClassifier(SVMMetadataZoneClassifier.getFeatureVectorBuilder());
		svm_parameter param = SVMZoneClassifier.getDefaultParam();
		param.svm_type = svm_parameter.C_SVC;
		param.gamma = gamma;
		param.C = C;
		param.degree = degree;
		param.kernel_type = kernelType;

		zoneClassifier.setParameter(param);
        zoneClassifier.buildClassifier(trainingSamples);
        zoneClassifier.printWeigths(featureVectorBuilder);
        zoneClassifier.saveModel("svm_initial_classifier");
		return zoneClassifier;
	}

	public static void main(String[] args) throws TransformationException, IOException, AnalysisException, ParseException, CloneNotSupportedException {
        Options options = new Options();
        options.addOption("input", true, "input path");
        options.addOption("output", true, "output model path");
        options.addOption("kernel", true, "kernel type");
        options.addOption("g", true, "gamma");
        options.addOption("C", true, "C");
        options.addOption("degree", true, "degree");

        CommandLineParser parser = new GnuParser();
        CommandLine line = parser.parse(options, args);
        if (!(line.hasOption("input") && line.hasOption("output") && line.hasOption("kernel") && line.hasOption("g") && line.hasOption("C") && line.hasOption("degree"))) {
            System.err.println("Usage: <training-xml-directory path> <output model path>");
            
            System.err.println(line.hasOption("input") + " " + line.hasOption("output") + " " + line.hasOption("kernel") +  line.hasOption("g") + line.hasOption("C") + line.hasOption("degree"));
            System.exit(1);
        }

        Double C = Double.valueOf(line.getOptionValue("C"));
        Double gamma = Double.valueOf(line.getOptionValue("g"));
        String inDir = line.getOptionValue("input");
        String outFile = line.getOptionValue("output");
        Integer degree = Integer.valueOf(line.getOptionValue("degree"));
        Integer kernelType;
        switch(Integer.valueOf(line.getOptionValue("kernel"))) {
        	case 0: kernelType = svm_parameter.LINEAR; break;
        	case 1: kernelType = svm_parameter.POLY; break;
        	case 2: kernelType = svm_parameter.RBF; break;
        	case 3: kernelType = svm_parameter.SIGMOID; break;
        	default:
        		throw new IllegalArgumentException("Invalid kernel value provided");
        }
        
        File input = new File(inDir);
        List<TrainingSample<BxZoneLabel>> trainingSamples;
        if(input.isDirectory()) {
        	List<BxDocument> trainingDocuments = EvaluationUtils.getDocumentsFromPath(inDir);
    		FeatureVectorBuilder<BxZone, BxPage> featureVectorBuilder = SVMMetadataZoneClassifier.getFeatureVectorBuilder();
            trainingSamples = BxDocsToTrainingSamplesConverter.getZoneTrainingSamples(trainingDocuments, featureVectorBuilder, 
                    BxZoneLabel.getIdentityMap());
        } else {
        	trainingSamples = SVMZoneClassifier.loadProblem(inDir, SVMMetadataZoneClassifier.getFeatureVectorBuilder());
        }
        
        trainingSamples = ClassificationUtils.filterElements(trainingSamples, BxZoneLabelCategory.CAT_METADATA);
    	SVMZoneClassifier classifier = getZoneClassifier(trainingSamples, kernelType, gamma, C, degree);
    	classifier.saveModel(outFile);
	}
}
