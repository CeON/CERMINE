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
import java.util.*;
import pl.edu.icm.cermine.structure.model.*;
import pl.edu.icm.cermine.structure.readingorder.BxZoneGroup;
import pl.edu.icm.cermine.structure.readingorder.DistElem;
import pl.edu.icm.cermine.structure.readingorder.DocumentPlane;
import pl.edu.icm.cermine.structure.readingorder.TreeToListConverter;
import pl.edu.icm.cermine.tools.Utils;
import pl.edu.icm.cermine.tools.timeout.TimeoutRegister;

/**
 * Class for setting a correct logical reading order of objects embedded in a BxDocument.
 *
 * @author Pawel Szostek
 */
public class HierarchicalReadingOrderResolver implements ReadingOrderResolver {

    static final int GRIDSIZE = 50;
    static final double BOXES_FLOW = 0.5;
    static final double EPS = 0.01;
    static final int MAX_ZONES = 1000;
    static final Comparator<BxObject> Y_ASCENDING_ORDER = new Comparator<BxObject>() {

        @Override
        public int compare(BxObject o1, BxObject o2) {
            return Utils.compareDouble(o1.getY(), o2.getY(), EPS);
        }
    };

    static final Comparator<BxObject> X_ASCENDING_ORDER = new Comparator<BxObject>() {

        @Override
        public int compare(BxObject o1, BxObject o2) {
            return Utils.compareDouble(o1.getX(), o2.getX(), EPS);
        }
    };
    
    static final Comparator<BxObject> YX_ASCENDING_ORDER = new Comparator<BxObject>() {

        @Override
        public int compare(BxObject o1, BxObject o2) {
            int yCompare = Y_ASCENDING_ORDER.compare(o1, o2);
            return yCompare == 0 ? X_ASCENDING_ORDER.compare(o1, o2) : yCompare;
        }
    };

    @Override
    public BxDocument resolve(BxDocument messyDoc) {
        BxDocument orderedDoc = new BxDocument();
        List<BxPage> pages = Lists.newArrayList(messyDoc);
        for (BxPage page : pages) {
            List<BxZone> zones = Lists.newArrayList(page);
            for (BxZone zone : zones) {
                List<BxLine> lines = Lists.newArrayList(zone);
                for (BxLine line : lines) {
                    List<BxWord> words = Lists.newArrayList(line);
                    for (BxWord word : words) {
                        List<BxChunk> chunks = Lists.newArrayList(word);
                        Collections.sort(chunks, X_ASCENDING_ORDER);
                        word.resetText();
                        word.setChunks(chunks);
                    }
                    Collections.sort(words, X_ASCENDING_ORDER);
                    line.resetText();
                    line.setWords(words);
                }
                Collections.sort(lines, YX_ASCENDING_ORDER);
                zone.resetText();
                zone.setLines(lines);
            }
            List<BxZone> orderedZones;
            if (zones.size() > MAX_ZONES) {
                orderedZones = new ArrayList<BxZone>(zones);
                Collections.sort(orderedZones, YX_ASCENDING_ORDER);
            } else {
                orderedZones = reorderZones(zones);
            }
            page.setZones(orderedZones);
            page.resetText();
            orderedDoc.addPage(page);
            TimeoutRegister.get().check();
        }
        setIdsAndLinkTogether(orderedDoc);
        return orderedDoc;
    }

    /**
     * Builds a binary tree from list of text zones by doing a hierarchical clustering and converting the result tree to
     * an ordered list.
     *
     * @param zones is a list of unordered zones
     * @return a list of ordered zones
     */
    private List<BxZone> reorderZones(List<BxZone> unorderedZones) {
        if (unorderedZones.isEmpty()) {
            return new ArrayList<BxZone>();
        } else if (unorderedZones.size() == 1) {
            List<BxZone> ret = new ArrayList<BxZone>(1);
            ret.add(unorderedZones.get(0));
            return ret;
        } else {
            BxZoneGroup bxZonesTree = groupZonesHierarchically(unorderedZones);
            sortGroupedZones(bxZonesTree);
            TreeToListConverter treeConverter = new TreeToListConverter();
            List<BxZone> orderedZones = treeConverter.convertToList(bxZonesTree);
            assert unorderedZones.size() == orderedZones.size();
            return orderedZones;
        }
    }
    
