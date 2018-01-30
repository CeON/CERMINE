/**
 * This file is part of CERMINE project.
 * Copyright (c) 2011-2018 ICM-UW
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

import com.google.common.collect.Lists;
import java.util.*;
import pl.edu.icm.cermine.structure.model.*;
import pl.edu.icm.cermine.tools.Utils;

/**
 * @author Krzysztof Rusek
 * @author Dominika Tkaczyk
 */
public final class BxModelUtils {

    private static final double SIMILARITY_TOLERANCE = 0.001;

    private BxModelUtils() {
    }

    public static void setParents(BxDocument doc) {
        for (BxPage page : doc) {
            page.setParent(doc);
            setParents(page);
        }
    }

    public static void setParents(BxPage page) {
        for (BxZone zone : page) {
            for (BxLine line : zone) {
                for (BxWord word : line) {
                    for (BxChunk chunk : word) {
                        chunk.setParent(word);
                    }
                    word.setParent(line);
                }
                line.setParent(zone);
            }
            zone.setParent(page);
        }
    }

    public static void sortZonesYX(BxPage page, final double tolerance) {
        List<BxZone> zones = Lists.newArrayList(page);
        Collections.sort(zones, new Comparator<BxZone>() {

            @Override
            public int compare(BxZone o1, BxZone o2) {
                int cmp = Utils.compareDouble(o1.getBounds().getY(), o2.getBounds().getY(), tolerance);
                if (cmp == 0) {
                    return Utils.compareDouble(o1.getBounds().getX(), o2.getBounds().getX(), tolerance);
                }
                return cmp;
            }
        });
        page.setZones(zones);
    }

    public static void sortZonesYX(BxPage page) {
        sortZonesYX(page, 0);
    }

    public static void sortZonesXY(BxPage page, final double tolerance) {
        List<BxZone> zones = Lists.newArrayList(page);
        Collections.sort(zones, new Comparator<BxZone>() {

            @Override
            public int compare(BxZone o1, BxZone o2) {
                int cmp = Utils.compareDouble(o1.getBounds().getX(), o2.getBounds().getX(), tolerance);
                if (cmp == 0) {
                    return Utils.compareDouble(o1.getBounds().getY(), o2.getBounds().getY(), tolerance);
                }
                return cmp;
            }
        });
        page.setZones(zones);
    }

    public static void sortZonesXY(BxPage page) {
        sortZonesXY(page, 0);
    }

    public static void sortLines(BxZone zone) {
        List<BxLine> lines = Lists.newArrayList(zone);
        Collections.sort(lines, new Comparator<BxLine>() {

            @Override
            public int compare(BxLine o1, BxLine o2) {
                int cmp = Double.compare(o1.getBounds().getY(), o2.getBounds().getY());
                if (cmp == 0) {
                    cmp = Double.compare(o1.getBounds().getX(), o2.getBounds().getX());
                }
                return cmp;
            }
        });
        zone.setLines(lines);
    }

    public static void sortWords(BxLine line) {
        List<BxWord> words = Lists.newArrayList(line);
        Collections.sort(words, new Comparator<BxWord>() {

            @Override
            public int compare(BxWord o1, BxWord o2) {
                return Double.compare(o1.getBounds().getX(), o2.getBounds().getX());
            }
        });
        line.setWords(words);
    }

    public static void sortChunks(BxWord word) {
        List<BxChunk> chunks = Lists.newArrayList(word);
        Collections.sort(chunks, new Comparator<BxChunk>() {

            @Override
            public int compare(BxChunk o1, BxChunk o2) {
                return Double.compare(o1.getBounds().getX(), o2.getBounds().getX());
            }
        });
        word.setChunks(chunks);
    }

    public static void sortZoneRecursively(BxZone zone) {
        sortLines(zone);
        for (BxLine line : zone) {
            sortWords(line);
            for (BxWord word : line) {
                sortChunks(word);
            }
        }
    }

