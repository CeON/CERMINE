package pl.edu.icm.cermine.tools.classification.sampleselection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import pl.edu.icm.cermine.tools.classification.hmm.training.TrainingElement;

public class SillyUndersamplingSelector <S> implements SampleSelector<S> {
	private List<S> zoneLabels;
	private Double inequalityFactor;

	public SillyUndersamplingSelector(List<S> zoneLabels, Double inequalityFactor) {
		assert inequalityFactor > 1.0;
		this.inequalityFactor = inequalityFactor;
		this.zoneLabels = zoneLabels;
	}

	@Override
	public List<TrainingElement<S>> pickElements(List<TrainingElement<S>> inputElements) {
		Map<S, Integer> labelCount = new HashMap<S, Integer>();
        for(S label: zoneLabels) {
        	labelCount.put(label, 0);
        }

        for(TrainingElement<S> elem: inputElements) {
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

        List<TrainingElement<S>> trainingElements = new ArrayList<TrainingElement<S>>();
        for(TrainingElement<S> elem: inputElements) {
        	if(labelCount.get(elem.getLabel()) < smallestClassNumber*inequalityFactor) {
        		trainingElements.add(elem);
        		labelCount.put(elem.getLabel(), labelCount.get(elem.getLabel())+1);
        	}
        }   
        return trainingElements;
	}
}