    /**
     * Generic function for setting IDs and creating a linked list by filling references. Used solely by
     * setIdsAndLinkTogether(). Can Handle all classes implementing Indexable interface.
     *
     * @param list is a list of Indexable objects
     */
    private <A extends Indexable<A>> void setIdsGenericImpl(List<A> list) {
        if (list.isEmpty()) {
            return;
        }
        if (list.size() == 1) {
            A elem = list.get(0);
            elem.setNext(null);
            elem.setPrev(null);
            elem.setId("0");
            elem.setNextId("-1");
            return;
        }

        //unroll the loop for the first and last element
        A firstElem = list.get(0);
        firstElem.setId("0");
        firstElem.setNextId("1");
        firstElem.setNext(list.get(1));
        firstElem.setPrev(null);
        for (int idx = 1; idx < list.size() - 1; ++idx) {
            A elem = list.get(idx);
            elem.setId(Integer.toString(idx));
            elem.setNextId(Integer.toString(idx + 1));
            elem.setNext(list.get(idx + 1));
            elem.setPrev(list.get(idx - 1));
        }
        A lastElem = list.get(list.size() - 1);
        lastElem.setId(Integer.toString(list.size() - 1));
        lastElem.setNextId("-1");
        lastElem.setNext(null);
        lastElem.setPrev(list.get(list.size() - 2));
    }

    /**
     * Function for setting up indices and reference for the linked list. Causes objects of BxPage, BxZone, BxLine,
     * BxWord and BxChunk to be included in the document's list of elements and sets indices according to the
     * corresponding list order.
     *
     * @param doc is a reference to a document with properly set reading order
     */
    private void setIdsAndLinkTogether(BxDocument doc) {
        setIdsGenericImpl(Lists.newArrayList(doc));
        setIdsGenericImpl(Lists.newArrayList(doc.asZones()));
        setIdsGenericImpl(Lists.newArrayList(doc.asLines()));
        setIdsGenericImpl(Lists.newArrayList(doc.asWords()));
        setIdsGenericImpl(Lists.newArrayList(doc.asChunks()));
    }

    /**
     * Builds a binary tree of zones and groups of zones from a list of unordered zones. This is done in hierarchical
     * clustering by joining two least distant nodes. Distance is calculated in the distance() method.
     *
     * @param zones is a list of unordered zones
     * @return root of the zones clustered in a tree
     */
    private BxZoneGroup groupZonesHierarchically(List<BxZone> zones) {
        /*
         * Distance tuples are stored sorted by ascending distance value
         */
        List<DistElem<BxObject>> dists = new ArrayList<DistElem<BxObject>>(zones.size()*zones.size()/2);
        for (int idx1 = 0; idx1 < zones.size(); ++idx1) {
            for (int idx2 = idx1 + 1; idx2 < zones.size(); ++idx2) {
                BxZone zone1 = zones.get(idx1);
                BxZone zone2 = zones.get(idx2);
                dists.add(new DistElem<BxObject>(false, distance(zone1, zone2),
                        zone1, zone2));
            }
        }
        Collections.sort(dists);
        TimeoutRegister.get().check();
        DocumentPlane plane = new DocumentPlane(zones, GRIDSIZE);
        while (!dists.isEmpty()) {
            DistElem<BxObject> distElem = dists.get(0);
            dists.remove(0);
            if (!distElem.isC() && plane.anyObjectsBetween(distElem.getObj1(), distElem.getObj2())) {
                dists.add(new DistElem<BxObject>(true, distElem.getDist(), distElem.getObj1(), distElem.getObj2()));
                continue;
            }
            TimeoutRegister.get().check();
            BxZoneGroup newGroup = new BxZoneGroup(distElem.getObj1(), distElem.getObj2());
            plane.remove(distElem.getObj1()).remove(distElem.getObj2());
            dists = removeDistElementsContainingObject(dists, distElem.getObj1());
            dists = removeDistElementsContainingObject(dists, distElem.getObj2());
            for (BxObject other : plane.getObjects()) {
                dists.add(new DistElem<BxObject>(false, distance(other,
                        newGroup), newGroup, other));
                TimeoutRegister.get().check();
            }
            Collections.sort(dists);
            TimeoutRegister.get().check();
            plane.add(newGroup);
        }
        
        assert plane.getObjects().size() == 1 : "There should be one object left at the plane after grouping";
        return (BxZoneGroup) plane.getObjects().get(0);
    }

    /**
     * Removes all distance tuples containing obj
     */
    private List<DistElem<BxObject>> removeDistElementsContainingObject(Collection<DistElem<BxObject>> list, BxObject obj) {
        List<DistElem<BxObject>> ret = new ArrayList<DistElem<BxObject>>();
        for (DistElem<BxObject> distElem : list) {
            if (distElem.getObj1() != obj && distElem.getObj2() != obj) {
                ret.add(distElem);
            }
        }
        return ret;
    }

