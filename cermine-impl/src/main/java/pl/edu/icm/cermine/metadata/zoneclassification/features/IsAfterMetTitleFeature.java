package pl.edu.icm.cermine.metadata.zoneclassification.features;

import pl.edu.icm.cermine.structure.model.BxPage;
import pl.edu.icm.cermine.structure.model.BxZone;
import pl.edu.icm.cermine.structure.model.BxZoneLabel;

public class IsAfterMetTitleFeature extends AbstractFeatureCalculator<BxZone, BxPage> {

    @Override
    public double calculateFeatureValue(BxZone object, BxPage context) {
        if (object.getPrev() == null || object.getPrev().getLabel() != BxZoneLabel.MET_TITLE) {
            return 0.0;
        }
        return 1.0;
    }
}
