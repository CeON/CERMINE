package pl.edu.icm.yadda.analysis.textr.tools;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import pl.edu.icm.yadda.analysis.textr.model.*;

/**
 *
 * @author krusek
 */
public class BxModelUtils {

    public static void sortZonesYX(BxPage page, final double tolerance) {
        Collections.sort(page.getZones(), new Comparator<BxZone>() {

            @Override
            public int compare(BxZone o1, BxZone o2) {
                int cmp = 0;
                if (Math.abs(o1.getBounds().getY() - o2.getBounds().getY()) > tolerance) {
                    cmp = Double.compare(o1.getBounds().getY(), o2.getBounds().getY());
                }
                if (cmp != 0) {
                    return cmp;
                } else {
                    return Double.compare(o1.getBounds().getX(), o2.getBounds().getX());
                }
            }
        });
    }

    public static void sortZonesYX(BxPage page) {
        sortZonesYX(page, 0);
    }

    public static void sortZonesXY(BxPage page, final double tolerance) {
        Collections.sort(page.getZones(), new Comparator<BxZone>() {

            @Override
            public int compare(BxZone o1, BxZone o2) {
                int cmp = 0;
                if (Math.abs(o1.getBounds().getX() - o2.getBounds().getX()) > tolerance) {
                    cmp = Double.compare(o1.getBounds().getX(), o2.getBounds().getX());
                }
                if (cmp != 0) {
                    return cmp;
                } else {
                    return Double.compare(o1.getBounds().getY(), o2.getBounds().getY());
                }
            }
        });
    }

    public static void sortZonesXY(BxPage page) {
        sortZonesXY(page, 0);
    }

    public static void sortLines(BxZone zone) {
        Collections.sort(zone.getLines(), new Comparator<BxLine>() {

            @Override
            public int compare(BxLine o1, BxLine o2) {
                int cmp = Double.compare(o1.getBounds().getY(), o2.getBounds().getY());
                if (cmp == 0) {
                    cmp = Double.compare(o1.getBounds().getX(), o2.getBounds().getX());
                }
                return cmp;
            }
        });
    }

    public static void sortWords(BxLine line) {
        Collections.sort(line.getWords(), new Comparator<BxWord>() {

            @Override
            public int compare(BxWord o1, BxWord o2) {
                return Double.compare(o1.getBounds().getX(), o2.getBounds().getX());
            }
        });
    }

    public static void sortChunks(BxWord word) {
        Collections.sort(word.getChunks(), new Comparator<BxChunk>() {

            @Override
            public int compare(BxChunk o1, BxChunk o2) {
                return Double.compare(o1.getBounds().getX(), o2.getBounds().getX());
            }
        });
    }

    public static void sortZoneRecursively(BxZone zone) {
        sortLines(zone);
        for (BxLine line : zone.getLines()) {
            sortWords(line);
            for (BxWord word : line.getWords()) {
                sortChunks(word);
            }
        }
    }

    public static void sortZonesRecursively(BxPage page) {
        for (BxZone zone : page.getZones()) {
            sortZoneRecursively(zone);
        }
    }

    public static void sortZonesRecursively(BxDocument document) {
        for (BxPage page : document.getPages()) {
            sortZonesRecursively(page);
        }
    }

    /**
     * Creates a deep copy of the word.
     *
     * @param zone
     * @return
     */
    public static BxWord deepClone(BxWord word) {
        BxWord copy = new BxWord().setBounds(word.getBounds());
        copy.setSorted(word.isSorted());
        for (BxChunk chunk : word.getChunks()) {
            copy.addChunks(chunk);
        }
        return copy;
    }

    /**
     * Creates a deep copy of the line.
     *
     * @param zone
     * @return
     */
    public static BxLine deepClone(BxLine line) {
        BxLine copy = new BxLine().setBounds(line.getBounds());
        copy.setSorted(line.isSorted());
        for (BxWord word : line.getWords()) {
            copy.addWord(deepClone(word));
        }
        return copy;
    }

    /**
     * Creates a deep copy of the zone.
     *
     * @param zone
     * @return
     */
    public static BxZone deepClone(BxZone zone) {
        BxZone copy = new BxZone().setLabel(zone.getLabel()).setBounds(zone.getBounds());
        copy.setSorted(zone.isSorted());
        for (BxLine line : zone.getLines()) {
            copy.addLine(deepClone(line));
        }
        for (BxChunk chunk : zone.getChunks()) {
            copy.addChunk(chunk);
        }
        return copy;
    }

    /**
     * Creates a deep copy of the page.
     * 
     * @param page
     * @return
     */
    public static BxPage deepClone(BxPage page) {
        BxPage copy = new BxPage().setBounds(page.getBounds());
        copy.setSorted(page.isSorted());
        for (BxZone zone : page.getZones()) {
            copy.addZone(deepClone(zone));
        }
        for (BxChunk chunk : page.getChunks()) {
            copy.addChunk(chunk);
        }
        return copy;
    }

    /**
     * Creates a deep copy of the document.
     *
     * @param document
     * @return
     */
    public static BxDocument deepClone(BxDocument document) {
        BxDocument copy = new BxDocument();
        for (BxPage page : document.getPages()) {
            copy.addPage(deepClone(page));
        }
        return copy;
    }

    /**
     * Maps segmented chunks to words which they belong to.
     *
     * @param page
     * @return
     */
    public static Map<BxChunk, BxWord> mapChunksToWords(BxPage page) {
        Map<BxChunk, BxWord> map = new HashMap<BxChunk, BxWord>();
        for (BxZone zone : page.getZones()) {
            for (BxLine line : zone.getLines()) {
                for (BxWord word : line.getWords()) {
                    for (BxChunk chunk : word.getChunks()) {
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
     * @param page
     * @return
     */
    public static Map<BxChunk, BxLine> mapChunksToLines(BxPage page) {
        Map<BxChunk, BxLine> map = new HashMap<BxChunk, BxLine>();
        for (BxZone zone : page.getZones()) {
            for (BxLine line : zone.getLines()) {
                for (BxWord word : line.getWords()) {
                    for (BxChunk chunk : word.getChunks()) {
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
     * @return
     */
    public static Map<BxChunk, BxZone> mapChunksToZones(BxPage page) {
        Map<BxChunk, BxZone> map = new HashMap<BxChunk, BxZone>();
        for (BxZone zone : page.getZones()) {
            for (BxLine line : zone.getLines()) {
                for (BxWord word : line.getWords()) {
                    for (BxChunk chunk : word.getChunks()) {
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
     * @param line
     * @return number of segmented chunks
     */
    public static int countChunks(BxLine line) {
        int chunkCount = 0;
        for (BxWord word : line.getWords()) {
            chunkCount += word.getChunks().size();
        }
        return chunkCount;
    }

    /**
     * Counts segmented chunks in zone.
     *
     * @param zone
     * @return number of segmented chunks
     */
    public static int countChunks(BxZone zone) {
        int chunkCount = 0;
        for (BxLine line : zone.getLines()) {
            for (BxWord word : line.getWords()) {
                chunkCount += word.getChunks().size();
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
}
