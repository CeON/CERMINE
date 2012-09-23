package pl.edu.icm.yadda.analysis.metadata.sampleselection;

import java.util.ArrayList;
import java.util.List;

import pl.edu.icm.yadda.analysis.classification.hmm.training.TrainingElement;
import pl.edu.icm.yadda.analysis.textr.model.BxZoneLabel;
import pl.edu.icm.yadda.analysis.textr.model.BxZoneLabelCategory;

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
