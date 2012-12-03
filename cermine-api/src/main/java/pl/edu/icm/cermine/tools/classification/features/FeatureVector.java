package pl.edu.icm.cermine.tools.classification.features;

import java.util.*;

/**
 * Simple feature vector.
 *
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class FeatureVector implements Cloneable {

    private Map<String, Double> features = new HashMap<String, Double>();

    public double getFeature(String name) {
        if (features.get(name) == null) {
            throw new IllegalArgumentException("Feature vector does not contain feature '" + name + "'.");
        }
        return features.get(name);
    }

    public void addFeature(String name, double calculateFeatureValue) {
        features.put(name, calculateFeatureValue);
    }

    public Set<String> getFeatureNames() {
        return features.keySet();
    }
    
    public String dump() {
    	StringBuilder ret = new StringBuilder();
    	Set<String> keysSet = features.keySet();
    	ArrayList<String> keys = new ArrayList<String>(keysSet);
    	Collections.sort(keys);
    	for(String name: keys) {
    		String shortName = (name.length() > 18 ? name.substring(0, 18) : name);
    		ret.append(String.format("%18s: %5.2f%n", shortName, features.get(name)));
    	}
    	return ret.toString();
    }

    public Integer size() {
    	return features.size();
    }

	public Double[] getFeatures() {
		Double[] ret = new Double[features.size()];
		return features.values().toArray(ret);
	}

	public void setFeature(String name, Double value) {
		if(!features.containsKey(name)) {
			throw new RuntimeException("Bad feature name: " + name);
        }
		features.put(name, value);
	}
	
	@Override
	public FeatureVector clone() throws CloneNotSupportedException {
        FeatureVector ret = (FeatureVector) super.clone();
        for (String feature: features.keySet()) {
            ret.features.put(feature, new Double(features.get(feature)));
        }
        return ret;
	}
	
}
