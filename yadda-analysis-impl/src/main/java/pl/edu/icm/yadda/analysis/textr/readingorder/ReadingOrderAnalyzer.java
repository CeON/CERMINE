package pl.edu.icm.yadda.analysis.textr.readingorder;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

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
	private <A extends Indexable> void setIdsGenericImpl(List<A> list) {
		Integer index = 0;
		for(A elem: list) {
			elem.setId(Integer.toString(index));
			++index;
			elem.setNextId(Integer.toString(index));
		}
		list.get(list.size()-1).setNextId("-1");
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
	
	/** Builds a binary tree of zones and groups of zones from a list of unordered zones.
	 * This is done in hierarchical clustering by joining two least distant nodes. Distance
	 * is calculated in the distance() method.
	 * @param zones is a list of unordered zones
	 * @return root of the zones clustered in a tree
	 */
	private BxZoneGroup groupZonesHierarchically(List<BxZone> zones) {
		/* Distance tuples are stored sorted by ascending distance value */
		PriorityQueue<DistElem<BxObject>> dists = new PriorityQueue<DistElem<BxObject>>();
		for (int idx1 = 0; idx1 < zones.size(); ++idx1)
			for (int idx2 = idx1 + 1; idx2 < zones.size(); ++idx2) {
				BxZone zone1 = zones.get(idx1);
				BxZone zone2 = zones.get(idx2);
				dists. add(new DistElem<BxObject>(false, distance(zone1, zone2),
						zone1, zone2));
			}
		DocumentPlane plane = new DocumentPlane(zones, GRIDSIZE);
		while (!dists.isEmpty()) {
			DistElem<BxObject> distElem = dists.poll();
			if (distElem.c == false
					&& plane.anyObjectsBetween(distElem.obj1, distElem.obj2)) {
				dists.add(new DistElem<BxObject>(true, distElem.dist,
						distElem.obj1, distElem.obj2));
				continue;
			}
				
	//	    System.out.println("(" + distElem.obj1.getX() + ", " + distElem.obj1.getY() + ", " + distElem.obj1.getWidth() + ", " + distElem.obj1.getHeight() + ")" 
	//				+ " + (" + distElem.obj2.getX() + ", " + distElem.obj2.getY() + ", " + distElem.obj2.getWidth() + ", " + distElem.obj2.getHeight() + ") " +distElem.dist);
			
			BxZoneGroup newGroup = new BxZoneGroup(distElem.obj1, distElem.obj2);
			plane.remove(distElem.obj1).remove(distElem.obj2);
			dists = removeDistElementsContainingObject(dists, distElem.obj1);
			dists = removeDistElementsContainingObject(dists, distElem.obj2);
			for (BxObject other : plane.getObjects()) {
				dists.add(new DistElem<BxObject>(false, distance(other,
						newGroup), newGroup, other));
			}
			plane.add(newGroup);
		}
	//	System.out.println("");
		assert plane.getObjects().size() == 1 : "There should be one object left at the plane after grouping";
		return (BxZoneGroup) plane.getObjects().get(0);
	}

	/** Removes all distance tuples containing obj */
	private PriorityQueue<DistElem<BxObject> > removeDistElementsContainingObject(Collection<DistElem<BxObject>> list, BxObject obj) {
		PriorityQueue<DistElem<BxObject> > ret = new PriorityQueue<DistElem<BxObject> >();
		for (DistElem<BxObject> distElem : list) {
			if (distElem.obj1 != obj && distElem.obj2 != obj)
				ret.add(distElem);
		}
		return ret;
	}

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
		/* black magic below */
		return  (1 + BOXES_FLOW) //constant
				* (2 * obj.getY() + obj.getHeight()) // y0 + y1
				+ (1 - BOXES_FLOW) //constant
				* (obj.getX()) // x0
				; 
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
		return dist;
	}

	public static void main(String[] args) throws TransformationException, IOException {
	//	String filename = "13191004.xml";
	//	String filename = "10255834.xml";
	//	String filename = "11781238.xml";
	//	String filename = "1748717X.xml";
		String filename = "09629351.xml";
		String path = "/pl/edu/icm/yadda/analysis/logicstr/train/";
		String inFile = path + filename;
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
				System.out.println("("+zone.getX()+", "+zone.getY()+", "+zone.getWidth()+", "+zone.getHeight()+")");
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
