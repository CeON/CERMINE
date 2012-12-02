package pl.edu.icm.cermine.content;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import org.jdom.JDOMException;
import org.junit.Before;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.exception.TransformationException;

/**
 *
 * @author Dominika Tkaczyk
 */
public class SVMLogicalStructureExtractorTest extends AbstractLogicalStructureExtractorTest {
    
    String filteringModel = "filtering.model";
    String filteringRange = "filtering.range";
    String headerModel = "header.model";
    String headerRange = "header.range";
    
       
    @Before
    @Override
    public void setUp() throws IOException, TransformationException, AnalysisException, URISyntaxException, JDOMException {
        super.setUp();
        BufferedReader br1 = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream(dir+filteringModel)));
        BufferedReader br2 = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream(dir+filteringRange)));
        BufferedReader br3 = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream(dir+headerModel)));
        BufferedReader br4 = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream(dir+headerRange)));

        extractor = new SVMLogicalStructureExtractor(br1, br2, br3, br4);
    }
    
}