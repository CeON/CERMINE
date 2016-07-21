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

package pl.edu.icm.cermine.tools.classification.sampleselection;

import java.util.Map.Entry;
import java.util.*;
import pl.edu.icm.cermine.tools.classification.general.TrainingSample;

/**
 * @author Pawel Szostek
 */
public class UndersamplingSelector<S> implements SampleSelector<S> {

    private final double inequalityFactor;

    public UndersamplingSelector(double inequalityFactor) {
        assert inequalityFactor > 1.0;
        this.inequalityFactor = inequalityFactor;
    }

    @Override
    public List<TrainingSample<S>> pickElements(List<TrainingSample<S>> inputElements) {
        Map<S, Integer> labelCount = new HashMap<S, Integer>();

        for (TrainingSample<S> elem : inputElements) {
            if (!labelCount.containsKey(elem.getLabel())) {
                labelCount.put(elem.getLabel(), 0);
            }
            labelCount.put(elem.getLabel(), labelCount.get(elem.getLabel()) + 1);
        }

        int smallestClassNumber = Integer.MAX_VALUE;
        for (Entry<S, Integer> entry : labelCount.entrySet()) {
            if (entry.getValue() < smallestClassNumber) {
                smallestClassNumber = entry.getValue();
            }
            System.out.println(entry.getKey() + " " + entry.getValue());
        }

        List<TrainingSample<S>> trainingSamples = new ArrayList<TrainingSample<S>>();

        for (S label : labelCount.keySet()) {
            List<TrainingSample<S>> thisLabelElements = new ArrayList<TrainingSample<S>>();
            for (TrainingSample<S> elem : inputElements) {
                if (elem.getLabel() == label) {
                    thisLabelElements.add(elem);
                }
            }
            if (thisLabelElements.size() < smallestClassNumber * inequalityFactor) {
                trainingSamples.addAll(thisLabelElements);
            } else {
                Random randomGenerator = new Random();
                List<TrainingSample<S>> chosenElements = new ArrayList<TrainingSample<S>>();
                while (chosenElements.size() < smallestClassNumber * inequalityFactor) {
                    int randInt = randomGenerator.nextInt(thisLabelElements.size());
                    TrainingSample<S> randElem = thisLabelElements.get(randInt);
                    if (!chosenElements.contains(randElem)) {
                        chosenElements.add(randElem);
                    }
                }
                trainingSamples.addAll(chosenElements);
            }
        }
        return trainingSamples;
    }
}
