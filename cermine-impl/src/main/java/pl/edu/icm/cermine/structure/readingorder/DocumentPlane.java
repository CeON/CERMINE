/**
 * This file is part of CERMINE project.
 * Copyright (c) 2011-2013 ICM-UW
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

package pl.edu.icm.cermine.structure.readingorder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import pl.edu.icm.cermine.structure.model.BxBounds;
import pl.edu.icm.cermine.structure.model.BxObject;
import pl.edu.icm.cermine.structure.model.BxZone;

/**
 * A set-like data structure for objects placed on a plane. Can efficiently find objects in a certain rectangular area.
 * It maintains two parallel lists of objects, each of which is sorted by its x or y coordinate.
 *
 * @author Pawel Szostek (p.szostek@icm.edu.pl) @date 05.2012
 *
 */
public class DocumentPlane {

    /**
     * List of objects on the plane. Stored in a random order
     */
    private List<BxObject> objs;
    /**
     * Size of a grid square. If gridSize=50, then the plane is divided into squares of size 50. Each square contains
     * objects placed in a 50x50 area
     */
    private Integer gridSize;
    /**
     * Redundant dictionary of objects on the plane. Allows efficient 2D space search. Keys are X-Y coordinates of a
     * grid square. Single object can be stored under several keys (depending on its physical size). Grid squares are
     * lazy-initialized.
     */
    private Map<GridXY, List<BxObject>> grid;

    /**
     * Representation of XY coordinates
     */
    private static class GridXY {

        public int x;
        public int y;

        public GridXY(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public int hashCode() {
            return x * y;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            GridXY comparedObj = (GridXY) obj;
            if (x != comparedObj.x || y != comparedObj.y) {
                return false;
            }
            return true;
        }

        @Override
        public String toString() {
            return "(" + x + "," + y + ")";
        }
    }; //class GridXY

    public List<BxObject> getObjects() {
        return objs;
    }

    public DocumentPlane(List<BxZone> objectList, Integer gridSize) {
        this.grid = new HashMap<GridXY, List<BxObject>>();
        this.objs = new ArrayList<BxObject>();
        this.gridSize = gridSize;
        for (BxZone obj : objectList) {
            add(obj);
        }
    }

    /**
     * Looks for objects placed between obj1 and obj2 excluding them
     */
    public List<BxObject> findObjectsBetween(BxObject obj1, BxObject obj2) {
        Double x0 = Math.min(obj1.getX(), obj2.getX());
        Double y0 = Math.min(obj1.getY(), obj2.getY());
        Double x1 = Math.max(obj1.getX() + obj1.getWidth(), obj2.getX() + obj2.getWidth());
        Double y1 = Math.max(obj1.getY() + obj1.getHeight(), obj2.getY() + obj2.getHeight());
        assert x1 >= x0 && y1 >= y0;
        BxBounds searchBounds = new BxBounds(x0, y0, x1 - x0, y1 - y0);
        List<BxObject> objsBetween = find(searchBounds);
        /*
         * the rectangle area must contain at least obj1 and obj2
         */
        //	assert objsBetween.size() >= 2;
        objsBetween.remove(obj1);
        objsBetween.remove(obj2);
        return objsBetween;
    }

    /**
     * Checks if there is any object placed between obj1 and obj2
     */
    public boolean anyObjectsBetween(BxObject obj1, BxObject obj2) {
        List<BxObject> lObjs = findObjectsBetween(obj1, obj2);
        return !(lObjs.isEmpty());
    }

