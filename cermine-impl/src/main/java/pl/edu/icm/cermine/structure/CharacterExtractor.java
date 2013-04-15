package pl.edu.icm.cermine.structure;

import java.io.InputStream;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.structure.model.BxDocument;

/**
 * Interface for extracting individual characters along with their bounding boxes from a file. 
 * 
 * @author Dominika Tkaczyk
 */
public interface CharacterExtractor {
	
    /**
     * Extracts characters from the file.
     * 
     * @param stream
     * @return a document containing pages with individual characters.
     * @throws AnalysisException 
     */
	BxDocument extractCharacters(InputStream stream) throws AnalysisException;
}
