package pl.edu.icm.yadda.analysis.articlecontent.features.zone;

import pl.edu.icm.yadda.analysis.classification.features.FeatureCalculator;
import pl.edu.icm.yadda.analysis.textr.model.BxLine;
import pl.edu.icm.yadda.analysis.textr.model.BxPage;
import pl.edu.icm.yadda.analysis.textr.model.BxZone;

/**
 *
 * @author Dominika Tkaczyk (dtkaczyk@icm.edu.pl)
*/ 
public class XVarianceFeature implements FeatureCalculator<BxZone, BxPage> {

    private static String featureName = "XVariance";

    @Override
    public String getFeatureName() {
        return featureName;
    }

    @Override
    public double calculateFeatureValue(BxZone zone, BxPage page) {
        double meanX = 0;
        for (BxLine line : zone.getLines()) {
            meanX += line.getX();
        }
        meanX /= zone.getLines().size();
        
        double meanXDiff = 0;
        for (BxLine line : zone.getLines()) {
            meanXDiff += Math.abs(line.getX() - meanX);
            
        }
                
        return meanXDiff / zone.getLines().size() / meanX;
     }
    
} 
