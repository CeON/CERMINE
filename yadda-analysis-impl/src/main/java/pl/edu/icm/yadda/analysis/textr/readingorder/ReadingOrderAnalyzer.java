package pl.edu.icm.yadda.analysis.textr.readingorder;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Collection;

import pl.edu.icm.yadda.analysis.textr.model.*;
import pl.edu.icm.yadda.analysis.textr.readingorder.BxZoneGroup;
import pl.edu.icm.yadda.analysis.textr.transformers.BxDocumentToTrueVizWriter;
import pl.edu.icm.yadda.analysis.textr.transformers.TrueVizToBxDocumentReader;
import pl.edu.icm.yadda.metadata.transformers.TransformationException;


/** Class for setting a correct logical reading order of
 * objects embedded in a BxDocument.
 *
 * @author Pawel Szostek (p.szostek@icm.edu.pl)
 * @date 05.2012
 *
 */

public class ReadingOrderAnalyzer {

	final static Integer GRIDSIZE = 50;
	final static Double BOXES_FLOW = 0.5;
	final static Comparator<BxObject> Y_ASCENDING_ORDER = new Comparator<BxObject>() {
		public int compare(BxObject o1, BxObject o2) {
			return (o1.getY() < o2.getY() ? -1 :
				   (o1.getY() == o2.getY() ? 0 : 1));
		}
	};

	final static Comparator<BxObject> X_ASCENDING_ORDER = new Comparator<BxObject>() {
		public int compare(BxObject o1, BxObject o2) {
			return (o1.getX() < o2.getX() ? -1 :
				   (o1.getX() == o2.getX() ? 0 : 1));
		}
	};

	public BxDocument setReadingOrder(BxDocument messyDoc) {
		BxDocument orderedDoc = new BxDocument();
		List<BxPage> pages = messyDoc.getPages();
		for (BxPage page : pages) {
			List<BxZone> zones = page.getZones();
			for (BxZone zone : zones) {
				List<BxLine> lines = zone.getLines();
				for (BxLine line : lines) {
					List<BxWord> words = line.getWords();
					 for(BxWord word: words) { 
						 List<BxChunk> chunks = word.getChunks();
						 Collections.sort(chunks, X_ASCENDING_ORDER);
				    }
					 Collections.sort(words, X_ASCENDING_ORDER);
				}
				Collections.sort(lines, Y_ASCENDING_ORDER);
			}
			List<BxZone> orderedZones = reorderZones(zones);
			page.setZones(orderedZones);
			orderedDoc.addPage(page);
		}
		setIdsAndLinkTogether(orderedDoc);
		return orderedDoc;
	}

	/** Builds a binary tree from list of text zones by doing
	 * a hierarchical clustering and converting the result tree to an ordered list. 
	 * @param zones is a list of unordered zones
	 * @return a list of ordered zones
	 */
	private List<BxZone> reorderZones(List<BxZone> unorderedZones) {
		BxZoneGroup bxZonesTree = groupZonesHierarchically(unorderedZones);
		sortGroupedZones(bxZonesTree);
		TreeToListConverter treeConverter = new TreeToListConverter();
		List<BxZone> orderedZones = treeConverter.convertToList(bxZonesTree);
		assert unorderedZones.size() == orderedZones.size();
		return orderedZones;
	}
	
	/** Generic function for setting IDs and creating a linked list by filling references.
	 * Used solely by setIdsAndLinkTogether(). Can Handle all classes implementing Indexable
	 * interface.
	 * @param list is a list of Indexable objects
	 */
	private <A extends Indexable<A>> void setIdsGenericImpl(List<A> list) {
		if(list.size() == 1) {
			A elem = list.get(0);
			elem.setNext(null);
			elem.setPrev(null);
			elem.setId("0");
			elem.setNextId("-1");
			return;
		}
		/* list.size() > 1 */
		
		//unroll the loop for the first and last element
		A firstElem = list.get(0);
		firstElem.setId("0");
		firstElem.setNextId("1");
		firstElem.setNext(list.get(1));
		firstElem.setPrev(null);
		for(Integer idx = 1; idx < list.size()-1; ++idx) {
			A elem = list.get(idx);
			elem.setId(Integer.toString(idx));
			elem.setNextId(Integer.toString(idx+1));
			elem.setNext(list.get(idx+1));
			elem.setPrev(list.get(idx-1));
		}
		A lastElem = list.get(list.size()-1);
		lastElem.setId(Integer.toString(list.size()-1));
		lastElem.setNextId("-1");
		lastElem.setNext(null);
		lastElem.setPrev(list.get(list.size()-2));
	}
	
