package pl.edu.icm.cermine.content.features.line;

import pl.edu.icm.cermine.structure.model.BxLine;
import pl.edu.icm.cermine.structure.model.BxPage;
import pl.edu.icm.cermine.tools.classification.features.FeatureCalculator;

/**
 *
 * @author Dominika Tkaczyk (dtkaczyk@icm.edu.pl)
 */
public class IsHigherThanNeighborsFeature extends FeatureCalculator<BxLine, BxPage> {

    @Override
    public double calculateFeatureValue(BxLine line, BxPage page) {
        double score = 0;

        double max = line.getHeight();
        double min = line.getHeight();

        BxLine l = line;
        int i = 0;
        while (l.hasPrev() && i < 2) {
            l = l.getPrev();
            max = Math.max(l.getHeight(), max);
            min = Math.min(l.getHeight(), min);
            ++i;
        }

        if (Math.abs(max - line.getHeight()) < 0.1 && Math.abs(min - line.getHeight()) > 1) {
            score += 0.5;
        }

        max = line.getHeight();
        min = line.getHeight();

        i = 0;
        l = line;
        while (l.hasNext() && i < 2) {
            l = l.getNext();
            max = Math.max(l.getHeight(), max);
            min = Math.min(l.getHeight(), min);
            ++i;
        }

        if (Math.abs(max - line.getHeight()) < 0.1 && Math.abs(min - line.getHeight()) > 1) {
            score += 0.5;
        }

        return score;
    }
}
