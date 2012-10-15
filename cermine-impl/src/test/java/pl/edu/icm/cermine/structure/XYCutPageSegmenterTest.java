package pl.edu.icm.cermine.structure;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import javax.xml.parsers.ParserConfigurationException;
import static org.junit.Assert.*;
import org.junit.*;
import org.xml.sax.SAXException;
import pl.edu.icm.cermine.exception.TransformationException;
import pl.edu.icm.cermine.structure.model.*;
import pl.edu.icm.cermine.structure.tools.Direction;
import pl.edu.icm.cermine.structure.tools.Range;
import pl.edu.icm.cermine.structure.tools.Valley;
import pl.edu.icm.cermine.structure.transformers.MargToTextrImporter;

/**
 *
 * @author estocka
 */
public class XYCutPageSegmenterTest {

    XYCutSegmenter xy;
    List<BxChunk> generatedVertices;
    BxDocument sampleDocument;

    public XYCutPageSegmenterTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
        xy = new XYCutSegmenter();
        generatedVertices = generateVertices();
        sampleDocument = createSampleDocument(generatedVertices);

    }

    @After
    public void tearDown() {
    }

    @Test
    public void toRanges() {
        SortedSet<Range> rangesList = xy.toRanges(generatedVertices,
                Direction.X);
        assertEquals(56, rangesList.size());

    }

    @Test
    public void inRangeTest() {
        BxChunk bc = new BxChunk(new BxBounds(0.0, 0.0, 30, 30), "o");
        

        Range sr = new Range();
        sr.setRangeStart(0);
        sr.setRangeEnd(15);
        boolean inRange = sr.inRange(bc.getBounds().getY(),
                bc.getBounds().getY() + bc.getBounds().getHeight());

        assertTrue(inRange);
        assertEquals(0, Double.compare(30, sr.getRangeEnd()));
    }

    @Test
    public void toValleyTest() {
        SortedSet<Range> rangesYList = xy.toRanges(generatedVertices,
                Direction.Y);
        List<Valley> toValleys = xy.toValleys(rangesYList, Direction.Y);
        assertEquals(32, toValleys.size());

    }

    @Test
    public void maxValleyTest() {
        SortedSet<Range> rangesYList = xy.toRanges(generatedVertices,
                Direction.Y);
        List<Valley> toYValleys = xy.toValleys(rangesYList, Direction.Y);
        SortedSet<Range> rangesXList = xy.toRanges(generatedVertices,
                Direction.X);
        List<Valley> toXValleys = xy.toValleys(rangesXList, Direction.X);
        Valley maxValley = xy.maxValley(toXValleys, toYValleys);
        assertEquals(Direction.Y, maxValley.getDirection());
        assertEquals(0, Double.compare(330, maxValley.getValleyStart()));
        assertEquals(0, Double.compare(600, maxValley.getValleyEnd()));

    }

    @Test
    public void divideChunksTest() {

        SortedSet<Range> rangesYList = xy.toRanges(generatedVertices,
                Direction.Y);
        List<Valley> toYValleys = xy.toValleys(rangesYList, Direction.Y);
        SortedSet<Range> rangesXList = xy.toRanges(generatedVertices,
                Direction.X);
        List<Valley> toXValleys = xy.toValleys(rangesXList, Direction.X);
        Valley maxValley = xy.maxValley(toXValleys, toYValleys);
        List<List<BxChunk>> dividedChunks = xy.divideChunks(rangesYList, maxValley);
        assertEquals(280, dividedChunks.get(0).size());
        assertEquals(1400, dividedChunks.get(1).size());

    }

    @Test
    public void xySegmentationTest() {
        BxPage page = sampleDocument.getPages().get(0);
        xy.setPageHeight(page.getBounds().getHeight());
        List<BxZone> xySegmentation = xy.xySegmentation(page);
        assertEquals(5, xySegmentation.size());
    }

    @Test
    public void xyCutSegmentationTest() throws IOException,
            ParserConfigurationException, SAXException, TransformationException {
        BxPage page = new MargToTextrImporter().read(new InputStreamReader(this.getClass().getResourceAsStream("/pl/edu/icm/cermine/structure/001.xml"))).get(0);
        System.out.println(page.getZones().size());
        List<BxChunk> chunksList = new ArrayList<BxChunk>();

        for (BxZone zone : page.getZones()) {
            System.out.println(zone.getBounds().getX() + " " + zone.getBounds().getY() + " "
                    + zone.getBounds().getWidth() + " " + zone.getBounds().getHeight());
            for (BxLine line : zone.getLines()) {
                for (BxWord word : line.getWords()) {
                    chunksList.addAll(word.getChunks());
                }
            }
        }


        BxDocument doc = createSampleDocument(chunksList);
        BxDocument xyCutSegmentation = xy.segmentDocument(doc);
        List<BxZone> zones = xyCutSegmentation.getPages().get(0).getZones();
        System.out.println("Size " + zones.size());
        for (BxZone zone : zones) {
            System.out.println(zone.getBounds().getX() + " " + zone.getBounds().getY() + " "
                    + zone.getBounds().getWidth() + " " + zone.getBounds().getHeight());
        }
        assertEquals(4, zones.size());
    }

    List<BxChunk> generateVertices() {
        List<BxChunk> bcList = new ArrayList<BxChunk>();
      
        int max_width = 1450;
        int max_hight = 2000;
     


        for (int y = 50; y < 250; y = y + 50) {
            for (int x = 50; x < max_width; x = x + 25) {
                int h, w;
                double random = Math.random();
                if (random < 0.1) {
                    h = 30;
                } else {
                    h = 25;
                }
                w = 20;

                bcList.add(new BxChunk(new BxBounds(x, y, w, h), "s"));
            }

        }



        int y = 300;
        for (int x = 50; x < max_width; x = x + 25) {
            int h, w;
            double random = Math.random();
            if (random < 0.1) {
                h = 30;
            } else {
                h = 25;
            }
            w = 20;

            bcList.add(new BxChunk(new BxBounds(x, y, w, h), "s"));
        }


        for (y = 600; y < 1000; y = y + 50) {
            for (int x = 50; x < 750; x = x + 25) {
                int h, w;
                double random = Math.random();
                if (random < 0.1) {
                    h = 30;
                } else {
                    h = 25;
                }
                w = 20;

                bcList.add(new BxChunk(new BxBounds(x, y, w, h), "s"));
            }

        }
        for (y = 1100; y < max_hight; y = y + 50) {
            for (int x = 50; x < 750; x = x + 25) {
                int h, w;
                double random = Math.random();
                if (random < 0.1) {
                    h = 30;
                } else {
                    h = 25;
                }
                w = 20;

                bcList.add(new BxChunk(new BxBounds(x, y, w, h), "s"));
            }

        }
        for (y = 600; y < max_hight; y = y + 50) {
            for (int x = 850; x < max_width; x = x + 25) {
                int h, w;
                double random = Math.random();
                if (random < 0.1) {
                    h = 30;
                } else {
                    h = 25;
                }
                w = 20;

                bcList.add(new BxChunk(new BxBounds(x, y, w, h), "s"));
            }

        }

        return bcList;
    }

    BxDocument createSampleDocument(List<BxChunk> chunksList) {
        BxBounds bounds = xy.computeChunksListBounds(chunksList);


        BxDocument doc = new BxDocument();
        doc.addPage(new BxPage().setBounds(bounds).setChunks(chunksList));
        return doc;
    }
}
