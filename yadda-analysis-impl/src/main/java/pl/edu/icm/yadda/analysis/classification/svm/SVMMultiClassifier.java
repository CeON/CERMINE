package pl.edu.icm.yadda.analysis.classification.svm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import libsvm.svm_parameter;

import pl.edu.icm.yadda.analysis.AnalysisException;
import pl.edu.icm.yadda.analysis.classification.features.FeatureVectorBuilder;
import pl.edu.icm.yadda.analysis.classification.hmm.training.TrainingElement;
import pl.edu.icm.yadda.analysis.textr.ZoneClassifier;
import pl.edu.icm.yadda.analysis.textr.model.BxDocument;
import pl.edu.icm.yadda.analysis.textr.model.BxPage;
import pl.edu.icm.yadda.analysis.textr.model.BxZone;
import pl.edu.icm.yadda.analysis.textr.model.BxZoneLabel;

public class SVMMultiClassifier implements ZoneClassifier {

	private FeatureVectorBuilder<BxZone, BxPage> featureVectorBuilder;
	private List<BxZoneLabel> possibleLabels;
	private SVMZoneClassifier[][] classifierMatrix;
	
	public SVMMultiClassifier(FeatureVectorBuilder<BxZone, BxPage> featureVectorBuilder) {
		this.featureVectorBuilder = featureVectorBuilder;
	}
	@Override
	public BxDocument classifyZones(BxDocument document)	throws AnalysisException {
		// TODO Auto-generated method stub
		return null;
	}
	
	public SVMZoneClassifier getClassifier(BxZoneLabel lab1, BxZoneLabel lab2) {
		if(lab1 == lab2)
			throw new RuntimeException("No classifier for two same labels");
		if(lab2.ordinal() > lab1.ordinal()) {
			return classifierMatrix[lab2.ordinal()][lab1.ordinal()];
		} else {
			return classifierMatrix[lab1.ordinal()][lab2.ordinal()];
		}
	}
	
	public void setPossibleLabels(Collection<BxZoneLabel> labels) {
		possibleLabels.addAll(labels);
		for(BxZoneLabel lab1: possibleLabels)
			for(BxZoneLabel lab2: possibleLabels) {
				if(lab2.ordinal() >= lab1.ordinal())
					continue;
				classifierMatrix[lab1.ordinal()][lab2.ordinal()] = new SVMZoneClassifier(featureVectorBuilder);
			}
	}
	
	public void buildClassifier(List<TrainingElement<BxZoneLabel>> trainigElements) {
		possibleLabels = new ArrayList<BxZoneLabel>();
		for(TrainingElement<BxZoneLabel> elem: trainigElements)
			if(!possibleLabels.contains(elem.getLabel()))
				possibleLabels.add(elem.getLabel());
		for(BxZoneLabel lab1: possibleLabels)
			for(BxZoneLabel lab2: possibleLabels) {
				if(lab2.ordinal() >= lab1.ordinal())
					continue;
				classifierMatrix[lab1.ordinal()][lab2.ordinal()] = new SVMZoneClassifier(featureVectorBuilder);
			}
	}
	
	private static svm_parameter clone(svm_parameter param) {
		svm_parameter ret = new svm_parameter();
		// default values
		ret.svm_type = param.svm_type;
		ret.C = param.C;
		ret.kernel_type = param.kernel_type;
		ret.degree = param.degree;
		ret.gamma = param.gamma; // 1/k
		ret.coef0 = param.coef0;
		ret.nu = param.nu;
		ret.cache_size = param.cache_size;
		ret.eps = param.eps;
		ret.p = param.p;
		ret.shrinking = param.shrinking;
		ret.probability = param.probability;
		ret.nr_weight = param.nr_weight;
		ret.weight_label = param.weight_label;
		ret.weight = param.weight;
		return ret;
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
