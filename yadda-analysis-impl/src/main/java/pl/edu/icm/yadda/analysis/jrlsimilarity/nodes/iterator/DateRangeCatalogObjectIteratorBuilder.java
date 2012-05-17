package pl.edu.icm.yadda.analysis.jrlsimilarity.nodes.iterator;

import java.util.Date;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.edu.icm.yadda.process.ctx.ProcessContext;
import pl.edu.icm.yadda.process.harvester.Constants;
import pl.edu.icm.yadda.process.iterator.IIdExtractor;
import pl.edu.icm.yadda.process.iterator.ISourceIterator;
import pl.edu.icm.yadda.process.iterator.ISourceIteratorBuilder;
import pl.edu.icm.yadda.service2.CatalogObject;
import pl.edu.icm.yadda.service2.CatalogObjectMeta;
import pl.edu.icm.yadda.service2.CatalogParamConstants;
import pl.edu.icm.yadda.service2.catalog.CatalogException;
import pl.edu.icm.yadda.service2.catalog.CountingIterator;
import pl.edu.icm.yadda.service2.catalog.ICatalogFacade;
/**
 * 
 * @author Michał Siemiończyk michsiem@icm.edu.pl
 *
 */
public class DateRangeCatalogObjectIteratorBuilder implements ISourceIteratorBuilder<CatalogObject<String>> {

    public static final String AUX_PARAM_FROM_DATE = "FROM_DATE";
    public static final String AUX_PARAM_TO_DATE = "TO_DATE";
    public static final String AUX_PARAM_META_TO_BE_FETCHED = "META_TO_BE_FETCHED";
    
	private ICatalogFacade<String> catalogFacade;
	
//	private String remoteDescriptorUrl;
	
//	protected Map<String, Serializer> serializers;

	protected final Logger log = LoggerFactory.getLogger(this.getClass());
	
//	private int iter = 0;

	
	@Override
	public ISourceIterator<CatalogObject<String>> build(ProcessContext ctx) throws Exception {
		
        Date fromDate = (Date) ctx.getAuxParam(AUX_PARAM_FROM_DATE);
        Date toDate = (Date) ctx.getAuxParam(AUX_PARAM_TO_DATE);
		String[] tags = (String[])(ctx.getAuxParam(Constants.PARAM_TAGS));
		
//		//TODO do it more proporly, with init()methods or so
//		if(this.catalogFacade != null && this.remoteDescriptorUrl != null)
//			new BeanInitializationException("Only one propersty should be set!");
//		else if(this.catalogFacade == null && this.remoteDescriptorUrl == null)
//			new BeanInitializationException("Setting one property is mandatory!");
		
        if (toDate == null) {
            toDate = new Date();
        }
        
        //Setting remote
//        if(this.remoteDescriptorUrl != null){
//        	ServiceDiscoverer discoverer=new ServiceDiscoverer(); 
//        	 discoverer.setDescriptorUrl(this.remoteDescriptorUrl);
//        	 discoverer.afterPropertiesSet();
//        	 @SuppressWarnings("unchecked")
//			 CatalogFacade<String> impl = (CatalogFacade<String>)discoverer.getService("catalog");
//        	 this.catalogFacade = impl;
//        }
        
		final Iterator<CatalogObjectMeta> it = catalogFacade.iterateObjects(
				new String[]{CatalogParamConstants.TYPE_BWMETA2},
				fromDate, toDate, tags, false);
		
		return new ISourceIterator<CatalogObject<String>>() {

			
			@Override
			public boolean hasNext() {
				return it.hasNext();
			}

			@Override
			public CatalogObject<String> next() {
				CatalogObject<String> nextObject = null;
				while(it.hasNext()){
					CatalogObjectMeta meta = it.next();
					
					if (meta.getStatus().isDeleted()) continue;
					try {
//						CatalogObjectPart<String> bwmetaPart = catalogFacade.getPart(meta.getId(), 
//								CatalogParamConstants.TYPE_BWMETA1, null);
//						nextObject = bwmetaPart.getData();
//						nextObject = catalogFacade.getObject(meta.getId());
						nextObject = catalogFacade.getObject(meta.getId());
					} catch (CatalogException e) {
						throw new RuntimeException("CatalogException while retrieving next bwmeta from catalog", e);
					}
//					log.info("BWMETA READED "  + nextObject.get);
					break;
				}
//				Bwmeta2_0ToYTransformer transformer = new Bwmeta2_0ToYTransformer();
//			
//				List<YExportable> yExportList = null;
//				try {
//					String temp = "";
//					for(CatalogObjectPart<String> part : nextObject.getParts()){
//						temp += part.getData();
//					}
//					log.info("BWMETA all:" + temp);
//					yExportList = transformer.read(temp, null);
//				} catch (TransformationException e) {
//					log.info("Error during transformation from BWMeta to Y-Model" + e);
//					
//				}
//			
//				if(yExportList == null){
//					log.info("No YModel-elements read from BWMETA...");
//					return null;
//				}	
				return nextObject;
			}

			@Override
			public void remove() {
				// TODO Auto-generated method stub
				
			}

			@Override
			public int getEstimatedSize() throws UnsupportedOperationException {
		          if (it instanceof CountingIterator<?>) {
	                    return ((CountingIterator<CatalogObjectMeta>) it).count();

	                } else {
	                    throw new UnsupportedOperationException("getting estimated size is unsupported");
	                }
			}

			@Override
			public void clean() {
				// TODO Auto-generated method stub
				
			}
		};
		
	}

	@Override
	public IIdExtractor<CatalogObject<String>> getIdExtractor() {
		return new IIdExtractor<CatalogObject<String>>() {

			@Override
			public String getId(CatalogObject<String> element) {
				return element.getId().getId();
			}
		};
	}

	public void setCatalogFacade(ICatalogFacade<String> catalogFacade) {
		this.catalogFacade = catalogFacade;
	}

//	public void setRemoteDescriptorUrl(String descriptorUrl){
//		this.remoteDescriptorUrl = descriptorUrl;
//	}

}
