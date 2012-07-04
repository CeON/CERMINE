package pl.edu.icm.yadda.analysis.classification.features;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Simple feature vector.
 *
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class SimpleFeatureVector implements FeatureVector {

    Map<String, Double> features = new HashMap<String, Double>();

    @Override
    public double getFeature(String name) {
        if (features.get(name) == null) {
            throw new IllegalArgumentException("Feature vector does not contain feature '" + name + "'.");
        }
        return features.get(name);
    }

    @Override
    public void addFeature(String name, double calculateFeatureValue) {
        features.put(name, calculateFeatureValue);
    }

    public Set<String> getFeatureNames() {
        return features.keySet();
    }
    
    public String dump() {
    	String ret = "";
    	Set<String> keysSet = features.keySet();
    	ArrayList<String> keys = new ArrayList<String>(keysSet);
    	Collections.sort(keys);
    	for(String name: keys) {
    		String shortName = (name.length() > 18 ? name.substring(0, 18) : name);
    		ret += String.format("%18s: %5.2f\n", shortName, features.get(name));
    	}
    	return ret;
    }

}
