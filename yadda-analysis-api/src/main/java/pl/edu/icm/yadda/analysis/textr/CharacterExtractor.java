package pl.edu.icm.yadda.analysis.textr;

import java.io.InputStream;
import pl.edu.icm.yadda.analysis.AnalysisException;
import pl.edu.icm.yadda.analysis.textr.model.BxDocument;

/**
 * Extracting individual characters along with their bounding boxes from a PDF file. 
 * 
 * @author Dominika Tkaczyk
 */
public interface CharacterExtractor {
	
	public BxDocument extractCharacters(InputStream stream) throws AnalysisException;
}
