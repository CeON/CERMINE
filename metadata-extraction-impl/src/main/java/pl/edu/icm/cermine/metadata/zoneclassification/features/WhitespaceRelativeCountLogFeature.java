package pl.edu.icm.cermine.metadata.zoneclassification.features;

import pl.edu.icm.cermine.structure.model.BxPage;
import pl.edu.icm.cermine.structure.model.BxZone;
import pl.edu.icm.cermine.tools.classification.features.FeatureCalculator;

public class WhitespaceRelativeCountLogFeature implements FeatureCalculator<BxZone, BxPage> {
    private static String featureName = "WhitespaceRelativeCountLog";

    @Override
    public String getFeatureName() {
        return featureName;
    }

    @Override
    public double calculateFeatureValue(BxZone zone, BxPage page) {
    	double spaceCount = new WhitespaceCountFeature().calculateFeatureValue(zone, page);
        return -Math.log(spaceCount / (zone.toText().length()) + Double.MIN_VALUE);
    }
}
