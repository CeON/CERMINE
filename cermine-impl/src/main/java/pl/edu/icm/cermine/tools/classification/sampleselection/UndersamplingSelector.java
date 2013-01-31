package pl.edu.icm.cermine.tools.classification.sampleselection;

import java.util.Map.Entry;
import java.util.*;
import pl.edu.icm.cermine.tools.classification.general.TrainingSample;

public class UndersamplingSelector<S> implements SampleSelector<S> {

    private Double inequalityFactor;

    
    public UndersamplingSelector(Double inequalityFactor) {
    	if(inequalityFactor <= 1.0) {
    		throw new RuntimeException("Inequality factor must be greater than 1.");
    	}
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

        Integer smallestClassNumber = Integer.MAX_VALUE;
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
            if (thisLabelElements.size() < smallestClassNumber*inequalityFactor) {
                trainingSamples.addAll(thisLabelElements);
                System.out.println(label + " " + thisLabelElements.size());
            } else {
                Random randomGenerator = new Random();
                List<TrainingSample<S>> chosenElements = new ArrayList<TrainingSample<S>>();
                while (chosenElements.size() < smallestClassNumber*inequalityFactor) {
                    Integer randInt = randomGenerator.nextInt(thisLabelElements.size());
                    TrainingSample<S> randElem = thisLabelElements.get(randInt);
                    if (!chosenElements.contains(randElem)) {
                        chosenElements.add(randElem);
                    }
                }
                trainingSamples.addAll(chosenElements);
                System.out.println(label + " " + chosenElements.size());
            }
        }
        return trainingSamples;
    }
}
