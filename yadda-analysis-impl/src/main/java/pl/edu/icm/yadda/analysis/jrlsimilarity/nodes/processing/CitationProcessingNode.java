package pl.edu.icm.yadda.analysis.jrlsimilarity.nodes.processing;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.edu.icm.yadda.analysis.jrlsimilarity.metadata.citations.JournalCitations;
import pl.edu.icm.yadda.bwmeta.model.YElement;
import pl.edu.icm.yadda.process.ctx.ProcessContext;
import pl.edu.icm.yadda.process.model.EnrichedPayload;
import pl.edu.icm.yadda.process.node.IProcessingNode;


/**
* Node which calculates citation's occurences in article given in input channel
* associate that data with specific journal and sends further.
*
* @author Michal Siemionczyk michsiem@icm.edu.pl
*/
public class CitationProcessingNode implements IProcessingNode<EnrichedPayload<YElement>[], JournalCitations> {

	/**
	 * @uml.property  name="log"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	protected final Logger log = LoggerFactory.getLogger(this.getClass());
	
	
   @Override
   public JournalCitations process(EnrichedPayload<YElement>[] input, ProcessContext ctx){
	   //TODO implement
	   
	   System.out.println("Process NODE : citation");
	   return new JournalCitations();
   	}
}

