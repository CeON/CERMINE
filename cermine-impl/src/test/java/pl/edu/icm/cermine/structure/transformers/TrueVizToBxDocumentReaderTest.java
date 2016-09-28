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
package pl.edu.icm.cermine.structure.transformers;

import com.google.common.collect.Lists;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipFile;
import javax.xml.parsers.ParserConfigurationException;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.xml.sax.SAXException;
import pl.edu.icm.cermine.exception.TransformationException;
import pl.edu.icm.cermine.structure.model.*;

/**
 * @author Krzysztof Rusek
 * @author Kuba Jurkiewicz
 * @author Pawel Szostek
 */
public class TrueVizToBxDocumentReaderTest {

    static String PATH = "/pl/edu/icm/cermine/structure/";

    public TrueVizToBxDocumentReaderTest() {
    }

    @Test
    public void testImporter() throws IOException, ParserConfigurationException, SAXException,
            TransformationException {
        BxPage page = new TrueVizToBxDocumentReader().read(new InputStreamReader(this.getClass().getResourceAsStream("/pl/edu/icm/cermine/structure/imports/MargImporterTest1.xml"))).get(0);
        boolean contains = false;
        boolean rightText = false;
        boolean rightSize = false;
        for (BxZone zone : page) {
            if (zone.getLabel() != null) {
                if (zone.getLabel().equals(BxZoneLabel.MET_AUTHOR)) {
                    contains = true;
                    if (zone.toText().trim().equalsIgnoreCase("Howard M  Schachter   Ba  Pham   Jim King tt\nStephanie Langford   David Moher".trim())) {
                        rightText = true;
                    }
                    if (zone.getBounds().getX() == 72 && zone.getBounds().getY() == 778 && zone.getBounds().getWidth() == 989 && zone.getBounds().getHeight() == 122) {
                        rightSize = true;
                    }
                }
            }
        }
        assertTrue(contains);
        assertTrue(rightText);
        assertTrue(rightSize);

        BxWord word = page.getChild(0).getChild(0).getChild(0);
        assertEquals("font-1", word.getChild(0).getFontName());
        assertEquals("font-2", word.getChild(1).getFontName());
    }

    private BxDocument getDocumentFromZipFile(String zipFilename, String filename) throws TransformationException, IOException, URISyntaxException {
        URL url = this.getClass().getResource(PATH + zipFilename);
        ZipFile zipFile = new ZipFile(new File(url.toURI()));
        InputStream is = zipFile.getInputStream(zipFile.getEntry(filename));
        InputStreamReader isr = new InputStreamReader(is);

        TrueVizToBxDocumentReader reader = new TrueVizToBxDocumentReader();
        BxDocument doc = new BxDocument().setPages(reader.read(isr));
        isr.close();
        return doc;
    }

    @Test
    public void testAllNextsAreSet1() throws TransformationException, IOException, URISyntaxException {
        BxDocument orderedDoc = getDocumentFromZipFile("roa_test.zip", "1748717X.xml.out");
        
        Integer nextNulls = 0;
        for (BxPage page : orderedDoc.asPages()) {
            if (page.getNext() == null) {
                ++nextNulls;
            }
        }
        assertEquals(nextNulls, Integer.valueOf(1));

        nextNulls = 0;
        for (BxZone zone : orderedDoc.asZones()) {
            if (zone.getNext() == null) {
                ++nextNulls;
            }
        }
        assertEquals(nextNulls, Integer.valueOf(1));

        nextNulls = 0;
        for (BxLine line : orderedDoc.asLines()) {
            if (line.getNext() == null) {
                ++nextNulls;
            }
        }
        assertEquals(nextNulls, Integer.valueOf(1));

        nextNulls = 0;
        for (BxWord word : orderedDoc.asWords()) {
            if (word.getNext() == null) {
                ++nextNulls;
            }
        }
        assertEquals(nextNulls, Integer.valueOf(1));

        nextNulls = 0;
        for (BxChunk chunk : orderedDoc.asChunks()) {
            if (chunk.getNext() == null) {
                ++nextNulls;
            }
        }
        assertEquals(nextNulls, Integer.valueOf(1));
    }

    public <A extends Indexable> Integer countChainedElements(List<A> list) throws TransformationException, IOException {
        Set<A> nextSet = new HashSet<A>();
        for (A elem : list) {
            A next = (A) elem.getNext();
            if (next != null && list.contains(next)) {
                nextSet.add(next);
            }
        }
        return nextSet.size();
    }

    @Test
    public void testChainedElementsEven() throws TransformationException, IOException, URISyntaxException {
        BxDocument doc = getDocumentFromZipFile("roa_test.zip", "1748717X.xml.out");
        List<BxPage> pages = Lists.newArrayList(doc);
        List<BxZone> zones = Lists.newArrayList(doc.asZones());
        List<BxLine> lines = Lists.newArrayList(doc.asLines());
        List<BxWord> words = Lists.newArrayList(doc.asWords());
        List<BxChunk> chunks = Lists.newArrayList(doc.asChunks());
        assertEquals(countChainedElements(pages), Integer.valueOf(pages.size() - 1));
        assertEquals(countChainedElements(zones), Integer.valueOf(zones.size() - 1));
        assertEquals(countChainedElements(lines), Integer.valueOf(lines.size() - 1));
        assertEquals(countChainedElements(words), Integer.valueOf(words.size() - 1));
        assertEquals(countChainedElements(chunks), Integer.valueOf(chunks.size() - 1));
    }

}
