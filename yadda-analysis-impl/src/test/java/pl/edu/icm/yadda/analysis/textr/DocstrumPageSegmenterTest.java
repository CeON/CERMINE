package pl.edu.icm.yadda.analysis.textr;

import java.io.InputStream;
import pl.edu.icm.yadda.analysis.AnalysisException;
import pl.edu.icm.yadda.analysis.textr.model.BxDocument;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import pl.edu.icm.yadda.analysis.textr.transformers.MargToTextrImporter;
import org.junit.Test;
import static org.junit.Assert.*;
import pl.edu.icm.yadda.analysis.textr.model.BxBounds;
import pl.edu.icm.yadda.analysis.textr.model.BxChunk;
import pl.edu.icm.yadda.analysis.textr.model.BxLine;
import pl.edu.icm.yadda.analysis.textr.model.BxPage;
import pl.edu.icm.yadda.analysis.textr.model.BxWord;
import pl.edu.icm.yadda.analysis.textr.model.BxZone;
import pl.edu.icm.yadda.analysis.textr.tools.UnsegmentedPagesFlattener;
import pl.edu.icm.yadda.metadata.transformers.TransformationException;

/**
 *
 * @author krusek
 */
public class DocstrumPageSegmenterTest {

    private InputStream getResource(String name) {
        return this.getClass().getResourceAsStream("/pl/edu/icm/yadda/analysis/textr/" + name);
    }

    @Test
    public void testSegmentPages() throws TransformationException, AnalysisException {
        Reader reader = new InputStreamReader(getResource("DocstrumPageSegmenter01.xml"));
        BxDocument inDoc = new BxDocument().setPages(new MargToTextrImporter().read(reader));
        new UnsegmentedPagesFlattener().process(inDoc);
        BxDocument outDoc = new DocstrumPageSegmenter().segmentPages(inDoc);

        // Check whether zones are correctly detected
        assertEquals(1, outDoc.getPages().size());

        // Check whether lines are correctly detected
        List<BxZone> outZones = outDoc.getPages().get(0).getZones();
        assertEquals(3, outZones.size());
        assertEquals(3, outZones.get(0).getLines().size());
        assertEquals(16, outZones.get(1).getLines().size());
        assertEquals(16, outZones.get(2).getLines().size());

        assertEquals(8, outZones.get(1).getLines().get(0).getWords().size());
        assertEquals("ABSTRACT", outZones.get(1).getLines().get(0).getWords().get(0).toText());

        for (BxZone zone : outZones) {
            for (BxLine line : zone.getLines()) {
                for (BxWord word : line.getWords()) {
                    for (BxChunk chunk : word.getChunks()) {
                        assertContains(zone.getBounds(), chunk.getBounds());
                    }
                    assertContains(zone.getBounds(), word.getBounds());
                }
                assertContains(zone.getBounds(), line.getBounds());
            }
        }

        assertNotNull(outDoc.getPages().get(0).getBounds());
    }

    public void testSegmentPages_badBounds(BxBounds bounds) throws AnalysisException {
        BxDocument doc = new BxDocument().addPage(new BxPage().addChunk(new BxChunk(bounds, "a")));
        new DocstrumPageSegmenter().segmentPages(doc);
    }

    @Test(expected=AnalysisException.class)
    public void testSegmentPages_badBounds1() throws AnalysisException {
        testSegmentPages_badBounds(null);
    }

    @Test(expected=AnalysisException.class)
    public void testSegmentPages_badBounds2() throws AnalysisException {
        testSegmentPages_badBounds(new BxBounds(Double.NaN, 0, 0, 0));
    }

    @Test(expected=AnalysisException.class)
    public void testSegmentPages_badBounds3() throws AnalysisException {
        testSegmentPages_badBounds(new BxBounds(0, 0, Double.POSITIVE_INFINITY, 0));
    }

    private static void assertBetween(int min, int max, int value) {
        assertTrue("expected between:<" + min + "> and:<" + max + "> but was:<" + value + ">",
                min <= value && value <= max);
    }

    private static void assertContains(BxBounds big, BxBounds small) {
        assertTrue(big.getX() <= small.getX());
        assertTrue(big.getY() <= small.getY());
        assertTrue(big.getX() + big.getWidth() >= small.getX() + small.getWidth());
        assertTrue(big.getY() + big.getHeight() >= small.getY() + small.getHeight());
    }
}
