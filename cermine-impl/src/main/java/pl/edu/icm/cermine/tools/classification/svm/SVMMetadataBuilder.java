package pl.edu.icm.cermine.tools.classification.svm;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import libsvm.svm_parameter;
import org.apache.commons.cli.*;
import pl.edu.icm.cermine.evaluation.tools.EvaluationUtils;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.exception.TransformationException;
import pl.edu.icm.cermine.structure.SVMMetadataZoneClassifier;
import pl.edu.icm.cermine.structure.model.*;
import pl.edu.icm.cermine.tools.classification.features.FeatureVectorBuilder;
import pl.edu.icm.cermine.tools.classification.general.BxDocsToTrainingSamplesConverter;
import pl.edu.icm.cermine.tools.classification.general.ClassificationUtils;
import pl.edu.icm.cermine.tools.classification.general.TrainingSample;
import pl.edu.icm.cermine.tools.classification.sampleselection.OversamplingSampler;
import pl.edu.icm.cermine.tools.classification.sampleselection.SampleSelector;

public class SVMMetadataBuilder {
	protected static SVMZoneClassifier getZoneClassifier(List<BxDocument> trainingDocuments, Integer kernelType, Double gamma, Double C, Integer degree) throws IOException, AnalysisException, CloneNotSupportedException
	{
		FeatureVectorBuilder<BxZone, BxPage> featureVectorBuilder = SVMMetadataZoneClassifier.getFeatureVectorBuilder();
        Map<BxZoneLabel, BxZoneLabel> labelMapper = BxZoneLabel.getLabelToGeneralMap();
        for (BxDocument doc : trainingDocuments) {
            for (BxZone zone : doc.asZones()) {
                if (zone.getLabel().getCategory() != BxZoneLabelCategory.CAT_METADATA) {
                    zone.setLabel(labelMapper.get(zone.getLabel()));
                }
            }
        }

        List<TrainingSample<BxZoneLabel>> TrainingSamplesUnrevised = BxDocsToTrainingSamplesConverter.getZoneTrainingSamples(trainingDocuments, featureVectorBuilder);
        TrainingSamplesUnrevised = ClassificationUtils.filterElements(TrainingSamplesUnrevised, BxZoneLabelCategory.CAT_METADATA);

        SampleSelector<BxZoneLabel> selector = new OversamplingSampler<BxZoneLabel>(0.7);
        List<TrainingSample<BxZoneLabel>> trainingSamples = selector.pickElements(TrainingSamplesUnrevised);

        // Filter the training documents
        // so that in the learning examples all classes are
        // represented equally

        SVMZoneClassifier zoneClassifier = new SVMZoneClassifier(featureVectorBuilder);
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
        options.addOption("input", true, "input xml directory path");
        options.addOption("output", true, "output model path");
        options.addOption("kernel", true, "kernel type");
        options.addOption("g", true, "gamma");
        options.addOption("C", true, "C");
        options.addOption("degree", true, "degree");

        CommandLineParser parser = new GnuParser();
        CommandLine line = parser.parse(options, args);
        if (!(line.hasOption("input") && line.hasOption("output") && line.hasOption("k") && line.hasOption("g") && line.hasOption("C") && line.hasOption("degree"))) {
            System.err.println("Usage: <training-xml-directory path> <output model path>");
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
        
		List<BxDocument> trainingDocuments = EvaluationUtils.getDocumentsFromPath(inDir);
		SVMZoneClassifier classifier = getZoneClassifier(trainingDocuments, kernelType, gamma, C, degree);
		classifier.saveModel(outFile);
	}
}
