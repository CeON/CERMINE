package pl.edu.icm.yadda.analysis.bibref.extraction.features;

import pl.edu.icm.yadda.analysis.bibref.extraction.model.BxDocumentBibReferences;
import pl.edu.icm.yadda.analysis.hmm.features.FeatureCalculator;
import pl.edu.icm.yadda.analysis.textr.model.BxLine;

/**
 *
 * @author Dominika Tkaczyk (dtkaczyk@icm.edu.pl)
 */
public class RelativeLengthFeature implements FeatureCalculator<BxLine, BxDocumentBibReferences> {

    private static String featureName = "RelativeLength";

    @Override
    public String getFeatureName() {
        return featureName;
    }

    @Override
    public double calculateFeatureValue(BxLine refLine, BxDocumentBibReferences refs) {
        return refLine.getBounds().getWidth() / refs.getZone(refLine).getBounds().getWidth();
    }
    
}
