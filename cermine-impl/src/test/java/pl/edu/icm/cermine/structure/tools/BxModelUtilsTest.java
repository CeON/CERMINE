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

package pl.edu.icm.cermine.structure.tools;

import java.io.*;
import java.util.Stack;
import static org.junit.Assert.*;
import org.junit.Test;
import pl.edu.icm.cermine.exception.TransformationException;
import pl.edu.icm.cermine.structure.model.BxChunk;
import pl.edu.icm.cermine.structure.model.BxDocument;
import pl.edu.icm.cermine.structure.model.BxLine;
import pl.edu.icm.cermine.structure.model.BxPage;
import pl.edu.icm.cermine.structure.model.BxWord;
import pl.edu.icm.cermine.structure.model.BxZone;
import pl.edu.icm.cermine.structure.transformers.TrueVizToBxDocumentReader;

/**
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class BxModelUtilsTest {
    static final private String EXP_STR_LAB_1 = "/pl/edu/icm/cermine/test1-structure-specific.xml";

    @Test
    public void deepCloneTest() throws TransformationException {
        InputStream stream = this.getClass().getResourceAsStream(EXP_STR_LAB_1);
        TrueVizToBxDocumentReader reader = new TrueVizToBxDocumentReader();
        BxDocument doc = new BxDocument().setPages(reader.read(new InputStreamReader(stream)));
        BxDocument copy = BxModelUtils.deepClone(doc);
        
        assertFalse(doc == copy);
        assertTrue(BxModelUtils.areEqual(doc, copy));
        
        Stack<BxPage> pages = new Stack<BxPage>();
        Stack<BxZone> zones = new Stack<BxZone>();
        Stack<BxLine> lines = new Stack<BxLine>();
        Stack<BxWord> words = new Stack<BxWord>();
        Stack<BxChunk> chunks = new Stack<BxChunk>();
        
        for (BxPage page : copy) {
            assertTrue(page.getParent() == copy);
            for (BxZone zone : page) {
                assertTrue(zone.getParent() == page);
                for (BxLine line : zone) {
                    assertTrue(line.getParent() == zone);
                    for (BxWord word : line) {
                        assertTrue(word.getParent() == line);
                        for (BxChunk chunk : word) {
                            assertTrue(chunk.getParent() == word);
                        }
                    }
                }
            }
        }
        
        BxPage page = copy.getFirstChild();
        while (page != null) {
            pages.push(page);
            page = page.getNext();
        }
        page = pages.peek();
        BxZone zone = copy.getFirstChild().getFirstChild();
        while (zone != null) {
            zones.push(zone);
            zone = zone.getNext();
        }
        zone = zones.peek();
        BxLine line = copy.getFirstChild().getFirstChild().getFirstChild();
        while (line != null) {
            lines.push(line);
            line = line.getNext();
        }
        line = lines.peek();
        BxWord word = copy.getFirstChild().getFirstChild().getFirstChild().getFirstChild();
        while (word != null) {
            words.push(word);
            word = word.getNext();
        }
        word = words.peek();
        BxChunk chunk = copy.getFirstChild().getFirstChild().getFirstChild().getFirstChild().getFirstChild();
        while (chunk != null) {
            chunks.push(chunk);
            chunk = chunk.getNext();
        }
        chunk = chunks.peek();
        
        while (!pages.empty() && !zones.empty() && !lines.empty() 
                && !words.empty() && !chunks.empty()) {
            BxChunk ch = chunks.pop();
            assertTrue(ch.getParent() == words.peek());
            assertTrue(words.peek().getParent() == lines.peek());
            assertTrue(lines.peek().getParent() == zones.peek());
            assertTrue(zones.peek().getParent() == pages.peek());
            if (chunks.empty()) {
                words.pop();
                lines.pop();
                zones.pop();
                pages.pop();
            } else if (ch.getParent() != chunks.peek().getParent()) {
                BxWord w = words.pop();
                assertFalse(words.empty());
                if (w.getParent() != words.peek().getParent()) {
                    BxLine l = lines.pop();
                    assertFalse(lines.empty());
                    if (l.getParent() != lines.peek().getParent()) {
                        BxZone z = zones.pop();
                        assertFalse(zones.empty());
                        if (z.getParent() != zones.peek().getParent()) {
                            pages.pop();
                            assertFalse(pages.empty());
                        }
                    }
                }
            }
        }
        
        assertTrue(pages.empty());
        assertTrue(zones.empty());
        assertTrue(lines.empty());
        assertTrue(words.empty());
        assertTrue(chunks.empty());
        
        
        while (page != null) {
            pages.push(page);
            page = page.getPrev();
        }
        while (zone != null) {
            zones.push(zone);
            zone = zone.getPrev();
        }
        while (line != null) {
            lines.push(line);
            line = line.getPrev();
        }
        while (word != null) {
            words.push(word);
            word = word.getPrev();
        }
        while (chunk != null) {
            chunks.push(chunk);
            chunk = chunk.getPrev();
        }
        
        while (!pages.empty() && !zones.empty() && !lines.empty() 
                && !words.empty() && !chunks.empty()) {
            BxChunk ch = chunks.pop();
            assertTrue(ch.getParent() == words.peek());
            assertTrue(words.peek().getParent() == lines.peek());
            assertTrue(lines.peek().getParent() == zones.peek());
            assertTrue(zones.peek().getParent() == pages.peek());
            if (chunks.empty()) {
                words.pop();
                lines.pop();
                zones.pop();
                pages.pop();
            } else if (ch.getParent() != chunks.peek().getParent()) {
                BxWord w = words.pop();
                assertFalse(words.empty());
                if (w.getParent() != words.peek().getParent()) {
                    BxLine l = lines.pop();
                    assertFalse(lines.empty());
                    if (l.getParent() != lines.peek().getParent()) {
                        BxZone z = zones.pop();
                        assertFalse(zones.empty());
                        if (z.getParent() != zones.peek().getParent()) {
                            pages.pop();
                            assertFalse(pages.empty());
                        }
                    }
                }
            }
        }
        
        assertTrue(pages.empty());
        assertTrue(zones.empty());
        assertTrue(lines.empty());
        assertTrue(words.empty());
        assertTrue(chunks.empty());
        
    }
    
}
