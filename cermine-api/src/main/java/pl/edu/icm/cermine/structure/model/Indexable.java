package pl.edu.icm.cermine.structure.model;

public interface Indexable<A> {
	/** Getter for the value based on TrueViz XxxID field */
	String getId();
	/** Getter for the value based on TrueViz XxxNext field */
	String getNextId();
	/** Setter for the value based on TrueViz XxxID field */
	void setId(String id);
	/** Setter for the value based on TrueViz XxxNext field */
	void setNextId(String nextId);
	
	/** Get next linked list element */
	A getNext();
	/** Set next linked list element */
	void setNext(A elem);
	boolean hasNext();
	
	A getPrev();
	void setPrev(A elem);
	boolean hasPrev();
}
