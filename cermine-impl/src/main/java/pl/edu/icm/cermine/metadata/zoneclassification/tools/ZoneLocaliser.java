package pl.edu.icm.cermine.metadata.zoneclassification.tools;

import pl.edu.icm.cermine.structure.model.BxZone;

public class ZoneLocaliser {
	
	private BxZone zone;
	private BxZone leftZone = null;
	private BxZone rightZone = null;
	private BxZone upperZone = null;
	private BxZone lowerZone = null;
	
	public ZoneLocaliser(BxZone zone) {
		this.zone = zone;
		
        for (BxZone otherZone : zone.getParent().getZones()) {
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
            
            if(oct == 1) {
            	if(upperZone == null || 
            			otherZone.getY()+otherZone.getHeight() > upperZone.getY()+upperZone.getHeight()) {
            		upperZone = otherZone;
            	}
            } else if(oct == 5) {
            	if(lowerZone == null ||
            			otherZone.getY() < lowerZone.getY()) {
            		lowerZone = otherZone;
            	}
            } else if(oct == 7) {
            	if(leftZone == null ||
            			otherZone.getX() + otherZone.getWidth() > leftZone.getX() + leftZone.getWidth()) {
            		leftZone = otherZone;
            	}
            } else if(oct == 3) {
            	if(rightZone == null ||
            			otherZone.getX() < rightZone.getX()) {
            	rightZone = otherZone;
            	}
            }
        }
	}
	
	public BxZone getLeftZone() {
		return leftZone;
	}
	
	public BxZone getRightZone() {
		return rightZone;
	}
	
	public BxZone getUpperZone() {
		return upperZone;
	}
	
	public BxZone getLowerZone() {
		return lowerZone;
	}
	
}
