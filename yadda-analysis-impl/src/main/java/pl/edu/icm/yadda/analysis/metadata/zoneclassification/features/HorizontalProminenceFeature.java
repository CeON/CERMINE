package pl.edu.icm.yadda.analysis.metadata.zoneclassification.features;

import pl.edu.icm.yadda.analysis.classification.features.FeatureCalculator;
import pl.edu.icm.yadda.analysis.textr.model.BxPage;
import pl.edu.icm.yadda.analysis.textr.model.BxZone;

/**
 * @author Pawel Szostek (p.szostek@icm.edu.pl)
 */

public class HorizontalProminenceFeature implements
		FeatureCalculator<BxZone, BxPage> {

    private static String featureName = "HorizontalProminence";

	@Override
	public String getFeatureName() {
		return featureName;
	}
	
	@Override
	public double calculateFeatureValue(BxZone zone, BxPage page) {		
		Double leftBorder = Double.MAX_VALUE;
		Double rightBorder = Double.MIN_VALUE;
		for(BxZone z: page.getZones()) {
			leftBorder = Math.min(leftBorder, z.getBounds().getX());
			rightBorder = Math.max(rightBorder, z.getBounds().getY());
		}
		assert leftBorder != Double.MAX_VALUE;
		assert rightBorder != Double.MIN_VALUE;
	
		Double leftProminence = leftBorder;
		Double rightProminence = rightBorder;
		
		for(BxZone otherZone: page.getZones()) {
			Double cx, cy, cw, ch, ox, oy, ow, oh;
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
				if (cy + ch <= oy)
					oct = 4; 
				else if (cy >= oy + oh)
					oct = 2; 
				else
					oct = 3; 
			} else if (ox + ow <= cw) {
				if (cy + ch <= oy)
					oct = 6;
				else if (oy + oh <= cy)
					oct = 0;
				else
					oct = 7;
			} else if (cy + ch <= oy) {
				oct = 5;
			} else { // oy + oh <= cy
				oct = 1;
			}
			
			if(oct != 7 && oct != 3)
				continue;
			if(oct == 7) {
				leftProminence = Math.min(leftProminence, cx-(ox+ow));
			} else { //oct == 3
				rightProminence = Math.min(rightProminence, ox-(cx+cw));
			}
		}
		return rightProminence + leftProminence;
	}
};
