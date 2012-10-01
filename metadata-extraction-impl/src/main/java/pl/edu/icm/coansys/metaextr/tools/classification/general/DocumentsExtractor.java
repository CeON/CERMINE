package pl.edu.icm.coansys.metaextr.tools.classification.general;

import java.util.List;

import pl.edu.icm.coansys.metaextr.structure.model.BxDocument;

public interface DocumentsExtractor {
	List<BxDocument> getDocuments() throws Exception;
}
