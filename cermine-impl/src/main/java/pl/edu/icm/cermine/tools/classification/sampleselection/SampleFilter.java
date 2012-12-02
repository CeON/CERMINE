package pl.edu.icm.cermine.tools.classification.sampleselection;

import java.util.ArrayList;
import java.util.List;
import pl.edu.icm.cermine.structure.model.BxZoneLabel;
import pl.edu.icm.cermine.structure.model.BxZoneLabelCategory;
import pl.edu.icm.cermine.tools.classification.general.TrainingSample;

public class SampleFilter implements SampleSelector<BxZoneLabel> {

    private BxZoneLabelCategory category;

    public SampleFilter(BxZoneLabelCategory category) {
        this.category = category;
    }

    @Override
    public List<TrainingSample<BxZoneLabel>> pickElements(List<TrainingSample<BxZoneLabel>> inputElements) {
        List<TrainingSample<BxZoneLabel>> ret = new ArrayList<TrainingSample<BxZoneLabel>>();
        for (TrainingSample<BxZoneLabel> elem : inputElements) {
            if (elem.getLabel().getCategory() == category) {
                ret.add(elem);
            }
        }
        return ret;
    }
}
