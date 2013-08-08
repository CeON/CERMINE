/**
 * This file is part of CERMINE project.
 * Copyright (c) 2011-2013 ICM-UW
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
