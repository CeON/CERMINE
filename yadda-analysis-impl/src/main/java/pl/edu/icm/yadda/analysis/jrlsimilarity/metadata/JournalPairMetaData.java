package pl.edu.icm.yadda.analysis.jrlsimilarity.metadata;

import pl.edu.icm.yadda.analysis.jrlsimilarity.common.JournalId;
import pl.edu.icm.yadda.analysis.jrlsimilarity.metadata.citations.JournalCitations;
import pl.edu.icm.yadda.analysis.jrlsimilarity.metadata.classifications.JournalClassifications;
import pl.edu.icm.yadda.analysis.jrlsimilarity.metadata.keywords.JournalKeywords;


/**
 * Encapsulator used for carrying pair of journals (with relevant metadata - 
 * journal's keywords, citations, classifications and identities)
 * whose similarity will be calculated in process node.
 * TODO put an exact name of process node!
 * @author Michał Siemiończyk michsiem@icm.edu.pl
 *
 */
public class JournalPairMetaData {
	
	/**
	 * @uml.property  name="journalId1"
	 * @uml.associationEnd  
	 */
	private JournalId journalId1;
	
	/**
	 * @uml.property  name="journalId2"
	 * @uml.associationEnd  
	 */
	private JournalId journalId2;
	
	/**
	 * @uml.property  name="journalKeywords1"
	 * @uml.associationEnd  
	 */
	private JournalKeywords journalKeywords1;
	
	/**
	 * @uml.property  name="journalKeywords2"
	 * @uml.associationEnd  
	 */
	private JournalKeywords journalKeywords2;
	
	/**
	 * @uml.property  name="journalCitations1"
	 * @uml.associationEnd  
	 */
	private JournalCitations journalCitations1;
	
	/**
	 * @uml.property  name="journalCitations2"
	 * @uml.associationEnd  
	 */
	private JournalCitations journalCitations2;
	
	/**
	 * @uml.property  name="journalClassifications1"
	 * @uml.associationEnd  
	 */
	private JournalClassifications journalClassifications1;
	
	/**
	 * @uml.property  name="journalClassifications2"
	 * @uml.associationEnd  
	 */
	private JournalClassifications journalClassifications2;
	
	
	/**
	 * @param journalId2
	 * @uml.property  name="journalId2"
	 */
	public void setJournalId2(JournalId journalId2) {
		this.journalId2 = journalId2;
	}
	/**
	 * @return
	 * @uml.property  name="journalId2"
	 */
	public JournalId getJournalId2() {
		return journalId2;
	}
	/**
	 * @param journalId1
	 * @uml.property  name="journalId1"
	 */
	public void setJournalId1(JournalId journalId1) {
		this.journalId1 = journalId1;
	}
	/**
	 * @return
	 * @uml.property  name="journalId1"
	 */
	public JournalId getJournalId1() {
		return journalId1;
	}
	/**
	 * @param journalClassifications2
	 * @uml.property  name="journalClassifications2"
	 */
	public void setJournalClassifications2(JournalClassifications journalClassifications2) {
		this.journalClassifications2 = journalClassifications2;
	}
	/**
	 * @return
	 * @uml.property  name="journalClassifications2"
	 */
	public JournalClassifications getJournalClassifications2() {
		return journalClassifications2;
	}
	/**
	 * @param journalCitations1
	 * @uml.property  name="journalCitations1"
	 */
	public void setJournalCitations1(JournalCitations journalCitations1) {
		this.journalCitations1 = journalCitations1;
	}
	/**
	 * @return
	 * @uml.property  name="journalCitations1"
	 */
	public JournalCitations getJournalCitations1() {
		return journalCitations1;
	}
	/**
	 * @param journalCitations2
	 * @uml.property  name="journalCitations2"
	 */
	public void setJournalCitations2(JournalCitations journalCitations2) {
		this.journalCitations2 = journalCitations2;
	}
	/**
	 * @return
	 * @uml.property  name="journalCitations2"
	 */
	public JournalCitations getJournalCitations2() {
		return journalCitations2;
	}
	/**
	 * @param journalClassifications1
	 * @uml.property  name="journalClassifications1"
	 */
	public void setJournalClassifications1(JournalClassifications journalClassifications1) {
		this.journalClassifications1 = journalClassifications1;
	}
	/**
	 * @return
	 * @uml.property  name="journalClassifications1"
	 */
	public JournalClassifications getJournalClassifications1() {
		return journalClassifications1;
	}
	/**
	 * @param journalKeywords1
	 * @uml.property  name="journalKeywords1"
	 */
	public void setJournalKeywords1(JournalKeywords journalKeywords1) {
		this.journalKeywords1 = journalKeywords1;
	}
	/**
	 * @return
	 * @uml.property  name="journalKeywords1"
	 */
	public JournalKeywords getJournalKeywords1() {
		return journalKeywords1;
	}
	/**
	 * @param journalKeywords2
	 * @uml.property  name="journalKeywords2"
	 */
	public void setJournalKeywords2(JournalKeywords journalKeywords2) {
		this.journalKeywords2 = journalKeywords2;
	}
	/**
	 * @return
	 * @uml.property  name="journalKeywords2"
	 */
	public JournalKeywords getJournalKeywords2() {
		return journalKeywords2;
	}
	
}
