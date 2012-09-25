package pl.edu.icm.coansys.metaextr.textr;

import java.io.InputStream;
import pl.edu.icm.coansys.metaextr.AnalysisException;
import pl.edu.icm.coansys.metaextr.textr.model.BxDocument;

/**
 * Extracting individual characters along with their bounding boxes from a PDF file. 
 * 
 * @author Dominika Tkaczyk
 */
public interface CharacterExtractor {
	
	public BxDocument extractCharacters(InputStream stream) throws AnalysisException;
}
