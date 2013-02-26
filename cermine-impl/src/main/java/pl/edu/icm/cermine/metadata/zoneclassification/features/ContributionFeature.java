package pl.edu.icm.cermine.metadata.zoneclassification.features;

import pl.edu.icm.cermine.structure.model.BxPage;
import pl.edu.icm.cermine.structure.model.BxZone;

public class ContributionFeature extends AbstractFeatureCalculator<BxZone, BxPage> {

    @Override
    public double calculateFeatureValue(BxZone zone, BxPage page) {
        String[] keywords = {"contribution",
                             };

        for (String keyword : keywords) {
            if (zone.toText().toLowerCase().contains(keyword)) {
                return 1;
            }
        }
        return 0;
    }
}
