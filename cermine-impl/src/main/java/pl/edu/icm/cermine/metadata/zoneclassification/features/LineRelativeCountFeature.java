package pl.edu.icm.cermine.metadata.zoneclassification.features;

import pl.edu.icm.cermine.structure.model.BxPage;
import pl.edu.icm.cermine.structure.model.BxZone;
import pl.edu.icm.cermine.tools.classification.features.FeatureCalculator;

/**
 *
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class LineRelativeCountFeature implements FeatureCalculator<BxZone, BxPage> {

    private static String featureName = "LineRelativeCount";

    @Override
    public String getFeatureName() {
        return featureName;
    }

    @Override
    public double calculateFeatureValue(BxZone zone, BxPage page) {
        int allLines = 0;
        for (BxZone pZone : page.getZones()) {
            allLines += pZone.getLines().size();
        }
        
        return (double) zone.getLines().size() / (double) allLines;
    }

}
