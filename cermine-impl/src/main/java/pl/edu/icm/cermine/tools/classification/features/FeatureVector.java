package pl.edu.icm.cermine.tools.classification.features;

import java.util.ArrayList;
import java.util.List;

/**
 * Simple feature vector.
 *
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 * @author Pawel Szostek (p.szostek@icm.edu.pl)
 */
public class FeatureVector {

    private List<String> names = new ArrayList<String>();
    private List<Double> values = new ArrayList<Double>();

    
    public int size() {
    	return values.size();
    }
    
    public List<String> getFeatureNames() {
		return names;
    }
    
    public double getValue(String name) {
        if (!names.contains(name)) {
            throw new IllegalArgumentException("Feature vector does not contain feature '" + name + "'!");
        }
        return values.get(names.indexOf(name));
    }
    
    public double getValue(int index) {
        if (index < 0 || index >= values.size()) {
            throw new IllegalArgumentException("Feature vector contains only " + size()+ " features!");
        }
        return values.get(index);
    }
    
    public double[] getValues() {
        double[] ret = new double[size()];
        for (int i = 0; i < size(); i++) {
            ret[i] = values.get(i);
        }
		return ret;
	}
    

    public void addFeature(String name, double value) {
    	names.add(name);
    	values.add(value);
    }
    
    public void setValue(String name, double value) {
        if (!names.contains(name)) {
            throw new IllegalArgumentException("Feature vector does not contain feature '" + name + "'!");
        }
        values.add(names.indexOf(name), value);
        values.remove(names.indexOf(name) + 1);
	}
    
    public void setValue(int index, double value) {
        if (index < 0 || index >= values.size()) {
            throw new IllegalArgumentException("Feature vector contains only " + size()+ " features!");
        }
        values.add(index, value);
        values.remove(index + 1);
	}
    
    public void setValues(double[] values) {
        if (names.size() != values.length) {
            throw new IllegalArgumentException("This feature vector has " + names.size() + " features!");
        }
        this.values.clear();
        for (double value: values) {
            this.values.add(value);
        }
	}
    
    public String dump() {
    	StringBuilder ret = new StringBuilder();
    	for(Integer idx=0; idx<size(); ++idx) {
    		String name = names.get(idx);
    		String shortName = (name.length() > 18 ? name.substring(0, 18) : name);
    		ret.append(String.format("%18s: %5.2f%n", shortName, values.get(idx)));
    	}
    	return ret.toString();
    }

    public FeatureVector copy() {
        FeatureVector ret = new FeatureVector();
        ret.names = new ArrayList<String>(names);
        ret.values = new ArrayList<Double>(values);
        return ret;
	}
	
}
