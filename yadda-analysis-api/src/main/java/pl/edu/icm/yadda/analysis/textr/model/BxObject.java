package pl.edu.icm.yadda.analysis.textr.model;

/**
 * Common class for all Bx* classes having physical properties (this is to say
 * BxBounds).
 * 
 * @author Pawel Szostek (p.szostek@icm.edu.pl)
 * @date 05.2012
 * 
 */

public abstract class BxObject {

    /** zone's bounding box */
    protected BxBounds bounds;

    public Double getArea() {
    	return (bounds.getHeight() * bounds.getWidth());
    }

    public BxBounds getBounds() {
        return bounds;
    }

    public BxObject setBounds(BxBounds bounds) {
    	this.bounds = bounds;
    	return this;
    }
    
    public double getX() {
    	return bounds.getX();
    }
    
    public double getY() {
    	return bounds.getY();
    }
    
    public double getWidth() {
    	return bounds.getWidth();
    }
    
    public double getHeight() {
    	return bounds.getHeight();
    }
}
