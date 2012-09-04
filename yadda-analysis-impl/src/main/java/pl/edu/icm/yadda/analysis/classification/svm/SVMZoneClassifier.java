package pl.edu.icm.yadda.analysis.classification.svm;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.ArrayUtils;

import pl.edu.icm.yadda.analysis.AnalysisException;
import pl.edu.icm.yadda.analysis.classification.features.FeatureVector;
import pl.edu.icm.yadda.analysis.classification.features.FeatureVectorBuilder;
import pl.edu.icm.yadda.analysis.classification.features.SimpleFeatureVector;
import pl.edu.icm.yadda.analysis.classification.hmm.training.HMMTrainingElement;
import pl.edu.icm.yadda.analysis.textr.ZoneClassifier;
import pl.edu.icm.yadda.analysis.textr.model.BxDocument;
import pl.edu.icm.yadda.analysis.textr.model.BxPage;
import pl.edu.icm.yadda.analysis.textr.model.BxZone;
import pl.edu.icm.yadda.analysis.textr.model.BxZoneLabel;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DefaultDataset;
import net.sf.javaml.core.DenseInstance;
import net.sf.javaml.core.Instance;
import libsvm.LibSVM;
import libsvm.svm_parameter;

public class SVMZoneClassifier implements ZoneClassifier {
	final static svm_parameter defaultParameter = new svm_parameter();		
	static {
		// default values
		defaultParameter.svm_type = svm_parameter.C_SVC;
		defaultParameter.C = 2048;
		defaultParameter.kernel_type = svm_parameter.LINEAR;
		defaultParameter.degree = 3;
		defaultParameter.gamma = 128; // 1/k
		defaultParameter.coef0 = 0.5;
		defaultParameter.nu = 0.5;
		defaultParameter.cache_size = 100;
		defaultParameter.eps = 1e-3;
		defaultParameter.p = 0.1;
		defaultParameter.shrinking = 1;
		defaultParameter.probability = 0;
		defaultParameter.nr_weight = 0;
		defaultParameter.weight_label = new int[0];
		defaultParameter.weight = new double[0];
	}
	private FeatureVectorBuilder<BxZone, BxPage> featureVectorBuilder;
	private final LibSVM svm = new LibSVM();
	private FeatureLimits[] limits;
	private String[] features;
	
	
	public SVMZoneClassifier(List<BxZoneLabel> zoneLabels, FeatureVectorBuilder<BxZone, BxPage> featureVectorBuilder) 
	{
		//discard zoneLabels;
		this.featureVectorBuilder = featureVectorBuilder;
		limits = new FeatureLimits[featureVectorBuilder.size()];

		//set default limits to: max = -inf, min = +inf
		for(int idx=0; idx<featureVectorBuilder.size(); ++idx) {
			limits[idx] = new FeatureLimits(Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY);
		}		
		
		svm_parameter param = getDefaultParam();
		svm.setParameters(param);
	}
	
	public LibSVM getSVM() {
		return svm;
	}
	
	public svm_parameter getDefaultParam() {
		svm_parameter param = new svm_parameter();
		// default values
		param.svm_type = defaultParameter.svm_type;
		param.C = defaultParameter.C;
		param.kernel_type = defaultParameter.kernel_type;
		param.degree = defaultParameter.degree;
		param.gamma = defaultParameter.gamma; // 1/k
		param.coef0 = defaultParameter.coef0;
		param.nu = defaultParameter.nu;
		param.cache_size = defaultParameter.cache_size;
		param.eps = defaultParameter.eps;
		param.p = defaultParameter.p;
		param.shrinking = defaultParameter.shrinking;
		param.probability = defaultParameter.probability;
		param.nr_weight = defaultParameter.nr_weight;
		param.weight_label = defaultParameter.weight_label;
		param.weight = defaultParameter.weight;
		return param;
	}
	
