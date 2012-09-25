package pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features;

import pl.edu.icm.coansys.metaextr.classification.features.FeatureCalculator;
import pl.edu.icm.coansys.metaextr.textr.model.BxBounds;
import pl.edu.icm.coansys.metaextr.textr.model.BxPage;
import pl.edu.icm.coansys.metaextr.textr.model.BxZone;

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
