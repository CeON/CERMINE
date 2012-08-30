package pl.edu.icm.yadda.analysis.metadata.optimization;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;

import pl.edu.icm.yadda.analysis.classification.features.FeatureVectorBuilder;
import pl.edu.icm.yadda.analysis.classification.hmm.training.HMMTrainingElement;
import pl.edu.icm.yadda.analysis.metadata.evaluation.CrossvalidatingZoneClassificationEvaluator;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.nodes.BxDocsToFVHMMTrainingElementsConverterNode;
import pl.edu.icm.yadda.analysis.textr.model.BxDocument;
import pl.edu.icm.yadda.analysis.textr.model.BxPage;
import pl.edu.icm.yadda.analysis.textr.model.BxZone;
import pl.edu.icm.yadda.analysis.textr.model.BxZoneLabel;
import pl.edu.icm.yadda.analysis.classification.features.FeatureVector;

public class SVMLibExporter {
	public static void toLibSVM(List<HMMTrainingElement<BxZoneLabel>> trainingElements, String filePath)
	{
        try {
        	FileWriter fstream = new FileWriter(filePath);
        	BufferedWriter svmDataFile = new BufferedWriter(fstream);
        	for(HMMTrainingElement<BxZoneLabel> elem: trainingElements) {
        		svmDataFile.write(String.valueOf(elem.getLabel().ordinal()));
        		svmDataFile.write(" ");
        		
        		Integer featureCounter = 1;
        		for(Double value: elem.getObservation().getFeatures()) {
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
        }
        System.out.println("Done.");
	}
	
	public static void main(String[] args)
	{
		if(args.length != 1)
		{
			System.out.println("Input directory required.");
			return;
		}
		String inputDirPath = args[0];
		List<BxDocument> evaluationDocs = CrossvalidatingZoneClassificationEvaluator.getEvaluationDocuments(inputDirPath);
		FeatureVectorBuilder<BxZone, BxPage> vectorBuilder = CrossvalidatingZoneClassificationEvaluator.getFeatureVectorBuilder();
		
		BxDocsToFVHMMTrainingElementsConverterNode node = new BxDocsToFVHMMTrainingElementsConverterNode();
		node.setLabelMap(BxZoneLabel.getLabelToGeneralMap());
        node.setFeatureVectorBuilder(vectorBuilder);
        
        List<HMMTrainingElement<BxZoneLabel>> trainingElements;
        try {
        	trainingElements = node.process(evaluationDocs, null);
        } catch(Exception e) {
			throw new RuntimeException("Unable to process the delivered training documents!");
		}    
        toLibSVM(trainingElements, "zone_classification.dat");
	}
}