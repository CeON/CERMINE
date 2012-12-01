package pl.edu.icm.cermine.content.filtering.features;

import pl.edu.icm.cermine.structure.model.BxPage;
import pl.edu.icm.cermine.structure.model.BxZone;
import pl.edu.icm.cermine.tools.classification.features.FeatureCalculator;

/**
 *
 * @author Dominika Tkaczyk (dtkaczyk@icm.edu.pl)
 */
public class MathSymbolsFeature extends FeatureCalculator<BxZone, BxPage> {

    @Override
    public double calculateFeatureValue(BxZone zone, BxPage page) {
        return (zone.toText().matches("^.*[=\\u2200-\\u22FF].*$")) ? 1 : 0;
    }
    
}