    public static void sortZonesRecursively(BxPage page) {
        for (BxZone zone : page) {
            sortZoneRecursively(zone);
        }
    }

    public static void sortZonesRecursively(BxDocument document) {
        for (BxPage page : document) {
            sortZonesRecursively(page);
        }
    }

    public static BxBounds deepClone(BxBounds bounds) {
        return new BxBounds(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight());
    }
    
    public static BxChunk deepClone(BxChunk chunk) {
        BxChunk copy = new BxChunk(deepClone(chunk.getBounds()), chunk.toText());
        copy.setFontName(chunk.getFontName());
        copy.setId(chunk.getId());
        copy.setNextId(chunk.getNextId());
        return copy;
    }
    
    /**
     * Creates a deep copy of the word.
     *
     * @param word word
     * @return copy
     */
    public static BxWord deepClone(BxWord word) {
        BxWord copy = new BxWord().setBounds(deepClone(word.getBounds()));
        copy.setId(word.getId());
        copy.setNextId(word.getNextId());
        BxChunk prev = null;
        for (BxChunk chunk : word) {
            BxChunk copiedChunk = deepClone(chunk);
            copiedChunk.setPrev(prev);
            if (prev != null) {
                prev.setNext(copiedChunk);
            }
            prev = copiedChunk;
            copy.addChunk(copiedChunk);
        }
        return copy;
    }

    /**
     * Creates a deep copy of the line.
     *
     * @param line line
     * @return copy
     */
    public static BxLine deepClone(BxLine line) {
        BxLine copy = new BxLine().setBounds(deepClone(line.getBounds()));
        copy.setId(line.getId());
        copy.setNextId(line.getNextId());
        BxWord prevW = null;
        BxChunk prevC = null;
        for (BxWord word : line) {
            BxWord copiedWord = deepClone(word);
            copiedWord.setPrev(prevW);
            if (prevW != null) {
                prevW.setNext(copiedWord);
            }
            prevW = copiedWord;
            for (BxChunk ch : copiedWord) {
                ch.setPrev(prevC);
                if (prevC != null) {
                    prevC.setNext(ch);
                }
                prevC = ch;
            }
            copy.addWord(copiedWord);
        }
        return copy;
    }

    /**
     * Creates a deep copy of the zone.
     *
     * @param zone zone
     * @return copy
     */
    public static BxZone deepClone(BxZone zone) {
        BxZone copy = new BxZone().setLabel(zone.getLabel()).setBounds(deepClone(zone.getBounds()));
        copy.setId(zone.getId());
        copy.setNextId(zone.getNextId());
        BxLine prevL = null;
        BxWord prevW = null;
        BxChunk prevC = null;
        for (BxLine line : zone) {
            BxLine copiedLine = deepClone(line);
            copiedLine.setPrev(prevL);
            if (prevL != null) {
                prevL.setNext(copiedLine);
            }
            prevL = copiedLine;
            for (BxWord w : copiedLine) {
                w.setPrev(prevW);
                if (prevW != null) {
                    prevW.setNext(w);
                }
                prevW = w;
                for (BxChunk ch : w) {
                    ch.setPrev(prevC);
                    if (prevC != null) {
                        prevC.setNext(ch);
                    }
                    prevC = ch;
                }
            }
            copy.addLine(copiedLine);
        }
        for (BxChunk chunk : zone.getChunks()) {
            copy.addChunk(chunk);
        }
        return copy;
    }

