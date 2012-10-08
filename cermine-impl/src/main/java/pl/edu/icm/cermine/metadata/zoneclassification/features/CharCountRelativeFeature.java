package pl.edu.icm.cermine.metadata.zoneclassification.features;

import pl.edu.icm.cermine.structure.model.*;
import pl.edu.icm.cermine.tools.classification.features.FeatureCalculator;

/**
 *
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class CharCountRelativeFeature extends FeatureCalculator<BxZone, BxPage> {

    @Override
    public double calculateFeatureValue(BxZone zone, BxPage page) {
        int count = 0;
        for (BxLine line : zone.getLines()) {
            for (BxWord word : line.getWords()) {
                for (BxChunk chunk : word.getChunks()) {
                    count += chunk.getText().length();
                }
            }
        }

        int pCount = 0;
        for (BxZone pZone : page.getZones()) {
            for (BxLine line : pZone.getLines()) {
                for (BxWord word : line.getWords()) {
                    for (BxChunk chunk : word.getChunks()) {
                        pCount += chunk.getText().length();
                    }
                }
            }
        }
        if(((double) count / (double) pCount)<0.0) {
        	System.out.println(count);
        	System.out.println(pCount);
        }
        return (double) count / (double) pCount;
    }
}
