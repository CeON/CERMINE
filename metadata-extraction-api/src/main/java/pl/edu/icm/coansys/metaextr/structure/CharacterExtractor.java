package pl.edu.icm.coansys.metaextr.structure;

import java.io.InputStream;
import pl.edu.icm.coansys.metaextr.AnalysisException;
import pl.edu.icm.coansys.metaextr.structure.model.BxDocument;

/**
 * Extracting individual characters along with their bounding boxes from a PDF file. 
 * 
 * @author Dominika Tkaczyk
 */
public interface CharacterExtractor {
	
	public BxDocument extractCharacters(InputStream stream) throws AnalysisException;
}
