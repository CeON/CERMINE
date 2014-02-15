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

package pl.edu.icm.cermine.tools.classification.sampleselection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import pl.edu.icm.cermine.tools.classification.general.TrainingSample;

public class SillyUndersamplingSelector <S> implements SampleSelector<S> {
	private List<S> zoneLabels;
	private double inequalityFactor;

	public SillyUndersamplingSelector(List<S> zoneLabels, double inequalityFactor) {
		assert inequalityFactor > 1.0;
		this.inequalityFactor = inequalityFactor;
		this.zoneLabels = zoneLabels;
	}

	@Override
	public List<TrainingSample<S>> pickElements(List<TrainingSample<S>> inputElements) {
		Map<S, Integer> labelCount = new HashMap<S, Integer>();
        for(S label: zoneLabels) {
        	labelCount.put(label, 0);
        }

        for(TrainingSample<S> elem: inputElements) {
        	labelCount.put(elem.getLabel(), labelCount.get(elem.getLabel())+1);
        }

        Integer smallestClassNumber = Integer.MAX_VALUE;
        for(Entry<S, Integer> entry: labelCount.entrySet()) {
        	if(entry.getValue() < smallestClassNumber) {
        		smallestClassNumber = entry.getValue();
            }
        	System.out.println(entry.getKey() + " " + entry.getValue());
        }

        for(S label: zoneLabels) {
        	labelCount.put(label, 0);
        }

        List<TrainingSample<S>> trainingSamples = new ArrayList<TrainingSample<S>>();
        for(TrainingSample<S> elem: inputElements) {
        	if(labelCount.get(elem.getLabel()) < smallestClassNumber*inequalityFactor) {
        		trainingSamples.add(elem);
        		labelCount.put(elem.getLabel(), labelCount.get(elem.getLabel())+1);
        	}
        }   
        return trainingSamples;
	}
}
