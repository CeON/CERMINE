package pl.edu.icm.yadda.analysis.jrlsimilarity.nodes.processing;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;



import pl.edu.icm.yadda.bwmeta.model.YElement;
import pl.edu.icm.yadda.bwmeta.model.YExportable;
import pl.edu.icm.yadda.bwmeta.model.YStructure;
import pl.edu.icm.yadda.bwmeta.transformers.Bwmeta2_0ToYTransformer;
import pl.edu.icm.yadda.bwmeta.transformers.BwmetaTransformers;
import pl.edu.icm.yadda.imports.utils.YModelToolbox;
import pl.edu.icm.yadda.metadata.transformers.MetadataTransformerFactory;
import pl.edu.icm.yadda.metadata.transformers.TransformationException;
import pl.edu.icm.yadda.process.ctx.ProcessContext;
import pl.edu.icm.yadda.process.model.EnrichedPayload;
import pl.edu.icm.yadda.process.node.IProcessingNode;
import pl.edu.icm.yadda.service2.CatalogObject;
import pl.edu.icm.yadda.service2.CatalogObjectPart;
import pl.edu.icm.yadda.service2.CatalogParamConstants;

/**
* Node which processes yElement in order to find data that will help to
* disambiguate journals.
* Currently it takes care of extracting only BWMETA2 elements.
* @author Michal Siemionczyk michsiem@icm.edu.pl
*/
public class CatalogObjectToYModelProcessingNode implements IProcessingNode<CatalogObject<String>, EnrichedPayload<YElement>[]> {

	
	protected final Logger log = LoggerFactory.getLogger(this.getClass());
		
   @Override
   public EnrichedPayload<YElement>[] process(CatalogObject<String> input, ProcessContext ctx){
	   
//	   System.out.println("LOG INFO CATALOG OBJECT  " + input.getId().toString());
		Bwmeta2_0ToYTransformer transformer = new Bwmeta2_0ToYTransformer();
		
			ArrayList<YElement> yElemList = new ArrayList<YElement>();
			List<YExportable> yExportList = null;
//			String temp = "";
			try {
				for(CatalogObjectPart<String> part : input.getParts()){
					if(part.getType().equals(CatalogParamConstants.TYPE_BWMETA2)){
						yExportList = transformer.read(part.getData(), null);
						for(YExportable yElemExport : yExportList)
							if(yElemExport instanceof YElement){
								yElemList.add((YElement)yElemExport);
								if(((YElement)yElemExport).getIds() == null)
								log.info("CCCCCCCCCCCCC \n" + 
										((YElement)yElemExport).getStructures() +
										"\n" + ((YElement)yElemExport).getId());
							}
					}
				}
				
			} catch (TransformationException e) {
				System.out.println("LOG INFO Error during transfdsdsormation from BWMeta to Y-Model" + e);
			}

			@SuppressWarnings("unchecked")
			EnrichedPayload<YElement>[] enrichPay = new EnrichedPayload[yElemList.size()];
			
			if(yElemList.size() == 0){
				log.info("LOG INFO No YModel-elements read from BWMETA...");
			}else{
				
				for(int i = 0; i < yElemList.size(); i++)
					enrichPay[i] = new EnrichedPayload<YElement>(yElemList.get(i),null,null);
			}
			return enrichPay;
	
   	}



}

