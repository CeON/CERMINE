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

package pl.edu.icm.cermine.structure.readingorder;

import java.util.Iterator;
import java.util.Set;
import pl.edu.icm.cermine.structure.model.BxBounds;
import pl.edu.icm.cermine.structure.model.BxObject;
import pl.edu.icm.cermine.structure.model.BxZone;

/**
 * Class used for clustering BxObjects into a tree
 * 
 * @author Pawel Szostek
 */
public class BxZoneGroup extends BxObject<BxZoneGroup, BxZoneGroup, BxZoneGroup> {

    private BxObject leftChild;
    private BxObject rightChild;

    public BxZoneGroup(BxObject child1, BxObject child2) {
        this.leftChild = child1;
        this.rightChild = child2;
        setBounds(Math.min(child1.getX(), child2.getX()),
                Math.min(child1.getY(), child2.getY()),
                Math.max(child1.getX() + child1.getWidth(), child2.getX() + child2.getWidth()),
                Math.max(child1.getY() + child1.getHeight(), child2.getY() + child2.getHeight()));
    }

    public BxZoneGroup(BxObject zone) {
        this.leftChild = zone;
        this.rightChild = null;
        setBounds(zone.getBounds());
    }

    @Override
    public BxZoneGroup setBounds(BxBounds bounds) {
        super.setBounds(bounds);
        return this;
    }

    public boolean hasZone() {
        return (rightChild == null);
    }

    public BxZone getZone() {
        if (!hasZone()) {
            throw new RuntimeException();
        }
        assert this.leftChild instanceof BxZone : "There is one child and its not of type BxZone. How comes?";
        return (BxZone) this.leftChild;
    }

    public BxObject getLeftChild() {
        return leftChild;
    }

    public BxObject getRightChild() {
        return rightChild;
    }

    public BxZoneGroup setLeftChild(BxObject obj) {
        this.leftChild = obj;
        return this;
    }

    public BxZoneGroup setRightChild(BxObject obj) {
        this.rightChild = obj;
        return this;
    }

    public BxZoneGroup setBounds(double x0, double y0, double x1, double y1) {
        assert x1 >= x0;
        assert y1 >= y0;
        this.setBounds(new BxBounds(x0, y0, x1 - x0, y1 - y0));
        return this;
    }

    @Override
    public String getMostPopularFontName() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Set<String> getFontNames() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String toText() {
        return leftChild.toText() + "\n" + rightChild.toText();
    }

    @Override
    public Iterator<BxZoneGroup> iterator() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int childrenCount() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public BxZoneGroup getChild(int index) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
