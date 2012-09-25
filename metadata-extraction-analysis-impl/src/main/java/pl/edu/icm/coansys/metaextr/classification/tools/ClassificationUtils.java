package pl.edu.icm.coansys.metaextr.classification.tools;

import java.util.ArrayList;
import java.util.List;

import pl.edu.icm.coansys.metaextr.classification.hmm.training.TrainingElement;
import pl.edu.icm.coansys.metaextr.textr.model.BxZoneLabel;
import pl.edu.icm.coansys.metaextr.textr.model.BxZoneLabelCategory;

public class ClassificationUtils {

	public static List<TrainingElement<BxZoneLabel>> filterElements(List<TrainingElement<BxZoneLabel>> elements, BxZoneLabelCategory category)
	{
		if(category == BxZoneLabelCategory.CAT_ALL) {
			List<TrainingElement<BxZoneLabel>> ret = new ArrayList<TrainingElement<BxZoneLabel>>();
			ret.addAll(elements);
			return ret;
		}
	
		List<TrainingElement<BxZoneLabel>> ret = new ArrayList<TrainingElement<BxZoneLabel>>(); 
		
		for(TrainingElement<BxZoneLabel> elem: elements)
			if(elem.getLabel().getCategory() == category)
				ret.add(elem);
		return ret;
	}

	public static List<TrainingElement<BxZoneLabel>> filterElements(List<TrainingElement<BxZoneLabel>> elements, List<BxZoneLabel> labels)
	{

		List<TrainingElement<BxZoneLabel>> ret = new ArrayList<TrainingElement<BxZoneLabel>>(); 
		
		for(TrainingElement<BxZoneLabel> elem: elements)
			if(labels.contains(elem.getLabel().getCategory()))
				ret.add(elem);
		return ret;
	}
}
