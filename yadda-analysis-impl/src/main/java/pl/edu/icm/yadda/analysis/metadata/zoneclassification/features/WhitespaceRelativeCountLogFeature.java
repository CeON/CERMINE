package pl.edu.icm.yadda.analysis.metadata.zoneclassification.features;

import pl.edu.icm.yadda.analysis.classification.features.FeatureCalculator;
import pl.edu.icm.yadda.analysis.textr.model.BxChunk;
import pl.edu.icm.yadda.analysis.textr.model.BxLine;
import pl.edu.icm.yadda.analysis.textr.model.BxPage;
import pl.edu.icm.yadda.analysis.textr.model.BxWord;
import pl.edu.icm.yadda.analysis.textr.model.BxZone;

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
