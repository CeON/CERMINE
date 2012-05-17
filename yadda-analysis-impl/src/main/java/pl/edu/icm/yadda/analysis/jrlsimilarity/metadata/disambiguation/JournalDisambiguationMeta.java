package pl.edu.icm.yadda.analysis.jrlsimilarity.metadata.disambiguation;

import java.util.List;

import org.mortbay.log.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.edu.icm.yadda.analysis.jrlsimilarity.common.JournalId;
import pl.edu.icm.yadda.analysis.jrlsimilarity.metadata.JournalMetaData;

/**
 * Encapsulator for journals's disambiguation metadata.
 * @author Michał Siemiończyk michsiem@icm.edu.pl
 *
 */
public class JournalDisambiguationMeta implements JournalMetaData {

	protected final Logger log = LoggerFactory.getLogger(this.getClass());
	
	private boolean empty = true;
	
	private JournalId journalId;
	
	private String issn = null;
	
	private String eIssn = null;
	
	/** Indicates number of articles contained in journal element; for research purposes.  */
//	private int articlesNr = 0;
	
	/** Full version of a title*/
	private String title = null;
	/** An acronym from full version of title*/
	private String acronymTitle = null;
	
	
	/**
	 * Constructor creating an empty element;
	 */
	public JournalDisambiguationMeta(String debug_case){
		this.setEmpty(true);
//		log.info("CREATED EMTPY EMTPY EMPTY " + debug_case);
	}
	
	public JournalDisambiguationMeta(JournalId journalId, String title, String acronymTitle, String issn, String eIssn) {
		this.journalId = journalId;
		this.acronymTitle = acronymTitle;
		this.title = title;
		this.setIssn(issn);
		this.seteIssn(eIssn);
		this.setEmpty(false);
	}
	
	@Override
	public JournalId getJournalId() {
		return this.journalId;
	}

	@Override
	public void setJournalId(JournalId journalId) {
		this.journalId = journalId;
		
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitle() {
		return title;
	}

	public void setAcronymTitle(String acronymTitle) {
		this.acronymTitle = acronymTitle;
	}

	public String getAcronymTitle() {
		return acronymTitle;
	}

	public void setIssn(String issn) {
		this.issn = issn;
	}

	public String getIssn() {
		return issn;
	}

	public void seteIssn(String eIssn) {
		this.eIssn = eIssn;
	}

	public String geteIssn() {
		return eIssn;
	}

	@Override
	public boolean isEmpty() {
		return this.empty;
	}

	@Override
	public void setEmpty(boolean isEmpty) {
		this.empty = isEmpty;
		
	}

//	public void setArticlesNr(int articlesNr) {
//		this.articlesNr = articlesNr;
//	}
//
//	public int getArticlesNr() {
//		return articlesNr;
//	}

}
