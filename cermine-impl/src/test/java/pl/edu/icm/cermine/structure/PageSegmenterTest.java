package pl.edu.icm.cermine.structure;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.zip.ZipException;
import javax.xml.parsers.ParserConfigurationException;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.exception.TransformationException;
import pl.edu.icm.cermine.structure.model.BxDocument;
import pl.edu.icm.cermine.structure.model.BxPage;
import pl.edu.icm.cermine.structure.model.BxZone;
import pl.edu.icm.cermine.structure.tools.UnsegmentedPagesFlattener;
import pl.edu.icm.cermine.structure.tools.UnsegmentedZonesFlattener;

/**
 *
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class PageSegmenterTest extends AbstractDocumentProcessorTest {

    protected static final double testSuccessPercentage = 0;
    protected static final double boundsTolerance = 20;
    static final String[] testResources = {"/pl/edu/icm/cermine/structure/001.xml"};
    static final String[] zipResources = {"/pl/edu/icm/cermine/structure/margSmallSample.zip"};

    @Before
    public void setUp() {
        this.startProcessFlattener = new UnsegmentedPagesFlattener();
        this.endProcessFlattener = new UnsegmentedZonesFlattener();
    }

    @Test
    public void xyCutFilesTest() throws IOException, ParserConfigurationException, SAXException, AnalysisException, TransformationException {
        testFiles(Arrays.asList(testResources), testSuccessPercentage);
    }

    @Test
    public void xyCutSampleFilesFromZipTest() throws IOException, ParserConfigurationException, SAXException,
            URISyntaxException, ZipException, AnalysisException, TransformationException {
        testSampleFilesFromZip(Arrays.asList(zipResources), testSuccessPercentage);
    }

    @Override
    protected BxDocument process(BxDocument doc) {
        return new XYCutPageSegmenter().segmentPages(doc);
    }

    @Override
    protected boolean compareDocuments(BxDocument testDoc, BxDocument expectedDoc) {
        if (testDoc.getPages().size() != expectedDoc.getPages().size()) {
            System.out.println("Different number of pages: " + testDoc.getPages().size()
                                                      + ", " + expectedDoc.getPages().size());
            return false;
        }
        for (int i = 0; i < testDoc.getPages().size(); i++) {
            BxPage testPage = testDoc.getPages().get(i);
            BxPage expectedPage = expectedDoc.getPages().get(i);

            if (!testPage.getChunks().isEmpty() || !expectedPage.getChunks().isEmpty()) {
            	System.out.println("Chunk is empty!");
                return false;
            }

            if (testPage.getZones().size() != expectedPage.getZones().size()) {
                System.out.println("Different number of zones: " + testPage.getZones().size()
                                                          + ", " + expectedPage.getZones().size());
                return false;
            }

            for (int j = 0; j < testPage.getZones().size(); j++) {
                BxZone testZone = testPage.getZones().get(j);
                BxZone expectedZone = expectedPage.getZones().get(j);

                if (!testZone.getBounds().isSimilarTo(expectedZone.getBounds(), boundsTolerance)) {
                    System.out.println("Different zone bounds: ");
                    System.out.println("   " + testZone.getBounds().getX() 
                                       + " " + testZone.getBounds().getY()
                                       + " " + testZone.getBounds().getWidth()
                                       + " " + testZone.getBounds().getHeight());
                    System.out.println("   " + expectedZone.getBounds().getX() 
                                       + " " + expectedZone.getBounds().getY()
                                       + " " + expectedZone.getBounds().getWidth()
                                       + " " + expectedZone.getBounds().getHeight());
                    return false;
                }

                if (testZone.getChunks().size() != expectedZone.getChunks().size()) {
                    System.out.println("Different number of zone chunks: " + testZone.getChunks().size()
                                                                    + ", " + expectedZone.getChunks().size());
                    return false;
                }
            }
        }

        System.out.println("Documents are similar");
        return true;
    }

}
