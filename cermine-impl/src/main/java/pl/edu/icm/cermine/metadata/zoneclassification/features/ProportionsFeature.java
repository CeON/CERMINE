package pl.edu.icm.cermine.metadata.zoneclassification.features;

import pl.edu.icm.cermine.structure.model.BxBounds;
import pl.edu.icm.cermine.structure.model.BxPage;
import pl.edu.icm.cermine.structure.model.BxZone;
import pl.edu.icm.cermine.tools.classification.features.FeatureCalculator;

/**
 *
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class ProportionsFeature implements FeatureCalculator<BxZone, BxPage> {

    private static String featureName = "Proportions";

    @Override
    public String getFeatureName() {
        return featureName;
    }

    @Override
    public double calculateFeatureValue(BxZone zone, BxPage page) {
        BxBounds bounds = zone.getBounds();
        if(bounds.getWidth() < 0.00005) {
        	return 0.0;
        } else {
        	return bounds.getHeight() / bounds.getWidth();
        }
    }

}