	/** Function for setting up indices and reference for the linked list.
	 * Causes objects of BxPage, BxZone, BxLine, BxWord and BxChunk to be included in the document's
	 * list of elements and sets indices according to the corresponding list order.
	 * 
	 * @param doc is a reference to a document with properly set reading order
	 */
	private void setIdsAndLinkTogether(BxDocument doc) {
		setIdsGenericImpl(doc.asPages());
		setIdsGenericImpl(doc.asZones());
		setIdsGenericImpl(doc.asLines());
		setIdsGenericImpl(doc.asWords());
		setIdsGenericImpl(doc.asChunks());
	}
	private String s(String string) {
		if(string.length() <= 10)
			return string;
		return string.substring(0, 10);
	}
	/** Builds a binary tree of zones and groups of zones from a list of unordered zones.
	 * This is done in hierarchical clustering by joining two least distant nodes. Distance
	 * is calculated in the distance() method.
	 * @param zones is a list of unordered zones
	 * @return root of the zones clustered in a tree
	 */
	private BxZoneGroup groupZonesHierarchically(List<BxZone> zones) {
		/* Distance tuples are stored sorted by ascending distance value */
		List<DistElem<BxObject>> dists = new ArrayList<DistElem<BxObject>>();
		for (int idx1 = 0; idx1 < zones.size(); ++idx1)
			for (int idx2 = idx1 + 1; idx2 < zones.size(); ++idx2) {
				BxZone zone1 = zones.get(idx1);
				BxZone zone2 = zones.get(idx2);
				dists. add(new DistElem<BxObject>(false, distance(zone1, zone2),
						zone1, zone2));
			}
		Collections.sort(dists);
		DocumentPlane plane = new DocumentPlane(zones, GRIDSIZE);
		while (!dists.isEmpty()) {
			DistElem<BxObject> distElem = dists.get(0);
			dists.remove(0);
			if (distElem.c == false && plane.anyObjectsBetween(distElem.obj1, distElem.obj2)) {
				dists.add(new DistElem<BxObject>(true, distElem.dist, distElem.obj1, distElem.obj2));
				continue;
			}
		
		// !!!! Code below is used for debugging purposes

		/*	String obj1Content = null; 
			try {
				obj1Content = ((BxZone)distElem.obj1).toText();
			} catch (Exception e) {
				obj1Content = "";
			}
			String obj2Content = null; 
			try {
				obj2Content = ((BxZone)distElem.obj2).toText();
			} catch (Exception e) {
				obj2Content = "";
			}
		    System.out.println("(" + distElem.obj1.getX() + ", " + distElem.obj1.getY() + ", " + (int)distElem.obj1.getWidth() + ", " + (int)distElem.obj1.getHeight() + ": "+ s(obj1Content)+"["+obj1Content.length()+"])" 
				+ " + (" + distElem.obj2.getX() + ", " + distElem.obj2.getY() + ", " + (int)distElem.obj2.getWidth() + ", " + (int)distElem.obj2.getHeight() + ": "+ s(obj2Content)+"["+obj2Content.length()+"]) " +distElem.dist);
			*/
			BxZoneGroup newGroup = new BxZoneGroup(distElem.obj1, distElem.obj2);
			plane.remove(distElem.obj1).remove(distElem.obj2);
			dists = removeDistElementsContainingObject(dists, distElem.obj1);
			dists = removeDistElementsContainingObject(dists, distElem.obj2);
			for (BxObject other : plane.getObjects()) {
				dists.add(new DistElem<BxObject>(false, distance(other,
						newGroup), newGroup, other));
			}
			Collections.sort(dists);
			plane.add(newGroup);
		}
  //    System.out.println("");
		assert plane.getObjects().size() == 1 : "There should be one object left at the plane after grouping";
		return (BxZoneGroup) plane.getObjects().get(0);
	}

	/** Removes all distance tuples containing obj */
	private List<DistElem<BxObject> > removeDistElementsContainingObject(Collection<DistElem<BxObject>> list, BxObject obj) {
		List<DistElem<BxObject> > ret = new ArrayList<DistElem<BxObject> >();
		for (DistElem<BxObject> distElem : list) {
			if (distElem.obj1 != obj && distElem.obj2 != obj)
				ret.add(distElem);
		}
		return ret;
	}
/*
	private void sortTree(BxZoneGroup tree) {
		if(tree.getLeftChild() != null && tree.getRightChild() != null) {
			double leftChildDist = distFromRoot(tree.getLeftChild());
			double rightChildDist = distFromRoot(tree.getRightChild());
			if(rightChildDist < leftChildDist) {
				// swap
				BxObject tmp = tree.getLeftChild();
				tree.setLeftChild(tree.getRightChild());
				tree.setRightChild(tmp);
			}
			if(tree.getLeftChild() instanceof BxZoneGroup) {
				sortGroupedZones((BxZoneGroup)tree.getLeftChild());
			}
			if(tree.getRightChild() instanceof BxZoneGroup) {
				sortGroupedZones((BxZoneGroup)tree.getRightChild());
			}
		}
	}
	*/
	
