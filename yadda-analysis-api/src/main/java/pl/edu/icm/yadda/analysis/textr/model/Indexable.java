package pl.edu.icm.yadda.analysis.textr.model;

public interface Indexable<A extends Indexable<A>> {
	/** Getter for the value based on TrueViz XxxID field */
	public String getId();
	/** Getter for the value based on TrueViz XxxNext field */
	public String getNextId();
	/** Setter for the value based on TrueViz XxxID field */
	public A setId(String id);
	/** Setter for the value based on TrueViz XxxNext field */
	public A setNextId(String nextId);
	
	/** Get next linked list element */
	public A getNext();
	/** Set next linked list element */
	public A setNext(A elem);
	public boolean hasNext();
	
	public A getPrev();
	public A setPrev(A elem);
	public boolean hasPrev();
}
