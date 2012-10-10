package pl.edu.icm.cermine.tools.classification.general;

import java.util.List;
import pl.edu.icm.cermine.exception.TransformationException;
import pl.edu.icm.cermine.structure.model.BxDocument;

public interface DocumentsExtractor {
    
	List<BxDocument> getDocuments() throws TransformationException;
    
}
