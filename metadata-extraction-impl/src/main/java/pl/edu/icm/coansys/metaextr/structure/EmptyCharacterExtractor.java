package pl.edu.icm.coansys.metaextr.structure;

import pl.edu.icm.coansys.metaextr.structure.CharacterExtractor;
import java.io.InputStream;
import pl.edu.icm.coansys.metaextr.exception.AnalysisException;
import pl.edu.icm.coansys.metaextr.structure.model.BxDocument;

/**
 *
 * @author Dominika Tkaczyk
 */
public class EmptyCharacterExtractor implements CharacterExtractor {

    @Override
    public BxDocument extractCharacters(InputStream stream) throws AnalysisException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
