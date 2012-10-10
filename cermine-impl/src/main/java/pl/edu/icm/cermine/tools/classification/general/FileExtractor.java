package pl.edu.icm.cermine.tools.classification.general;

import java.io.InputStream;
import java.io.InputStreamReader;
import pl.edu.icm.cermine.exception.TransformationException;
import pl.edu.icm.cermine.structure.model.BxDocument;
import pl.edu.icm.cermine.structure.transformers.TrueVizToBxDocumentReader;

public class FileExtractor {
	private InputStream inputStream;

	public FileExtractor(InputStream is) {
		this.inputStream = is;
	}

	public BxDocument getDocument() throws TransformationException {
        InputStreamReader isr = new InputStreamReader(inputStream);
        
        TrueVizToBxDocumentReader reader = new TrueVizToBxDocumentReader();
        BxDocument document = new BxDocument().setPages(reader.read(isr));
        return document;
	}

}
