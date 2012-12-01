package pl.edu.icm.cermine.content.filtering.features;

import pl.edu.icm.cermine.structure.model.*;
import pl.edu.icm.cermine.tools.classification.features.FeatureCalculator;

/**
 *
 * @author Dominika Tkaczyk (dtkaczyk@icm.edu.pl)
 */
public class FigureTableFeature extends FeatureCalculator<BxZone, BxPage> {

    @Override
    public double calculateFeatureValue(BxZone zone, BxPage page) {
        int i = 0;
        for (BxLine line : zone.getLines()) {
            String text = line.toText().toLowerCase();
            if (text.matches("figure ?[0-9ivx]+[\\.:].*$") || text.matches("table ?[0-9ivx]+[\\.:].*$")
                    || text.matches("figure ?[0-9ivx]+$") || text.matches("table ?[0-9ivx]+$")) {
                if (i == 0) {
                    return 1;
                }
                if (Math.abs(line.getX() - zone.getLines().get(i - 1).getX()) > 5) {
                    return 1;
                }
                double prevW = 0;
                for (BxWord w : zone.getLines().get(i - 1).getWords()) {
                    for (BxChunk ch : w.getChunks()) {
                        prevW += ch.getArea();
                    }
                }
                prevW /= Math.max(zone.getLines().get(i - 1).getArea(), line.getArea());
                double lineW = 0;
                for (BxWord w : line.getWords()) {
                    for (BxChunk ch : w.getChunks()) {
                        prevW += ch.getArea();
                    }
                }
                lineW /= Math.max(zone.getLines().get(i - 1).getArea(), line.getArea());
                if (Math.abs(lineW -prevW) < 0.3) {
                    return 1;
                }
                return 0.3;
            }
            i++;
        }
        return 0;
    }
    
}
