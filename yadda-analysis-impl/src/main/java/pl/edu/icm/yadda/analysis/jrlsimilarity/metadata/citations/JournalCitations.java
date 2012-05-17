package pl.edu.icm.yadda.analysis.jrlsimilarity.metadata.citations;

import java.util.List;

import pl.edu.icm.yadda.analysis.jrlsimilarity.common.JournalId;
import pl.edu.icm.yadda.analysis.jrlsimilarity.metadata.JournalMetaData;


/**
 * Encapsulator for citations's keywords metadata.
 * @author Michał Siemiończyk michsiem@icm.edu.pl
 *
 */
public class JournalCitations implements JournalMetaData {

	
	private boolean empty = true;
	
	private List<String> citationlist;
	
	/**
	 * @uml.property  name="journalId"
	 * @uml.associationEnd  
	 */
	private JournalId journalId;
	
	public List<String> getCitationlist() {
		return citationlist;
	}
	
	public int getCitationOccurences(String citation) {
		return 0;
		//TODO implement
	}

	public int getCitationListSize(){
		//TODO implement
		return 0;
	}
	
	public void setCitationlist(List<String> citationlist) {
		this.citationlist = citationlist;
	}

	@Override
	public JournalId getJournalId() {
		return this.journalId;
	}


	public void setJournalId(JournalId journalId) {
		this.journalId = journalId;
	}

	@Override
	public boolean isEmpty() {
		return this.empty;
	}

	@Override
	public void setEmpty(boolean isEmpty) {
		this.empty = isEmpty;
		
	}

}
