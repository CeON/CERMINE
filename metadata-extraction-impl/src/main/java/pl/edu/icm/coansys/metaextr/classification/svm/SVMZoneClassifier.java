package pl.edu.icm.coansys.metaextr.classification.svm;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import libsvm.svm;
import libsvm.svm_model;
import libsvm.svm_node;
import libsvm.svm_parameter;
import libsvm.svm_problem;

import org.apache.commons.collections.iterators.ArrayIterator;
import org.apache.commons.lang.ArrayUtils;

import pl.edu.icm.coansys.metaextr.AnalysisException;
import pl.edu.icm.coansys.metaextr.classification.features.FeatureVector;
import pl.edu.icm.coansys.metaextr.classification.features.FeatureVectorBuilder;
import pl.edu.icm.coansys.metaextr.classification.features.SimpleFeatureVector;
import pl.edu.icm.coansys.metaextr.classification.hmm.training.TrainingElement;
import pl.edu.icm.coansys.metaextr.classification.tools.FeatureLimits;
import pl.edu.icm.coansys.metaextr.classification.tools.FeatureVectorScaler;
import pl.edu.icm.coansys.metaextr.classification.tools.LinearScaling;
import pl.edu.icm.coansys.metaextr.textr.ZoneClassifier;
import pl.edu.icm.coansys.metaextr.textr.model.BxDocument;
import pl.edu.icm.coansys.metaextr.textr.model.BxPage;
import pl.edu.icm.coansys.metaextr.textr.model.BxZone;
import pl.edu.icm.coansys.metaextr.textr.model.BxZoneLabel;

