package pl.edu.icm.cermine.bibref.extraction.features;

import pl.edu.icm.cermine.bibref.extraction.model.BxDocumentBibReferences;
import pl.edu.icm.cermine.structure.model.BxLine;
import pl.edu.icm.cermine.tools.classification.features.FeatureCalculator;

/**
 *
 * @author Dominika Tkaczyk (dtkaczyk@icm.edu.pl)
 */
public class StartsWithRefFeature implements FeatureCalculator<BxLine, BxDocumentBibReferences> {

    private static String featureName = "StartsWithRef";

    @Override
    public String getFeatureName() {
        return featureName;
    }

    @Override
    public double calculateFeatureValue(BxLine refLine, BxDocumentBibReferences refs) {
        String[] keywords = {"refe", "bibl"};

        for (String keyword : keywords) {
            if (refLine.toText().startsWith(keyword)) {
                return 1;
            }
        }
        return 0;
    }
    
}