    /**
     * Creates a deep copy of the page.
     *
     * @param page page
     * @return copy
     */
    public static BxPage deepClone(BxPage page) {
        BxPage copy = new BxPage().setBounds(deepClone(page.getBounds()));
        copy.setId(page.getId());
        copy.setNextId(page.getNextId());
        BxZone prevZ = null;
        BxLine prevL = null;
        BxWord prevW = null;
        BxChunk prevC = null;
        for (BxZone zone : page) {
            BxZone copiedZone = deepClone(zone);
            copiedZone.setPrev(prevZ);
            if (prevZ != null) {
                prevZ.setNext(copiedZone);
            }
            prevZ = copiedZone;
            for (BxLine l : copiedZone) {
                l.setPrev(prevL);
                if (prevL != null) {
                    prevL.setNext(l);
                }
                prevL = l;
                for (BxWord w : l) {
                    w.setPrev(prevW);
                    if (prevW != null) {
                        prevW.setNext(w);
                    }
                    prevW = w;
                    for (BxChunk ch : w) {
                        ch.setPrev(prevC);
                        if (prevC != null) {
                            prevC.setNext(ch);
                        }
                        prevC = ch;
                    }
                }
            }
            copy.addZone(copiedZone);
        }
        Iterator<BxChunk> chunks = page.getChunks();
        while (chunks.hasNext()) {
            copy.addChunk(deepClone(chunks.next()));
        }
        return copy;
    }

    /**
     * Creates a deep copy of the document.
     *
     * @param document document
     * @return copy
     */
    public static BxDocument deepClone(BxDocument document) {
        BxDocument copy = new BxDocument();
        copy.setFilename(document.getFilename());
        BxPage prevP = null;
        BxZone prevZ = null;
        BxLine prevL = null;
        BxWord prevW = null;
        BxChunk prevC = null;
        for (BxPage page : document) {
            BxPage copiedPage = deepClone(page);
            copiedPage.setPrev(prevP);
            if (prevP != null) {
                prevP.setNext(copiedPage);
            }
            prevP = copiedPage;
            for (BxZone z : copiedPage) {
                z.setPrev(prevZ);
                if (prevZ != null) {
                    prevZ.setNext(z);
                }
                prevZ = z;
                for (BxLine l : z) {
                    l.setPrev(prevL);
                    if (prevL != null) {
                        prevL.setNext(l);
                    }
                    prevL = l;
                    for (BxWord w : l) {
                        w.setPrev(prevW);
                        if (prevW != null) {
                            prevW.setNext(w);
                        }
                        prevW = w;
                        for (BxChunk ch : w) {
                            ch.setPrev(prevC);
                            if (prevC != null) {
                                prevC.setNext(ch);
                            }
                            prevC = ch;
                        }
                    }
                }
            }
            copy.addPage(copiedPage);
        }
        return copy;
    }

    public static List<BxDocument> deepClone(List<BxDocument> documents) {
        List<BxDocument> copy = new ArrayList<BxDocument>(documents.size());
        for (BxDocument doc : documents) {
            copy.add(deepClone(doc));
        }
        return copy;
    }

    /**
     * Maps segmented chunks to words which they belong to.
     *
     * @param page page
     * @return map
     */
    public static Map<BxChunk, BxWord> mapChunksToWords(BxPage page) {
        Map<BxChunk, BxWord> map = new HashMap<BxChunk, BxWord>();
        for (BxZone zone : page) {
            for (BxLine line : zone) {
                for (BxWord word : line) {
                    for (BxChunk chunk : word) {
                        map.put(chunk, word);
                    }
                }
            }
        }
        return map;
    }

    /**
     * Maps segmented chunks to lines which they belong to.
     *
     * @param page page
     * @return map
     */
    public static Map<BxChunk, BxLine> mapChunksToLines(BxPage page) {
        Map<BxChunk, BxLine> map = new HashMap<BxChunk, BxLine>();
        for (BxZone zone : page) {
            for (BxLine line : zone) {
                for (BxWord word : line) {
                    for (BxChunk chunk : word) {
                        map.put(chunk, line);
                    }
                }
            }
        }
        return map;
    }

    /**
     * Maps segmented chunks to zones which they belong to.
     *
     * @param page page containing zones
     * @return map
     */
    public static Map<BxChunk, BxZone> mapChunksToZones(BxPage page) {
        Map<BxChunk, BxZone> map = new HashMap<BxChunk, BxZone>();
        for (BxZone zone : page) {
            for (BxLine line : zone) {
                for (BxWord word : line) {
                    for (BxChunk chunk : word) {
                        map.put(chunk, zone);
                    }
                }
            }
        }
        return map;
    }

