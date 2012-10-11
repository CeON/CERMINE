package pl.edu.icm.cermine.structure.tools;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import pl.edu.icm.cermine.structure.model.BxZone;

public class BxZoneTree {
	public static class BxZoneTreeNode {
		private BxZoneOrderTuple tuple;
		private BxZoneTreeNode parent;
		private List<BxZoneTreeNode> children;

		
		public BxZoneTreeNode getParent() {
			return parent;
		}
		public void setParent(BxZoneTreeNode parent) {
			this.parent = parent;
		}
		public BxZoneTreeNode(BxZone zone, Integer order, BxZoneTreeNode parent) {
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
			if (this.tuple.getOrder().equals(tuple.getOrder())) {
				parent.getChildren().add(new BxZoneTreeNode(tuple, parent));
				return parent;
			} else if (this.tuple.getOrder() > tuple.getOrder()) {
				return parent.addChild(tuple);
			} else { // if(zone.getOrder() < tuple.getOrder())
				if(children.isEmpty()) {
					children.add(new BxZoneTreeNode(tuple, this));
					return this;
				} else {
					BxZoneTreeNode lastChild = children.get(children.size() - 1);
					if (lastChild.getTuple().getOrder() == tuple.getOrder()
							|| lastChild.getTuple().getOrder() > tuple.getOrder()) {
						children.add(new BxZoneTreeNode(tuple, this));
						return this;
					} else { // lastChild.getZone().getOrder() > tuple.getOrder()
						return lastChild.addChild(tuple);
					}
				}
			}
		}
		private Boolean checkChildren(BxZoneTreeNode other) {
			for(Integer i=0; i<getChildren().size(); ++i) {
				if(!getChildren().get(i).correspondsTo(other.getChildren().get(i)))
					return false;
			}
			return true;
		}
		private Boolean correspondsTo(BxZoneTreeNode other) {
			if(getTuple().getZone() == null && other.getTuple().getZone() == null) {
				if(getChildren().size() != other.getChildren().size()) {
					return false;
				}
				return checkChildren(other);
			}
			if(getTuple().getZone() == null && other.getTuple().getZone() != null)
				return false;
			else if(getTuple().getZone() != null && other.getTuple().getZone() == null)
				return false;
			else if(!getTuple().getZone().equals(other.getTuple().getZone()))
				return false;
			else if(getChildren().size() != other.getChildren().size())
				return false;
			else {
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

	public BxZoneTree addNode(BxZone zone, Integer order) {
		return this.addNode(new BxZoneOrderTuple(zone, order));
	}
	
	public BxZoneTree addNode(BxZoneOrderTuple what) {
		lastNodeExtended = lastNodeExtended.addChild(what);
		return this;
	}
	
	public Boolean correspondsTo(BxZoneTree other) {
		return root.correspondsTo(other.root);
	}

	public static void main(String[] args) {
		Random rand = new Random();
		BxZoneTree tree = new BxZoneTree();
		for(Integer i=0; i<100; ++i) {
			Integer order;
			do {
				order = rand.nextInt() % 5;
			} while(order < 0);
			System.out.println("adding " + i + " " + order);
			tree.addNode(new BxZone(), order);
		}
		System.out.println(tree.correspondsTo(tree));
		BxZoneTree tree1 = new BxZoneTree();
		BxZoneTree tree2 = new BxZoneTree();
		BxZone zone1 = new BxZone();
		BxZone zone2 = new BxZone();
		BxZone zone3 = new BxZone();
		BxZone zone4 = new BxZone();

		tree1.addNode(zone1, 1);
		tree1.addNode(zone2, 1);
		tree1.addNode(zone3, 2);
		tree1.addNode(zone4, 2);
		
		tree2.addNode(zone1, 1);
		tree2.addNode(zone2, 1);
		tree2.addNode(zone3, 3);
		tree2.addNode(zone4, 2);
		System.out.println(tree1.correspondsTo(tree2));
	}
};
