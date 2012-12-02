package pl.edu.icm.cermine.content.headers;

import java.util.ArrayList;
import java.util.List;
import pl.edu.icm.cermine.content.model.BxDocContentStructure;
import pl.edu.icm.cermine.structure.model.BxLine;

/**
 *
 * @author Dominika Tkaczyk
 */
public class HeaderLinesCompletener {
    
    public static final int DEFAULT_MAX_ADDED_LINES = 2;
    
    public static final double DEFAULT_HEADER_HEIGHT_TOL = 0.01;
    
    public static final int DEFAULT_MIN_HEADER_SCORE = 1;
    
    public static final double DEFAULT_HEADER_LINE_WIDTH_MULT = 0.7;
    
    public static final double DEFAULT_HEADER_LINE_MULT = 0.7;
    
    public static final int DEFAULT_MIN_HEADER_LINE_SCORE = 1;
    
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
    
    public void completeLines(BxDocContentStructure contentStructure) {
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