public class SVMZoneClassifier implements ZoneClassifier {
	final protected static svm_parameter defaultParameter = new svm_parameter();		
	static {
		// default values
		defaultParameter.svm_type = svm_parameter.C_SVC;
		defaultParameter.C = 2048;
		defaultParameter.kernel_type = svm_parameter.POLY;
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
	protected FeatureVectorBuilder<BxZone, BxPage> featureVectorBuilder;
	protected FeatureVectorScaler scaler;
	protected String[] features;
	
	private svm_parameter param;
	private svm_problem problem;
	private svm_model model;
	
	
	public SVMZoneClassifier(FeatureVectorBuilder<BxZone, BxPage> featureVectorBuilder) 
	{
		//discard zoneLabels;
		this.featureVectorBuilder = featureVectorBuilder;
		Integer dimensions = featureVectorBuilder.size();
		
		Double scaledLowerBound = 0.0;
		Double scaledUpperBound = 1.0;
		scaler = new FeatureVectorScaler(dimensions, scaledLowerBound, scaledUpperBound);
		scaler.setStrategy(new LinearScaling());
		
		param = getDefaultParam();
	}
	
	protected static svm_parameter clone(svm_parameter param) {
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
	
	public static svm_parameter getDefaultParam() {
		return clone(defaultParameter);
	}
	
	public void buildClassifier(List<TrainingElement<BxZoneLabel>> trainingElements) 
	{
		assert trainingElements.size() > 0;
		if(features == null) {
			features = (String[])trainingElements.get(0).getObservation().getFeatureNames().toArray(new String[1]);
		}
		scaler.setFeatureLimits(trainingElements);
		problem = buildDatasetForTraining(trainingElements);
		model = libsvm.svm.svm_train(problem, param);
	}
	
	BxZoneLabel predictZoneLabel(BxZone zone) {
		svm_node[] instance = buildDatasetForClassification(zone);
		double predictedVal = svm.svm_predict(model, instance);
		return BxZoneLabel.values()[(int)predictedVal];
	}
	
	@Override
	public BxDocument classifyZones(BxDocument document) throws AnalysisException 
	{       
		for (BxZone zone: document.asZones()) {
			svm_node[] instance = buildDatasetForClassification(zone);
			double predictedVal = svm.svm_predict(model, instance);
			System.out.println("predictedVal " + predictedVal + " " + BxZoneLabel.values()[(int)predictedVal] + " (is " + zone.getLabel() + ")");
			zone.setLabel(BxZoneLabel.values()[(int)predictedVal]);
		}
		return document;
	}

	protected svm_problem buildDatasetForTraining(List<TrainingElement<BxZoneLabel>> trainingElements)
	{
		svm_problem problem = new svm_problem();
		problem.l = trainingElements.size();
		problem.x = new svm_node[problem.l][trainingElements.get(0).getObservation().size()];
		problem.y = new double[trainingElements.size()];
		
		Integer elemIdx = 0;
		for(TrainingElement<BxZoneLabel> trainingElem : trainingElements) {
			FeatureVector scaledFV = scaler.scaleFeatureVector(trainingElem.getObservation());
			Integer featureIdx = 0;
			for(Double val: scaledFV.getFeatures()) {
				svm_node cur = new svm_node();
				cur.index = featureIdx;
				cur.value = val;
				problem.x[elemIdx][featureIdx] = cur;
				++featureIdx;
			}
			problem.y[elemIdx] = trainingElem.getLabel().ordinal();
			System.out.println("training " + trainingElem.getLabel().ordinal() + " (" + trainingElem.getLabel() + ")");
			++elemIdx;
		}
		return problem;
	}
	
	protected svm_node[] buildDatasetForClassification(BxZone zone)
	{
		svm_node[] ret = new svm_node[featureVectorBuilder.getFeatureNames().size()];
		FeatureVector scaledFV = scaler.scaleFeatureVector(featureVectorBuilder.getFeatureVector(zone, zone.getContext()));
		
		Integer featureIdx = 0;
		for(Double val: scaledFV.getFeatures()) {
			svm_node cur = new svm_node();
			cur.index = featureIdx;
			cur.value = val;
			ret[featureIdx] = cur;
			++featureIdx;
		}
		return ret;
	}

	public double[] getWeights() {
		double[][] coef = model.sv_coef;

		double[][] prob = new double[model.SV.length][featureVectorBuilder.size()];
		for (int i = 0; i < model.SV.length; i++) {
			for (int j = 0; j < model.SV[i].length; j++) {
				prob[i][j] = model.SV[i][j].value;
			}
		}
		double w_list[][][] = new double[model.nr_class][model.nr_class - 1][model.SV[0].length];
		System.out.println(model.nr_class);

		for (int i = 0; i < model.SV[0].length; ++i) {
			for (int j = 0; j < model.nr_class - 1; ++j) {
				int index = 0;
				int end = 0;
				double acc;
				for (int k = 0; k < model.nr_class; ++k) {
					acc = 0.0;
					index += (k == 0) ? 0 : model.nSV[k - 1];
					end = index + model.nSV[k];
					for (int m = index; m < end; ++m) {
						acc += coef[j][m] * prob[m][i];
					}
					w_list[k][j][i] = acc;
				}
			}
		}

		double[] weights = new double[model.SV[0].length];
		for (int i = 0; i < model.nr_class - 1; ++i) {
			for (int j = i + 1, k = i; j < model.nr_class; ++j, ++k) {
				for (int m = 0; m < model.SV[0].length; ++m) {
					weights[m] = (w_list[i][k][m] + w_list[j][i][m]);

				}
			}
		}
		return weights;
	}

	@Override
	public void loadModel(String modelPath) throws IOException
	{
		BufferedReader rangeFile = new BufferedReader(new FileReader(modelPath + ".range"));
		Double feature_min, feature_max;
		Integer idx;
		if(rangeFile.read() == 'x') {
			rangeFile.readLine();		// pass the '\n' after 'x'
			StringTokenizer st = new StringTokenizer(rangeFile.readLine());
			Double scaledLowerBound = Double.parseDouble(st.nextToken());
			Double scaledUpperBound = Double.parseDouble(st.nextToken());
			if(scaledLowerBound != 0 || scaledUpperBound != 1) {
				throw new RuntimeException("Feature lower bound and upper bound must"
						+ "be set in range file to resepctively 0 and 1");
			}
			String restore_line = null;
			List<FeatureLimits> limits = new ArrayList<FeatureLimits>();
			while((restore_line = rangeFile.readLine())!=null)
			{
				StringTokenizer st2 = new StringTokenizer(restore_line);
				idx = Integer.parseInt(st2.nextToken());
				feature_min = Double.parseDouble(st2.nextToken());
				feature_max = Double.parseDouble(st2.nextToken());
				FeatureLimits newLimit = new FeatureLimits(feature_min, feature_max);
				limits.add(newLimit);
			}
			scaler = new FeatureVectorScaler(limits.size(), scaledLowerBound, scaledUpperBound);
		} else {
			throw new RuntimeException("y scaling not supported");
		}
		rangeFile.close();
		model = svm.svm_load_model(modelPath);
	}

	@Override
	public void saveModel(String modelPath) throws IOException
	{
		Formatter formatter = new Formatter(new StringBuilder());
		BufferedWriter fp_save;
		fp_save = new BufferedWriter(new FileWriter(modelPath + ".range"));
		
		Double lower = 0.0;
		Double upper = 1.0;

		formatter.format("x\n");
		formatter.format("%.16g %.16g\n", lower, upper);
		for(Integer i=0 ; i<featureVectorBuilder.size() ; ++i) {
			formatter.format("%d %.16g %.16g\n", i, scaler.getLimits()[i].getMin(), scaler.getLimits()[i].getMax());
		}
		
		fp_save.write(formatter.toString());
		fp_save.close();
		
		svm.svm_save_model(modelPath, model);
	}
	
	public void printWeigths(FeatureVectorBuilder<BxZone, BxPage> vectorBuilder)
	{
		Set<String> fnames = featureVectorBuilder.getFeatureNames();
		Iterator<String> namesIt = fnames.iterator();
		Iterator<Double> valueIt = (Iterator<Double>)new ArrayIterator(getWeights());

		assert fnames.size() == getWeights().length;
		
        while(namesIt.hasNext() && valueIt.hasNext()) {
        	String name = namesIt.next();
        	Double val = valueIt.next();
        	System.out.println(name + " " + val);
        }
	}

	public void setParameter(svm_parameter param) {
		this.param = param;
	}

}
