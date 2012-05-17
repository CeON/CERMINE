package pl.edu.icm.yadda.analysis.jrlsimilarity.nodes.processing;

import pl.edu.icm.yadda.analysis.jrlsimilarity.metadata.identities.IdentityElement;
import pl.edu.icm.yadda.analysis.jrlsimilarity.metadata.identities.JournalIdentities;
import pl.edu.icm.yadda.bwmeta.model.YElement;
import pl.edu.icm.yadda.process.ctx.ProcessContext;
import pl.edu.icm.yadda.process.node.IProcessingNode;


/**
 * A Node which connects an identity given on input and associates it with
 * a specific journal id and sends it further in an encapsulated class - 
 * JournalIdentity.
 * @author Michał Siemiończyk michsiem@icm.edu.pl
 *
 */
public class IdentitiesProcessingNode implements IProcessingNode<IdentityElement, JournalIdentities> {

	@Override
	public JournalIdentities process(IdentityElement input,
			ProcessContext ctx) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
