package pl.edu.icm.cermine.metadata.zoneclassification.features;

import pl.edu.icm.cermine.structure.model.BxPage;
import pl.edu.icm.cermine.structure.model.BxZone;
import pl.edu.icm.cermine.tools.classification.features.FeatureCalculator;

public class IsGreatestFontOnPageFeature extends AbstractFeatureCalculator<BxZone, BxPage> {

    @Override
    public double calculateFeatureValue(BxZone object, BxPage context) {
        FeatureCalculator<BxZone, BxPage> fc = new FontHeightMeanFeature();
        for (BxZone otherZone : getOtherZones(object)) {
            if (fc.calculateFeatureValue(otherZone, context) > fc.calculateFeatureValue(object, context)) {
                return 0.0;
            }
        }
        return 1.0;
    }
}
