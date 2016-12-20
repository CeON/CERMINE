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
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.structure.model.*;
import pl.edu.icm.cermine.structure.tools.BxBoundsBuilder;
import pl.edu.icm.cermine.structure.tools.BxModelUtils;
import pl.edu.icm.cermine.tools.DisjointSets;
import pl.edu.icm.cermine.tools.Histogram;
import pl.edu.icm.cermine.tools.timeout.TimeoutRegister;

/**
 * Page segmenter using Docstrum algorithm.
 * 
 * @author Krzysztof Rusek
 */
public class DocstrumSegmenter implements DocumentSegmenter {

    public static final int MAX_ZONES_PER_PAGE = 300;
    public static final int PAGE_MARGIN = 2;
    public static final double ORIENTATION_MARGIN = 0.2;
    public static final int LINES_PER_PAGE_MARGIN = 100;
    
    private double docOrientation = Double.NaN;
    
    private Map<BxPage, List<Component>> componentMap = new HashMap<BxPage, List<Component>>();
    
    @Override
    public BxDocument segmentDocument(BxDocument document) throws AnalysisException {
        computeDocumentOrientation(document);
        BxDocument output = new BxDocument();
        for (BxPage page: document) {
        	BxPage segmentedPage = segmentPage(page);
        	if (segmentedPage.getBounds() != null) {
        		output.addPage(segmentedPage);
        	}
        }
        return output;
    }
    
    protected void computeDocumentOrientation(BxDocument document) throws AnalysisException {
        List<Component> components = new ArrayList<Component>();
        for (BxPage page : document.asPages()) {
            List<Component> pageComponents = createComponents(page);
            componentMap.put(page, pageComponents);
            components.addAll(pageComponents);
        }
        
        docOrientation = computeInitialOrientation(components);
    }
    
    protected void computeDocumentOrientation(Map<BxPage, List<Component>> componentMap) throws AnalysisException {
        this.componentMap = componentMap;
        List<Component> components = new ArrayList<Component>();
        for (Map.Entry<BxPage, List<Component>> entry : componentMap.entrySet()) {
            components.addAll(entry.getValue());
        }
        docOrientation = computeInitialOrientation(components);
    }
    
    protected BxPage segmentPage(BxPage page) throws AnalysisException {
        List<Component> components = componentMap.get(page);
        double orientation = docOrientation;
        if (Double.isNaN(orientation)) {
            orientation = computeInitialOrientation(components);
        }
        double characterSpacing = computeCharacterSpacing(components, orientation);
        double lineSpacing = computeLineSpacing(components, orientation);

        List<ComponentLine> lines = determineLines(components, orientation,
                characterSpacing * componentDistanceCharacterMultiplier,
                lineSpacing * maxVerticalComponentDistanceMultiplier);
    
        if (Math.abs(orientation) > ORIENTATION_MARGIN) {
            List<ComponentLine> linesZero = determineLines(components, 0,
                characterSpacing * componentDistanceCharacterMultiplier,
                lineSpacing * maxVerticalComponentDistanceMultiplier);
    
            if (Math.abs(lines.size() - LINES_PER_PAGE_MARGIN) > Math.abs(linesZero.size() - LINES_PER_PAGE_MARGIN)) {
                orientation = 0;
                lines = linesZero;
            }
        }
        
        double lineOrientation = computeOrientation(lines);
        if (!Double.isNaN(lineOrientation)) {
            orientation = lineOrientation;
        }
        
        List<List<ComponentLine>> zones = determineZones(lines, orientation,
                characterSpacing * minHorizontalDistanceMultiplier, Double.POSITIVE_INFINITY,
                lineSpacing * minVerticalDistanceMultiplier, lineSpacing * maxVerticalDistanceMultiplier,
                characterSpacing * minHorizontalMergeDistanceMultiplier, 0.0,
                0.0, lineSpacing * maxVerticalMergeDistanceMultiplier);
        zones = mergeZones(zones, characterSpacing * 0.5);
        zones = mergeLines(zones, orientation,
                Double.NEGATIVE_INFINITY, 0.0,
                0.0, lineSpacing * maxVerticalMergeDistanceMultiplier);
        return convertToBxModel(page, zones, wordDistanceMultiplier * characterSpacing);
    }

    /**
     * Constructs sorted by x coordinate array of components from page's chunks.
     *
     * @param page page containing chunks
     * @return array of components
     * @throws AnalysisException AnalysisException
     */
    protected List<Component> createComponents(BxPage page) throws AnalysisException {
        List<BxChunk> chunks = Lists.newArrayList(page.getChunks());
        Component[] components = new Component[chunks.size()];
        for (int i = 0; i < components.length; i++) {
            try {
                components[i] = new Component(chunks.get(i));
            } catch(IllegalArgumentException ex) {
                throw new AnalysisException(ex);
            }
        }
        TimeoutRegister.get().check();
        Arrays.sort(components, ComponentXComparator.getInstance());
        TimeoutRegister.get().check();
        findNeighbors(components);
        return Arrays.asList(components);
    }
    
