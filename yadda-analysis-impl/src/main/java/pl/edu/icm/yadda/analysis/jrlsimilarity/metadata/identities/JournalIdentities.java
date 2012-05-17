package pl.edu.icm.yadda.analysis.jrlsimilarity.metadata.identities;

import java.util.List;

import pl.edu.icm.yadda.analysis.jrlsimilarity.common.JournalId;
import pl.edu.icm.yadda.analysis.jrlsimilarity.metadata.JournalMetaData;



/**
 * Encapsulator for identities's keywords metadata.
 * @author Michał Siemiończyk michsiem@icm.edu.pl
 *
 */
public class JournalIdentities implements JournalMetaData {

	private boolean empty = true;
	
	private List<String> citationlist;
	
	
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
		// TODO Auto-generated method stub
		return null;
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