	/** Swaps children of BxZoneGroup if necessary. A group with smaller sort factor
	 * is placed to the left (leftChild). An object with greater sort factor is placed
	 * on the right (rightChild). This plays an important role when traversing the tree
	 * in conversion to a one dimensional list.
	 * @param group
	 */
	private void sortGroupedZones(BxZoneGroup group) {
		BxObject leftChild = group.getLeftChild();
		BxObject rightChild = group.getRightChild();
		Double leftChildSortPrecedence = sortPrecedence(leftChild);
		Double rightChildSortPrecedence = sortPrecedence(rightChild);
		if (leftChildSortPrecedence < rightChildSortPrecedence) {
			// the order is fine, don't do anything
		} else {
			// swap
			group.setLeftChild(rightChild);
			group.setRightChild(leftChild);
		}
		if (leftChild instanceof BxZoneGroup) // if the child is a tree node, then recurse
			sortGroupedZones((BxZoneGroup) leftChild);
		if (rightChild instanceof BxZoneGroup) // as above - recurse
			sortGroupedZones((BxZoneGroup) rightChild);
	}
	
	/** Key function for sorting in sortGroupedZones(). Allows to order
	 * two objects joined together in a logical order.
	 * 
	 * @param obj is an object to have a sorting key
	 * @return value based on object's physical properties
	 */
	private Double sortPrecedence(BxObject obj) {
		/* para-euclidean distance from (0,0) */
		return Math.sqrt((obj.getX()+obj.getWidth()/2)*(obj.getX()+obj.getWidth()/2)+obj.getY()*obj.getY());
	}
	
	/**
	 * A distance function between two TextBoxes.
	 * 
	 * Consider the bounding rectangle for obj1 and obj2. 
	 * Return its area minus the areas of obj1 and obj2,
	 * shown as 'www' below. This value may be negative.
	 *         (x0,y0) +------+..........+
	 *                 | obj1 |wwwwwwwwww:
	 *                 +------+www+------+ 
	 *                 :wwwwwwwwww| obj2 |
	 *                 +..........+------+ (x1,y1)
	 *                 
	 * @return distance value based on objects' coordinates and physical size on
	 * 		   a plane      
	 *             
	 */
	private Double distance(BxObject obj1, BxObject obj2) {

		Double x0 = Math.min(obj1.getX(), obj2.getX());
		Double y0 = Math.min(obj1.getY(), obj2.getY());
		Double x1 = Math.max(obj1.getX() + obj1.getWidth(),
				obj2.getX() + obj2.getWidth());
		Double y1 = Math.max(obj1.getY() + obj1.getHeight(),
				obj2.getY() + obj2.getHeight());
		Double dist = ((x1 - x0) * (y1 - y0) - obj1.getArea() - obj2.getArea());
		
		Double obj1CenterX = obj1.getX()+obj1.getWidth()/2;
		Double obj1CenterY = obj1.getY()+obj1.getHeight()/2;
		Double obj2CenterX = obj2.getX()+obj2.getWidth()/2;
		Double obj2CenterY = obj2.getY()+obj2.getHeight()/2;
		
		Double obj1obj2VectorCosineAbs = Math.abs((obj2CenterX-obj1CenterX)/Math.sqrt((obj2CenterX-obj1CenterX)*(obj2CenterX-obj1CenterX)+(obj2CenterY-obj1CenterY)*(obj2CenterY-obj1CenterY))); 
		final Double MAGIC_COEFF = 0.5;
		return dist*(MAGIC_COEFF+obj1obj2VectorCosineAbs);
	}

	public static void main(String[] args) throws TransformationException, IOException {
	//	String filename = "13191004.xml";
	//	String filename = "10255834.xml";
	//	String filename = "11781238.xml";
	//	String filename = "1748717X.xml";
		String filename = "02.xml";
		String path = "/pl/edu/icm/yadda/analysis/logicstr/train/";
	//	String inFile = path + filename;
		String inFile = "/pl/edu/icm/yadda/analysis/metadata/zoneclassification/09629351.xml";
		InputStream is = ReadingOrderAnalyzer.class.getResourceAsStream(inFile);
		InputStreamReader isr = new InputStreamReader(is);

		TrueVizToBxDocumentReader reader = new TrueVizToBxDocumentReader();
		BxDocument doc = new BxDocument().setPages(reader.read(isr));

		BxObjectDump dump = new BxObjectDump();
	//	System.out.println(dump.dump(doc));

		ReadingOrderAnalyzer roa = new ReadingOrderAnalyzer();
		BxDocument sortedDoc = roa.setReadingOrder(doc);

		for (BxPage page : sortedDoc.getPages()) {
			for (BxZone zone : page.getZones()) {
				System.out.format("(X=%5.2f, Y=%5.2f, W=%5.2f, H=%5.2f)\n", zone.getX(), zone.getY(), zone.getWidth(), zone.getHeight());
				System.out.println(zone.toText() + "\n");
			}
		}
		
		BxDocumentToTrueVizWriter trueVizWriter = new BxDocumentToTrueVizWriter();
		
		FileWriter fw = new FileWriter(filename + ".out");
		fw.write(trueVizWriter.write(sortedDoc.getPages()));
		fw.flush();
		fw.close();
		
		fw = new FileWriter(filename + ".dump");
		fw.write(dump.dump(sortedDoc));
		fw.flush();
		fw.close();
		
	//	System.out.println(dump.dump(sortedDoc));

		isr.close();
	}
}
