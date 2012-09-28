package pl.edu.icm.coansys.metaextr.content.features.zone;

import pl.edu.icm.coansys.metaextr.tools.classification.features.FeatureCalculator;
import pl.edu.icm.coansys.metaextr.structure.model.BxPage;
import pl.edu.icm.coansys.metaextr.structure.model.BxZone;

/**
 *
 * @author Dominika Tkaczyk (dtkaczyk@icm.edu.pl)
 */
public class MathSymbolsFeature implements FeatureCalculator<BxZone, BxPage> {

    private static String featureName = "MathSymbols";

    @Override
    public String getFeatureName() {
        return featureName;
    }

    @Override
    public double calculateFeatureValue(BxZone zone, BxPage page) {
        return (zone.toText().matches("^.*[=\\u2200-\\u22FF].*$")) ? 1 : 0;
    }
    
}
