package pl.edu.icm.cermine.bibref;

import org.junit.Before;
import pl.edu.icm.cermine.bibref.model.BibEntry;
import pl.edu.icm.cermine.exception.AnalysisException;

/**
 *
 * @author Dominika Tkaczyk
 */
public class CRFBibReferenceParserTest extends AbstractBibReferenceParserTest {
    
    protected static final String modelFile = "/pl/edu/icm/cermine/bibref/acrf-small.ser.gz";
    
    private double minPercentage = 0.9;
    
    private CRFBibReferenceParser parser;
    
    @Before
    public void setUp() throws AnalysisException {
        parser = new CRFBibReferenceParser(this.getClass().getResourceAsStream(modelFile));
    }

    @Override
    protected BibReferenceParser<BibEntry> getParser() {
        return parser;
    }

    @Override
    protected double getMinPercentage() {
        return minPercentage;
    }
}
