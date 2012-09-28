package pl.edu.icm.coansys.metaextr.content.features.line;

import pl.edu.icm.coansys.metaextr.tools.classification.features.FeatureCalculator;
import pl.edu.icm.coansys.metaextr.structure.model.BxLine;
import pl.edu.icm.coansys.metaextr.structure.model.BxPage;

/**
 *
 * @author Dominika Tkaczyk (dtkaczyk@icm.edu.pl)
 */
public class PrevEndsWithDotFeature implements FeatureCalculator<BxLine, BxPage> {

    private static String featureName = "PrevEndsWithDotFeature";

    @Override
    public String getFeatureName() {
        return featureName;
    }

    @Override
    public double calculateFeatureValue(BxLine line, BxPage page) {
        if (!line.hasPrev()) {
            return 0;
        }
        return (line.getPrev().toText().endsWith(".")) ? 1 : 0;
    }
    
}
