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

package pl.edu.icm.cermine.structure.model;

import java.io.Serializable;

/**
 * Bounding box of an object. Immutable.
 * 
 * @author Lukasz Bolikowski (bolo@icm.edu.pl)
 */
public final class BxBounds implements Serializable {
    
    private static final long serialVersionUID = 5062840871474513495L;

    /** X coordinate of the bounding box' position on the page */
    private final double x;

    /** Y coordinate of the bounding box' position on the page */
    private final double y;

    /** width of the bounding box */
    private final double width;

    /** height of the bounding box */
    private final double height;

    /**
     * Default constructor returning a unit box at the origin of the coordinate
     * system.
     */
    public BxBounds() {
        this.x = 0.0;
        this.y = 0.0;
        this.width = 1.0;
        this.height = 1.0;
    }

    public BxBounds(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
    
    public double getX() {
        return x;
    }

    public BxBounds withX(double x) {
        return new BxBounds(x, y, width, height);
    }

    public double getY() {
        return y;
    }

    public BxBounds withY(double y) {
        return new BxBounds(x, y, width, height);
    }

    public double getWidth() {
        return width;
    }

    public BxBounds withWidth(double width) {
        return new BxBounds(x, y, width, height);
    }

    public double getHeight() {
        return height;
    }

    public BxBounds withHeight(double height) {
        return new BxBounds(x, y, width, height);
    }

    public boolean isSimilarTo(BxBounds bounds, double tolerance) {
        double diffX1 = Math.abs(x - bounds.getX());
        double diffX2 = Math.abs(x + width - bounds.getX() - bounds.getWidth());
        double diffY1 = Math.abs(y - bounds.getY());
        double diffY2 = Math.abs(y + height - bounds.getY() - bounds.getHeight());

        return ((diffX1 <= tolerance) && (diffX2 <= tolerance) && (diffY1 <= tolerance) && (diffY2 <= tolerance));
    }

}
