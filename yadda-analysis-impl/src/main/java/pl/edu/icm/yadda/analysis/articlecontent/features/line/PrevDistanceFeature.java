package pl.edu.icm.yadda.analysis.articlecontent.features.line;

import pl.edu.icm.yadda.analysis.classification.features.FeatureCalculator;
import pl.edu.icm.yadda.analysis.textr.model.BxLine;
import pl.edu.icm.yadda.analysis.textr.model.BxPage;

/**
 *
 * @author Dominika Tkaczyk (dtkaczyk@icm.edu.pl)
 */
public class PrevDistanceFeature implements FeatureCalculator<BxLine, BxPage> {

    private static String featureName = "PrevDistance";

    @Override
    public String getFeatureName() {
        return featureName;
    }

    @Override
    public double calculateFeatureValue(BxLine line, BxPage page) {
        return line.getPrev().getBounds().getY() - line.getBounds().getY();
    }
    
}
