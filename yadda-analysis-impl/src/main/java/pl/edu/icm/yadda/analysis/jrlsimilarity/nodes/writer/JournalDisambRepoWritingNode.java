package pl.edu.icm.yadda.analysis.jrlsimilarity.nodes.writer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.edu.icm.yadda.analysis.jrlsimilarity.JournalSimProperties;
import pl.edu.icm.yadda.analysis.jrlsimilarity.metadata.JournalMetaData;
import pl.edu.icm.yadda.analysis.jrlsimilarity.metadata.citations.JournalCitations;
import pl.edu.icm.yadda.analysis.jrlsimilarity.metadata.classifications.JournalClassifications;
import pl.edu.icm.yadda.analysis.jrlsimilarity.metadata.disambiguation.JournalDisambiguationMeta;
import pl.edu.icm.yadda.analysis.jrlsimilarity.metadata.keywords.JournalKeywords;
import pl.edu.icm.yadda.analysis.jrlsimilarity.repo.meta.PostgresJMRepo;
import pl.edu.icm.yadda.analysis.jrlsimilarity.repo.meta.connector.PostgresJMRepoConnector;
import pl.edu.icm.yadda.process.ctx.ProcessContext;
import pl.edu.icm.yadda.process.node.IWriterNode;

/**
 * WriterNode which is used to save metadata gained from nodes processing
 * 
 * @author Michał Siemiończyk michsiem@icm.edu.pl
 *
 */
public class JournalDisambRepoWritingNode implements IWriterNode<JournalMetaData>  {

	protected final Logger log = LoggerFactory.getLogger(this.getClass());

	@Override
	public void store(JournalMetaData data, ProcessContext ctx)
			throws Exception {
	
		PostgresJMRepo repo = PostgresJMRepo.getInstance();
		PostgresJMRepoConnector connector = repo.getConnector();
		JournalDisambiguationMeta meta = null;
		if(data.isEmpty())
			;
//			log.info("JrlDisRepoWriNod: Input data is empty!");
		else{
			meta = (JournalDisambiguationMeta) data;
			connector.addJournalDebugOccur(meta);
			connector.addJournalMetaData(meta);
		}
	}

}
