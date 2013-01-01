package pl.edu.icm.cermine.tools.classification.general;

import java.util.ArrayList;
import java.util.List;
import pl.edu.icm.cermine.structure.model.BxZoneLabel;
import pl.edu.icm.cermine.structure.model.BxZoneLabelCategory;

public class ClassificationUtils {

    public static List<TrainingSample<BxZoneLabel>> filterElements(List<TrainingSample<BxZoneLabel>> elements, BxZoneLabelCategory category) {
        if (category == BxZoneLabelCategory.CAT_ALL) {
            List<TrainingSample<BxZoneLabel>> ret = new ArrayList<TrainingSample<BxZoneLabel>>();
            ret.addAll(elements);
            return ret;
        }

        List<TrainingSample<BxZoneLabel>> ret = new ArrayList<TrainingSample<BxZoneLabel>>();

        for (TrainingSample<BxZoneLabel> elem : elements) {
        //	System.out.println(elem);
        //	System.out.println(elem.getLabel());
        //	System.out.println(elem.getLabel().getCategory());
            if (elem.getLabel().getCategory() == category) {
                ret.add(elem);
            }
        }
        return ret;
    }

    public static List<TrainingSample<BxZoneLabel>> filterElements(List<TrainingSample<BxZoneLabel>> elements, List<BxZoneLabel> labels) {

        List<TrainingSample<BxZoneLabel>> ret = new ArrayList<TrainingSample<BxZoneLabel>>();

        for (TrainingSample<BxZoneLabel> elem : elements) {
            if (labels.contains(elem.getLabel())) {
                ret.add(elem);
            }
        }
        return ret;
    }
}
