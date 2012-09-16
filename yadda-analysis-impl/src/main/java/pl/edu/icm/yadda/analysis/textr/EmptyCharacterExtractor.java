package pl.edu.icm.yadda.analysis.textr;

import java.io.InputStream;
import pl.edu.icm.yadda.analysis.AnalysisException;
import pl.edu.icm.yadda.analysis.textr.model.BxDocument;

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
