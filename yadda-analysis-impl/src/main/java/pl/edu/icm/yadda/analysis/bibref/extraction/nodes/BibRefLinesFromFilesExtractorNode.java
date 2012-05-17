package pl.edu.icm.yadda.analysis.bibref.extraction.nodes;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.icm.yadda.analysis.bibref.extraction.model.BxDocumentBibReferences;
import pl.edu.icm.yadda.analysis.bibref.extraction.tools.BibRefExtractionUtils;
import pl.edu.icm.yadda.analysis.textr.model.BxDocument;
import pl.edu.icm.yadda.analysis.textr.model.BxPage;
import pl.edu.icm.yadda.analysis.textr.transformers.TrueVizToBxDocumentReader;
import pl.edu.icm.yadda.metadata.transformers.IMetadataReader;
import pl.edu.icm.yadda.process.ctx.ProcessContext;
import pl.edu.icm.yadda.process.node.IProcessingNode;

/**
 * Bibliographic references' lines extractor node. The node extracts
 * references' lines from TrueViz documents and corresponding txt files
 * containing document's citations (one per line).
 *
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class BibRefLinesFromFilesExtractorNode implements IProcessingNode<File[], BxDocumentBibReferences[]> {

    Logger log = LoggerFactory.getLogger(BibRefLinesFromFilesExtractorNode.class);

    private IMetadataReader<BxPage> reader = new TrueVizToBxDocumentReader();

    @Override
    public BxDocumentBibReferences[] process(File[] input, ProcessContext ctx) throws Exception {
        List<BxDocumentBibReferences> bibrefLinesList = new ArrayList<BxDocumentBibReferences>();

        List<File> inputFiles = Arrays.asList(input);
        Collections.sort(inputFiles, new Comparator<File>() {

            @Override
            public int compare(File f1, File f2) {
                return f1.getName().compareTo(f2.getName());
            }

        });

        File txtFile = null;
        for (File file : inputFiles) {
            if ("txt".equals(FilenameUtils.getExtension(file.getName()))) {
                txtFile = file;
            }
            if ("xml".equals(FilenameUtils.getExtension(file.getName()))) {
                if (FilenameUtils.removeExtension(txtFile.getName())
                        .equals(FilenameUtils.removeExtension(file.getName()))) {
                    BxDocument document = new BxDocument().setPages(reader.read(new FileReader(file)));
                    List<String> extractedLines = FileUtils.readLines(txtFile);
                    bibrefLinesList.add(BibRefExtractionUtils.extractBibRefLines(document, extractedLines));
                }
            }
        }

        return bibrefLinesList.toArray(new BxDocumentBibReferences[]{});
    }

}
