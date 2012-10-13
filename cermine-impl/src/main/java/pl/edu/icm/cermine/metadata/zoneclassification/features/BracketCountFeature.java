package pl.edu.icm.cermine.metadata.zoneclassification.features;

import pl.edu.icm.cermine.structure.model.BxPage;
import pl.edu.icm.cermine.structure.model.BxZone;
import pl.edu.icm.cermine.tools.classification.features.FeatureCalculator;

public class BracketCountFeature extends FeatureCalculator<BxZone, BxPage> {

    @Override
    public double calculateFeatureValue(BxZone zone, BxPage page) {
        int bracketCount = 0;
        for (char c : zone.toText().toCharArray()) {
            if (c == '[' || c == ']') {
                ++bracketCount;
            }
        }
        return bracketCount;
    }
}
