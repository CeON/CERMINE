package pl.edu.icm.coansys.metaextr.content.features.zone;

import pl.edu.icm.coansys.metaextr.tools.classification.features.FeatureCalculator;
import pl.edu.icm.coansys.metaextr.structure.model.BxPage;
import pl.edu.icm.coansys.metaextr.structure.model.BxZone;

/**
 *
 * @author Dominika Tkaczyk (dtkaczyk@icm.edu.pl)
 */
public class GreekLettersFeature implements FeatureCalculator<BxZone, BxPage> {

    private static String featureName = "GreekLetters";

    @Override
    public String getFeatureName() {
        return featureName;
    }

    @Override
    public double calculateFeatureValue(BxZone zone, BxPage page) {
        return (zone.toText().matches("^.*[\\u0391-\\u03A9].*$") ||
                zone.toText().matches("^.*[\\u03B1-\\u03C9].*$")) ? 1: 0;
    }
    
}
