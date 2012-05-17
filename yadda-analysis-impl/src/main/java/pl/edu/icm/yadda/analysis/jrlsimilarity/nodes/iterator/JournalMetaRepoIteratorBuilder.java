package pl.edu.icm.yadda.analysis.jrlsimilarity.nodes.iterator;

import pl.edu.icm.yadda.analysis.jrlsimilarity.metadata.JournalPairMetaData;
import pl.edu.icm.yadda.process.ctx.ProcessContext;
import pl.edu.icm.yadda.process.iterator.IIdExtractor;
import pl.edu.icm.yadda.process.iterator.ISourceIterator;
import pl.edu.icm.yadda.process.iterator.ISourceIteratorBuilder;

/**
 * Builder for an iterator over journal metadata repository.
 * @author Michał Siemiończyk michsiem@icm.edu.pl
 *
 */
public class JournalMetaRepoIteratorBuilder implements ISourceIteratorBuilder<JournalPairMetaData> {

	@Override
	public ISourceIterator<JournalPairMetaData> build(ProcessContext ctx)
			throws Exception {
		// TODO Auto-generated method stub
		return new ISourceIterator<JournalPairMetaData>() {

			@Override
			public boolean hasNext() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public JournalPairMetaData next() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public void remove() {
				// TODO Auto-generated method stub
				
			}

			@Override
			public int getEstimatedSize() throws UnsupportedOperationException {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public void clean() {
				// TODO Auto-generated method stub
				
			}
		};
	}

	@Override
	public IIdExtractor<JournalPairMetaData> getIdExtractor() {
		// TODO Auto-generated method stub
		return null;
	}

}
