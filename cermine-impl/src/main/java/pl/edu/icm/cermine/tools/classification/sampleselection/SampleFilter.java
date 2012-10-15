package pl.edu.icm.cermine.tools.classification.sampleselection;

import java.util.ArrayList;
import java.util.List;
import pl.edu.icm.cermine.structure.model.BxZoneLabel;
import pl.edu.icm.cermine.structure.model.BxZoneLabelCategory;
import pl.edu.icm.cermine.tools.classification.hmm.training.TrainingElement;

public class SampleFilter implements SampleSelector<BxZoneLabel> {

    BxZoneLabelCategory category;

    public SampleFilter(BxZoneLabelCategory category) {
        this.category = category;
    }

    @Override
    public List<TrainingElement<BxZoneLabel>> pickElements(List<TrainingElement<BxZoneLabel>> inputElements) {
        List<TrainingElement<BxZoneLabel>> ret = new ArrayList<TrainingElement<BxZoneLabel>>();
        for (TrainingElement<BxZoneLabel> elem : inputElements) {
            if (elem.getLabel().getCategory() == category) {
                ret.add(elem);
            }
        }
        return ret;
    }
}
