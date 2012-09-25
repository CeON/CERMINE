package pl.edu.icm.coansys.metaextr.articlecontent.features.zone;

import pl.edu.icm.coansys.metaextr.classification.features.FeatureCalculator;
import pl.edu.icm.coansys.metaextr.textr.model.BxPage;
import pl.edu.icm.coansys.metaextr.textr.model.BxZone;

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