    /**
     * Swaps children of BxZoneGroup if necessary. A group with smaller sort factor is placed to the left (leftChild).
     * An object with greater sort factor is placed on the right (rightChild). This plays an important role when
     * traversing the tree in conversion to a one dimensional list.
     *
     * @param group
     */
    private void sortGroupedZones(BxZoneGroup group) {
        BxObject leftChild = group.getLeftChild();
        BxObject rightChild = group.getRightChild();
        if (shouldBeSwapped(leftChild, rightChild)) {
            // swap
            group.setLeftChild(rightChild);
            group.setRightChild(leftChild);
        }
        
        if (leftChild instanceof BxZoneGroup) // if the child is a tree node, then recurse
        {
            sortGroupedZones((BxZoneGroup) leftChild);
        }
        if (rightChild instanceof BxZoneGroup) // as above - recurse
        {
            sortGroupedZones((BxZoneGroup) rightChild);
        }
    }

    private boolean shouldBeSwapped(BxObject first, BxObject second) {
        double cx, cy, cw, ch, ox, oy, ow, oh;
        cx = first.getBounds().getX();
        cy = first.getBounds().getY();
        cw = first.getBounds().getWidth();
        ch = first.getBounds().getHeight();

        ox = second.getBounds().getX();
        oy = second.getBounds().getY();
        ow = second.getBounds().getWidth();
        oh = second.getBounds().getHeight();

        // Determine Octant
        //
        // 0 | 1 | 2
        // __|___|__
        // 7 | 9 | 3   First is placed in 9th square
        // __|___|__
        // 6 | 5 | 4

        if (cx + cw <= ox) { //2,3,4
        	return false; 
        } else if (ox + ow <= cx) { //0,6,7
        	return true; //6
        } else if (cy + ch <= oy) {
            return false; //5
        } else if (oy + oh <= cy) {
            return true; //1
        } else { //two zones
            double xdiff = ox+ow/2 - cx-cw/2;
            double ydiff = oy+oh/2 - cy-ch/2;
            return xdiff + ydiff < 0;
        }
    }

    /**
     * A distance function between two TextBoxes.
     *
     * Consider the bounding rectangle for obj1 and obj2. Return its area minus the areas of obj1 and obj2, shown as
     * 'www' below. This value may be negative. (x0,y0) +------+..........+ | obj1 |wwwwwwwwww: +------+www+------+
     * :wwwwwwwwww| obj2 | +..........+------+ (x1,y1)
     *
     * @return distance value based on objects' coordinates and physical size on a plane
     *
     */
    private double distance(BxObject obj1, BxObject obj2) {

        double x0 = Math.min(obj1.getX(), obj2.getX());
        double y0 = Math.min(obj1.getY(), obj2.getY());
        double x1 = Math.max(obj1.getX() + obj1.getWidth(),
                obj2.getX() + obj2.getWidth());
        double y1 = Math.max(obj1.getY() + obj1.getHeight(),
                obj2.getY() + obj2.getHeight());
        double dist = ((x1 - x0) * (y1 - y0) - obj1.getArea() - obj2.getArea());

        double obj1X = obj1.getX();
        double obj1CenterX = obj1.getX() + obj1.getWidth() / 2;
        double obj1CenterY = obj1.getY() + obj1.getHeight() / 2;
        double obj2X = obj2.getX();
        double obj2CenterX = obj2.getX() + obj2.getWidth() / 2;
        double obj2CenterY = obj2.getY() + obj2.getHeight() / 2;

        double obj1obj2VectorCosineAbsLeft = Math.abs((obj2X - obj1X) / Math.sqrt((obj2X - obj1X) * (obj2X - obj1X) + (obj2CenterY - obj1CenterY) * (obj2CenterY - obj1CenterY)));     
        double obj1obj2VectorCosineAbsCenter = Math.abs((obj2CenterX - obj1CenterX) / Math.sqrt((obj2CenterX - obj1CenterX) * (obj2CenterX - obj1CenterX) + (obj2CenterY - obj1CenterY) * (obj2CenterY - obj1CenterY)));
        
        double cosine = Math.min(obj1obj2VectorCosineAbsLeft, obj1obj2VectorCosineAbsCenter);
        
        final double MAGIC_COEFF = 0.5;
        return dist * (MAGIC_COEFF + cosine);
    }
}
