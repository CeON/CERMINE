package pl.edu.icm.coansys.metaextr.metadata.sampleselection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import pl.edu.icm.coansys.metaextr.tools.classification.hmm.training.TrainingElement;
import pl.edu.icm.coansys.metaextr.structure.model.BxZoneLabel;

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
        for(S lab: labelCount.keySet()) {
        	if(labelCount.get(lab) < smallestClassNumber)
        		smallestClassNumber = labelCount.get(lab);
        	System.out.println(lab + " " + labelCount.get(lab));
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