    /**
     * Performs for each component search for nearest-neighbors and stores the
     * result in component's neighbors attribute.
     *
     * @param components array of components
     * @throws AnalysisException if the number of components is less than or
     * equal to the number of nearest-neighbors per component.
     */
    private void findNeighbors(Component[] components) throws AnalysisException {
        if (components.length == 0) {
            return;
        }
        if (components.length == 1) {
            components[0].setNeighbors(new ArrayList<Neighbor>());
            return;
        }
        int pageNeighborCount = neighborCount;
        if (components.length <= neighborCount) {
            pageNeighborCount = components.length - 1;
        }

        List<Neighbor> candidates = new ArrayList<Neighbor>();
        for (int i = 0; i < components.length; i++) {
            int start = i, end = i + 1;
            // Contains components from components array
            // from ranges [start, i) and [i+1, end)
            double dist = Double.POSITIVE_INFINITY;
            for (double searchDist = 0; searchDist < dist; ) {
                searchDist += DISTANCE_STEP;
                boolean newCandidatesFound = false;
                
                while (start > 0 && components[i].getX() - components[start - 1].getX() < searchDist) {
                    start--;
                    candidates.add(new Neighbor(components[start], components[i]));
                    if (candidates.size() > pageNeighborCount) {
                        Collections.sort(candidates, NeighborDistanceComparator.getInstance());
                        candidates.subList(pageNeighborCount, candidates.size()).clear();
                    }
                    newCandidatesFound = true;
                }
                TimeoutRegister.get().check();
                while (end < components.length && components[end].getX() - components[i].getX() < searchDist) {
                    candidates.add(new Neighbor(components[end], components[i]));
                    if (candidates.size() > pageNeighborCount) {
                        Collections.sort(candidates, NeighborDistanceComparator.getInstance());
                        candidates.subList(pageNeighborCount, candidates.size()).clear();
                    }
                    end++;
                    newCandidatesFound = true;
                }
                
                if (newCandidatesFound && candidates.size() >= pageNeighborCount) {
                    Collections.sort(candidates, NeighborDistanceComparator.getInstance());
                    dist = candidates.get(pageNeighborCount - 1).getDistance();
                }
                TimeoutRegister.get().check();
            }
            candidates.subList(pageNeighborCount, candidates.size()).clear();
            components[i].setNeighbors(new ArrayList<Neighbor>(candidates));
            candidates.clear();
        }
    }

    /**
     * Computes initial orientation estimation based on nearest-neighbors' angles.
     * 
     * @param components
     * @return initial orientation estimation
     */
    private double computeInitialOrientation(List<Component> components) {
        Histogram histogram = new Histogram(-Math.PI/2, Math.PI/2, angleHistogramResolution);
        for (Component component : components) {
            for (Neighbor neighbor : component.getNeighbors()) {
                histogram.add(neighbor.getAngle());
            }
        }
        // Rectangular smoothing window has been replaced with gaussian smoothing window
        histogram.circularGaussianSmooth(angleHistogramSmoothingWindowLength,
                angleHistogramSmoothingWindowStdDeviation);
        return histogram.getPeakValue();
    }

    /**
     * Computes within-line spacing based on nearest-neighbors distances.
     *
     * @param components
     * @param orientation estimated text orientation
     * @return estimated within-line spacing
     */
    private double computeCharacterSpacing(List<Component> components, double orientation) {
        return computeSpacing(components, orientation);
    }

    /**
     * Computes between-line spacing based on nearest-neighbors distances.
     *
     * @param components
     * @param orientation estimated text orientation
     * @return estimated between-line spacing
     */
    private double computeLineSpacing(List<Component> components, double orientation) {
        if (orientation >= 0) {
            return computeSpacing(components, orientation - Math.PI / 2);
        } else {
            return computeSpacing(components, orientation + Math.PI / 2);
        }
    }

    private double computeSpacing(List<Component> components, double angle) {
        double maxDistance = Double.NEGATIVE_INFINITY;
        for (Component component : components) {
            for (Neighbor neighbor : component.getNeighbors()) {
                maxDistance = Math.max(maxDistance, neighbor.getDistance());
            }
        }
        Histogram histogram = new Histogram(0, maxDistance, spacingHistogramResolution);
        AngleFilter filter = AngleFilter.newInstance(angle - angleTolerance, angle + angleTolerance);
        for (Component component : components) {
            for (Neighbor neighbor : component.getNeighbors()) {
                if (filter.matches(neighbor)) {
                    histogram.add(neighbor.getDistance());
                }
            }
        }
        // Rectangular smoothing window has been replaced with gaussian smoothing window
        histogram.gaussianSmooth(spacingHistogramSmoothingWindowLength,
                spacingHistogramSmoothingWindowStdDeviation);
        return histogram.getPeakValue();
    }

