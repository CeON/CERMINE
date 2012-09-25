package pl.edu.icm.coansys.metaextr.textr;

import pl.edu.icm.coansys.metaextr.textr.CharacterExtractor;
import java.io.InputStream;
import pl.edu.icm.coansys.metaextr.AnalysisException;
import pl.edu.icm.coansys.metaextr.textr.model.BxDocument;

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
