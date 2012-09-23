package pl.edu.icm.yadda.analysis.metadata.evaluation;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import pl.edu.icm.yadda.analysis.classification.features.FeatureVectorBuilder;
import pl.edu.icm.yadda.analysis.classification.hmm.training.TrainingElement;
import pl.edu.icm.yadda.analysis.classification.tools.DirExtractor;
import pl.edu.icm.yadda.analysis.classification.tools.DocumentsExtractor;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.nodes.BxDocsToTrainingElementsConverterNode;
import pl.edu.icm.yadda.analysis.textr.model.BxDocument;
import pl.edu.icm.yadda.analysis.textr.model.BxPage;
import pl.edu.icm.yadda.analysis.textr.model.BxZone;
import pl.edu.icm.yadda.analysis.textr.model.BxZoneLabel;
import pl.edu.icm.yadda.analysis.textr.tools.DocumentPreprocessor;

public class EvaluationUtils {
    public static List<BxDocument> getEvaluationDocuments(String inputDirPath)
	{
		if (inputDirPath == null) {
			throw new NullPointerException("Input directory must not be null.");
		}

		if (!inputDirPath.endsWith(File.separator)) {
			inputDirPath += File.separator;
		}
		DocumentsExtractor extractor = new DirExtractor(inputDirPath);
		
		List<BxDocument> evaluationDocuments;
		try {
			 evaluationDocuments = extractor.getDocuments();
		} catch(Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Unable to get evaluation documents from the indicated location! Got exception: " + e);
		}
		/*
		for(BxDocument doc: evaluationDocuments) {
				for(BxZone zone: doc.asZones()) {
				System.out.println(";; " + zone.getLabel());
			}
		}*/
		return evaluationDocuments;
	}
    
	public static List<TrainingElement<BxZoneLabel>> getTrainingElements(BxDocument trainingDocument, FeatureVectorBuilder<BxZone, BxPage> featureVectorBuilder) {
		List<BxDocument> dummyList = new ArrayList<BxDocument>(1);
		dummyList.add(trainingDocument);
		return getTrainingElements(new ArrayList<BxDocument>(dummyList), featureVectorBuilder);
	}
	
	public static List<TrainingElement<BxZoneLabel>> getTrainingElements(List<BxDocument> trainingDocuments, FeatureVectorBuilder<BxZone, BxPage> featureVectorBuilder) { 
		BxDocsToTrainingElementsConverterNode node = new BxDocsToTrainingElementsConverterNode();
		node.setLabelMap(null);

		node.setFeatureVectorBuilder(featureVectorBuilder);
		List<TrainingElement<BxZoneLabel>> trainingElements = null;
		try {
			trainingElements = node.process(trainingDocuments, null);
		} catch (Exception e) {
			System.out.println(e.getCause());
			e.printStackTrace();
			System.exit(1);
		}
		return trainingElements;
	}
}
