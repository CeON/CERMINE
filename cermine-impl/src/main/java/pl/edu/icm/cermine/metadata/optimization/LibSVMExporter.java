package pl.edu.icm.cermine.metadata.optimization;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;
import org.apache.commons.cli.*;
import pl.edu.icm.cermine.evaluation.tools.EvaluationUtils;
import pl.edu.icm.cermine.evaluation.tools.EvaluationUtils.DocumentsIterator;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.exception.TransformationException;
import pl.edu.icm.cermine.structure.HierarchicalReadingOrderResolver;
import pl.edu.icm.cermine.structure.SVMInitialZoneClassifier;
import pl.edu.icm.cermine.structure.SVMMetadataZoneClassifier;
import pl.edu.icm.cermine.structure.model.*;
import pl.edu.icm.cermine.tools.classification.features.FeatureVectorBuilder;
import pl.edu.icm.cermine.tools.classification.general.BxDocsToTrainingSamplesConverter;
import pl.edu.icm.cermine.tools.classification.general.TrainingSample;
import pl.edu.icm.cermine.tools.classification.sampleselection.NormalSelector;
import pl.edu.icm.cermine.tools.classification.sampleselection.OversamplingSampler;
import pl.edu.icm.cermine.tools.classification.sampleselection.SampleSelector;
import pl.edu.icm.cermine.tools.classification.sampleselection.UndersamplingSelector;

public class LibSVMExporter {

    public static void toLibSVM(TrainingSample<BxZoneLabel> trainingElement, BufferedWriter fileWriter) throws IOException {
        try {
        	if(trainingElement.getLabel() == null) {
        		return;
        	}
        	fileWriter.write(String.valueOf(trainingElement.getLabel().ordinal()));
        	fileWriter.write(" ");
        	
        	Integer featureCounter = 1;
        	for (Double value : trainingElement.getFeatures().getFeatures()) {
        		StringBuilder sb = new StringBuilder();
        		Formatter formatter = new Formatter(sb, Locale.US);
        		formatter.format("%d:%.5f", featureCounter++, value);
        		fileWriter.write(sb.toString());
        		fileWriter.write(" ");
        	}
        	fileWriter.write("\n");
        	fileWriter.close();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            return;
        }
    }
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
    
    public static void main(String[] args) throws ParseException, IOException, TransformationException, AnalysisException, CloneNotSupportedException {
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
        File inputDirFile = new File(inputDirPath);

        SampleSelector<BxZoneLabel> sampler = null;
        if (line.hasOption("over")) {
            sampler = new OversamplingSampler<BxZoneLabel>(1.0);
        } else if (line.hasOption("under")) {
            sampler = new UndersamplingSelector<BxZoneLabel>(1.0);
        } else if (line.hasOption("normal")) {
            sampler = new NormalSelector<BxZoneLabel>();
        } else {
            System.err.println("Sampling pattern is not specified!");
            System.exit(1);
        }

        List<TrainingSample<BxZoneLabel>> initialTrainingElements = new ArrayList<TrainingSample<BxZoneLabel>>();
        List<TrainingSample<BxZoneLabel>> metaTrainingElements = new ArrayList<TrainingSample<BxZoneLabel>>();
        
        HierarchicalReadingOrderResolver ror = new HierarchicalReadingOrderResolver();
        EvaluationUtils.DocumentsIterator iter = new DocumentsIterator(inputDirPath);
        FeatureVectorBuilder<BxZone, BxPage> vectorBuilder;
        Integer docIdx = 0;
        for(BxDocument doc: iter) {
        	System.out.println(docIdx + ": " + doc.getFilename());
        	String filename = doc.getFilename();
        	doc = ror.resolve(doc);
        	doc.setFilename(filename);
        	////
        	for (BxZone zone : doc.asZones()) {
        		if (zone.getLabel() != null) {
        			if (zone.getLabel().getCategory() != BxZoneLabelCategory.CAT_METADATA) {
        				zone.setLabel(zone.getLabel().getGeneralLabel());
        			}
        		}
        		else {
        			zone.setLabel(BxZoneLabel.OTH_UNKNOWN);
        		}
        	}
        	vectorBuilder = SVMMetadataZoneClassifier.getFeatureVectorBuilder();
        	List<TrainingSample<BxZoneLabel>> newSamples = BxDocsToTrainingSamplesConverter.getZoneTrainingSamples(doc, vectorBuilder, BxZoneLabel.getIdentityMap());
        	
        	for(TrainingSample<BxZoneLabel> sample: newSamples) {
        		if(sample.getLabel().getCategory() == BxZoneLabelCategory.CAT_METADATA) {
        			metaTrainingElements.add(sample);
        		}
        	}
        	////
        	vectorBuilder = SVMInitialZoneClassifier.getFeatureVectorBuilder();
        	newSamples = BxDocsToTrainingSamplesConverter.getZoneTrainingSamples(doc, vectorBuilder, BxZoneLabel.getLabelToGeneralMap());
        	initialTrainingElements.addAll(newSamples);
        	////
        	++docIdx;
        }

        initialTrainingElements = sampler.pickElements(initialTrainingElements);
        metaTrainingElements = sampler.pickElements(metaTrainingElements);

        toLibSVM(initialTrainingElements, "initial_" + inputDirFile.getName() + ".dat");
        toLibSVM(metaTrainingElements, "meta_" + inputDirFile.getName() + ".dat");
    }
}