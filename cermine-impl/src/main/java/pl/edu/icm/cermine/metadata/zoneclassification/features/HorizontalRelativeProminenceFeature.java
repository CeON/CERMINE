package pl.edu.icm.cermine.metadata.zoneclassification.features;

import pl.edu.icm.cermine.structure.model.BxPage;
import pl.edu.icm.cermine.structure.model.BxZone;
import pl.edu.icm.cermine.tools.classification.features.FeatureCalculator;

/**
 * @author Pawel Szostek (p.szostek@icm.edu.pl)
 */
public class HorizontalRelativeProminenceFeature extends FeatureCalculator<BxZone, BxPage> {

    @Override
    public double calculateFeatureValue(BxZone zone, BxPage page) {
        Double leftProminence = zone.getX();
        Double rightProminence = page.getWidth() - (zone.getX() + zone.getWidth());

        for (BxZone otherZone : page.getZones()) {
            if (otherZone == zone) {
                continue;
            }
            Double cx, cy, cw, ch, ox, oy, ow, oh;
            Double newLeftProminence, newRightProminence;

            cx = zone.getBounds().getX();
            cy = zone.getBounds().getY();
            cw = zone.getBounds().getWidth();
            ch = zone.getBounds().getHeight();

            ox = otherZone.getBounds().getX();
            oy = otherZone.getBounds().getY();
            ow = otherZone.getBounds().getWidth();
            oh = otherZone.getBounds().getHeight();

            // Determine Octant
            //
            // 0 | 1 | 2
            // __|___|__
            // 7 | 9 | 3
            // __|___|__
            // 6 | 5 | 4

            Integer oct;
            if (cx + cw <= ox) {
                if (cy + ch <= oy) {
                    oct = 4;
                } else if (cy >= oy + oh) {
                    oct = 2;
                } else {
                    oct = 3;
                }
            } else if (ox + ow <= cx) {
                if (cy + ch <= oy) {
                    oct = 6;
                } else if (oy + oh <= cy) {
                    oct = 0;
                } else {
                    oct = 7;
                }
            } else if (cy + ch <= oy) {
                oct = 5;
            } else { // oy + oh <= cy
                oct = 1;
            }

            if (oct != 7 && oct != 3) {
                continue;
            }
            if (oct == 7) {
                newLeftProminence = Math.min(leftProminence, cx - (ox + ow));
                if (Math.abs(newLeftProminence - leftProminence) > Double.MIN_VALUE) {
                    leftProminence = newLeftProminence;
                }
            } else { //oct == 3
                newRightProminence = Math.min(rightProminence, ox - (cx + cw));
                if (Math.abs(newRightProminence - rightProminence) > Double.MIN_VALUE) {
                    rightProminence = newRightProminence;
                }
            }
        }
        Double ret = (rightProminence + leftProminence + zone.getWidth()) / page.getWidth();
        if (ret >= 0.0) {
            return ret;
        } else {
            return 0.0;
        }
    }
};
