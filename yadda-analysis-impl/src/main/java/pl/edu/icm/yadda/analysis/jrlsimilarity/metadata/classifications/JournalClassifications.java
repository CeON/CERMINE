package pl.edu.icm.yadda.analysis.jrlsimilarity.metadata.classifications;

import java.util.List;

import pl.edu.icm.yadda.analysis.jrlsimilarity.common.JournalId;
import pl.edu.icm.yadda.analysis.jrlsimilarity.metadata.JournalMetaData;



/**
 * Encapsulator for classifications's keywords metadata.
 * @author Michał Siemiończyk michsiem@icm.edu.pl
 *
 */
public class JournalClassifications implements JournalMetaData {

	private boolean empty = true;
	
	private List<String> classificationlist;
	
	/**
	 * @uml.property  name="journalId"
	 * @uml.associationEnd  
	 */
	private JournalId journalId;
	
	public List<String> getClassificationlist() {
		return classificationlist;
	}
	
	public int getClassificationOccurences(String classification) {
		return 0;
		//TODO implement
	}

	public int getclassificationListSize(){
		//TODO implement
		return 0;
	}
	
	public void setClassificationlist(List<String> classification) {
		this.classificationlist = classificationlist;
	}

	@Override
	public JournalId getJournalId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setJournalId(JournalId journalId) {
		// TODO Auto-generated method stub
		
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
