package pl.edu.icm.yadda.analysis.jrlsimilarity.nodes.processing;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.edu.icm.yadda.analysis.jrlsimilarity.metadata.disambiguation.JournalDisambiguationParser;
import pl.edu.icm.yadda.analysis.jrlsimilarity.metadata.keywords.JournalKeywords;
import pl.edu.icm.yadda.analysis.jrlsimilarity.metadata.keywords.JournalKeywordsParser;
import pl.edu.icm.yadda.bwmeta.model.YConstants;
import pl.edu.icm.yadda.bwmeta.model.YElement;
import pl.edu.icm.yadda.bwmeta.model.YStructure;
import pl.edu.icm.yadda.process.ctx.ProcessContext;
import pl.edu.icm.yadda.process.model.EnrichedPayload;
import pl.edu.icm.yadda.process.node.IProcessingNode;



/**
* Node which calculates keyword's occurences in article given in input channel
* associate that data with specific journal and sends further.
*
* @author Michal Siemionczyk michsiem@icm.edu.pl
*/
public class KeywordProcessingNode implements IProcessingNode<EnrichedPayload<YElement>[], JournalKeywords> {


	protected final Logger log = LoggerFactory.getLogger(this.getClass());
	
	
   @Override
   public JournalKeywords process(EnrichedPayload<YElement>[] input, ProcessContext ctx){
	   
	   for(EnrichedPayload<YElement> enrichedP : input){
		   JournalKeywordsParser parser =  null;
		   YElement yElem = enrichedP.getObject();
		   YStructure yStruc =  yElem.getStructure(YConstants.EXT_HIERARCHY_JOURNAL);
		   if(yStruc.getCurrent().getLevel().equals(YConstants.EXT_LEVEL_JOURNAL_JOURNAL)){
			   parser = new JournalKeywordsParser(yElem);
			   if(parser.parse()) {
				   return parser.getParsedData();
			   }
			   else log.info("Problem with parsing occured in " + parser);
		   }
		   else
			   log.info("No yElement on journal level (in journal hierarchy) found in input channel!");
		   
	   }
   	//TODO check this if it's OK
   return null;
   }
}

