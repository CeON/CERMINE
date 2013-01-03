package pl.edu.icm.cermine.metadata.optimization;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.apache.commons.cli.*;
import pl.edu.icm.cermine.evaluation.EvaluationUtils;
import pl.edu.icm.cermine.evaluation.EvaluationUtils.DocumentsIterator;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.exception.TransformationException;
import pl.edu.icm.cermine.structure.SVMInitialZoneClassifier;
import pl.edu.icm.cermine.structure.SVMMetadataZoneClassifier;
import pl.edu.icm.cermine.structure.model.*;
import pl.edu.icm.cermine.tools.classification.features.FeatureVectorBuilder;
import pl.edu.icm.cermine.tools.classification.general.BxDocsToTrainingSamplesConverter;
import pl.edu.icm.cermine.tools.classification.general.ClassificationUtils;
import pl.edu.icm.cermine.tools.classification.general.TrainingSample;
import pl.edu.icm.cermine.tools.classification.sampleselection.NormalSelector;
import pl.edu.icm.cermine.tools.classification.sampleselection.OversamplingSelector;
import pl.edu.icm.cermine.tools.classification.sampleselection.SampleSelector;
import pl.edu.icm.cermine.tools.classification.sampleselection.UndersamplingSelector;

public class LibSVMExporter {

    public static void toLibSVM(List<TrainingSample<BxZoneLabel>> trainingElements, String filePath) throws IOException {
    	BufferedWriter svmDataFile = null;
        try {
            FileWriter fstream = new FileWriter(filePath);
            svmDataFile = new BufferedWriter(fstream);
            for (TrainingSample<BxZoneLabel> elem : trainingElements) {
            	if(elem.getLabel() == null) {
            		continue;
            	}
                svmDataFile.write(String.valueOf(elem.getLabel().ordinal()));
                svmDataFile.write(" ");

                Integer featureCounter = 1;
                for (Double value : elem.getFeatures().getFeatures()) {
                    StringBuilder sb = new StringBuilder();
                    Formatter formatter = new Formatter(sb, Locale.US);
                    formatter.format("%d:%.5f", featureCounter++, value);
                    svmDataFile.write(sb.toString());
                    svmDataFile.write(" ");
                }
                svmDataFile.write("\n");
            }
            svmDataFile.close();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            return;
        } finally {
        	if(svmDataFile != null) {
        		svmDataFile.close();
        	}
        }
        
        System.out.println("Done.");
    }
    
    public static void main(String[] args) throws ParseException, IOException, TransformationException, AnalysisException {
        Options options = new Options();
        options.addOption("under", false, "use undersampling for data selection");
        options.addOption("over", false, "use oversampling for data selection");
        options.addOption("normal", false, "don't use any special strategy for data selection");

        CommandLineParser parser = new GnuParser();
        CommandLine line = parser.parse(options, args);

        if (args.length != 2 ||  !(line.hasOption("under") ^ line.hasOption("over") ^ line.hasOption("normal"))) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(" [-options] input-directory", options);
            System.exit(1); 
        }
        String inputDirPath = line.getArgs()[0];

        Map<BxZoneLabel, BxZoneLabel> labelMap = BxZoneLabel.getLabelToGeneralMap();

        SampleSelector<BxZoneLabel> sampler = null;
        if (line.hasOption("over")) {
            sampler = new OversamplingSelector<BxZoneLabel>(1.0);
        } else if (line.hasOption("under")) {
            sampler = new UndersamplingSelector<BxZoneLabel>(2.0);
        } else if (line.hasOption("normal")) {
            sampler = new NormalSelector<BxZoneLabel>();
        } else {
            System.err.println("Sampling strategy is not specified!");
            System.exit(1);
        }

        List<TrainingSample<BxZoneLabel>> initialTrainingElements = new ArrayList<TrainingSample<BxZoneLabel>>();
        List<TrainingSample<BxZoneLabel>> metaTrainingElements = new ArrayList<TrainingSample<BxZoneLabel>>();
        
        EvaluationUtils.DocumentsIterator iter = new DocumentsIterator(inputDirPath);
        FeatureVectorBuilder<BxZone, BxPage> vectorBuilder;
        Integer docIdx = 0;
        for(BxDocument doc: iter) {
        	System.out.println(docIdx + ": " + doc.getFilename());
        	////
        	for (BxZone zone : doc.asZones()) {
        		if (zone.getLabel() != null) {
        			if (zone.getLabel().getCategory() != BxZoneLabelCategory.CAT_METADATA) {
        				zone.setLabel(zone.getLabel().getGeneralLabel());
        			}
        		}
        	}
        	vectorBuilder = SVMMetadataZoneClassifier.getFeatureVectorBuilder();
        	List<TrainingSample<BxZoneLabel>> newSamples = BxDocsToTrainingSamplesConverter.getZoneTrainingSamples(doc, vectorBuilder, labelMap);
        	metaTrainingElements.addAll(newSamples);
        	////
        	for (BxZone zone : doc.asZones()) {
        		if(zone.getLabel() != null) {
        			zone.setLabel(zone.getLabel().getGeneralLabel());
        		}
        	}
        	vectorBuilder = SVMInitialZoneClassifier.getFeatureVectorBuilder();
        	newSamples = BxDocsToTrainingSamplesConverter.getZoneTrainingSamples(doc, vectorBuilder, labelMap);
        	initialTrainingElements.addAll(newSamples);
        	////
        	++docIdx;
        }

        initialTrainingElements = sampler.pickElements(initialTrainingElements);
        metaTrainingElements = sampler.pickElements(metaTrainingElements);

        toLibSVM(initialTrainingElements, "initial_zone_classification.dat");
        toLibSVM(metaTrainingElements, "meta_zone_classification.dat");
    }
}