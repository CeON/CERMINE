package pl.edu.icm.coansys.metaextr.classification.tools;

import java.util.List;

import pl.edu.icm.coansys.metaextr.textr.model.BxDocument;

public interface DocumentsExtractor {
	List<BxDocument> getDocuments() throws Exception;
}
