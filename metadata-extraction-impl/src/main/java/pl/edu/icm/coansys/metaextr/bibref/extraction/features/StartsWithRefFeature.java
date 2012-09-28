package pl.edu.icm.coansys.metaextr.bibref.extraction.features;

import pl.edu.icm.coansys.metaextr.bibref.extraction.model.BxDocumentBibReferences;
import pl.edu.icm.coansys.metaextr.tools.classification.features.FeatureCalculator;
import pl.edu.icm.coansys.metaextr.structure.model.BxLine;

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
