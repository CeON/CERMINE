package pl.edu.icm.yadda.analysis.bibref.extraction.features;

import pl.edu.icm.yadda.analysis.bibref.extraction.model.BxDocumentBibReferences;
import pl.edu.icm.yadda.analysis.classification.features.FeatureCalculator;
import pl.edu.icm.yadda.analysis.textr.model.BxLine;

/**
 *
 * @author Dominika Tkaczyk (dtkaczyk@icm.edu.pl)
 */
public class RelativeStartTresholdFeature implements FeatureCalculator<BxLine, BxDocumentBibReferences> {

    private static String featureName = "RelativeStartTreshold";

    @Override
    public String getFeatureName() {
        return featureName;
    }

    @Override
    public double calculateFeatureValue(BxLine refLine, BxDocumentBibReferences refs) {
        return (refLine.getBounds().getX() - refs.getZone(refLine).getBounds().getX() > 10) ? 1 : 0;
    }
    
}
