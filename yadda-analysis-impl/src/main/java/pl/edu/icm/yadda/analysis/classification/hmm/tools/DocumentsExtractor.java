package pl.edu.icm.yadda.analysis.classification.hmm.tools;

import java.util.List;

import pl.edu.icm.yadda.analysis.textr.model.BxDocument;

public interface DocumentsExtractor {
	List<BxDocument> getDocuments() throws Exception;
}
