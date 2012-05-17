package pl.edu.icm.yadda.analysis.zentralblattimporter.nodes;

import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.edu.icm.yadda.bwmeta.model.YElement;
import pl.edu.icm.yadda.bwmeta.model.YExportable;
import pl.edu.icm.yadda.bwmeta.transformers.YToBwmeta2_0Transformer;
import pl.edu.icm.yadda.metadata.transformers.TransformationException;
import pl.edu.icm.yadda.process.ctx.ProcessContext;
import pl.edu.icm.yadda.process.node.IProcessingNode;
import pl.edu.icm.yadda.service2.CatalogObject;
import pl.edu.icm.yadda.service2.CatalogObjectPart;
import pl.edu.icm.yadda.service2.CatalogParamConstants;
import pl.edu.icm.yadda.service2.YaddaObjectID;

/**
 * Processing node that takes YElements and converts to CatalogObjects.
 * 
 * @author tkusm
 *
 */
public class YModelToCatalogObjectProcessingNode implements IProcessingNode<YElement, CatalogObject<String>> {

	protected final Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Override
	public CatalogObject<String> process(YElement yelement, ProcessContext ctx)
			throws Exception {
		synchronized (this) {
			log.info("[process] yelement={}", yelement);
			
			String bwmetaStr = yElementToBwMetaString(yelement);
			log.info("[process] bwmetaStr={}", bwmetaStr);
			
			YaddaObjectID id = new YaddaObjectID(yelement.getId());
			CatalogObject<String> co = new  CatalogObject<String>(id);
			CatalogObjectPart<String> part = new CatalogObjectPart<String>(CatalogParamConstants.TYPE_BWMETA2, bwmetaStr);
			co.addPart(part);			
			log.info("[process] co={}", co);
			
			return co;
		}
	}

	/**
	 * Converts YElement to BwMeta stored as a string.
	 * 
	 * @param yelement
	 * @return
	 * @throws TransformationException
	 */
	private static String yElementToBwMetaString(YElement yelement)
			throws TransformationException {
		YToBwmeta2_0Transformer transformer = new YToBwmeta2_0Transformer();
		List<YExportable> yelements = new LinkedList<YExportable>();
		yelements.add(yelement);				
		String bwmetaStr = transformer.write(yelements);
		return bwmetaStr;
	}


}
