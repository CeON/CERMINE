package pl.edu.icm.cermine.service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.*;
import org.junit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.icm.cermine.PdfNLMContentExtractor;

/**
 *
 * @author Aleksander Nowinski <a.nowinski@icm.edu.pl>
 */
public class CermineExtractorServiceImplTest {
    
    Logger log = LoggerFactory.getLogger(CermineExtractorServiceImplTest.class);
    public CermineExtractorServiceImplTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of extractNLM method, of class CermineExtractorServiceImpl.
     */
    @Test
    @Ignore
    public void testExtractNLM() throws Exception {
        System.out.println("extractNLM");
        InputStream is = this.getClass().getResourceAsStream("/pdf/test1.pdf");
        log.debug("Input stream is: {}", is);
        CermineExtractorServiceImpl instance = new CermineExtractorServiceImpl();
        instance.init();
        ExtractionResult result = instance.extractNLM(is);
        assertNotNull(result);
        assertTrue(result.isSucceeded());
    }

    
    boolean sleeping=true;

    /**
     * Test of obtainExtractor method, of class CermineExtractorServiceImpl.
     */
    @Test
    @Ignore
    public void testObtainExtractor() throws Exception{
        System.out.println("obtainExtractor");
        final CermineExtractorServiceImpl instance = new CermineExtractorServiceImpl();
        instance.setThreadPoolSize(3);
        instance.init();
        List<PdfNLMContentExtractor> list = new ArrayList<PdfNLMContentExtractor>();
        for(int i=0;i<3;i++) {
            list.add(instance.obtainExtractor());
        }
        sleeping=true;
        new Thread(new Runnable() {

            @Override
            public void run() {
                PdfNLMContentExtractor res = instance.obtainExtractor();
                sleeping=false;
            }
        }).start();
        assertTrue(sleeping);
        Thread.sleep(100);
        assertTrue(sleeping);
        instance.returnExtractor(list.remove(0));
        Thread.sleep(100);
        assertFalse(sleeping);
    }

    
}