    /**
     * Counts segmented chunks in line.
     *
     * @param line line
     * @return number of segmented chunks
     */
    public static int countChunks(BxLine line) {
        int chunkCount = 0;
        for (BxWord word : line) {
            chunkCount += word.childrenCount();
        }
        return chunkCount;
    }

    /**
     * Counts segmented chunks in zone.
     *
     * @param zone zone
     * @return number of segmented chunks
     */
    public static int countChunks(BxZone zone) {
        int chunkCount = 0;
        for (BxLine line : zone) {
            for (BxWord word : line) {
                chunkCount += word.childrenCount();
            }
        }
        return chunkCount;
    }

    public static boolean contains(BxBounds containing, BxBounds contained, double tolerance) {
        return containing.getX() <= contained.getX() + tolerance
                && containing.getY() <= contained.getY() + tolerance
                && containing.getX() + containing.getWidth() >= contained.getX() + contained.getWidth() - tolerance
                && containing.getY() + containing.getHeight() >= contained.getY() + contained.getHeight() - tolerance;
    }

    public static boolean areEqual(BxChunk chunk1, BxChunk chunk2) {
        if (!chunk1.toText().equals(chunk2.toText())) {
            return false;
        }
        if (!chunk1.getFontName().equals(chunk2.getFontName())) {
            return false;
        }
        if (!chunk1.getId().equals(chunk2.getId())) {
            return false;
        }
        return chunk1.getBounds().isSimilarTo(chunk2.getBounds(), SIMILARITY_TOLERANCE);
    }

    public static boolean areEqual(BxWord word1, BxWord word2) {
        if (!word1.getId().equals(word2.getId())) {
            return false;
        }
        if (word1.childrenCount() != word2.childrenCount()) {
            return false;
        }
        for (int i = 0; i < word1.childrenCount(); i++) {
            if (!areEqual(word1.getChild(i), word2.getChild(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean areEqual(BxLine line1, BxLine line2) {
        if (!line1.getId().equals(line2.getId())) {
            return false;
        }
        if (line1.childrenCount() != line2.childrenCount()) {
            return false;
        }
        for (int i = 0; i < line1.childrenCount(); i++) {
            if (!areEqual(line1.getChild(i), line2.getChild(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean areEqual(BxZone zone1, BxZone zone2) {
        if (!zone1.getId().equals(zone2.getId())) {
            return false;
        }
        if (zone1.getLabel() != zone2.getLabel()){
            return false;
        }
        if (zone1.childrenCount() != zone2.childrenCount()) {
            return false;
        }
        for (int i = 0; i < zone1.childrenCount(); i++) {
            if (!areEqual(zone1.getChild(i), zone2.getChild(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean areEqual(BxPage page1, BxPage page2) {
        if (!page1.getId().equals(page2.getId())) {
            return false;
        }
        if (page1.childrenCount() != page2.childrenCount()) {
            return false;

        }
        for (int i = 0; i < page1.childrenCount(); i++) {
            if (!areEqual(page1.getChild(i), page2.getChild(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean areEqual(BxDocument doc1, BxDocument doc2) {
        if (doc1.getFilename() == null && doc2.getFilename() != null) {
            return false;
        }
        if (doc1.getFilename() != null && doc2.getFilename() == null) {
            return false;
        }
        if (doc1.getFilename() != null && doc2.getFilename() != null
                && !doc1.getFilename().equals(doc2.getFilename())) {
            return false;
        }
        if (doc1.childrenCount() != doc2.childrenCount()) {
            return false;
        }
        for (int i = 0; i < doc1.childrenCount(); i++) {
            if (!areEqual(doc1.getChild(i), doc2.getChild(i))) {
                return false;
            }
        }
        return true;
    }
}
