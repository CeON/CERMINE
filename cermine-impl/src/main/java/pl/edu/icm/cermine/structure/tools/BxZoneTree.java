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

import java.util.ArrayList;
import java.util.List;
import pl.edu.icm.cermine.structure.model.BxZone;

/**
 * @author Pawel Szostek
 */
public class BxZoneTree {

    public static class BxZoneTreeNode {

        private BxZoneOrderTuple tuple;
        private BxZoneTreeNode parent;
        private final List<BxZoneTreeNode> children;

        public BxZoneTreeNode getParent() {
            return parent;
        }

        public void setParent(BxZoneTreeNode parent) {
            this.parent = parent;
        }

        public BxZoneTreeNode(BxZone zone, int order, BxZoneTreeNode parent) {
            this.tuple = new BxZoneOrderTuple(zone, order);
            this.children = new ArrayList<BxZoneTreeNode>();
            this.parent = parent;
        }

        public BxZoneTreeNode(BxZoneOrderTuple node, BxZoneTreeNode parent) {
            this.tuple = node;
            this.children = new ArrayList<BxZoneTreeNode>();
            this.parent = parent;
        }

        public BxZoneOrderTuple getTuple() {
            return tuple;
        }

        public void setTuple(BxZoneOrderTuple tuple) {
            this.tuple = tuple;
        }

        public List<BxZoneTreeNode> getChildren() {
            return children;
        }

        public BxZoneTreeNode addChild(BxZoneOrderTuple tuple) {
            if (this.tuple.getOrder() == tuple.getOrder()) {
                parent.getChildren().add(new BxZoneTreeNode(tuple, parent));
                return parent;
            } else if (this.tuple.getOrder() > tuple.getOrder()) {
                return parent.addChild(tuple);
            } else {
                if (children.isEmpty()) {
                    children.add(new BxZoneTreeNode(tuple, this));
                    return this;
                } else {
                    BxZoneTreeNode lastChild = children.get(children.size() - 1);
                    if (lastChild.getTuple().getOrder() == tuple.getOrder()
                            || lastChild.getTuple().getOrder() > tuple.getOrder()) {
                        children.add(new BxZoneTreeNode(tuple, this));
                        return this;
                    } else {
                        return lastChild.addChild(tuple);
                    }
                }
            }
        }

        private boolean checkChildren(BxZoneTreeNode other) {
            for (int i = 0; i < getChildren().size(); ++i) {
                if (!getChildren().get(i).correspondsTo(other.getChildren().get(i))) {
                    return false;
                }
            }
            return true;
        }

        private boolean correspondsTo(BxZoneTreeNode other) {
            if (getTuple().getZone() == null && other.getTuple().getZone() == null) {
                if (getChildren().size() != other.getChildren().size()) {
                    return false;
                }
                return checkChildren(other);
            }
            if (getTuple().getZone() == null && other.getTuple().getZone() != null) {
                return false;
            } else if (getTuple().getZone() != null && other.getTuple().getZone() == null) {
                return false;
            } else if (!getTuple().getZone().equals(other.getTuple().getZone())) {
                return false;
            } else if (getChildren().size() != other.getChildren().size()) {
                return false;
            } else {
                return checkChildren(other);
            }
        }
    };
    private BxZoneTreeNode root;
    private BxZoneTreeNode lastNodeExtended;

    public BxZoneTree() {
        this.root = new BxZoneTreeNode(null, -1, null);
        this.lastNodeExtended = this.root;
    }

    public BxZoneTreeNode getRoot() {
        return root;
    }

    public void addToRoot(BxZoneOrderTuple node) {
        root.addChild(node);
    }

    public BxZoneTree addNode(BxZone zone, int order) {
        return this.addNode(new BxZoneOrderTuple(zone, order));
    }

    public BxZoneTree addNode(BxZoneOrderTuple what) {
        lastNodeExtended = lastNodeExtended.addChild(what);
        return this;
    }

    public boolean correspondsTo(BxZoneTree other) {
        return root.correspondsTo(other.root);
    }

}
