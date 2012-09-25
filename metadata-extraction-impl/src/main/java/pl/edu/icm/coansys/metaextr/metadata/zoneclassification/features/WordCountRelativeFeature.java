package pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features;

import pl.edu.icm.coansys.metaextr.classification.features.FeatureCalculator;
import pl.edu.icm.coansys.metaextr.textr.model.BxLine;
import pl.edu.icm.coansys.metaextr.textr.model.BxPage;
import pl.edu.icm.coansys.metaextr.textr.model.BxZone;

/**
 *
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class WordCountRelativeFeature implements FeatureCalculator<BxZone, BxPage> {

    private static String featureName = "WordCountRelative";

    @Override
    public String getFeatureName() {
        return featureName;
    }

    @Override
    public double calculateFeatureValue(BxZone zone, BxPage page) {
        int count = 0;
        for (BxLine line : zone.getLines()) {
            count += line.getWords().size();
        }

        int pCount = 0;
        for (BxZone pZone: page.getZones()) {
            for (BxLine line : pZone.getLines()) {
                pCount += line.getWords().size();
            }
        }

        return (double) count / (double) pCount;
    }

}
