package pl.edu.icm.yadda.analysis.classification.svm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pl.edu.icm.yadda.analysis.AnalysisException;
import pl.edu.icm.yadda.analysis.classification.features.FeatureVectorBuilder;
import pl.edu.icm.yadda.analysis.classification.hmm.training.TrainingElement;
import pl.edu.icm.yadda.analysis.classification.tools.ClassificationUtils;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.tools.LabelPair;
import pl.edu.icm.yadda.analysis.textr.model.BxDocument;
import pl.edu.icm.yadda.analysis.textr.model.BxPage;
import pl.edu.icm.yadda.analysis.textr.model.BxZone;
import pl.edu.icm.yadda.analysis.textr.model.BxZoneLabel;

public class SVMMultiClassifier2 extends SVMZoneClassifier {
	private FeatureVectorBuilder<BxZone, BxPage> featureVectorBuilder;
	private List<BxZoneLabel> possibleLabels;
	private Map<BxZoneLabel, SVMZoneClassifier> classifiers;
	private SVMZoneClassifier definitiveClassifier;
	
	public SVMMultiClassifier2(FeatureVectorBuilder<BxZone, BxPage> featureVectorBuilder) {
		super(featureVectorBuilder);
		this.featureVectorBuilder = featureVectorBuilder;
	}
	
	@Override
	public BxDocument classifyZones(BxDocument document)	throws AnalysisException {
		for(BxZone zone: document.asZones()) {
		}
		return document;
	}
	
	public SVMZoneClassifier getClassifier(BxZoneLabel lab) {
		if(lab == null)
			return definitiveClassifier;
		return classifiers.get(lab);
	}
	
	public void setPossibleLabels(Collection<BxZoneLabel> labels) {
		possibleLabels.addAll(labels);
			for(BxZoneLabel lab: possibleLabels) {
				classifiers.put(lab, new SVMZoneClassifier(featureVectorBuilder));
			}
	}
	
	@Override
	public void buildClassifier(final List<TrainingElement<BxZoneLabel>> trainingElements) {
		possibleLabels = new ArrayList<BxZoneLabel>();
		for(TrainingElement<BxZoneLabel> elem: trainingElements)
			if(!possibleLabels.contains(elem.getLabel()))
				possibleLabels.add(elem.getLabel());
		
		for(final BxZoneLabel lab: possibleLabels) {
			SVMZoneClassifier clas = new SVMZoneClassifier(featureVectorBuilder);
			List<TrainingElement<BxZoneLabel>> thisLabelElements = new ArrayList<TrainingElement<BxZoneLabel>>() {{
				for(TrainingElement<BxZoneLabel> elem: trainingElements) {
					TrainingElement<BxZoneLabel> toBeAdded = elem.clone();
					//TODO: change label to be one of two
					add(elem.clone());
				}
			}};
			//TODO:...
		}
	}
	
	@Override
	public void loadModel(String modelPath) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void saveModel(String modelPath) throws IOException {
		// TODO Auto-generated method stub
	}

}
