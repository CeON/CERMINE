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

import java.util.ArrayList;
import java.util.List;
import pl.edu.icm.cermine.content.model.BxContentStructure;
import pl.edu.icm.cermine.structure.model.BxLine;

/**
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class HeaderLinesCompletener {
    
    public static final int DEFAULT_MAX_ADDED_LINES = 2;
    
    public static final double DEFAULT_HEADER_HEIGHT_TOL = 0.1;
    
    public static final int DEFAULT_MIN_HEADER_SCORE = 2;
    
    public static final double DEFAULT_HEADER_LINE_WIDTH_MULT = 0.7;
    
    public static final double DEFAULT_HEADER_LINE_MULT = 0.7;
    
    public static final int DEFAULT_MIN_HEADER_LINE_SCORE = 2;
    
    /**
     * The maximum number of additional line following the first header line added as a part of the header.
     */
    private int maxAddedHeaderLines = DEFAULT_MAX_ADDED_LINES;

    /**
     * The maximum difference between heights of lines belonging to the same header.
     */
    private double headerHeightTolerance = DEFAULT_HEADER_HEIGHT_TOL;
    
    /**
     * The minimum score for a line to be considered as a candidate for header expanding.
     */
    private int minHeaderCandidateScore = DEFAULT_MIN_HEADER_SCORE;
    
    private double headerLineWidthMultiplier = DEFAULT_HEADER_LINE_WIDTH_MULT;
    
    private double headerLineSpacingMultiplier = DEFAULT_HEADER_LINE_MULT;
    
    private int minHeaderLineScore = DEFAULT_MIN_HEADER_LINE_SCORE;
    
    public void completeLines(BxContentStructure contentStructure) {
        for (BxLine headerLine : contentStructure.getFirstHeaderLines()) {
            int added = 0;
            BxLine actLine = headerLine;
            List<BxLine> candidates = new ArrayList<BxLine>();
            while (actLine.hasNext()) {
                if (added > maxAddedHeaderLines) {
                    break;
                }
                actLine = actLine.getNext();
                if (contentStructure.containsFirstHeaderLine(actLine)) {
                    break;
                }
                int score = 0;
                if (Math.abs(actLine.getHeight() - headerLine.getHeight()) < headerHeightTolerance) {
                    score++;
                }
                if (actLine.getMostPopularFontName().equals(headerLine.getMostPopularFontName())) {
                    score++;
                }
                if (score >= minHeaderCandidateScore) {
                    candidates.add(actLine);
                    added++;
                } else {
                    break;
                }
            }

            if (added == 0) {
                continue;
            }
            int score = 0;
            BxLine firstCandidate = candidates.get(0);
            BxLine lastCandidate = candidates.get(added-1);
            if (lastCandidate.getWidth() < headerLine.getWidth() * headerLineWidthMultiplier) {
                score++;
            }
            if (lastCandidate.hasNext() 
                    && Math.abs(headerLine.getY() - firstCandidate.getY()) 
                  < Math.abs(lastCandidate.getY() - lastCandidate.getNext().getY()) * headerLineSpacingMultiplier) {
                score++;
            }
            if (lastCandidate.hasNext() && lastCandidate.getNext().toText().matches("[A-Z].*")) {
                score++;
            }
            if (lastCandidate.hasNext() && !lastCandidate.getMostPopularFontName().equals(lastCandidate.getNext().getMostPopularFontName())) {
                score++;
            }
            if (score >= minHeaderLineScore) {
                for (BxLine candidate : candidates) {
                    contentStructure.addAdditionalHeaderLine(headerLine, candidate);
                }
            }
        }

    }

    public void setHeaderHeightTolerance(double headerHeightTolerance) {
        this.headerHeightTolerance = headerHeightTolerance;
    }

    public void setHeaderLineSpacingMultiplier(double headerLineSpacingMultiplier) {
        this.headerLineSpacingMultiplier = headerLineSpacingMultiplier;
    }

    public void setHeaderLineWidthMultiplier(double headerLineWidthMultiplier) {
        this.headerLineWidthMultiplier = headerLineWidthMultiplier;
    }

    public void setMaxAddedHeaderLines(int maxAddedHeaderLines) {
        this.maxAddedHeaderLines = maxAddedHeaderLines;
    }

    public void setMinHeaderCandidateScore(int minHeaderCandidateScore) {
        this.minHeaderCandidateScore = minHeaderCandidateScore;
    }

    public void setMinHeaderLineScore(int minHeaderLineScore) {
        this.minHeaderLineScore = minHeaderLineScore;
    }
    
}
