package pl.edu.icm.coansys.metaextr.tools.classification.sampleselection;

import java.util.ArrayList;
import java.util.List;

import pl.edu.icm.coansys.metaextr.tools.classification.hmm.training.TrainingElement;
import pl.edu.icm.coansys.metaextr.structure.model.BxZoneLabel;
import pl.edu.icm.coansys.metaextr.structure.model.BxZoneLabelCategory;

public class SampleFilter implements SampleSelector<BxZoneLabel> {
	BxZoneLabelCategory category;

	public SampleFilter(BxZoneLabelCategory category) {
		this.category = category;
	}
	
	@Override
	public List<TrainingElement<BxZoneLabel>> pickElements(List<TrainingElement<BxZoneLabel>> inputElements) {
		List<TrainingElement<BxZoneLabel>> ret = new ArrayList<TrainingElement<BxZoneLabel>>();
		for(TrainingElement<BxZoneLabel> elem: inputElements) {
			if(elem.getLabel().getCategory() == category)
				ret.add(elem);
		}
		return ret;
	}

}
