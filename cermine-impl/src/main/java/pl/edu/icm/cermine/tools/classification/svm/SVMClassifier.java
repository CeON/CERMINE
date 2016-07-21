/**
 * This file is part of CERMINE project.
 * Copyright (c) 2011-2016 ICM-UW
 *
 * CERMINE is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * CERMINE is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with CERMINE. If not, see <http://www.gnu.org/licenses/>.
 */
package pl.edu.icm.cermine.tools.classification.svm;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import libsvm.*;
import org.apache.commons.collections.iterators.ArrayIterator;
import pl.edu.icm.cermine.tools.classification.general.*;
import pl.edu.icm.cermine.tools.timeout.TimeoutRegister;

/**
 * @author Pawel Szostek
 *
 * @param <S> classified object's class
 * @param <T> context class
 * @param <E> target enumeration for labels
 */
public abstract class SVMClassifier<S, T, E extends Enum<E>> {

    protected static final svm_parameter DEFAULT_PARAMETER = new svm_parameter();

    static {
        // default values
        DEFAULT_PARAMETER.svm_type = svm_parameter.C_SVC;
        DEFAULT_PARAMETER.C = 8;
        DEFAULT_PARAMETER.kernel_type = svm_parameter.POLY;
        DEFAULT_PARAMETER.degree = 3;
        DEFAULT_PARAMETER.gamma = 1.0 / 8.0; // 1/k
        DEFAULT_PARAMETER.coef0 = 0.5;
        DEFAULT_PARAMETER.nu = 0.5;
        DEFAULT_PARAMETER.cache_size = 100;
        DEFAULT_PARAMETER.eps = 1e-3;
        DEFAULT_PARAMETER.p = 0.1;
        DEFAULT_PARAMETER.shrinking = 1;
        DEFAULT_PARAMETER.probability = 0;
        DEFAULT_PARAMETER.nr_weight = 0;
        DEFAULT_PARAMETER.weight_label = new int[0];
        DEFAULT_PARAMETER.weight = new double[0];
    }

    protected FeatureVectorBuilder<S, T> featureVectorBuilder;
    protected FeatureVectorScaler scaler;
    protected String[] featuresNames;

    protected svm_parameter param;
    protected svm_problem problem;
    protected svm_model model;

    protected Class<E> enumClassObj;

    public SVMClassifier(FeatureVectorBuilder<S, T> featureVectorBuilder, Class<E> enumClassObj) {
        this.featureVectorBuilder = featureVectorBuilder;
        this.enumClassObj = enumClassObj;
        int dimensions = featureVectorBuilder.size();

        double scaledLowerBound = 0.0;
        double scaledUpperBound = 1.0;
        FeatureVectorScalerImpl lScaler = new FeatureVectorScalerImpl(dimensions, scaledLowerBound, scaledUpperBound);
        lScaler.setStrategy(new LinearScaling());
	this.scaler = lScaler;
		
        featuresNames = (String[])featureVectorBuilder.getFeatureNames().toArray(new String[0]);
		
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
            return clone(DEFAULT_PARAMETER);
	}
	
	public void buildClassifier(List<TrainingSample<E>> trainingElements) {
		assert trainingElements.size() > 0;
		scaler.calculateFeatureLimits(trainingElements);
		problem = buildDatasetForTraining(trainingElements);
		model = libsvm.svm.svm_train(problem, param);
	}
	
	public E predictLabel(S object, T context) {
		svm_node[] instance = buildDatasetForClassification(object, context);
		TimeoutRegister.get().check(); //12s-70s
		int predictedVal = (int)svm.svm_predict(model, instance);
		TimeoutRegister.get().check();
		return enumClassObj.getEnumConstants()[predictedVal];
	}
	
	public E predictLabel(TrainingSample<E> sample) {
		svm_node[] instance = buildDatasetForClassification(sample.getFeatureVector());
        TimeoutRegister.get().check();
		int predictedVal = (int)svm.svm_predict(model, instance);
		TimeoutRegister.get().check();
		return enumClassObj.getEnumConstants()[predictedVal];
	}

    public Map<E, Double> predictProbabilities(S object, T context) {
        svm_node[] instance = buildDatasetForClassification(object, context);
        double[] probEstimates = new double[enumClassObj.getEnumConstants().length];
        svm.svm_predict_probability(model, instance, probEstimates);

        Map<E, Double> result = new HashMap<E, Double>();
        for (int i = 0; i < probEstimates.length; ++i) {
            result.put(enumClassObj.getEnumConstants()[model.label[i]], probEstimates[i]);
        }
        return result;
    }

