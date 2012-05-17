package pl.edu.icm.yadda.analysis.jrlsimilarity.nodes.writer;

import pl.edu.icm.yadda.analysis.jrlsimilarity.metadata.JournalMetaData;
import pl.edu.icm.yadda.analysis.jrlsimilarity.metadata.citations.JournalCitations;
import pl.edu.icm.yadda.analysis.jrlsimilarity.metadata.classifications.JournalClassifications;
import pl.edu.icm.yadda.analysis.jrlsimilarity.metadata.keywords.JournalKeywords;
import pl.edu.icm.yadda.process.ctx.ProcessContext;
import pl.edu.icm.yadda.process.node.IWriterNode;

/**
 * WriterNode which is used to save metadata gained from Citation
 * @author Michał Siemiończyk michsiem@icm.edu.pl
 *
 */
public class JournalMetaRepoWritingNode implements IWriterNode<JournalMetaData>  {

	@Override
	public void store(JournalMetaData data, ProcessContext ctx)
			throws Exception {
		
//		XmlJMRepoConnector metaRepoCon = XmlJMRepo.getInstance().getConnector();
		System.out.print("Writer Node : ");
		if (data instanceof JournalKeywords) System.out.println("keywords");
		else if (data instanceof JournalCitations) System.out.println("citations");
		else if (data instanceof JournalClassifications) System.out.println("classifications");
		else System.out.println("UNKNOWN");
		
//		metaRepoCon.addJournalMetaData(new JournalKeywords());
	
	}

}
