package pl.edu.icm.coansys.metaextr.structure.model;

/**
 * Common class for all Bx* classes having physical properties (this is to say
 * BxBounds).
 * 
 * @param <S> the actual type
 * @param <T> context type (e.g. BxPage for BxZone)
 * @author Pawel Szostek (p.szostek@icm.edu.pl)
 * @date 05.2012
 * 
 */

public abstract class BxObject <S, T> implements Indexable<S> {

    /** zone's bounding box */
    protected BxBounds bounds;
    /** page number in the document */
    protected String objId;

    /** number of the next page from the sequence */
    protected String nextObjId;

    /** next page in the linked list of pages. Stored in the logical reading order */
    protected S nextObj;
    protected S prevObj;

    protected T context;
    
    protected Boolean isSorted;

    public void setContext(T context) {
    	this.context = context;
    }
    
    public T getContext() {
    	return context;
    }
    
    @Override
    public void setId(String objId) {
    	this.objId = objId;
    }
    
    @Override
    public String getId() {
    	return this.objId;
    }
    
    @Override
    public void setNextId(String nextObjId) {
    	this.nextObjId = nextObjId;
    }
    
    @Override
    public String getNextId() {
    	return this.nextObjId;
    }

    @Override
    public void setNext(S nextObj) {
    	this.nextObj = nextObj;
    }

    @Override
    public S getNext() {
    	return this.nextObj;
    }

    @Override
	public boolean hasNext() {
		return getNext() != null;
	}    

    @Override
    public void setPrev(S prevObj) {
    	this.prevObj = prevObj;
    }

    @Override
    public S getPrev() {
    	return this.prevObj;
    }

    @Override
	public boolean hasPrev() {
		return getPrev() != null;
	}

    public Double getArea() {
    	return (bounds.getHeight() * bounds.getWidth());
    }

    public BxBounds getBounds() {
        return bounds;
    }

    public S setBounds(BxBounds bounds) {
    	this.bounds = bounds;
    	return (S)this;
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
