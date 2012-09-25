package pl.edu.icm.coansys.metaextr.textr.model;

public interface Indexable<A> {
	/** Getter for the value based on TrueViz XxxID field */
	public String getId();
	/** Getter for the value based on TrueViz XxxNext field */
	public String getNextId();
	/** Setter for the value based on TrueViz XxxID field */
	public void setId(String id);
	/** Setter for the value based on TrueViz XxxNext field */
	public void setNextId(String nextId);
	
	/** Get next linked list element */
	public A getNext();
	/** Set next linked list element */
	public void setNext(A elem);
	public boolean hasNext();
	
	public A getPrev();
	public void setPrev(A elem);
	public boolean hasPrev();
	
	public Boolean isSorted();
	public void setSorted(Boolean isSorted);
	
}
