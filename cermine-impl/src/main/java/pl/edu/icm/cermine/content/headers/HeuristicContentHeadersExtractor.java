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

package pl.edu.icm.cermine.content.headers;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Map.Entry;
import java.util.*;
import pl.edu.icm.cermine.content.model.BxContentStructure;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.structure.model.*;
import pl.edu.icm.cermine.tools.CountMap;
import pl.edu.icm.cermine.tools.statistics.Population;
import pl.edu.icm.cermine.tools.timeout.TimeoutRegister;

/**
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class HeuristicContentHeadersExtractor implements ContentHeadersExtractor {

    private final SimpleHeadersClusterizer headersClusterizer;
    
    private final HeaderLinesCompletener headerLinesCompletener;

    public HeuristicContentHeadersExtractor() {
        this.headersClusterizer = new SimpleHeadersClusterizer();
        this.headerLinesCompletener = new HeaderLinesCompletener();
    }
    
    @Override
    public BxContentStructure extractHeaders(BxDocument document) throws AnalysisException {

        Population heightPopulation = new Population();
        Population fontPopulation = new Population();
        Population distancePopulation = new Population();
        Population lengthPopulation = new Population();
        Population indentationPopulation = new Population();

        Set<BxLine> candidates = new HashSet<BxLine>();
        for (BxPage page : document) {
            for (BxZone zone : page) {
                if (zone.getLabel().equals(BxZoneLabel.BODY_CONTENT) || zone.getLabel().equals(BxZoneLabel.GEN_BODY)) {
                    for (BxLine line : zone) {
                        heightPopulation.addObservation(line.getHeight());
                        lengthPopulation.addObservation(line.getWidth());
                        indentationPopulation.addObservation(line.getX());
                        if (line.hasPrev() && line.getY()-line.getPrev().getY() > 0) {
                            distancePopulation.addObservation(line.getY()-line.getPrev().getY());
                        }
                        fontPopulation.addObservation(getFontIndex(line));
                        
                        if (isFirstInZone(line) && looksLikeHeader(line)) {
                            candidates.add(line);
                        }
                        TimeoutRegister.get().check();
                    }
                }
            }
        }
        
        Set<BxLine> toDelete = new HashSet<BxLine>();
        
        for (BxLine line : candidates){
            if (shouldBeRemoved(line, heightPopulation, fontPopulation, distancePopulation, indentationPopulation)) {
                toDelete.add(line);
            }
            if (lengthPopulation.getZScore(line.getWidth()) > candMaxLengthZScore) {
                toDelete.add(line);
            }
        }
        
        candidates.removeAll(toDelete);
        toDelete.clear();

        Set<String> headerFonts = new HashSet<String>();
        List<BxLine> candidatesList = Lists.newArrayList(candidates);

        Set<String> docFontPopulation = Sets.newHashSet();
        if (!candidatesList.isEmpty()) {
            docFontPopulation = candidatesList.get(0).getParent().getParent().getParent().getFontNames();
        }
        CountMap<String> fontCandidates = new CountMap<String>();
        for (int x = 0; x < candidatesList.size(); x++) {            
            fontCandidates.add(candidatesList.get(x).getMostPopularFontName());
        }
        for (Entry<String, Integer> entry : fontCandidates.getSortedEntries(3)) {
            if (Math.abs(fontPopulation.getZScore(getFontIndex(entry.getKey(), docFontPopulation))) 
                    > outlFontZScore) {
                headerFonts.add(entry.getKey());
            }
            TimeoutRegister.get().check();
        }
        
        for (BxPage page : document) {
            for (BxZone zone : page) {
                if (zone.getLabel().equals(BxZoneLabel.BODY_CONTENT) || zone.getLabel().equals(BxZoneLabel.GEN_BODY)) {
                    for (BxLine line : zone) {
                        if (looksLikeHeader(line) && headerFonts.contains(line.getMostPopularFontName())) {
                            candidates.add(line);
                        }
                    }
                }
            }
        }
        
        for (BxLine line : candidates){
            if (shouldBeRemoved(line, heightPopulation, fontPopulation, distancePopulation, indentationPopulation)) {
                toDelete.add(line);
            }
            if (lengthPopulation.getZScore(line.getWidth()) > candMaxLengthZScore2) {
                toDelete.add(line);
            }
        }
 
        candidates.removeAll(toDelete);
        toDelete.clear();
        
        for (BxLine line : candidates) {
            int i = 0;
            for (BxLine line2 : candidates) {
                if (line.equals(line2)) {
                    continue;
                }
                if (areSimilar(line, line2)) {
                    i++;
                }
            }
            if (i == 0 || i > maxSimilarLinesCount) {
                toDelete.add(line);
                for (BxLine line2 : candidates) {
                    if (areSimilar(line, line2)) {
                        toDelete.add(line2);
                    }
                }
            }
        }
        
        candidates.removeAll(toDelete);

        candidatesList = new ArrayList<BxLine>();
        for (BxPage page : document) {
            for (BxZone zone : page) {
                for (BxLine line : zone) {
                    if (candidates.contains(line)) {
                        candidatesList.add(line);
                    }
                }
            }
        }
        int clusters[] = headersClusterizer.clusterLines(candidatesList);
        Set<Integer> keptClusters = new HashSet<Integer>();
        for (int clusterIdx = 0; clusterIdx < clusters.length; clusterIdx++) {
            int cluster = clusters[clusterIdx];
            if (keptClusters.size() < 3) {
                keptClusters.add(cluster);
            }
            if (!keptClusters.contains(cluster)) {
                candidates.remove(candidatesList.get(clusterIdx));
            }
        }

        BxContentStructure contentStructure = new BxContentStructure();
        BxLine lastHeaderLine = null;
        for (BxPage page : document) {
            for (BxZone zone : page) {
                if (zone.getLabel().equals(BxZoneLabel.BODY_CONTENT) || zone.getLabel().equals(BxZoneLabel.GEN_BODY)) {
                    for (BxLine line : zone) {
                        if (candidates.contains(line)) {
                            contentStructure.addFirstHeaderLine(page, line);
                            lastHeaderLine = line;
                        } else if (zone.getLabel().equals(BxZoneLabel.BODY_CONTENT) || zone.getLabel().equals(BxZoneLabel.GEN_BODY)) {
                            if (lastHeaderLine == null) {
                                BxChunk chunk = new BxChunk(new BxBounds(), "--");
                                BxWord word = new BxWord().addChunk(chunk);
                                lastHeaderLine = new BxLine().addWord(word);
                                contentStructure.addFirstHeaderLine(page, lastHeaderLine);
                            }
                            contentStructure.addContentLine(lastHeaderLine, line);
                        }
                    }
                }
            }
        }
        
        headerLinesCompletener.completeLines(contentStructure);
        
        return contentStructure;
    }

    private double getFontIndex(BxLine line) {
        return getFontIndex(line.getMostPopularFontName(), 
                line.getParent().getParent().getParent().getFontNames());
    }
    
    private double getFontIndex(String fontName, Set<String> population) {
        List<String> fonts = Lists.newArrayList(population);
        Collections.sort(fonts);
        return fonts.indexOf(fontName);
    }
    
    private boolean isFirstInZone(BxLine line) {
        return !line.hasPrev() || line.getParent() != line.getPrev().getParent();
    }

    private boolean looksLikeHeader(BxLine line) {
        String text = line.toText();
        return text.matches("^[A-Z].*") || text.matches("^[1-9].*[a-zA-Z].*") || text.matches("^[a-h]\\).*[a-zA-Z].*");
    }

    private boolean looksLikeEquation(BxLine line) {
        return line.toText().contains("=");
    }

    private boolean looksLikeFigure(BxLine line) {
        return line.toText().toLowerCase().matches("fig\\.? .*") || line.toText().toLowerCase().matches("figure .*");
    }
    
    private boolean looksLikeTable(BxLine line) {
        return line.toText().toLowerCase().matches("table .*");
    }
    
    private boolean containsMostlyLetters(BxLine line) {
        double letterCount = 0;
        for (char ch : line.toText().toCharArray()) {
            if (Character.isLetter(ch)) {
                letterCount++;
            }
        }
        return 2*letterCount > line.toText().length();
    }
    
    private boolean containsWord(BxLine line) {
        return line.toText().toLowerCase().matches(".*[a-z][a-z][a-z][a-z].*");
    }
    
    private boolean startsWithLargeNumber(BxLine line) {
        return line.toText().matches("[0-9][0-9].*");
    }

    private boolean areSimilar(BxLine line1, BxLine line2){
        return line1.getMostPopularFontName().equals(line2.getMostPopularFontName())
                && Math.abs(line1.getHeight()-line2.getHeight()) < maxHeightSimilarity;
    }
    
    private boolean shouldBeRemoved(BxLine line, Population heightPopulation, Population fontPopulation, 
            Population distancePopulation, Population indentationPopulation) {
        if (line.getMostPopularFontName() == null) {
            return true;
        }
        if (heightPopulation.getZScore(line.getHeight()) < candMinHeightZScore) {
            return true;
        }
        if (looksLikeEquation(line)) {
            return true;
        }
        if (looksLikeFigure(line)) {
            return true;
        }
        if (looksLikeTable(line)) {
            return true;
        }
        if (!containsMostlyLetters(line)) {
            return true;
        }
        if (!containsWord(line)) {
            return true;
        }
        if (startsWithLargeNumber(line)) {
            return true;
        }
        if (heightPopulation.getZScore(line.getHeight()) < outlHeightZScore
                && Math.abs(fontPopulation.getZScore(getFontIndex(line))) < outlFontZScore
                && (!line.hasPrev() || distancePopulation.getZScore(line.getY()-line.getPrev().getY()) < outlDistanceZScore)
                && Math.abs(indentationPopulation.getZScore(line.getX())) < outlIndentZScore) {
            return true;
        }
        int i = 0;
        BxLine actLine = line;
        while (actLine.hasNext()) {
            actLine = actLine.getNext();
            if (actLine.toText().matches("[A-Z].*")) {
                break;
            }
            if (i++ == maxHeaderLineCount) {
                return true;
            }
        }
        return false;
    }
        
    private static final double CAND_MAX_LENGTH_ZSCORE = -0.1;
    
    private static final double CAND_MAX_LENGTH_ZSCORE_2 = 1;
    
    private static final double CAND_MIN_HEIGHT_ZSCORE = -1;
    
    private static final double OUTL_HEIGHT_ZSCORE = 0.5;
            
    private static final double OUTL_FONT_ZSCORE = 0.5;
    
    private static final double OUTL_DIST_ZSCORE = 0.4;
                            
    private static final double OUTL_INDENT_ZSCORE = 0.5;
                 
    private static final double MAX_HEIGHT_SIMILARITY = 1;
       
    private static final int MAX_SIMILAR_LINES_COUNT = 50;
    
    private static final int MAX_HEADER_LINE_COUNT = 5;
    
            
    private final double candMaxLengthZScore = CAND_MAX_LENGTH_ZSCORE;
    
    private final double candMaxLengthZScore2 = CAND_MAX_LENGTH_ZSCORE_2;
    
    private final double candMinHeightZScore = CAND_MIN_HEIGHT_ZSCORE;
    
    private final double outlHeightZScore = OUTL_HEIGHT_ZSCORE;
            
    private final double outlFontZScore = OUTL_FONT_ZSCORE;
    
    private final double outlDistanceZScore = OUTL_DIST_ZSCORE;
                            
    private final double outlIndentZScore = OUTL_INDENT_ZSCORE;
    
    private final double maxHeightSimilarity = MAX_HEIGHT_SIMILARITY;

    private final int maxSimilarLinesCount = MAX_SIMILAR_LINES_COUNT;
    
    private final int maxHeaderLineCount = MAX_HEADER_LINE_COUNT;
    
}
