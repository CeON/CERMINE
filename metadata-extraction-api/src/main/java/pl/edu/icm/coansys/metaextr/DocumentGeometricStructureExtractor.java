package pl.edu.icm.coansys.metaextr;

import java.io.InputStream;
import pl.edu.icm.coansys.metaextr.exception.AnalysisException;
import pl.edu.icm.coansys.metaextr.structure.model.BxDocument;

/**
 * @author Dominika Tkaczyk
 */
public interface DocumentGeometricStructureExtractor {
	
	public BxDocument extractStructure(InputStream stream) throws AnalysisException;
}
