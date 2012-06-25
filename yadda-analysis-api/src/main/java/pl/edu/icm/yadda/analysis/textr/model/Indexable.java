package pl.edu.icm.yadda.analysis.textr.model;

public interface Indexable {
	/** Getter for the value based on TrueViz XxxID field */
	public String getId();
	/** Getter for the value based on TrueViz XxxNext field */
	public String getNextId();
	/** Setter for the value based on TrueViz XxxID field */
	public Indexable setId(String id);
	/** Setter for the value based on TrueViz XxxNext field */
	public Indexable setNextId(String nextId);
	
	/** Get next linked list element */
	public Indexable getNext();
	/** Set next linked list element */
	public Indexable setNext(Indexable elem);
	public boolean hasNext();
}
