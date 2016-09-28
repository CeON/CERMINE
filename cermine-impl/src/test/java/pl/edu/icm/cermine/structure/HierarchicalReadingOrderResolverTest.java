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

import com.google.common.collect.Lists;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import pl.edu.icm.cermine.exception.TransformationException;
import pl.edu.icm.cermine.structure.model.*;
import pl.edu.icm.cermine.structure.transformers.TrueVizToBxDocumentReader;

/**
 * @author Pawel Szostek
 */
public class HierarchicalReadingOrderResolverTest {

    static final private String PATH = "/pl/edu/icm/cermine/structure/";
    static final private String[] TEST_FILENAMES = {"1748717X.xml"};
    static final private String ZIP_FILE_NAME = "roa_test_small.zip";
    static private ZipFile zipFile;

    @Before
    public void setUp() throws URISyntaxException, ZipException, IOException {
        URL url = HierarchicalReadingOrderResolverTest.class.getResource(PATH + ZIP_FILE_NAME);
        URI uri = url.toURI();
        File file = new File(uri);
        zipFile = new ZipFile(file);
    }

    private BxDocument getDocumentFromZip(String filename) throws TransformationException, IOException {
        TrueVizToBxDocumentReader tvReader = new TrueVizToBxDocumentReader();
        Enumeration<? extends ZipEntry> entries = zipFile.entries();
        while (entries.hasMoreElements()) {
            ZipEntry zipEntry = (ZipEntry) entries.nextElement();
            if (zipEntry.getName().equals(filename)) {
                List<BxPage> pages = tvReader.read(new InputStreamReader(zipFile.getInputStream(zipEntry)));
                BxDocument newDoc = new BxDocument();
                for (BxPage page : pages) {
                    page.setParent(newDoc);
                }
                newDoc.setFilename(zipEntry.getName());
                newDoc.setPages(pages);
                return newDoc;
            }
        }
        return null;
    }

    private Boolean areDocumentsEqual(BxDocument doc1, BxDocument doc2) {
        if (doc1.childrenCount() != doc2.childrenCount()) {
            return false;
        }
        for (Integer pageIdx = 0; pageIdx < doc1.childrenCount(); ++pageIdx) {
            BxPage page1 = doc1.getChild(pageIdx);
            BxPage page2 = doc2.getChild(pageIdx);
            if (page1.childrenCount() != page2.childrenCount()) {
                return false;
            }
            for (Integer zoneIdx = 0; zoneIdx < page1.childrenCount(); ++zoneIdx) {
                BxZone zone1 = page1.getChild(zoneIdx);
                BxZone zone2 = page2.getChild(zoneIdx);
                if (zone1.getChunks().size() != zone2.getChunks().size()) {
                    return false;
                }
                for (Integer chunkIdx = 0; chunkIdx < zone1.getChunks().size(); ++chunkIdx) {
                    BxChunk chunk1 = zone1.getChunks().get(chunkIdx);
                    BxChunk chunk2 = zone2.getChunks().get(chunkIdx);
                    if (!chunk1.toText().equals(chunk2.toText())) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    @Test
    public void testSetReadingOrder() throws TransformationException, IOException {
        for (Enumeration<? extends ZipEntry> e = zipFile.entries(); e.hasMoreElements();) {
            String filename = e.nextElement().getName();
            if (!filename.endsWith(".xml")) {
                continue;
            }
            BxDocument doc = getDocumentFromZip(filename);
            BxDocument modelOrderedDoc = getDocumentFromZip(filename + ".out");

            HierarchicalReadingOrderResolver roa = new HierarchicalReadingOrderResolver();
            BxDocument orderedDoc = roa.resolve(doc);

            assertTrue(areDocumentsEqual(orderedDoc, modelOrderedDoc));
        }
    }

    @Test
    public void testConstantElementsNumber() throws TransformationException, IOException {
        for (String filename : TEST_FILENAMES) {
            BxDocument doc = getDocumentFromZip(filename);

            HierarchicalReadingOrderResolver roa = new HierarchicalReadingOrderResolver();
            BxDocument orderedDoc = roa.resolve(doc);

            List<BxPage> pages1 = Lists.newArrayList(doc.asPages());
            List<BxPage> pages2 = Lists.newArrayList(orderedDoc.asPages());
            List<BxZone> zones1 = Lists.newArrayList(doc.asZones());
            List<BxZone> zones2 = Lists.newArrayList(orderedDoc.asZones());
            List<BxLine> lines1 = Lists.newArrayList(doc.asLines());
            List<BxLine> lines2 = Lists.newArrayList(orderedDoc.asLines());
            List<BxWord> words1 = Lists.newArrayList(doc.asWords());
            List<BxWord> words2 = Lists.newArrayList(orderedDoc.asWords());
            List<BxChunk> chunks1 = Lists.newArrayList(doc.asChunks());
            List<BxChunk> chunks2 = Lists.newArrayList(orderedDoc.asChunks());

            assertEquals(pages1.size(), pages2.size());
            assertEquals(zones1.size(), zones2.size());
            assertEquals(lines1.size(), lines2.size());
            assertEquals(words1.size(), words2.size());
            assertEquals(chunks1.size(), chunks2.size());
        }
    }
}
