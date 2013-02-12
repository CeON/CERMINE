package pl.edu.icm.cermine.tools.classification.general;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import org.apache.commons.io.IOUtils;
import pl.edu.icm.cermine.tools.classification.features.FeatureVector;

public class FeatureVectorScalerImpl implements FeatureVectorScaler {
	protected FeatureLimits[] limits;
	protected Double scaledLowerBound;
	protected Double scaledUpperBound;
	protected ScalingStrategy strategy ;
	
	public FeatureVectorScalerImpl(Integer size, Double lowerBound, Double upperBound) {
		this.scaledLowerBound = lowerBound;
		this.scaledUpperBound = upperBound;
		limits = new FeatureLimits[size];
		//set default limits to: max = -inf, min = +inf
		for(int idx=0; idx<size; ++idx) {
			limits[idx] = new FeatureLimits(Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY);
		}
		strategy = new LinearScaling();
	}
	
	public void setStrategy(ScalingStrategy strategy) {
		this.strategy = strategy;
	}
	
	public FeatureVector scaleFeatureVector(FeatureVector fv) {
		for(FeatureLimits l: limits) {
			assert l.getMin() != Double.POSITIVE_INFINITY && l.getMax() != Double.NEGATIVE_INFINITY;
		}
		return strategy.scaleFeatureVector(scaledLowerBound, scaledUpperBound, limits, fv);
	}
	
	public void setFeatureLimits(List<FeatureLimits> featureLimits) {
		this.limits = featureLimits.toArray(new FeatureLimits[featureLimits.size()]);
	}
	
	public <A extends Enum<A>> void calculateFeatureLimits(List<TrainingSample<A>> trainingElements) {
		for(TrainingSample<A> trainingElem: trainingElements) {
			FeatureVector fv = trainingElem.getFeatures();
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
			if(Double.isInfinite(limit.getMin()) || Double.isInfinite(limit.getMax())) {
				throw new RuntimeException("Feature limit is not calculated properly!");
			}
		}
	}
	
	public FeatureLimits[] getLimits() {
		return limits;
	}

    public void saveRangeFile(String path) throws IOException {
        BufferedWriter fp_save = null;
        try {
            Formatter formatter = new Formatter(new StringBuilder());
            fp_save = new BufferedWriter(new FileWriter(path));

            Double lower = 0.0;
            Double upper = 1.0;

            formatter.format("x\n");
            formatter.format("%.16g %.16g\n", lower, upper);
            for(Integer i = 0; i < limits.length; ++i) {
                formatter.format("%d %.16g %.16g\n", i, limits[i].getMin(), limits[i].getMax());
            }

            fp_save.write(formatter.toString());
        } finally {
            if(fp_save != null) {
                fp_save.close();
            }
        }
    }

    public static FeatureVectorScalerImpl fromRangeReader(BufferedReader rangeFile) throws IOException {
        try {
            Double feature_min, feature_max;
            if(rangeFile.read() == 'x') {
                rangeFile.readLine();		// pass the '\n' after 'x'
                String line = rangeFile.readLine();
                if (line == null) {
                    line = "";
                }
                StringTokenizer st = new StringTokenizer(line);
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
                    st2.nextToken(); //discard feature index
                    feature_min = Double.parseDouble(st2.nextToken());
                    feature_max = Double.parseDouble(st2.nextToken());
                    FeatureLimits newLimit = new FeatureLimits(feature_min, feature_max);
                    limits.add(newLimit);
                }

                FeatureVectorScalerImpl scaler = new FeatureVectorScalerImpl(limits.size(), scaledLowerBound, scaledUpperBound);
                scaler.setStrategy(new LinearScaling());
                scaler.setFeatureLimits(limits);

                return scaler;
            } else {
                throw new RuntimeException("y scaling not supported");
            }
        } finally {
            IOUtils.closeQuietly(rangeFile);
        }
    }
}
