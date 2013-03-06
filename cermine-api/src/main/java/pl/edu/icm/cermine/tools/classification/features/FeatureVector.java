package pl.edu.icm.cermine.tools.classification.features;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Simple feature vector.
 *
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class FeatureVector implements Cloneable {

    private List<String> names = new ArrayList<String>();
    private List<Double> values = new ArrayList<Double>();

    public double getFeatureValue(String name) {
        if (!names.contains(name)) {
            throw new IllegalArgumentException("Feature vector does not contain feature '" + name + "'.");
        }
        return values.get(names.indexOf(name));
    }

    public void addFeature(String name, double featureValue) {
    	names.add(name);
    	values.add(featureValue);
    }

	public List<String> getFeatureNames() {
		return names;
    }
    
    public String dump() {
    	StringBuilder ret = new StringBuilder();
    	for(Integer idx=0; idx<values.size(); ++idx) {
    		String name = names.get(idx);
    		String shortName = (name.length() > 18 ? name.substring(0, 18) : name);
    		ret.append(String.format("%18s: %5.2f%n", shortName, values.get(idx)));
    	}
    	return ret.toString();
    }

    public Integer size() {
    	return values.size();
    }

	public Double[] getFeatureValues() {
		return values.toArray(new Double[values.size()]);
	}

	public void setValues(Double[] values) {
		this.values = new ArrayList<Double>(Arrays.asList(values));
	}
	
	public void setNames(List<String> names) {
		this.names = names;
	}
	
	public void addValue(String name, Double value) {
		if(name.contains(name)) {
			throw new RuntimeException("Bad feature name: " + name);
        }
		names.add(name);
		values.add(value);
	}
	
	@Override
	public FeatureVector clone() throws CloneNotSupportedException {
        FeatureVector ret = (FeatureVector) super.clone();
        List<Double> copiedValues = new ArrayList<Double>();
        List<String> copiedNames = new ArrayList<String>();
        Collections.copy(names, copiedNames);
        Collections.copy(values, copiedValues);
        ret.names = copiedNames;
        ret.values = copiedValues;
        return ret;
	}
	
}
