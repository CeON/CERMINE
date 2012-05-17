package pl.edu.icm.yadda.analysis.jrlsimilarity.repo.meta.connector;

import pl.edu.icm.yadda.analysis.jrlsimilarity.common.JournalId;
import pl.edu.icm.yadda.analysis.jrlsimilarity.metadata.JournalMetaData;
import pl.edu.icm.yadda.analysis.jrlsimilarity.metadata.disambiguation.JournalDisambiguationMeta;
import pl.edu.icm.yadda.analysis.jrlsimilarity.repo.meta.XmlJMRepo;

/**
 * A xml-files based implementation of Journal metadata repository.
 * @author Michał Siemiończyk michsiem@icm.edu.pl
 *
 */
public class XmlJMRepoConnector implements JMRepoConnector<JournalDisambiguationMeta> {

	/**
	 * @uml.property  name="repoInstance"
	 * @uml.associationEnd  readOnly="true"
	 */
	private XmlJMRepo repoInstance;
	

	@Override
	public JournalMetaData getJournalMetaData(JournalId journalId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addJournalMetaData(JournalDisambiguationMeta journalMetadata) {
		// TODO Auto-generated method stub
		
	}

}
