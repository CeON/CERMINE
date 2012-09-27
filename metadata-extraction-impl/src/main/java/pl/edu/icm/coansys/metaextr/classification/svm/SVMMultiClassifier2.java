package pl.edu.icm.coansys.metaextr.classification.svm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import pl.edu.icm.coansys.metaextr.AnalysisException;
import pl.edu.icm.coansys.metaextr.classification.features.FeatureVectorBuilder;
import pl.edu.icm.coansys.metaextr.classification.hmm.training.TrainingElement;
import pl.edu.icm.coansys.metaextr.textr.model.BxDocument;
import pl.edu.icm.coansys.metaextr.textr.model.BxPage;
import pl.edu.icm.coansys.metaextr.textr.model.BxZone;
import pl.edu.icm.coansys.metaextr.textr.model.BxZoneLabel;

public class SVMMultiClassifier2 extends SVMZoneClassifier {
	private FeatureVectorBuilder<BxZone, BxPage> featureVectorBuilder;
	private List<BxZoneLabel> possibleLabels;
	private Map<BxZoneLabel, SVMZoneClassifier> classifiers;
	private SVMZoneClassifier ultimateClassifier;
	
	public SVMMultiClassifier2(FeatureVectorBuilder<BxZone, BxPage> featureVectorBuilder) {
		super(featureVectorBuilder);
		this.featureVectorBuilder = featureVectorBuilder;
	}
	
	@Override
	public BxDocument classifyZones(BxDocument document)	throws AnalysisException {
		for(BxZone zone: document.asZones()) {
			Map<BxZoneLabel, Integer> votes = new HashMap<BxZoneLabel, Integer>();
			for(BxZoneLabel lab: possibleLabels) {
				votes.put(lab, 0);
			}
			votes.put(BxZoneLabel.OTH_UNKNOWN, 0);
			
			for(BxZoneLabel lab: possibleLabels) {
				BxZoneLabel predicted = classifiers.get(lab).predictZoneLabel(zone);
				votes.put(predicted, votes.get(predicted) + 1);
			}
			if(votes.get(BxZoneLabel.OTH_UNKNOWN) == possibleLabels.size()-1) { //one classifier recognized this class
				for(BxZoneLabel lab: possibleLabels)
					if(votes.get(lab) == 1) {
						zone.setLabel(lab);
						continue;
					}
			} else { //at least two classifiers chose this class - leave decistion to the definitive classifier
				zone.setLabel(ultimateClassifier.predictZoneLabel(zone));
			}
		}
		return document;
	}
	
	public SVMZoneClassifier getClassifier(BxZoneLabel lab) {
		if(lab == null)
			return ultimateClassifier;
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
			
			List<TrainingElement<BxZoneLabel>> convertedElements = new ArrayList<TrainingElement<BxZoneLabel>>() {{
				Integer elemIdx = 0;
				for(TrainingElement<BxZoneLabel> elem: trainingElements) {
					//convert training elements' labels to one of two: lab (current iteration) or OTH_UNKNOWN
					TrainingElement<BxZoneLabel> toBeAdded = elem.clone();
					if(toBeAdded.getLabel() != lab) { //if label is wrong then change it
						toBeAdded.setLabel(BxZoneLabel.OTH_UNKNOWN);
						if(elemIdx != trainingElements.size()-1) {//last element
							trainingElements.get(elemIdx+1).getObservation().setFeature("PreviousZoneFeature",
									(double)BxZoneLabel.OTH_UNKNOWN.ordinal());
						}
					} //else leave it as it is
					add(toBeAdded);
					++elemIdx;
				}
			}};
			clas.buildClassifier(convertedElements);
			classifiers.put(lab, clas);
		}
		//build a classifier for ultimate recognition
		ultimateClassifier.buildClassifier(trainingElements);
	}
}