	private void setFeatureLimits(List<HMMTrainingElement<BxZoneLabel>> trainingElements) 
	{
		for(HMMTrainingElement<BxZoneLabel> trainingElem: trainingElements) {
			FeatureVector fv = trainingElem.getObservation();
			Set<String> names = fv.getFeatureNames();
			
			int featureIdx = 0;
			for(String name: names) {
				double val = fv.getFeature(name);
				if(val > limits[featureIdx].max) {
					limits[featureIdx].setMax(val);
				}
				if(val < limits[featureIdx].min){
					limits[featureIdx].setMin(val);
				}
				++featureIdx;
			}
		}
		for(FeatureLimits limit: limits) {
			assert limit.getMin() != Double.MAX_VALUE;
			assert limit.getMax() != Double.MIN_VALUE;
		}
	}
	
	public void buildClassifier(List<HMMTrainingElement<BxZoneLabel>> trainingElements) 
	{
		assert trainingElements.size() > 0;
		if(features == null) {
			features = (String[])trainingElements.get(0).getObservation().getFeatureNames().toArray(new String[1]);
		}
		setFeatureLimits(trainingElements);
		Dataset data = buildDatasetForTraining(trainingElements);
		svm.buildClassifier(data);
	}
	
	private FeatureVector scaleFeatureVector(FeatureVector fv)
	{
		final double EPS = 0.00001;
		FeatureVector newVector = new SimpleFeatureVector();

		int featureIdx = 0;
		for(String name: fv.getFeatureNames()) {
			//scaling function: y = a*x+b
			// 0 = a*v_min + b
			// 1 = a*v_max + b
			if(Math.abs(limits[featureIdx].getMax()-limits[featureIdx].getMin()) < EPS) {
				newVector.addFeature(name, 1.0);
			} else {
				Double featureValue = fv.getFeature(name);
				Double a = 1/(limits[featureIdx].getMax()-limits[featureIdx].getMin());
				Double b = -a*limits[featureIdx].getMin();
				
				featureValue = a*featureValue+b; 
				newVector.addFeature(name, featureValue);
			}
			++featureIdx;
		}
		return newVector;
	}
	
	private Dataset buildDatasetForTraining(List<HMMTrainingElement<BxZoneLabel>> trainingElements)
	{
		Dataset data = new DefaultDataset();
		for(HMMTrainingElement<BxZoneLabel> trainingElem : trainingElements) {
			FeatureVector fv = scaleFeatureVector(trainingElem.getObservation());
			System.out.println("Training label: " + trainingElem.getLabel());
			Instance dataInstance = new DenseInstance(ArrayUtils.toPrimitive(fv.getFeatures()), trainingElem.getLabel());
			data.add(dataInstance);
		}
		assert data.size() == trainingElements.size();
		return data;
	}
	
	private Dataset buildDatasetForClassification(List<FeatureVector> featureVectors)
	{
		Dataset data = new DefaultDataset();
		for(FeatureVector fv: featureVectors) {
			FeatureVector newVector = scaleFeatureVector(fv);
			Instance dataInstance = new DenseInstance(ArrayUtils.toPrimitive(newVector.getFeatures()));
			data.add(dataInstance);
		}
		assert data.size() == featureVectors.size();
		return data;
	}
	
	@Override
	public BxDocument classifyZones(BxDocument document) throws AnalysisException 
	{       
		for (BxPage page : document.getPages()) {
			for (BxZone zone : page.getZones()) {
				List<FeatureVector> featureVectors = new ArrayList<FeatureVector>();
				featureVectors.add(featureVectorBuilder.getFeatureVector(zone, page));
				
				Dataset unclassifiedData = buildDatasetForClassification(featureVectors);

				Instance instance = unclassifiedData.get(0);
				Object o = svm.classify(instance);
				BxZoneLabel predictedClassValue = (BxZoneLabel)o; 

				zone.setLabel(predictedClassValue);
			}
		}
        return document;
	} 

	public double[] getWeights() {
		return svm.getWeights();
	}

	private static class FeatureLimits 
	{
		public FeatureLimits(Double minValue, Double maxValue) {
			min = minValue;
			max = maxValue;
		}
		public double getMin() {
			return min;
		}
		public void setMin(double min) {
			this.min = min;
		}
		public double getMax() {
			return max;
		}
		public void setMax(double max) {
			this.max = max;
		}
		private double min;
		private double max;
	}
}