    protected svm_problem buildDatasetForTraining(List<TrainingSample<E>> trainingElements) {
        svm_problem svmProblem = new svm_problem();
        svmProblem.l = trainingElements.size();
        svmProblem.x = new svm_node[svmProblem.l][trainingElements.get(0).getFeatureVector().size()];
        svmProblem.y = new double[svmProblem.l];

        int elemIdx = 0;
        for (TrainingSample<E> trainingElem : trainingElements) {
            FeatureVector scaledFV = scaler.scaleFeatureVector(trainingElem.getFeatureVector());
            int featureIdx = 0;
            for (double val : scaledFV.getValues()) {
                svm_node cur = new svm_node();
                cur.index = featureIdx;
                cur.value = val;
                svmProblem.x[elemIdx][featureIdx] = cur;
                ++featureIdx;
            }
            svmProblem.y[elemIdx] = trainingElem.getLabel().ordinal();
            ++elemIdx;
        }
        return svmProblem;
    }

    protected svm_node[] buildDatasetForClassification(FeatureVector fv) {
        FeatureVector scaled = scaler.scaleFeatureVector(fv);
        svm_node[] ret = new svm_node[featureVectorBuilder.size()];
        int featureIdx = 0;
        for (double val : scaled.getValues()) {
            svm_node cur = new svm_node();
            cur.index = featureIdx;
            cur.value = val;
            ret[featureIdx] = cur;
            ++featureIdx;
        }
        return ret;
    }

    protected svm_node[] buildDatasetForClassification(S object, T context) {
        return buildDatasetForClassification(featureVectorBuilder.getFeatureVector(object, context));
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

        for (int i = 0; i < model.SV[0].length; ++i) {
            for (int j = 0; j < model.nr_class - 1; ++j) {
                int index = 0;
                int end;
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

    public void loadModelFromResources(String modelFilePath, String rangeFilePath) throws IOException {
        InputStreamReader modelISR = new InputStreamReader(this.getClass().getResourceAsStream(modelFilePath));
        BufferedReader modelFile = new BufferedReader(modelISR);

        InputStreamReader rangeISR = new InputStreamReader(this.getClass().getResourceAsStream(rangeFilePath));
        BufferedReader rangeFile = new BufferedReader(rangeISR);
        loadModelFromFile(modelFile, rangeFile);
    }

    public void loadModelFromFile(String modelFilePath, String rangeFilePath) throws IOException {
        BufferedReader modelFile = new BufferedReader(new InputStreamReader(new FileInputStream(modelFilePath)));
        BufferedReader rangeFile = null;
        if (rangeFilePath != null) {
            rangeFile = new BufferedReader(new InputStreamReader(new FileInputStream(rangeFilePath)));
        }
        loadModelFromFile(modelFile, rangeFile);
    }

    public void loadModelFromFile(BufferedReader modelFile, BufferedReader rangeFile) throws IOException {
        if (rangeFile == null) {
            this.scaler = new FeatureVectorScalerNoOp();
        } else {
            FeatureVectorScalerImpl lScaler
                    = FeatureVectorScalerImpl.fromRangeReader(rangeFile);

            if (lScaler.getLimits().length != featureVectorBuilder.size()) {
                throw new IllegalArgumentException("Supplied .range file has "
                        + "wrong number of features (got " + lScaler.getLimits().length
                        + ", expected " + featureVectorBuilder.size() + " )");
            }

            this.scaler = lScaler;
        }

        this.model = svm.svm_load_model(modelFile);
    }

    public void saveModel(String modelPath) throws IOException {
        scaler.saveRangeFile(modelPath + ".range");
        svm.svm_save_model(modelPath, model);
    }

    public void printWeigths(FeatureVectorBuilder<S, T> vectorBuilder) {
        List<String> fnames = featureVectorBuilder.getFeatureNames();
        Iterator<String> namesIt = fnames.iterator();
        Iterator<Double> valueIt = (Iterator<Double>) new ArrayIterator(getWeights());

        assert fnames.size() == getWeights().length;

        while (namesIt.hasNext() && valueIt.hasNext()) {
            String name = namesIt.next();
            Double val = valueIt.next();
            System.out.println(name + " " + val);
        }
    }

    public void setParameter(svm_parameter param) {
        this.param = param;
    }

}
