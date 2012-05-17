package pl.edu.icm.yadda.analysis.jrlsimilarity.common;

/**
 * Interface for encapsulator for a value of similarity between two journals. Classes that implements  {@link JournalSimilarity}  specify exact type of similarity value.
 * @author  Michał Siemiończyk michsiem@icm.edu.pl
 */
public interface JournalSimilarity<T> {
	 
	T getValue();
	
	void setValue(T value);
	
	/**
	 * @uml.property  name="journalId1"
	 * @uml.associationEnd  
	 */
	JournalId getJournalId1();
	
	/**
	 * @uml.property  name="journalId2"
	 * @uml.associationEnd  
	 */
	JournalId getJournalId2();
	
	/**
	 * @param journalId1
	 * @uml.property  name="journalId1"
	 */
	void setJournalId1(JournalId journalId1);
	
	/**
	 * @param journalId2
	 * @uml.property  name="journalId2"
	 */
	void setJournalId2(JournalId journalId2);
	 
}
