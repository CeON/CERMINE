package pl.edu.icm.cermine.tools.classification.sampleselection;

import java.util.Map.Entry;
import java.util.*;
import pl.edu.icm.cermine.tools.classification.hmm.training.TrainingElement;

public class UndersamplingSelector<S> implements SampleSelector<S> {
	private Double inequalityFactor;
	
	public UndersamplingSelector(Double inequalityFactor) {
		assert inequalityFactor > 1.0;
		this.inequalityFactor = inequalityFactor;
	}
	
	@Override
	public List<TrainingElement<S>> pickElements(List<TrainingElement<S>> inputElements) {
        Map<S, Integer> labelCount = new HashMap<S, Integer>();

        for(TrainingElement<S> elem: inputElements) {
        	if(!labelCount.containsKey(elem.getLabel()))
        		labelCount.put(elem.getLabel(), 0);
        	labelCount.put(elem.getLabel(), labelCount.get(elem.getLabel())+1);
        }

        Integer smallestClassNumber = Integer.MAX_VALUE;
        for(Entry<S, Integer> entry: labelCount.entrySet()) {
        	if(entry.getValue() < smallestClassNumber) {
        		smallestClassNumber = entry.getValue();
            }
        	System.out.println(entry.getKey() + " " + entry.getValue());
        }
        
        List<TrainingElement<S>> trainingElements = new ArrayList<TrainingElement<S>>();
        
        for(S label: labelCount.keySet()) {
        	List<TrainingElement<S>> thisLabelElements = new ArrayList<TrainingElement<S>>();
        	for(TrainingElement<S> elem: inputElements) {
        		if(elem.getLabel() == label) {
        			thisLabelElements.add(elem);
        		}
        	}
        	if(thisLabelElements.size() < smallestClassNumber*inequalityFactor) {
        		trainingElements.addAll(thisLabelElements);
	        } else {
	    		Random randomGenerator = new Random();
	    		List<TrainingElement<S>> chosenElements = new ArrayList<TrainingElement<S>>();
	    		while(chosenElements.size() < smallestClassNumber*inequalityFactor) {
	    			Integer randInt = randomGenerator.nextInt(thisLabelElements.size());
	    			TrainingElement<S> randElem = thisLabelElements.get(randInt);
	    			if(!chosenElements.contains(randElem)) {
	    				chosenElements.add(randElem);
	    			}
	    		}
	    		trainingElements.addAll(chosenElements);
	        }
        }
        return trainingElements;
	}

}
