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
import pl.edu.icm.yadda.bwmeta.model.YElement;
import pl.edu.icm.yadda.process.ctx.ProcessContext;
import pl.edu.icm.yadda.process.model.EnrichedPayload;
import pl.edu.icm.yadda.process.node.IWriterNode;

/**
 * WriterNode which simply logs out basis metada information from
 * YElement given, such as element.id, element.name
 * 
 * @author Michał Siemiończyk michsiem@icm.edu.pl
 *
 */
public class DummyWritingNode implements IWriterNode<EnrichedPayload<YElement>[]>  {

	protected final Logger log = LoggerFactory.getLogger(this.getClass());

	@Override
	public void store(EnrichedPayload<YElement>[] data, ProcessContext ctx)
			throws Exception {
	
	for(EnrichedPayload<YElement> enrichedP : data){
		log.info("----------------------\n" + 
				"Element Id:" + enrichedP.getObject().getId() + "\n" + 
				"Element Name:" + enrichedP.getObject().getNames().get(0) + "\n" +
				"Element Structures:" + enrichedP.getObject().getStructures() + "\n"); 
	}
	
	}

}
