package pl.edu.icm.coansys.metaextr.textr.tools;

import pl.edu.icm.coansys.metaextr.textr.model.BxBounds;
import pl.edu.icm.coansys.metaextr.textr.model.BxChunk;
import pl.edu.icm.coansys.metaextr.textr.model.BxLine;
import pl.edu.icm.coansys.metaextr.textr.model.BxPage;
import pl.edu.icm.coansys.metaextr.textr.model.BxWord;
import pl.edu.icm.coansys.metaextr.textr.model.BxZone;

/**
 * Bounding box builder class.
 * 
 * Initially bounding box builder contains empty bounding box (null). Successive
 * calls of expand methods expand current bounding box so that it contains given
 * objects. If current bonding box is empty, expanding it is equivalent to
 * setting it to the dimensions of the given object.
 * 
 * @author krusek
 */
public class BxBoundsBuilder {

    private double minX = Double.POSITIVE_INFINITY;
    private double minY = Double.POSITIVE_INFINITY;
    private double maxX = Double.NEGATIVE_INFINITY;
    private double maxY = Double.NEGATIVE_INFINITY;

    /**
     * Expands current bounding box so that it contains bounding boxes
     * of page zones.
     *
     * @param page
     */
    public void expandByZones(BxPage page) {
        for (BxZone zone : page.getZones()) {
            expand(zone.getBounds());
        }
    }

    /**
     * Expands current bounding box so that it contains bounding boxes
     * of zone lines.
     *
     * @param zone
     */
    public void expandByLines(BxZone zone) {
        for (BxLine line : zone.getLines()) {
            expand(line.getBounds());
        }
    }

    /**
     * Expands current bounding box so that it contains bounding boxes
     * of line words.
     *
     * @param line
     */
    public void expandByWords(BxLine line) {
        for (BxWord word : line.getWords()) {
            expand(word.getBounds());
        }
    }

    /**
     * Expands current bounding box so that it contains bounding boxes
     * of word chunks.
     *
     * @param line
     */
    public void expandByChunks(BxWord word) {
        for (BxChunk chunk : word.getChunks()) {
            expand(chunk.getBounds());
        }
    }

    /**
     * Expands current bounding box so that it contains the point (x, y).
     *
     * @param x x coordinate of the point
     * @param y y coordinate of the point
     */
    public void expand(double x, double y) {
        minX = Math.min(minX, x);
        minY = Math.min(minY, y);
        maxX = Math.max(maxX, x);
        maxY = Math.max(maxY, y);
    }

    /**
     * Expands current bounding box so that it contains given bounding box.
     * If given bounding box is null, this method has no effect.
     * 
     * @param bounds
     */
    public void expand(BxBounds bounds) {
        if (bounds != null) {
            minX = Math.min(minX, bounds.getX());
            minY = Math.min(minY, bounds.getY());
            maxX = Math.max(maxX, bounds.getX() + bounds.getWidth());
            maxY = Math.max(maxY, bounds.getY() + bounds.getHeight());
        }
    }

    /**
     * Returns current bounding box or null if current bounding box is empty.
     *
     * @return
     */
    public BxBounds getBounds() {
        if (minX <= maxX && minY <= maxY) {
            return new BxBounds(minX, minY, maxX - minX, maxY - minY);
        } else {
            return null;
        }
    }

    /**
     * Replaces current bounding box with empty bounding box.
     */
    public void clear() {
        minX = Double.POSITIVE_INFINITY;
        minY = Double.POSITIVE_INFINITY;
        maxX = Double.NEGATIVE_INFINITY;
        maxY = Double.NEGATIVE_INFINITY;
    }

    /**
     * Sets the bounding box of page based on bounding boxes of page zones.
     *
     * @param page
     */
    public static void setBounds(BxPage page) {
        BxBoundsBuilder builder = new BxBoundsBuilder();
        builder.expandByZones(page);
        page.setBounds(builder.getBounds());
    }

    /**
     * Sets the bounding box of zone based on bounding boxes of zone lines.
     *
     * @param zone
     */
    public static void setBounds(BxZone zone) {
        BxBoundsBuilder builder = new BxBoundsBuilder();
        builder.expandByLines(zone);
        zone.setBounds(builder.getBounds());
    }

    /**
     * Sets the bounding box of line based on bounding boxes of line words.
     *
     * @param zone
     */
    public static void setBounds(BxLine line) {
        BxBoundsBuilder builder = new BxBoundsBuilder();
        builder.expandByWords(line);
        line.setBounds(builder.getBounds());
    }

    /**
     * Sets the bounding box of word based on bounding boxes of word chunks.
     *
     * @param zone
     */
    public static void setBounds(BxWord word) {
        BxBoundsBuilder builder = new BxBoundsBuilder();
        builder.expandByChunks(word);
        word.setBounds(builder.getBounds());
    }
}
