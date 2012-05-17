package pl.edu.icm.yadda.analysis.metadata.process.nodes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.icm.yadda.analysis.textr.PageSegmenter;
import pl.edu.icm.yadda.analysis.textr.model.BxDocument;
import pl.edu.icm.yadda.process.ctx.ProcessContext;
import pl.edu.icm.yadda.process.model.EnrichedPayload;
import pl.edu.icm.yadda.process.node.IProcessingNode;

/**
 * Page segmenter node.
 *
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class PageSegmenterNode implements IProcessingNode<EnrichedPayload<BxDocument>, EnrichedPayload<BxDocument>> {

    private static final Logger log = LoggerFactory.getLogger(PageSegmenterNode.class);

    private PageSegmenter pageSegmenter;

    @Override
    public EnrichedPayload<BxDocument> process(EnrichedPayload<BxDocument> input, ProcessContext ctx) throws Exception {
        EnrichedPayload<BxDocument> payload = new EnrichedPayload<BxDocument>(input.getId(),
                pageSegmenter.segmentPages(input.getObject()), input.getCollections(), input.getLicenses());
        log.info("Pages segmented in " + payload.getId());
        return payload;
    }

    public PageSegmenter getPageSegmenter() {
        return pageSegmenter;
    }

    public void setPageSegmenter(PageSegmenter pageSegmenter) {
        this.pageSegmenter = pageSegmenter;
    }
}
