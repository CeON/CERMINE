package pl.edu.icm.cermine.content.features.zone;

import pl.edu.icm.cermine.structure.model.*;
import pl.edu.icm.cermine.tools.classification.features.FeatureCalculator;

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
