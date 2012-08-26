package pl.edu.icm.yadda.analysis.articlecontent.features.zone;

import pl.edu.icm.yadda.analysis.classification.features.FeatureCalculator;
import pl.edu.icm.yadda.analysis.textr.model.*;

/**
 *
 * @author Dominika Tkaczyk (dtkaczyk@icm.edu.pl)
 */
public class AreaFeature implements FeatureCalculator<BxZone, BxPage> {

    private static String featureName = "Area";

    @Override
    public String getFeatureName() {
        return featureName;
     }

    @Override
    public double calculateFeatureValue(BxZone zone, BxPage page) {
        double charsArea = 0;
        for (BxLine line : zone.getLines()) {
            for (BxWord word : line.getWords()) {
                for (BxChunk chunk : word.getChunks()) {
                    charsArea += chunk.getArea();
                }
            }
        }
        
        return charsArea / zone.getArea();
    } 
    
} 
