package pl.edu.icm.cermine.structure.readingorder;

import pl.edu.icm.cermine.structure.model.BxBounds;
import pl.edu.icm.cermine.structure.model.BxObject;
import pl.edu.icm.cermine.structure.model.BxZone;

/** Class used for clustering BxObjects into a tree
*
* @author Pawel Szostek (p.szostek@icm.edu.pl)
* @date 05.2012
* 
*/

public class BxZoneGroup extends BxObject<BxZoneGroup, BxZoneGroup> {
	private BxObject leftChild;
	private BxObject rightChild;

    public BxZoneGroup(BxObject child1, BxObject child2) {
        this.leftChild = child1;
        this.rightChild = child2;
        setBounds(Math.min(child1.getX(), child2.getX()), 
        		  Math.min(child1.getY(), child2.getY()),
        		  Math.max(child1.getX()+child1.getWidth(), child2.getX()+child2.getWidth()),
        		  Math.max(child1.getY()+child1.getHeight(), child2.getY()+child2.getHeight()));
    }

    public BxZoneGroup(BxObject zone) {
    	this.leftChild = zone;
    	this.rightChild = null;
    	setBounds(zone.getBounds());
    }

    @Override
    public BxZoneGroup setBounds(BxBounds bounds) {
    	super.setBounds(bounds);
    	return this;
    }
    
    public boolean hasZone() {
    	return (rightChild == null);
    }

    public BxZone getZone() throws RuntimeException {
    	if(!hasZone())
    		throw new RuntimeException();
    	assert this.leftChild instanceof BxZone : "There is one child and its not of type BxZone. How comes?";
    	return (BxZone)this.leftChild;
    }

    public BxObject getLeftChild() {
    	return leftChild;
    }

    public BxObject getRightChild() {
    	return rightChild;
    }
    
    public BxZoneGroup setLeftChild(BxObject obj) {
    	this.leftChild = obj;
    	return this;
    }

    public BxZoneGroup setRightChild(BxObject obj) {
    	this.rightChild = obj;
    	return this;
    }

    public BxZoneGroup setBounds(Double x0, Double y0, Double x1, Double y1) {
    	assert x1 >= x0;
    	assert y1 >= y0;
    	this.bounds = new BxBounds(x0, y0, x1-x0, y1-y0);
    	return this;
    }
}
