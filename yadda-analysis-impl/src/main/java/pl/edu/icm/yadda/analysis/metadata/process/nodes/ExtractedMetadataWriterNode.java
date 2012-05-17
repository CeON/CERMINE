package pl.edu.icm.yadda.analysis.metadata.process.nodes;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.icm.yadda.bwmeta.model.YElement;
import pl.edu.icm.yadda.bwmeta.model.YExportable;
import pl.edu.icm.yadda.metadata.transformers.IMetadataWriter;
import pl.edu.icm.yadda.process.ctx.ProcessContext;
import pl.edu.icm.yadda.process.model.EnrichedPayload;
import pl.edu.icm.yadda.process.node.IWriterNode;

/**
 * Extracted metadata writer node.
 *
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class ExtractedMetadataWriterNode implements IWriterNode<EnrichedPayload<YElement>> {

    private static final Logger log = LoggerFactory.getLogger(ExtractedMetadataWriterNode.class);

    public static final String AUX_PARAM_TARGET_DIR = "target_dir";

    private String targetDir = "/tmp/bwmeta/";

    private IMetadataWriter<YExportable> writer;

    @Override
    public void store(EnrichedPayload<YElement> data, ProcessContext ctx) throws Exception {
        String targetDirPath;
        if (ctx.containsAuxParam(AUX_PARAM_TARGET_DIR)) {
            targetDirPath = (String)ctx.getAuxParam(AUX_PARAM_TARGET_DIR);
        } else {
            targetDirPath = targetDir;
	}

        String filePath = targetDirPath + File.separator + data.getId() + ".xml";

        File file = new File(filePath);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            file.createNewFile();
        }
        Writer w = new FileWriter(filePath);
        writer.write(w, Arrays.asList((YExportable)data.getObject()));
        w.close();
        log.info("Document " + data.getId() + " stored.");
    }

    public void setWriter(IMetadataWriter<YExportable> writer) {
        this.writer = writer;
    }

}
