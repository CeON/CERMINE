package pl.edu.icm.yadda.analysis.metadata.process.nodes;

import pl.edu.icm.yadda.process.model.EnrichedPayload;
import pl.edu.icm.yadda.process.ctx.ProcessContext;
import pl.edu.icm.yadda.analysis.AnalysisException;
import pl.edu.icm.yadda.analysis.textr.model.BxPage;
import pl.edu.icm.yadda.analysis.textr.model.BxDocument;
import pl.edu.icm.yadda.analysis.textr.PageSegmenter;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author krusek
 */
public class PageSegmenterNodeTest {

    private BxDocument inDoc = new BxDocument().addPage(new BxPage());
    private BxDocument outDoc = new BxDocument().addPage(new BxPage()).addPage(new BxPage());

    @Test
    public void testProcess() throws Exception {
        PageSegmenterNode node = new PageSegmenterNode();
        node.setPageSegmenter(new Segmenter());
        EnrichedPayload<BxDocument> in = new EnrichedPayload<BxDocument>("2", inDoc, null, null);
        assertEquals(outDoc, node.process(in, new ProcessContext("1")).getObject());
    }

    private class Segmenter implements PageSegmenter {

        @Override
        public BxDocument segmentPages(BxDocument document) throws AnalysisException {
            assertEquals(inDoc, document);
            return outDoc;
        }

    }
}
