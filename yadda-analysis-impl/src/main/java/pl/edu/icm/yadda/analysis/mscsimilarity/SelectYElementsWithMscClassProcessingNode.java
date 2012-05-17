package pl.edu.icm.yadda.analysis.mscsimilarity;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.edu.icm.yadda.bwmeta.model.YCategoryRef;
import pl.edu.icm.yadda.bwmeta.model.YConstants;
import pl.edu.icm.yadda.bwmeta.model.YElement;
import pl.edu.icm.yadda.process.ctx.ProcessContext;
import pl.edu.icm.yadda.process.model.EnrichedPayload;
import pl.edu.icm.yadda.process.node.IProcessingNode;


/**
 * Takes collection of yelements and selects these having MSc classification.
 * 
 * @author tkusm
 *
 */
public class SelectYElementsWithMscClassProcessingNode implements IProcessingNode<EnrichedPayload<YElement>[], List<YElement> > {

	protected final Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Override
	public List<YElement> process(EnrichedPayload<YElement>[] input,
			ProcessContext ctx) throws Exception {
	
		List<YElement> selected = new ArrayList<YElement>();
		
		for (EnrichedPayload<YElement> e: input) {
			YElement yelement = e.getObject();
			List<YCategoryRef> refs = yelement.getCategoryRefs();
			//check if MSc classification is available: if yes then select
			for (YCategoryRef ref: refs) {
				if (ref.getClassification().equals(YConstants.EXT_CLASSIFICATION_MSC)) {
					selected.add(yelement);
					break;
				}
			}
		}
		
		log.info("Selected {} out of {}. ", selected.size(), input.length);
		return selected;
	}

}
