package pl.edu.icm.yadda.analysis.metadata.process.nodes;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.icm.yadda.analysis.textr.model.BxDocument;
import pl.edu.icm.yadda.analysis.textr.model.BxPage;
import pl.edu.icm.yadda.metadata.transformers.IMetadataWriter;
import pl.edu.icm.yadda.process.ctx.ProcessContext;
import pl.edu.icm.yadda.process.model.EnrichedPayload;
import pl.edu.icm.yadda.process.node.IWriterNode;

/**
 *
 * @author domin
 */
public class BxDocumentWriterNode implements IWriterNode<EnrichedPayload<BxDocument>> {

    private static final Logger log = LoggerFactory.getLogger(BxDocumentWriterNode.class);

    public static final String AUX_PARAM_TARGET_DIR = "target_dir";

    private String targetDir = "/tmp/trueviz/";

    private IMetadataWriter<BxPage> writer;

    @Override
    public void store(EnrichedPayload<BxDocument> input, ProcessContext ctx) throws Exception {
        String targetDirPath;
        if (ctx.containsAuxParam(AUX_PARAM_TARGET_DIR)) {
            targetDirPath = (String)ctx.getAuxParam(AUX_PARAM_TARGET_DIR);
        } else {
            targetDirPath = targetDir;
	}

        String filePath = targetDirPath + File.separator + input.getId() + ".xml";

        File file = new File(filePath);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            file.createNewFile();
        }
        Writer w = new FileWriter(filePath);
        writer.write(w, input.getObject().getPages());
        w.close();
        log.info("Document " + input.getId() + " stored.");
    }

    public void setTargetDir(String targetDir) {
        this.targetDir = targetDir;
    }

    public void setWriter(IMetadataWriter<BxPage> writer) {
        this.writer = writer;
    }

}
