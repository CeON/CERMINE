package pl.edu.icm.cermine.evaluation;

import java.io.File;
import java.util.List;

import pl.edu.icm.cermine.exception.TransformationException;
import pl.edu.icm.cermine.structure.model.BxDocument;
import pl.edu.icm.cermine.tools.classification.general.DirExtractor;
import pl.edu.icm.cermine.tools.classification.general.DocumentsExtractor;

public class EvaluationUtils {
    public static List<BxDocument> getDocumentsFromPath(String inputDirPath) throws TransformationException
	{
		if (inputDirPath == null) {
			throw new NullPointerException("Input directory must not be null.");
		}

		if (!inputDirPath.endsWith(File.separator)) {
			inputDirPath += File.separator;
		}
		DocumentsExtractor extractor = new DirExtractor(inputDirPath);
		
		List<BxDocument> evaluationDocuments;
		evaluationDocuments = extractor.getDocuments();
		return evaluationDocuments;
	}
   
}
