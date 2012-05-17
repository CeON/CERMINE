package pl.edu.icm.yadda.analysis.jrlsimilarity.nodes.iterator;

import pl.edu.icm.yadda.analysis.jrlsimilarity.metadata.identities.IdentityElement;
import pl.edu.icm.yadda.process.ctx.ProcessContext;
import pl.edu.icm.yadda.process.iterator.IIdExtractor;
import pl.edu.icm.yadda.process.iterator.ISourceIterator;
import pl.edu.icm.yadda.process.iterator.ISourceIteratorBuilder;

/**
 * Iterator that is supposed to iterator over a set of identities, so they
 * can  be extracted and send further, to process them in a manner that allows
 * them to be linked with journals.
 * @author Michał Siemiończyk michsiem@icm.edu.pl
 *
 */
public class IdentitiesIteratorBuilder implements ISourceIteratorBuilder<IdentityElement> {

	@Override
	public ISourceIterator<IdentityElement> build(ProcessContext ctx)
			throws Exception {
		return new ISourceIterator<IdentityElement>() {

			@Override
			public boolean hasNext() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public IdentityElement next() {
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
	public IIdExtractor<IdentityElement> getIdExtractor() {
		// TODO Auto-generated method stub
		return null;
	}

}
