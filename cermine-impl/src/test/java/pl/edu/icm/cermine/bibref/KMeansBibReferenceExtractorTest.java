package pl.edu.icm.cermine.bibref;

import org.junit.Before;

/**
 *
 * @author Dominika Tkaczyk
 */
public class KMeansBibReferenceExtractorTest extends AbstractBibReferenceExtractorTest {

    private BibReferenceExtractor extractor;
    
    @Before
    @Override
    public void setUp() {
        super.setUp();
        extractor = new KMeansBibReferenceExtractor();
    }

    @Override
    protected BibReferenceExtractor getExtractor() {
       return extractor;
    }
    
}
