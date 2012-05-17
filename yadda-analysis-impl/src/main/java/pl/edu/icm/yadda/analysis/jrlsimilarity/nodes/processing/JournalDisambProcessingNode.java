package pl.edu.icm.yadda.analysis.jrlsimilarity.nodes.processing;

import java.net.MalformedURLException;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanInitializationException;

import pl.edu.icm.yadda.analysis.jrlsimilarity.metadata.JournalMetaData;
import pl.edu.icm.yadda.analysis.jrlsimilarity.metadata.disambiguation.JournalDisambiguationMeta;
import pl.edu.icm.yadda.analysis.jrlsimilarity.metadata.disambiguation.JournalDisambiguationParser;
import pl.edu.icm.yadda.analysis.jrlsimilarity.metadata.keywords.JournalKeywords;
import pl.edu.icm.yadda.bwmeta.model.YConstants;
import pl.edu.icm.yadda.bwmeta.model.YElement;
import pl.edu.icm.yadda.bwmeta.model.YStructure;
import pl.edu.icm.yadda.process.ctx.ProcessContext;
import pl.edu.icm.yadda.process.model.EnrichedPayload;
import pl.edu.icm.yadda.process.node.IProcessingNode;
import pl.edu.icm.yadda.service2.catalog.ICatalogFacade;
import pl.edu.icm.yadda.service2.catalog.impl.CatalogFacade;

/**
* Node which processes yElement in order to find data that will help to
* disambiguate journals.
*
* @author Michal Siemionczyk michsiem@icm.edu.pl
*/
public class JournalDisambProcessingNode implements IProcessingNode<EnrichedPayload<YElement>[], JournalMetaData> {

	protected final Logger log = LoggerFactory.getLogger(this.getClass());
	
	private ICatalogFacade catalogFacade;		

//	private String remoteDescriptorUrl;
	
   public void setCatalogFacade(ICatalogFacade<String> catalogFacade) {
		this.catalogFacade = catalogFacade;
	}
   
//	public void setRemoteDescriptorUrl(String descriptorUrl){
//		this.remoteDescriptorUrl = descriptorUrl;
//	}



@Override
   public JournalDisambiguationMeta process(EnrichedPayload<YElement>[] input, ProcessContext ctx){
	   
//		//TODO do it more properly, with init()methods or so
//		if(this.catalogFacade != null && this.remoteDescriptorUrl != null)
//			new BeanInitializationException("Only one propersty should be set!");
//		else if(this.catalogFacade == null && this.remoteDescriptorUrl == null)
//			new BeanInitializationException("Setting one property is mandatory!");
//		
//        if(this.remoteDescriptorUrl != null){
//        	ServiceDiscoverer discoverer=new ServiceDiscoverer(); 
//        	 try {
//				discoverer.setDescriptorUrl(this.remoteDescriptorUrl);
//	        	 discoverer.afterPropertiesSet();
//	           	 @SuppressWarnings("unchecked")
//				 CatalogFacade<String> impl = (CatalogFacade<String>)discoverer.getService("catalog");
//	        	 this.catalogFacade = impl;
//			} catch (MalformedURLException e) {
//				log.error(e.toString());
//			} catch (Exception e) {
//				log.error(e.toString());
//			}
//        }
	   
	   ArrayList<YElement> yElemPayload = new ArrayList<YElement>();
//	   if(input.length > 0 && input[0] != null && input[0].getObject() != null)
//		   yElemPayload.add((input[0]).getObject());
	   
	   for(EnrichedPayload<YElement> enrichedP : input)
		   if(enrichedP.getObject() != null)
			   yElemPayload.add(enrichedP.getObject());
		   
	   JournalDisambiguationParser parser = new JournalDisambiguationParser(yElemPayload);
	   parser.setCatalogFacade(catalogFacade);
	   parser.parse();
	   
	   return parser.getParsedData();	
	   
	
   	}
}

