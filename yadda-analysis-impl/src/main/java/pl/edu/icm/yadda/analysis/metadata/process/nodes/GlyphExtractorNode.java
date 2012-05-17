package pl.edu.icm.yadda.analysis.metadata.process.nodes;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.icm.yadda.analysis.textr.GlyphExtractor;
import pl.edu.icm.yadda.analysis.textr.model.BxDocument;
import pl.edu.icm.yadda.process.ctx.ProcessContext;
import pl.edu.icm.yadda.process.model.EnrichedPayload;
import pl.edu.icm.yadda.process.node.IProcessingNode;

/**
 * Glyph extractor node.
 *
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class GlyphExtractorNode implements IProcessingNode<EnrichedPayload<File>, EnrichedPayload<BxDocument>> {

    private static final Logger log = LoggerFactory.getLogger(GlyphExtractorNode.class);

    private GlyphExtractor glyphExtractor;

    @Override
    public EnrichedPayload<BxDocument> process(EnrichedPayload<File> input, ProcessContext ctx) throws Exception {

        InputStream is = new FileInputStream(input.getObject());
        try {
            BxDocument doc = glyphExtractor.extractGlyphs(is);
            String id = FilenameUtils.getBaseName(input.getObject().getName());
            log.info("Glyphs extracted from " + id);
            return new EnrichedPayload(id, doc, input.getCollections(), input.getLicenses());
        } finally {
            is.close();
        }
    }

    public void setGlyphExtractor(GlyphExtractor glyphExtractor) {
        this.glyphExtractor = glyphExtractor;
    }

}
