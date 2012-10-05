package pl.edu.icm.coansys.metaextr.bibref;

import java.io.InputStream;
import org.junit.Before;
import pl.edu.icm.coansys.metaextr.bibref.model.BibEntry;
import pl.edu.icm.coansys.metaextr.exception.AnalysisException;

/**
 *
 * @author Dominika Tkaczyk
 */
public class CRFBibReferenceParserTest extends AbstractBibReferenceParserTest {
    
    protected static final String modelFile = "/pl/edu/icm/coansys/metaextr/bibref/acrf-small.ser.gz";
    
    private double minPercentage = 0.9;
    
    private CRFBibReferenceParser parser;
    
    @Before
    public void setUp() throws AnalysisException {
        parser = new CRFBibReferenceParser();
        InputStream is = this.getClass().getResourceAsStream(modelFile);
        parser.setModel(is);
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