    /**
     * Groups components into text lines.
     *
     * @param components component list
     * @param orientation - estimated text orientation
     * @param maxHorizontalDistance - maximum horizontal distance between components
     * @param maxVerticalDistance - maximum vertical distance between components
     * @return lines of components
     */
    private List<ComponentLine> determineLines(List<Component> components, double orientation,
            double maxHorizontalDistance, double maxVerticalDistance) {
        DisjointSets<Component> sets = new DisjointSets<Component>(components);
        AngleFilter filter = AngleFilter.newInstance(orientation - angleTolerance, orientation + angleTolerance);
        for (Component component : components) {
            for (Neighbor neighbor : component.getNeighbors()) {
                double x = neighbor.getHorizontalDistance(orientation) / maxHorizontalDistance;
                double y = neighbor.getVerticalDistance(orientation) / maxVerticalDistance;
                if (filter.matches(neighbor) && x * x + y * y <= 1) {
                    sets.union(component, neighbor.getComponent());
                }
            }
        }
        List<ComponentLine> lines = new ArrayList<ComponentLine>();
        for (Set<Component> group : sets) {
          List<Component> lineComponents = new ArrayList<Component>(group);
          Collections.sort(lineComponents, ComponentXComparator.getInstance());
          lines.add(new ComponentLine(lineComponents, orientation));
        }
        return lines;
    }

    private double computeOrientation(List<ComponentLine> lines) {
        // Compute weighted mean of line angles
        double valueSum = 0.0;
        double weightSum = 0.0;
        for (ComponentLine line : lines) {
            valueSum += line.getAngle() * line.getLength();
            weightSum += line.getLength();
        }
        return valueSum / weightSum;
    }

    private List<List<ComponentLine>> determineZones(List<ComponentLine> lines, double orientation,
            double minHorizontalDistance, double maxHorizontalDistance,
            double minVerticalDistance, double maxVerticalDistance,
            double minHorizontalMergeDistance, double maxHorizontalMergeDistance,
            double minVerticalMergeDistance, double maxVerticalMergeDistance) {
        DisjointSets<ComponentLine> sets = new DisjointSets<ComponentLine>(lines);
        // Mean height is computed so that all distances can be scaled
        // relative to the line height
        double meanHeight = 0.0, weights = 0.0;
        for (ComponentLine line : lines) {
            double weight = line.getLength();
            meanHeight += line.getHeight() * weight;
            weights += weight;
        }
        meanHeight /= weights;

        for (int i = 0; i < lines.size(); i++) {
            ComponentLine li = lines.get(i);
            for (int j = i + 1; j < lines.size(); j++) {
                ComponentLine lj = lines.get(j);
                double scale = Math.min(li.getHeight(), lj.getHeight()) / meanHeight;
                scale = Math.max(minLineSizeScale, Math.min(scale, maxLineSizeScale));
                // "<=" is used instead of "<" for consistency and to allow setting minVertical(Merge)Distance
                // to 0.0 with meaning "no minimal distance required"
                if (!sets.areTogether(li, lj) && li.angularDifference(lj) <= angleTolerance) {
                    double hDist = li.horizontalDistance(lj, orientation) / scale;
                    double vDist = li.verticalDistance(lj, orientation) / scale;
                    // Line over or above
                    if (minHorizontalDistance <= hDist && hDist <= maxHorizontalDistance
                            && minVerticalDistance <= vDist && vDist <= maxVerticalDistance) {
                        sets.union(li, lj);
                            }
                    // Split line that needs later merging
                    else if (minHorizontalMergeDistance <= hDist && hDist <= maxHorizontalMergeDistance
                            && minVerticalMergeDistance <= vDist && vDist <= maxVerticalMergeDistance) {
                        sets.union(li, lj);
                    }
                }
                TimeoutRegister.get().check();
            }
        }
        List<List<ComponentLine>> zones = new ArrayList<List<ComponentLine>>();
        for (Set<ComponentLine> group : sets) {
            zones.add(new ArrayList<ComponentLine>(group));
        }
        return zones;
    }

