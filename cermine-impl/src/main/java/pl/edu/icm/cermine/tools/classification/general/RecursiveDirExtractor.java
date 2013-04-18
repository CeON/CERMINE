package pl.edu.icm.cermine.tools.classification.general;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.FileUtils;
import pl.edu.icm.cermine.exception.TransformationException;
import pl.edu.icm.cermine.structure.model.BxDocument;
import pl.edu.icm.cermine.structure.model.BxPage;
import pl.edu.icm.cermine.structure.transformers.TrueVizToBxDocumentReader;

public class RecursiveDirExtractor implements DocumentsExtractor {

    protected File directory;

    public RecursiveDirExtractor(String path) {
        directory = new File(path);
        if (!directory.exists() || !directory.isDirectory()) {
            throw new RuntimeException("Source directory for documents doesn't exist: " + path);
        }
    }

    public RecursiveDirExtractor(File directory) {
        this.directory = directory;
    }

    @Override
    public List<BxDocument> getDocuments() throws TransformationException {
        TrueVizToBxDocumentReader tvReader = new TrueVizToBxDocumentReader();
        List<BxDocument> documents = new ArrayList<BxDocument>();

        for (File file : FileUtils.listFiles(directory, new String[]{"xml"}, true)) {
            InputStream is = null;
            try {
                is = new FileInputStream(file);
                List<BxPage> pages = tvReader.read(new InputStreamReader(is));
                BxDocument doc = new BxDocument();
                doc.setFilename(file.getName());
                doc.setPages(pages);
                documents.add(doc);
            } catch (FileNotFoundException ex) {
                throw new TransformationException(ex);
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException ex) {
                        throw new TransformationException("Cannot close stream!", ex);
                    }
                }
            }
        }

        return documents;
    }

}
