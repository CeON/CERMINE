package pl.edu.icm.cermine.structure;

import java.io.InputStream;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.structure.model.BxDocument;

/**
 * Extracting individual characters along with their bounding boxes from a PDF file. 
 * 
 * @author Dominika Tkaczyk
 */
public interface CharacterExtractor {
	
	BxDocument extractCharacters(InputStream stream) throws AnalysisException;
}