    /**
     * Adds object to the plane
     */
    public DocumentPlane add(BxObject obj) {
        Integer objsBefore = this.objs.size();
        /*
         * iterate over grid squares
         */
        for (int y = ((int) obj.getY()) / gridSize; y < ((int) (obj.getY() + obj.getHeight() + gridSize - 1)) / gridSize; ++y) {
            for (int x = ((int) obj.getX()) / gridSize; x < ((int) (obj.getX() + obj.getWidth() + gridSize - 1)) / gridSize; ++x) {
                GridXY xy = new GridXY(x, y);
                if (!grid.keySet().contains(xy)) {
                    /*
                     * add the non-existing key
                     */
                    grid.put(xy, new ArrayList<BxObject>());
                    grid.get(xy).add(obj);
                    assert grid.get(xy).size() == 1;
                } else {
                    grid.get(xy).add(obj);
                }
            }
        }
        objs.add(obj);
        /*
         * size of the object list should be incremented
         */
        assert objsBefore + 1 == objs.size();
        /*
         * object list must contain the same number of objects as object dictionary
         */
        assert objs.size() == elementsInGrid();
        return this;
    }

    public DocumentPlane remove(BxObject obj) {
        /*
         * iterate over grid squares
         */
        for (int y = ((int) obj.getY()) / gridSize; y < ((int) (obj.getY() + obj.getHeight() + gridSize - 1)) / gridSize; ++y) {
            for (int x = ((int) obj.getX()) / gridSize; x < ((int) (obj.getX() + obj.getWidth() + gridSize - 1)) / gridSize; ++x) {
                GridXY xy = new GridXY(x, y);
                if (grid.get(xy).contains(obj)) {
                    grid.get(xy).remove(obj);
                }
            }
        }
        objs.remove(obj);
        assert objs.size() == elementsInGrid();
        return this;
    }

    /**
     * Find objects within search bounds
     *
     * @param searchBounds is a search rectangle
     * @return list of objects in!side search rectangle
     */
    public List<BxObject> find(BxBounds searchBounds) {
        List<BxObject> done = new ArrayList<BxObject>(); //contains already considered objects (wrt. optimization)
        List<BxObject> ret = new ArrayList<BxObject>();
        Double x0 = searchBounds.getX();
        Double y0 = searchBounds.getY();
        Double y1 = searchBounds.getY() + searchBounds.getHeight();
        Double x1 = searchBounds.getX() + searchBounds.getWidth();
        /*
         * iterate over grid squares
         */
        for (int y = y0.intValue() / gridSize; y < ((int) (y1 + gridSize - 1)) / gridSize; ++y) {
            for (Integer x = x0.intValue() / gridSize; x < ((int) (x1 + gridSize - 1)) / gridSize; ++x) {
                GridXY xy = new GridXY(x, y);
                if (!grid.containsKey(xy)) {
                    continue;
                }
                for (BxObject obj : grid.get(xy)) {
                    if (done.contains(obj)) /*
                     * omit if already checked
                     */ {
                        continue;
                    }
                    /*
                     * add to the checked objects
                     */
                    done.add(obj);
                    /*
                     * check if two objects overlap
                     */
                    if (obj.getX() + obj.getWidth() <= x0 || x1 <= obj.getX()
                            || obj.getY() + obj.getHeight() <= y0 || y1 <= obj.getY()) {
                        continue;
                    }
                    ret.add(obj);
                }
            }
        }
        return ret;
    }

    /**
     * Count objects stored in objects dictionary
     */
    protected Integer elementsInGrid() {
        List<BxObject> objs_ = new ArrayList<BxObject>();
        for (GridXY coord : grid.keySet()) {
            for (BxObject obj : grid.get(coord)) {
                if (!objs_.contains(obj)) {
                    objs_.add(obj);
                }
            }
        }
        return objs_.size();
    }

    /**
     * Used for debugging purposes
     */
    public String dump() {
        StringBuilder sb = new StringBuilder();
        for (GridXY iter : grid.keySet()) {
            sb.append(iter.toString()).append(" [");
            for (BxObject obj : grid.get(iter)) {
                if (obj instanceof BxZoneGroup) {
                    BxZoneGroup group = (BxZoneGroup) obj;
                    sb.append(group.getLeftChild());
                    sb.append(group.getRightChild());
                } else if (obj instanceof BxZone) {
                    BxZone zone = (BxZone) obj;
                    sb.append(zone);
                }
                sb.append("\n");
            }
            sb.append("]\n");
        }
        return sb.toString();
    }
};
