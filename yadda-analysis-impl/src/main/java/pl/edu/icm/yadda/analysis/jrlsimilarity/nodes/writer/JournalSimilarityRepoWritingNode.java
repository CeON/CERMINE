package pl.edu.icm.yadda.analysis.jrlsimilarity.nodes.writer;

import pl.edu.icm.yadda.analysis.jrlsimilarity.common.DoubleJournalSimilarity;
import pl.edu.icm.yadda.process.ctx.ProcessContext;
import pl.edu.icm.yadda.process.node.IWriterNode;

/**
 * Writer node that puts journal similarity values into journal similarity
 * repository, so later it can be accesed via user API as well as backend API.
 * 
 * @author Michał Siemiończyk michsiem@icm.edu.pl
 *
 */
public class JournalSimilarityRepoWritingNode implements IWriterNode<DoubleJournalSimilarity> {

	@Override
	public void store(DoubleJournalSimilarity data, ProcessContext ctx)
			throws Exception {
		// TODO Auto-generated method stub
		
	}



}
