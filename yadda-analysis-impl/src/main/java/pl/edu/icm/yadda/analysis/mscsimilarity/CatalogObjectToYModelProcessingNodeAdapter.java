package pl.edu.icm.yadda.analysis.mscsimilarity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.edu.icm.yadda.analysis.jrlsimilarity.nodes.processing.CatalogObjectToYModelProcessingNode;
import pl.edu.icm.yadda.bwmeta.model.YElement;
import pl.edu.icm.yadda.process.ctx.ProcessContext;
import pl.edu.icm.yadda.process.model.EnrichedPayload;
import pl.edu.icm.yadda.service2.CatalogObject;
import pl.edu.icm.yadda.service2.CatalogObjectPart;
import pl.edu.icm.yadda.service2.CatalogParamConstants;

/**
 * Extends functionality CatalogObjectToYModelProcessingNode with additional
 * logging.
 * 
 * @author tkusm
 * 
 */
public class CatalogObjectToYModelProcessingNodeAdapter extends CatalogObjectToYModelProcessingNode {

    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public EnrichedPayload<YElement>[] process(CatalogObject<String> input, ProcessContext ctx) {
        for (CatalogObjectPart<String> part : input.getParts()) {
            if (part.getType().equals(CatalogParamConstants.TYPE_BWMETA2)) {
                log.info("data: \n {}", part.getData());
            }
        }

        return super.process(input, ctx);
    }
}