    private List<List<ComponentLine>> mergeZones(List<List<ComponentLine>> zones, double tolerance) {
        List<BxBounds> bounds = new ArrayList<BxBounds>(zones.size());
        for (List<ComponentLine> zone : zones) {
            BxBoundsBuilder builder = new BxBoundsBuilder();
            for (ComponentLine line : zone) {
                for (Component component : line.getComponents()) {
                    builder.expand(component.getChunk().getBounds());
                }
            }
            bounds.add(builder.getBounds());
        }

        List<List<ComponentLine>> outputZones = new ArrayList<List<ComponentLine>>();
        mainFor: for (int i = 0; i < zones.size(); i++) {
            for (int j = 0; j < zones.size(); j++) {
                if (i == j || bounds.get(j) == null || bounds.get(i) == null) {
                    continue;
                }
                if (BxModelUtils.contains(bounds.get(j), bounds.get(i), tolerance)) {
                    zones.get(j).addAll(zones.get(i));
                    bounds.set(i, null);
                    continue mainFor;
                }
                TimeoutRegister.get().check();
            }
            outputZones.add(zones.get(i));
        }
        return outputZones;
    }

    private List<List<ComponentLine>> mergeLines(List<List<ComponentLine>> zones, double orientation,
            double minHorizontalDistance, double maxHorizontalDistance,
            double minVerticalDistance, double maxVerticalDistance) {
        List<List<ComponentLine>> outputZones = new ArrayList<List<ComponentLine>>(zones.size());
        for (List<ComponentLine> zone : zones) {
            outputZones.add(mergeLinesInZone(zone, orientation,
                minHorizontalDistance, maxHorizontalDistance,
                minVerticalDistance, maxVerticalDistance));
        }
        return outputZones;
    }
    
    private List<ComponentLine> mergeLinesInZone(List<ComponentLine> lines, double orientation,
            double minHorizontalDistance, double maxHorizontalDistance,
            double minVerticalDistance, double maxVerticalDistance) {
        DisjointSets<ComponentLine> sets = new DisjointSets<ComponentLine>(lines);
        for (int i = 0; i < lines.size(); i++) {
            ComponentLine li = lines.get(i);
            for (int j = i + 1; j < lines.size(); j++) {
                ComponentLine lj = lines.get(j);
                double hDist = li.horizontalDistance(lj, orientation);
                double vDist = li.verticalDistance(lj, orientation);
                if (minHorizontalDistance <= hDist && hDist <= maxHorizontalDistance
                        && minVerticalDistance <= vDist && vDist <= maxVerticalDistance) {
                    sets.union(li, lj);
                } else if (minVerticalDistance <= vDist && vDist <= maxVerticalDistance
                        && Math.abs(hDist-Math.min(li.getLength(), lj.getLength())) < 0.1) {
                    boolean componentOverlap = false;
                    int overlappingCount = 0;
                    for (Component ci : li.getComponents()) {
                        for (Component cj : lj.getComponents()) {
                            double dist = ci.overlappingDistance(cj, orientation);
                            if (dist > 2) {
                                componentOverlap = true;
                            }
                            if (dist > 0) {
                                overlappingCount++;
                            }
                        }
                    }
                    if (!componentOverlap && overlappingCount <= 2) {
                        sets.union(li, lj);
                    }
                }
            }
        }
        List<ComponentLine> outputZone = new ArrayList<ComponentLine>();
        for (Set<ComponentLine> group : sets) {
            List<Component> components = new ArrayList<Component>();
            for (ComponentLine line : group) {
                components.addAll(line.getComponents());
            }
            Collections.sort(components, ComponentXComparator.getInstance());
            outputZone.add(new ComponentLine(components, orientation));
        }
        return outputZone;
    }

    /**
     * Converts list of zones from internal format (using components and
     * component lines) to BxModel.
     *
     * @param zones zones in internal format
     * @param wordSpacing - maximum allowed distance between components that
     * belong to one word
     * @return BxModel page
     */
    private BxPage convertToBxModel(BxPage origPage, List<List<ComponentLine>> zones, double wordSpacing) {
        BxPage page = new BxPage();
        List<BxPage> pages = Lists.newArrayList(origPage.getParent());
        int pageIndex = pages.indexOf(origPage);
        boolean groupped = false;
        if (zones.size() > MAX_ZONES_PER_PAGE && pageIndex >= PAGE_MARGIN
                && pageIndex < pages.size() - PAGE_MARGIN) {
            List<ComponentLine> oneZone = new ArrayList<ComponentLine>();
            for (List<ComponentLine> zone : zones) {
                oneZone.addAll(zone);
            }
            zones = new ArrayList<List<ComponentLine>>();
            zones.add(oneZone);
            groupped = true;
        }
        
        for (List<ComponentLine> lines : zones) {
            BxZone zone = new BxZone();
            if (groupped) {
                zone.setLabel(BxZoneLabel.GEN_OTHER);
            }
            for (ComponentLine line : lines) {
                zone.addLine(line.convertToBxLine(wordSpacing));
            }
            List<BxLine> zLines = Lists.newArrayList(zone);
            Collections.sort(zLines, new Comparator<BxLine>() {

                @Override
                public int compare(BxLine o1, BxLine o2) {
                    return Double.compare(o1.getBounds().getY(), o2.getBounds().getY());
                }

            });
            zone.setLines(zLines);
            BxBoundsBuilder.setBounds(zone);
            page.addZone(zone);
        }
        BxModelUtils.sortZonesYX(page);
        BxBoundsBuilder.setBounds(page);
        return page;
    }

