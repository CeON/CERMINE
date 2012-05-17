package pl.edu.icm.yadda.analysis.textr;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;
import javax.xml.parsers.ParserConfigurationException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
//import org.apache.commons.math.*;
//import org.apache.commons.math.stat.Frequency;
//import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import pl.edu.icm.yadda.analysis.textr.transformers.MargToTextrImporter;
import pl.edu.icm.yadda.analysis.textr.model.BxBounds;
import pl.edu.icm.yadda.analysis.textr.model.BxChunk;
import pl.edu.icm.yadda.analysis.textr.model.BxDocument;
import pl.edu.icm.yadda.analysis.textr.model.BxLine;
import pl.edu.icm.yadda.analysis.textr.model.BxPage;
import pl.edu.icm.yadda.analysis.textr.model.BxWord;
import pl.edu.icm.yadda.analysis.textr.model.BxZone;
import pl.edu.icm.yadda.analysis.textr.tools.Direction;
import pl.edu.icm.yadda.analysis.textr.tools.Range;
import pl.edu.icm.yadda.analysis.textr.tools.Valley;
import pl.edu.icm.yadda.metadata.transformers.TransformationException;

/**
 *
 * @author estocka
 */
public class XYCutPageSegmenterTest {

    XYCutPageSegmenter xy;
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
        xy = new XYCutPageSegmenter();
        generatedVertices = generateVertices();
        sampleDocument = createSampleDocument(generatedVertices);

    }

    @After
    public void tearDown() {
    }

    @Test
    public void toRanges() {
        TreeSet<Range> rangesList = xy.toRanges(generatedVertices,
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
        TreeSet<Range> rangesYList = xy.toRanges(generatedVertices,
                Direction.Y);
        List<Valley> toValleys = xy.toValleys(rangesYList, Direction.Y);
        assertEquals(32, toValleys.size());

    }

    @Test
    public void maxValleyTest() {
        TreeSet<Range> rangesYList = xy.toRanges(generatedVertices,
                Direction.Y);
        List<Valley> toYValleys = xy.toValleys(rangesYList, Direction.Y);
        TreeSet<Range> rangesXList = xy.toRanges(generatedVertices,
                Direction.X);
        List<Valley> toXValleys = xy.toValleys(rangesXList, Direction.X);
        Valley maxValley = xy.maxValley(toXValleys, toYValleys);
        assertEquals(Direction.Y, maxValley.getDirection());
        assertEquals(0, Double.compare(330, maxValley.getValleyStart()));
        assertEquals(0, Double.compare(600, maxValley.getValleyEnd()));

    }

    @Test
    public void divideChunksTest() {

        TreeSet<Range> rangesYList = xy.toRanges(generatedVertices,
                Direction.Y);
        List<Valley> toYValleys = xy.toValleys(rangesYList, Direction.Y);
        TreeSet<Range> rangesXList = xy.toRanges(generatedVertices,
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
        xy.pageHight = page.getBounds().getHeight();
        List<BxZone> xySegmentation = xy.xySegmentation(page);
        assertEquals(5, xySegmentation.size());


    }

    @Test
    public void xyCutSegmentationTest() throws IOException,
            ParserConfigurationException, SAXException, TransformationException {
        BxPage page = new MargToTextrImporter().read(new InputStreamReader(this.getClass().getResourceAsStream("/pl/edu/icm/yadda/analysis/textr/001.xml"))).get(0);
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
        BxDocument xyCutSegmentation = xy.segmentPages(doc);
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
