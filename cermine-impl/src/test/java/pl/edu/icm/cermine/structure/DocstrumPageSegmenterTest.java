/**
 * This file is part of CERMINE project.
 * Copyright (c) 2011-2016 ICM-UW
 *
 * CERMINE is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * CERMINE is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with CERMINE. If not, see <http://www.gnu.org/licenses/>.
 */

package pl.edu.icm.cermine.structure;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import static org.junit.Assert.*;
import org.junit.Test;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.exception.TransformationException;
import pl.edu.icm.cermine.structure.model.*;
import pl.edu.icm.cermine.structure.tools.UnsegmentedPagesFlattener;
import pl.edu.icm.cermine.structure.transformers.TrueVizToBxDocumentReader;

/**
 * @author Krzysztof Rusek
 */
public class DocstrumPageSegmenterTest {

    private InputStream getResource(String name) {
        return this.getClass().getResourceAsStream("/pl/edu/icm/cermine/structure/" + name);
    }

    @Test
    public void testSegmentPages() throws TransformationException, AnalysisException {
        Reader reader = new InputStreamReader(getResource("DocstrumPageSegmenter01.xml"));
        BxDocument inDoc = new BxDocument().setPages(new TrueVizToBxDocumentReader().read(reader));
        new UnsegmentedPagesFlattener().process(inDoc);
        
        DocstrumSegmenter pageSegmenter = new DocstrumSegmenter();
        pageSegmenter.setSpacingHistogramResolution(2.0);
        pageSegmenter.setSpacingHistogramSmoothingWindowLength(10.0);
        pageSegmenter.setSpacingHistogramSmoothingWindowStdDeviation(2.0);
        pageSegmenter.setMaxLineSizeScale(1.5);
        pageSegmenter.setWordDistanceMultiplier(0.5);
        pageSegmenter.setMinHorizontalDistanceMultiplier(1.5);
        pageSegmenter.setMaxVerticalDistanceMultiplier(1.3);
        pageSegmenter.setMaxVerticalMergeDistanceMultiplier(0.5);
        pageSegmenter.setComponentDistanceCharacterMultiplier(3.0);
        
        BxDocument outDoc = pageSegmenter.segmentDocument(inDoc);

        // Check whether zones are correctly detected
        assertEquals(1, outDoc.childrenCount());

        // Check whether lines are correctly detected
        assertEquals(3, outDoc.getFirstChild().childrenCount());
        assertEquals(3, outDoc.getFirstChild().getChild(0).childrenCount());
        assertEquals(16, outDoc.getFirstChild().getChild(1).childrenCount());
        assertEquals(16, outDoc.getFirstChild().getChild(2).childrenCount());

        assertEquals(8, outDoc.getFirstChild().getChild(1).getFirstChild().childrenCount());
        assertEquals("ABSTRACT", outDoc.getFirstChild().getChild(1).getFirstChild().getFirstChild().toText());

        for (BxZone zone : outDoc.getFirstChild()) {
            for (BxLine line : zone) {
                for (BxWord word : line) {
                    for (BxChunk chunk : word) {
                        assertContains(zone.getBounds(), chunk.getBounds());
                    }
                    assertContains(zone.getBounds(), word.getBounds());
                }
                assertContains(zone.getBounds(), line.getBounds());
            }
        }

        assertNotNull(outDoc.getFirstChild().getBounds());
    }

    public void testSegmentPages_badBounds(BxBounds bounds) throws AnalysisException {
        BxDocument doc = new BxDocument().addPage(new BxPage().addChunk(new BxChunk(bounds, "a")));
        new DocstrumSegmenter().segmentDocument(doc);
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

    private static void assertContains(BxBounds big, BxBounds small) {
        assertTrue(big.getX() <= small.getX());
        assertTrue(big.getY() <= small.getY());
        assertTrue(big.getX() + big.getWidth() >= small.getX() + small.getWidth());
        assertTrue(big.getY() + big.getHeight() >= small.getY() + small.getHeight());
    }
}
