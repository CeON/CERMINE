package pl.edu.icm.cermine.content.filtering.features;

import pl.edu.icm.cermine.structure.model.BxPage;
import pl.edu.icm.cermine.structure.model.BxZone;
import pl.edu.icm.cermine.tools.classification.features.FeatureCalculator;

/**
 *
 * @author Dominika Tkaczyk (dtkaczyk@icm.edu.pl)
 */
public class GreekLettersFeature extends FeatureCalculator<BxZone, BxPage> {

    @Override
    public double calculateFeatureValue(BxZone zone, BxPage page) {
        return (zone.toText().matches("^.*[\\u0391-\\u03A9].*$") ||
                zone.toText().matches("^.*[\\u03B1-\\u03C9].*$")) ? 1: 0;
    }
    
}
