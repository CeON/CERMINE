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
import java.io.InputStream;
import java.util.List;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.structure.model.BxBounds;
import pl.edu.icm.cermine.structure.model.BxChunk;
import pl.edu.icm.cermine.structure.model.BxDocument;
import pl.edu.icm.cermine.structure.model.BxPage;

/**
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class ITextCharacterExtractionTest {
    static final private String INPUT_DIR = "/pl/edu/icm/cermine/structure/";
    static final private String[] INPUT_FILES = {"1.pdf", "2.pdf", "3.pdf", "4.pdf", "5.pdf", "6.pdf"};
    
    private final CharacterExtractor extractor = new ITextCharacterExtractor();
    
    private final BxBounds b0 = new BxBounds(50.0, 79.0, 12.0, 16.65);
    private final BxBounds b1 = new BxBounds(62.0, 79.0, 12.0, 16.65);
    private final BxBounds b2 = new BxBounds(74.0, 79.0, 13.0, 16.65);
    private final BxBounds b3 = new BxBounds(87.0, 79.0, 13.0, 16.65);
    private final BxBounds b4 = new BxBounds(110.0, 79.0, 12.0, 16.65);
    private final BxBounds b5 = new BxBounds(122.0, 79.0, 11.0, 16.65);
    private final BxBounds b6 = new BxBounds(133.0, 79.0, 14.0, 16.65);
    private final BxBounds b7 = new BxBounds(147.0, 79.0, 13.0, 16.65);

  
    @Test
    public void metadataExtractionTest() throws AnalysisException {
        for (String file : INPUT_FILES) {
            InputStream testStream = this.getClass().getResourceAsStream(INPUT_DIR + file);
            BxDocument testDocument = extractor.extractCharacters(testStream);
            BxPage page = testDocument.getFirstChild();
            List<BxChunk> chunks = Lists.newArrayList(page.getChunks());
            assertTrue(chunks.get(0).getBounds().isSimilarTo(b0, 0.08));
            assertTrue(chunks.get(1).getBounds().isSimilarTo(b1, 0.08));
            assertTrue(chunks.get(2).getBounds().isSimilarTo(b2, 0.08));
            assertTrue(chunks.get(3).getBounds().isSimilarTo(b3, 0.08));
            assertTrue(chunks.get(4).getBounds().isSimilarTo(b4, 0.08));
            assertTrue(chunks.get(5).getBounds().isSimilarTo(b5, 0.08));
            assertTrue(chunks.get(6).getBounds().isSimilarTo(b6, 0.08));
            assertTrue(chunks.get(7).getBounds().isSimilarTo(b7, 0.08));
        }
    }
}
