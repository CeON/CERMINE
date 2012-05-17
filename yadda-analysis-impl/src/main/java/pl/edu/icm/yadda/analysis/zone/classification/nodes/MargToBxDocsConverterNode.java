package pl.edu.icm.yadda.analysis.zone.classification.nodes;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import pl.edu.icm.yadda.analysis.textr.model.BxDocument;
import pl.edu.icm.yadda.analysis.textr.model.BxPage;
import pl.edu.icm.yadda.analysis.textr.transformers.TrueVizToBxDocumentReader;
import pl.edu.icm.yadda.metadata.transformers.IMetadataReader;
import pl.edu.icm.yadda.process.ctx.ProcessContext;
import pl.edu.icm.yadda.process.node.IProcessingNode;


/**
 * MARG files to BxDocument converter node.
 *
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class MargToBxDocsConverterNode implements IProcessingNode<File[], BxDocument[]> {

    @Override
    public BxDocument[] process(File[] input, ProcessContext ctx) throws Exception {
        List<BxDocument> docs = new ArrayList<BxDocument>();
        IMetadataReader<BxPage> reader = new TrueVizToBxDocumentReader();
        for (File file : input) {
            docs.add(new BxDocument().setPages(reader.read(new FileReader(file))));
        }
        return docs.toArray(new BxDocument[] {});
    }

}