    /**
     * Internal representation of character.
     */
    protected static class Component {

        private final double x;
        private final double y;
        private final BxChunk chunk;

        private List<Neighbor> neighbors;

        public Component(BxChunk chunk) {
            BxBounds bounds = chunk.getBounds();
            if (bounds == null) {
                throw new IllegalArgumentException("Bounds must not be null");
            }
            if (Double.isNaN(bounds.getX()) || Double.isInfinite(bounds.getX())) {
                throw new IllegalArgumentException("Bounds x coordinate must be finite");
            }
            if (Double.isNaN(bounds.getY()) || Double.isInfinite(bounds.getY())) {
                throw new IllegalArgumentException("Bounds y coordinate must be finite");
            }
            if (Double.isNaN(bounds.getWidth()) || Double.isInfinite(bounds.getWidth())) {
                throw new IllegalArgumentException("Bounds width must be finite");
            }
            if (Double.isNaN(bounds.getHeight()) || Double.isInfinite(bounds.getHeight())) {
                throw new IllegalArgumentException("Bounds height must be finite");
            }
            this.x = chunk.getBounds().getX() + chunk.getBounds().getWidth() / 2;
            this.y = chunk.getBounds().getY() + chunk.getBounds().getHeight() / 2;
            this.chunk = chunk;
        }

        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }

        public double getHeight() {
            return chunk.getBounds().getHeight();
        }

        public double distance(Component c) {
            double dx = getX() - c.getX(), dy = getY() - c.getY();
            return Math.sqrt(dx * dx + dy * dy);
        }

        /**
         * Computes horizontal distance between components.
         * 
         * @param c component
         * @param orientation orientation angle
         * @return distance
         */
        public double horizontalDistance(Component c, double orientation) {
            // TODO: take orientation into account
            return Math.abs(getX() - c.getX());
        }

        public double verticalDistance(Component c, double orientation) {
            return Math.abs(getY() - c.getY());
        }

        public double horizontalBoundsDistance(Component c, double orientation) {
            // TODO: take orientation into account
            return horizontalDistance(c, orientation) - getChunk().getBounds().getWidth() / 2 -
                    c.getChunk().getBounds().getWidth() / 2;
        }


        public BxChunk getChunk() {
            return chunk;
       }

        public List<Neighbor> getNeighbors() {
            return neighbors;
        }

        public void setNeighbors(List<Neighbor> neighbors) {
            this.neighbors = neighbors;
        }

        private double angle(Component c) {
            if (getX() > c.getX()) {
                return Math.atan2(getY() - c.getY(), getX() - c.getX());
            } else {
                return Math.atan2(c.getY() - getY(), c.getX() - getX());
            }
        }
        
