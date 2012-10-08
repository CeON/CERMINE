package pl.edu.icm.cermine.metadata.zoneclassification.features;

import pl.edu.icm.cermine.structure.model.BxPage;
import pl.edu.icm.cermine.structure.model.BxZone;
import pl.edu.icm.cermine.tools.classification.features.FeatureCalculator;

/**
 *
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class BibinfoFeature extends FeatureCalculator<BxZone, BxPage> {

    @Override
    public double calculateFeatureValue(BxZone zone, BxPage page) {

        String[] keywords = {"cite", "pages", "article", "volume", "publishing", "journal", "doi", "cite this article",
                             "citation", "issue", "issn"};

        int count = 0;
        for (String keyword : keywords) {
            if (zone.toText().toLowerCase().contains(keyword)) {
                count++;
            }
        }

        return count;
    }

}
