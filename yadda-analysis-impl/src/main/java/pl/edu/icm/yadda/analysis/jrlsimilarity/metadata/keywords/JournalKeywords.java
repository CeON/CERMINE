package pl.edu.icm.yadda.analysis.jrlsimilarity.metadata.keywords;

import java.util.List;

import pl.edu.icm.yadda.analysis.jrlsimilarity.common.JournalId;
import pl.edu.icm.yadda.analysis.jrlsimilarity.metadata.JournalMetaData;

/**
 * Encapsulator for journal's keywords metadata.
 * @author Michał Siemiończyk michsiem@icm.edu.pl
 *
 */
public class JournalKeywords implements JournalMetaData {

	private boolean empty = true;
	
	private List<String> keywordlist;
	
	private JournalId journalId;
	
	public List<String> getKeywordlist() {
		return keywordlist;
	}
	
	public int getKeywordOccurences(String keyword) {
		return 0;
		//TODO implement
	}

	public int getKeywordListSize(){
		//TODO implement
		return 0;
	}
	
	public void setKeywordlist(List<String> keywordlist) {
		this.keywordlist = keywordlist;
	}

	@Override
	public JournalId getJournalId() {
		// TODO Auto-generated method stub
		return null;
	}
	/**
	 * @param journalId
	 * @uml.property  name="journalId"
	 */
	@Override
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
