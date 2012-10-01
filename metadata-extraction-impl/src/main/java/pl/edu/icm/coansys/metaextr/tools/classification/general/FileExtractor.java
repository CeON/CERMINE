package pl.edu.icm.coansys.metaextr.tools.classification.general;

import java.io.InputStream;
import java.io.InputStreamReader;
import pl.edu.icm.coansys.metaextr.structure.model.BxDocument;
import pl.edu.icm.coansys.metaextr.structure.transformers.TrueVizToBxDocumentReader;

public class FileExtractor {
	private InputStream inputStream;

	public FileExtractor(InputStream is) {
		this.inputStream = is;
	}

	public BxDocument getDocument() throws Exception {
        InputStreamReader isr = new InputStreamReader(inputStream);
        
        TrueVizToBxDocumentReader reader = new TrueVizToBxDocumentReader();
        BxDocument document = new BxDocument().setPages(reader.read(isr));
        return document;
	}

}