        public double overlappingDistance(Component other, double orientation) {
            double[] xs = new double[4];
            double s = Math.sin(-orientation), c = Math.cos(-orientation);
            xs[0] = c * x - s * y;
            xs[1] = c * (x+chunk.getWidth()) - s * (y+chunk.getHeight());
            xs[2] = c * other.x - s * other.y;
            xs[3] = c * (other.x+other.chunk.getWidth()) - s * (other.y+other.chunk.getHeight());
            boolean overlapping = xs[1] >= xs[2] && xs[3] >= xs[0];
            Arrays.sort(xs);
            return Math.abs(xs[2] - xs[1]) * (overlapping ? 1 : -1);
        }
    }

    /**
     * Class representing nearest-neighbor pair.
     */
    protected static class Neighbor {
        
        private final double distance;
        private final double angle;
        private final Component component;
        private final Component origin;

        public Neighbor(Component neighbor, Component origin) {
            this.distance = neighbor.distance(origin);
            this.angle = neighbor.angle(origin);
            this.component = neighbor;
            this.origin = origin;
        }

        public double getDistance() {
            return distance;
        }

        public double getHorizontalDistance(double orientation) {
            return component.horizontalDistance(origin, orientation);
        }

        public double getVerticalDistance(double orientation) {
            return component.verticalDistance(origin, orientation);
        }

        public double getAngle() {
            return angle;
        }

        public Component getComponent() {
            return component;
        }
    }

    /**
     * Component comparator based on x coordinate of the centroid of component.
     *
     * The ordering is not consistent with equals.
     */
    protected static final class ComponentXComparator implements Comparator<Component> {

        private ComponentXComparator() {
        }
        
        @Override
        public int compare(Component o1, Component o2) {
            return Double.compare(o1.getX(), o2.getX());
        }

        private static ComponentXComparator instance = new ComponentXComparator();

        public static ComponentXComparator getInstance() {
            return instance;
        }
    }

    /**
     * Neighbor distance comparator based on the distance.
     *
     * The ordering is not consistent with equals.
     */
    protected static final class NeighborDistanceComparator implements Comparator<Neighbor> {

        private NeighborDistanceComparator() {
        }

        @Override
        public int compare(Neighbor o1, Neighbor o2) {
            return Double.compare(o1.getDistance(), o2.getDistance());
        }

        private static NeighborDistanceComparator instance = new NeighborDistanceComparator();

        public static NeighborDistanceComparator getInstance() {
            return instance;
        }
    }

    /**
     * Internal representation of the text line.
     */
    protected static class ComponentLine {
        
        private final double x0;
        private final double y0;

        private final double x1;
        private final double y1;

        private final double height;

        private List<Component> components;

        public ComponentLine(List<Component> components, double orientation) {
            this.components = components;

            if (components.size() >= 2) {
                // Simple linear regression
                double sx = 0.0, sxx = 0.0, sxy = 0.0, sy = 0.0;
                for (Component component : components) {
                    sx += component.getX();
                    sxx += component.getX() * component.getX();
                    sxy += component.getX() * component.getY();
                    sy += component.getY();
                }
                double b = (components.size() * sxy - sx * sy) / (components.size() * sxx - sx * sx);
                double a = (sy - b * sx) / components.size();

                this.x0 = components.get(0).getX();
                this.y0 = a + b * this.x0;
                this.x1 = components.get(components.size() - 1).getX();
                this.y1 = a + b * this.x1;
            }
            else if (! components.isEmpty()) {
                Component component = components.get(0);
                double dx = component.getChunk().getBounds().getWidth() / 3;
                double dy = dx * Math.tan(orientation);
                this.x0 = component.getX() - dx;
                this.x1 = component.getX() + dx;
                this.y0 = component.getY() - dy;
                this.y1 = component.getY() + dy;
            }
            else {
                throw new IllegalArgumentException("Component list must not be empty");
            }
            height = computeHeight();
        }

        public double getAngle() {
            return Math.atan2(y1 - y0, x1 - x0);
        }

        public double getSlope() {
            return (y1 - y0) / (x1 - x0);
        }

        public double getLength() {
            return Math.sqrt((x0 - x1) * (x0 - x1) + (y0 - y1) * (y0 - y1));
        }

        private double computeHeight() {
            double sum = 0.0;
            for (Component component : components) {
                sum += component.getHeight();
            }
            return sum / components.size();
        }

        public double getHeight() {
            return height;
        }

        public List<Component> getComponents() {
            return components;
        }

        public double angularDifference(ComponentLine j) {
            double diff = Math.abs(getAngle() - j.getAngle());
            if (diff <= Math.PI/2) {
                return diff;
            } else {
                return Math.PI - diff;
            }
        }

        public double horizontalDistance(ComponentLine other, double orientation) {
            double[] xs = new double[4];
            double s = Math.sin(-orientation), c = Math.cos(-orientation);
            xs[0] = c * x0 - s * y0;
            xs[1] = c * x1 - s * y1;
            xs[2] = c * other.x0 - s * other.y0;
            xs[3] = c * other.x1 - s * other.y1;
            boolean overlapping = xs[1] >= xs[2] && xs[3] >= xs[0];
            Arrays.sort(xs);
            return Math.abs(xs[2] - xs[1]) * (overlapping ? 1 : -1);
        }

        public double verticalDistance(ComponentLine other, double orientation) {
            double xm = (x0 + x1) / 2, ym = (y0 + y1) / 2;
            double xn = (other.x0 + other.x1) / 2, yn = (other.y0 + other.y1) / 2;
            double a = Math.tan(orientation);
            return Math.abs(a * (xn - xm) + ym - yn) / Math.sqrt(a * a + 1);
        }

        public BxLine convertToBxLine(double wordSpacing) {
            BxLine line = new BxLine();
            BxWord word = new BxWord();
            Component previousComponent = null;
            for (Component component : components) {
                if (previousComponent != null) {
                    double dist = component.getChunk().getBounds().getX() -
                            previousComponent.getChunk().getBounds().getX() -
                            previousComponent.getChunk().getBounds().getWidth();
                    if(dist > wordSpacing) {
                        BxBoundsBuilder.setBounds(word);
                        line.addWord(word);
                        word = new BxWord();
                    }
                }
                word.addChunk(component.getChunk());
                previousComponent = component;
            }
            BxBoundsBuilder.setBounds(word);
            line.addWord(word);
            BxBoundsBuilder.setBounds(line);
            return line;
        }
    }

    /**
     * Filter class for neighbor objects that checks if the angle of the
     * neighbor is within specified range.
     */
    protected abstract static class AngleFilter {

        private final double lowerAngle;
        private final double upperAngle;

        private AngleFilter(double lowerAngle, double upperAngle) {
            this.lowerAngle = lowerAngle;
            this.upperAngle = upperAngle;
        }

        /**
         * Constructs new angle filter.
         *
         * @param lowerAngle minimum angle in range [-3*pi/2, pi/2)
         * @param upperAngle maximum angle in range [-pi/2, 3*pi/2)
         * @return newly constructed angle filter
         */
        public static AngleFilter newInstance(double lowerAngle, double upperAngle) {
            if (lowerAngle < -Math.PI/2) {
                lowerAngle += Math.PI;
            }
            if (upperAngle >= Math.PI/2) {
                upperAngle -= Math.PI;
            }
            if (lowerAngle <= upperAngle) {
                return new AndFilter(lowerAngle, upperAngle);
            } else {
                return new OrFilter(lowerAngle, upperAngle);
            }
        }

        public double getLowerAngle() {
            return lowerAngle;
        }

        public double getUpperAngle() {
            return upperAngle;
        }
        
        public abstract boolean matches(Neighbor neighbor);

        public static final class AndFilter extends AngleFilter {

            private AndFilter(double lowerAngle, double upperAngle) {
                super(lowerAngle, upperAngle);
            }

            @Override
            public boolean matches(Neighbor neighbor) {
                return getLowerAngle() <= neighbor.getAngle() && neighbor.getAngle() < getUpperAngle();
            }

        }

        public static final class OrFilter extends AngleFilter {

            private OrFilter(double lowerAngle, double upperAngle) {
                super(lowerAngle, upperAngle);
            }

            @Override
            public boolean matches(Neighbor neighbor) {
                return getLowerAngle() <= neighbor.getAngle() || neighbor.getAngle() < getUpperAngle();
            }

        }
    }
    
    private static final double DISTANCE_STEP = 16.0;
    
    public static final double DEFAULT_ANGLE_HIST_RES = Math.toRadians(0.5);

    public static final double DEFAULT_ANGLE_HIST_SMOOTH_LEN = 0.25 * Math.PI;

    public static final double DEFAULT_ANGLE_HIST_SMOOTH_STDDEV = 0.0625 * Math.PI;

    public static final double DEFAULT_SPACE_HIST_RES = 0.5;

    public static final double DEFAULT_SPACE_HIST_SMOOTH_LEN = 2.5;
    
    public static final double DEFAULT_SPACE_HIST_SMOOTH_STDDEV = 0.5;
    
    public static final double DEFAULT_MAX_VERT_COMP_DIST = 0.67;

    public static final double DEFAULT_MIN_LINE_SIZE_SCALE = 0.9;

    public static final double DEFAULT_MAX_LINE_SIZE_SCALE = 2.5;

    public static final double DEFAULT_MIN_HORIZONTAL_DIST = -0.5;

    public static final double DEFAULT_MIN_VERTICAL_DIST = 0.0;

    public static final double DEFAULT_MAX_VERTICAL_DIST = 1.2;

    public static final double DEFAULT_COMP_DIST_CHAR = 3.5;

    public static final double DEFAULT_WORD_DIST = 0.2;

    public static final double DEFAULT_MIN_HORIZONTAL_MERGE_DIST = -3.0;

    public static final double DEFAULT_MAX_VERTICAL_MERGE_DIST = 0.5;

    public static final double DEFAULT_ANGLE_TOLERANCE = Math.PI / 6;

    public static final int DEFAULT_NEIGHBOR_COUNT = 5;
    
     
    /**
     * Angle histogram resolution in radians per bin.
     */
    private double angleHistogramResolution = DEFAULT_ANGLE_HIST_RES;

    /**
     * Angle histogram smoothing window length in radians.
     * Length of angle histogram is equal to pi.
     */
    private double angleHistogramSmoothingWindowLength = DEFAULT_ANGLE_HIST_SMOOTH_LEN;

    /**
     * Angle histogram gaussian smoothing window standard deviation in radians.
     */
    private double angleHistogramSmoothingWindowStdDeviation = DEFAULT_ANGLE_HIST_SMOOTH_STDDEV;

    /**
     * Spacing histogram resolution per bin.
     */
    private double spacingHistogramResolution = DEFAULT_SPACE_HIST_RES;

    /**
     * Spacing histogram smoothing window length.
     */
    private double spacingHistogramSmoothingWindowLength = DEFAULT_SPACE_HIST_SMOOTH_LEN;

    /**
     * Spacing histogram gaussian smoothing window standard deviation.
     */
    private double spacingHistogramSmoothingWindowStdDeviation = DEFAULT_SPACE_HIST_SMOOTH_STDDEV;
    
    /**
     * Maximum vertical component distance multiplier used during line
     * determination.
     * 
     * Maximum vertical distance between components (characters) that belong
     * to the same line is equal to the product of this value and estimated
     * between-line spacing.
     */
    private double maxVerticalComponentDistanceMultiplier = DEFAULT_MAX_VERT_COMP_DIST;

    /**
     * Minimum line size scale value.
     *
     * During zone determination (merging lines into zones) line height is
     * taken into account. To achieve this, line size scale is estimated and
     * limited to range [minLineSizeScale, maxLineSizeScale].
     */
    private double minLineSizeScale = DEFAULT_MIN_LINE_SIZE_SCALE;

    /**
     * Maximum line size scale value.
     *
     * See minLineSizeScale for more information.
     */
    private double maxLineSizeScale = DEFAULT_MAX_LINE_SIZE_SCALE;

    /**
     * Minimum horizontal line distance multiplier.
     *
     * Minimum horizontal distance between lines that belong to the same zone
     * is equal to the product of this value and estimated within-line spacing.
     */
    private double minHorizontalDistanceMultiplier = DEFAULT_MIN_HORIZONTAL_DIST;

    /**
     * Minimum vertical line distance multiplier.
     *
     * Minimum vertical distance between lines that belong to the same zone
     * is equal to the product of this value and estimated between-line spacing.
     */
    private double minVerticalDistanceMultiplier = DEFAULT_MIN_VERTICAL_DIST;

    /**
     * Maximum vertical line distance multiplier.
     * 
     * Maximum vertical distance between lines that belong to the same zone
     * is equal to the product of this value and estimated between-line spacing.
     */
    private double maxVerticalDistanceMultiplier = DEFAULT_MAX_VERTICAL_DIST;

    /**
     * Component distance character spacing multiplier.
     *
     * Maximum distance between components that belong to the same line is
     * equal to (lineSpacing * componentDistanceLineMultiplier +
     * characterSpacing * componentDistanceCharacterMultiplier), where
     * lineSpacing and characterSpacing are estimated between-line and
     * within-line spacing, respectively.
     */
    private double componentDistanceCharacterMultiplier = DEFAULT_COMP_DIST_CHAR;

    /**
     * Word distance multiplier.
     *
     * Maximum distance between components that belong to the same word is
     * equal to the product of this value and estimated within-line spacing.
     */
    private double wordDistanceMultiplier = DEFAULT_WORD_DIST;

    /**
     * Minimum horizontal line merge distance multiplier.
     *
     * Minimum horizontal distance between lines that should be merged is equal
     * to the product of this value and estimated within-line spacing.
     *
     * Because split lines do not overlap this value should be negative.
     */

    private double minHorizontalMergeDistanceMultiplier = DEFAULT_MIN_HORIZONTAL_MERGE_DIST;

    /**
     * Maximum vertical line merge distance multiplier.
     * 
     * Maximum vertical distance between lines that should be merged is equal
     * to the product of this value and estimated between-line spacing.
     */

    private double maxVerticalMergeDistanceMultiplier = DEFAULT_MAX_VERTICAL_MERGE_DIST;

    /**
     * Angle tolerance for comparisons of angles between components and angles
     * between lines.
     */
    private double angleTolerance = DEFAULT_ANGLE_TOLERANCE;

    /**
     * Number of nearest-neighbors found per component.
     */
    private int neighborCount = DEFAULT_NEIGHBOR_COUNT;
    

    public void setSpacingHistogramResolution(double value) {
        spacingHistogramResolution = value;
    }

    public void setSpacingHistogramSmoothingWindowLength(double value) {
        spacingHistogramSmoothingWindowLength = value;
    }
    
    public void setSpacingHistogramSmoothingWindowStdDeviation(double value) {
        spacingHistogramSmoothingWindowStdDeviation = value;
    }

    public void setMaxLineSizeScale(double value) {
        maxLineSizeScale = value;
    }

    public void setMaxVerticalDistanceMultiplier(double value) {
        maxVerticalDistanceMultiplier = value;
    }

    public void setMinHorizontalDistanceMultiplier(double value) {
        minHorizontalDistanceMultiplier = value;
    }

    public void setComponentDistanceCharacterMultiplier(double value) {
        componentDistanceCharacterMultiplier = value;
    }

    public void setWordDistanceMultiplier(double value) {
        wordDistanceMultiplier = value;
    }

    public void setMaxVerticalMergeDistanceMultiplier(double value) {
        maxVerticalMergeDistanceMultiplier = value;
    }

    public void setAngleTolerance(double value) {
        angleTolerance = value;
    }

}
