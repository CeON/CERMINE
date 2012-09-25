package pl.edu.icm.coansys.metaextr.classification.svm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import libsvm.svm;
import libsvm.svm_node;
import libsvm.svm_parameter;

import pl.edu.icm.coansys.metaextr.AnalysisException;
import pl.edu.icm.coansys.metaextr.classification.features.FeatureVectorBuilder;
import pl.edu.icm.coansys.metaextr.classification.hmm.training.TrainingElement;
import pl.edu.icm.coansys.metaextr.classification.tools.ClassificationUtils;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.tools.LabelPair;
import pl.edu.icm.coansys.metaextr.textr.ZoneClassifier;
import pl.edu.icm.coansys.metaextr.textr.model.BxDocument;
import pl.edu.icm.coansys.metaextr.textr.model.BxPage;
import pl.edu.icm.coansys.metaextr.textr.model.BxZone;
import pl.edu.icm.coansys.metaextr.textr.model.BxZoneLabel;

public class SVMMultiClassifier extends SVMZoneClassifier {

	private FeatureVectorBuilder<BxZone, BxPage> featureVectorBuilder;
	private List<BxZoneLabel> possibleLabels;
	private Map<LabelPair, SVMZoneClassifier> classifierMatrix;
	
	public SVMMultiClassifier(FeatureVectorBuilder<BxZone, BxPage> featureVectorBuilder) {
		super(featureVectorBuilder);
		this.featureVectorBuilder = featureVectorBuilder;
	}
	
	private BxZoneLabel findMaxLabel(Map<BxZoneLabel, Integer> votes) {
		Integer maxVote = 0;
		BxZoneLabel bestLab = null;
		for(BxZoneLabel lab: votes.keySet()) {
			if(votes.get(lab) > maxVote) {
				maxVote = votes.get(lab);
				bestLab = lab;
			}
		}
		assert bestLab != null;
		return bestLab;
	}
	
	@Override
	public BxDocument classifyZones(BxDocument document)	throws AnalysisException {
		for(BxZone zone: document.asZones()) {
			Map<BxZoneLabel, Integer> votes = new HashMap<BxZoneLabel, Integer>();
			for(LabelPair labelPair: classifierMatrix.keySet()) {
				SVMZoneClassifier clas = classifierMatrix.get(labelPair);
				BxZoneLabel vote = clas.predictZoneLabel(zone);
				if(votes.containsKey(vote))
					votes.put(vote, votes.get(vote)+1);
				else
					votes.put(vote, 1);
				zone.setLabel(findMaxLabel(votes));
			}
		}
		return document;
	}
	
	public SVMZoneClassifier getClassifier(BxZoneLabel lab1, BxZoneLabel lab2) {
		if(lab1 == lab2)
			throw new RuntimeException("No classifier for two same labels");
		if(lab2.ordinal() > lab1.ordinal()) {
			return classifierMatrix.get(new LabelPair(lab2, lab1));
		} else {
			return classifierMatrix.get(new LabelPair(lab1, lab2));
		}
	}
	
	public void setPossibleLabels(Collection<BxZoneLabel> labels) {
		possibleLabels.addAll(labels);
		for(BxZoneLabel lab1: possibleLabels)
			for(BxZoneLabel lab2: possibleLabels) {
				if(lab2.ordinal() >= lab1.ordinal())
					continue;
				classifierMatrix.put(new LabelPair(lab1, lab2), new SVMZoneClassifier(featureVectorBuilder));
			}
	}
	
	@Override
	public void buildClassifier(List<TrainingElement<BxZoneLabel>> trainingElements) {
		possibleLabels = new ArrayList<BxZoneLabel>();
		for(TrainingElement<BxZoneLabel> elem: trainingElements)
			if(!possibleLabels.contains(elem.getLabel()))
				possibleLabels.add(elem.getLabel());
		
		for(final BxZoneLabel lab1: possibleLabels)
			for(final BxZoneLabel lab2: possibleLabels) {
				if(lab2.ordinal() >= lab1.ordinal())
					continue;
				LabelPair coord = new LabelPair(lab1, lab2);
				SVMZoneClassifier clas = new SVMZoneClassifier(featureVectorBuilder);
				List<TrainingElement<BxZoneLabel>> filteredTrainigElements = ClassificationUtils.filterElements(trainingElements, new ArrayList<BxZoneLabel>() {{
					add(lab1);
					add(lab2);
				}});
				clas.buildClassifier(filteredTrainigElements);
				classifierMatrix.put(coord, clas);
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
