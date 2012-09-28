package pl.edu.icm.coansys.metaextr.tools.classification.tools;

import java.util.List;

import pl.edu.icm.coansys.metaextr.structure.model.BxDocument;

public interface DocumentsExtractor {
	List<BxDocument> getDocuments() throws Exception;
}
