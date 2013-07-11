package pl.edu.icm.cermine.evaluation.tools;

import java.util.*;

public class CumulativeFunc {
	public static Map<Double, Integer> calculate(List<Double> values) {
		Collections.sort(values);
		List<Double> uniqueValues = new ArrayList<Double>(new HashSet<Double>(values));
		Collections.sort(uniqueValues);
		Map<Double, Integer> quantities = new HashMap<Double, Integer>();
		for(Double value: values) {
			if(quantities.containsKey(value)) {
				quantities.put(value, quantities.get(value) + 1);
			} else {
				quantities.put(value, 1);
			}
		}
		Map<Double, Integer> cumulated = new HashMap<Double, Integer>();
		cumulated.put(values.get(0), quantities.get(values.get(0)));
		Integer prevQuantity = 0;
		if(!uniqueValues.contains(0.0)) {
			cumulated.put(0.0, 0);
		}
		for(Double value: uniqueValues) {
			Integer curQuantity = quantities.get(value) + prevQuantity;
			cumulated.put(value, curQuantity);
			prevQuantity = curQuantity;
		}
		System.out.println(cumulated);
		return cumulated;
	}
	
	public static void main(String[] args) {
		List<Double> list = new ArrayList<Double>();
		list.add(.1);
		list.add(.3);
		list.add(.4);
		list.add(.023);
		list.add(.4);
		list.add(.8);
		calculate(list);
	}
}
