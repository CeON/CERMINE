package pl.edu.icm.yadda.analysis.metadata.zoneclassification.features;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Stack;

import pl.edu.icm.yadda.analysis.classification.features.FeatureCalculator;
import pl.edu.icm.yadda.analysis.textr.model.BxChunk;
import pl.edu.icm.yadda.analysis.textr.model.BxLine;
import pl.edu.icm.yadda.analysis.textr.model.BxPage;
import pl.edu.icm.yadda.analysis.textr.model.BxWord;
import pl.edu.icm.yadda.analysis.textr.model.BxZone;

public class EmptySpaceFeature implements FeatureCalculator<BxZone, BxPage> {

	private static String featureName = "EmptySpace";

	static class Point {
		public Point(double x, double y) {
			this.x = x;
			this.y = y;
		}

		public double x;
		public double y;
	};

	private static double det(Point p1, Point p2, Point p3) {
		return p1.x * p2.y + p2.x * p3.y + p3.x * p1.y - p3.x * p2.y - p1.x
				* p3.y - p2.x * p1.y;
	}

	private static class LexicographicSorter {
		private double alpha(Point p) {
			if (p.x >= 0)
				if (p.y >= 0)
					return p.y / d(p);
				else
					return 4 - Math.abs(p.y) / d(p);
			else if (p.y >= 0)
				return 2 - p.y / d(p);
			else
				return 2 + Math.abs(p.y) / d(p);
		}

		private double d(Point p) {
			return Math.abs(p.x) + Math.abs(p.y);
		}

		private class AlphaComparator implements Comparator<Point> {
			private int compareX(Point p1, Point p2) {
				return (p1.x > p2.x ? -1 : (p1.x == p2.x ? 0 : 1));
			}

			@Override
			public int compare(Point p1, Point p2) {
				double a1 = alpha(p1);
				double a2 = alpha(p2);
				if (a1 > a2)
					return -1;
				else if (a1 == a2)
					return compareX(p1, p2);
				else
					return -1;
			}
		}

		public List<Point> sortLexicographically(List<Point> points) {
			List<Point> ret = new ArrayList<Point>(points);
			Collections.sort(ret, new AlphaComparator());
			return ret;
		}
	};

	static class ConvexHullCalculator {
		private boolean turnsRight(Stack<Point> stack, Point p3) {
			Point p2, p1;
			p2 = stack.peek();
			stack.pop();
			p1 = stack.peek();
			stack.push(p2);
			if (det(p1, p2, p3) > 0)
				return false;
			else
				return true;
		}

		public List<Point> calculateConvexHull(BxZone zone) {
			List<Point> points = new ArrayList<Point>();
			for (BxLine line : zone.getLines())
				for (BxWord word : line.getWords())
					for (BxChunk chunk : word.getChunks()) {
						points.add(new Point(chunk.getX(), chunk.getY()));
						points.add(new Point(chunk.getX() + chunk.getWidth(),
								chunk.getY()));
						points.add(new Point(chunk.getX(), chunk.getY()
								+ chunk.getHeight()));
						points.add(new Point(chunk.getX() + chunk.getWidth(),
								chunk.getY() + chunk.getHeight()));
					}
			LexicographicSorter sorter = new LexicographicSorter();
			List<Point> sortedPoints = sorter.sortLexicographically(points);
			Stack<Point> stack = new Stack<Point>();
			stack.add(sortedPoints.get(0));
			sortedPoints.remove(0);
			stack.add(sortedPoints.get(0));
			sortedPoints.remove(0);
			stack.add(sortedPoints.get(0));
			sortedPoints.remove(0);
			for (Point curPoint : sortedPoints) {
				System.out.println(curPoint.x + " " + curPoint.y + " " + stack.size());
				while (turnsRight(stack, curPoint))
					stack.pop();
				stack.push(curPoint);
			}
			assert stack.size() <= points.size();
			return new ArrayList<Point>(stack);
		}
	};

	static class AreaCalculator {
		public double calculateArea(List<Point> convexHull) {
			double area = 0.0;
			for (int i = 0; i < convexHull.size(); ++i)
				area += convexHull.get(i).x
						* (convexHull.get(Math.abs((i + 1) % convexHull.size())).y - convexHull .get(Math.abs((i - 1) % convexHull.size())).y);
			return area / 2;
		}
	};

	@Override
	public String getFeatureName() {
		return featureName;
	}

	@Override
	public double calculateFeatureValue(BxZone zone, BxPage page) {
		double usedSpace = 0.0;
		for (BxLine line : zone.getLines()) {
			for (BxWord word : line.getWords()) {
				for (BxChunk chunk : word.getChunks()) {
					usedSpace += chunk.getArea();
				}
			}
		}
		ConvexHullCalculator hullCalculator = new ConvexHullCalculator();
		List<Point> hull = hullCalculator.calculateConvexHull(zone);
		AreaCalculator areaCalculator = new AreaCalculator();
		double zoneArea = areaCalculator.calculateArea(hull);
		return zoneArea - usedSpace;
	}
}
