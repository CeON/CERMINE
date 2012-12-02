package pl.edu.icm.cermine.content.cleaning;

import java.util.ArrayList;
import java.util.List;
import pl.edu.icm.cermine.content.model.BxDocContentStructure;
import pl.edu.icm.cermine.content.model.BxDocContentStructure.BxDocContentPart;
import pl.edu.icm.cermine.structure.model.BxLine;

/**
 *
 * @author Dominika Tkaczyk
 */
public class ContentCleaner {
    
    public static final double DEFAULT_PAR_LINE_MULT = 0.5;
    
    public static final double DEFAULT_MIN_PAR_IND = 5;
    
    public static final double DEFAULT_LAST_PAR_LINE_MULT = 0.8;
    
    public static final double DEFAULT_FIRST_PAR_LINE_SCORE = 3;
    
      
    private double paragraphLineIndentMultiplier = DEFAULT_PAR_LINE_MULT;
    
    private double minParagraphIndent = DEFAULT_MIN_PAR_IND;
    
    private double lastParagraphLineLengthMult = DEFAULT_LAST_PAR_LINE_MULT;
    
    private double firstParagraphLineMinScore = DEFAULT_FIRST_PAR_LINE_SCORE;
    
    public void cleanupContent(BxDocContentStructure contentStructure) {
        for (BxDocContentPart contentPart : contentStructure.getParts()) {
            List<BxLine> headerLines = contentPart.getHeaderLines();
            StringBuilder sb = new StringBuilder();
            for (BxLine headerLine : headerLines) {
                String lineText = headerLine.toText();
                if (lineText.endsWith("-")) {
                    lineText = lineText.substring(0, lineText.length()-1);
                    if (lineText.lastIndexOf(' ') < 0) {
                        sb.append(lineText);
                    } else {
                        sb.append(lineText.substring(0, lineText.lastIndexOf(' ')));
                        sb.append(" ");
                        sb.append(lineText.substring(lineText.lastIndexOf(' ')+1));
                    }
                } else {
                    sb.append(lineText);
                    sb.append(" ");
                }
            }
            contentPart.setCleanHeaderText(cleanLigatures(sb.toString().trim()));
            
            List<BxLine> contentLines = contentPart.getContentLines();
            List<String> contentTexts = new ArrayList<String>();
            
            double maxLen = Double.NEGATIVE_INFINITY;
            for (BxLine line : contentLines) {
                if (line.getWidth() > maxLen) {
                    maxLen = line.getWidth();
                }
            }
            
            String contentText = "";
            for (BxLine line : contentLines) {
                int score = 0;
                BxLine prev = line.getPrev();
                BxLine next = line.getNext();
                if (line.toText().matches("^[A-Z].*$")) {
                    score++;
                }
                if (prev != null) {
                    if (line.getX() > prev.getX() && line.getX() - prev.getX() < paragraphLineIndentMultiplier * maxLen 
                            && line.getX() - prev.getX() > minParagraphIndent) {
                        score++;
                    }
                    if (prev.getWidth() < lastParagraphLineLengthMult * maxLen) {
                        score++;
                    }
                    if (prev.toText().endsWith(".")) {
                        score++;
                    }
                }
                if (next != null && line.getX() > next.getX() && line.getX() - next.getX() < paragraphLineIndentMultiplier * maxLen 
                            && line.getX() - next.getX() > minParagraphIndent) {
                    score++;
                }
                
                if (score >= firstParagraphLineMinScore) {
                    if (!contentText.isEmpty()) {
                        contentTexts.add(cleanLigatures(contentText.trim()));
                    }
                    contentText = "";
                }
                
                String lineText = line.toText();
                if (lineText.endsWith("-")) {
                    lineText = lineText.substring(0, lineText.length()-1);
                    if (lineText.lastIndexOf(' ') < 0) {
                        contentText += lineText;
                    } else {
                        contentText += lineText.substring(0, lineText.lastIndexOf(' '));
                        contentText += "\n";
                        contentText += lineText.substring(lineText.lastIndexOf(' ')+1);
                    }
                } else {
                    contentText += lineText;
                    contentText += "\n";
                }
            }
            if (!contentText.isEmpty()) {
                contentTexts.add(cleanLigatures(contentText.trim()));
            }
            
            contentPart.setCleanContentTexts(contentTexts);
        }
    }
    
    private String cleanLigatures(String str) {
        return str.replaceAll("\uFB00", "ff")
                  .replaceAll("\uFB01", "fi")
                  .replaceAll("\uFB02", "fl")
                  .replaceAll("\uFB03", "ffi")
                  .replaceAll("\uFB04", "ffl")
                  .replaceAll("\uFB05", "ft")
                  .replaceAll("\uFB06", "st");
    }

    public void setFirstParagraphLineMinScore(double firstParagraphLineMinScore) {
        this.firstParagraphLineMinScore = firstParagraphLineMinScore;
    }

    public void setLastParagraphLineLengthMult(double lastParagraphLineLengthMult) {
        this.lastParagraphLineLengthMult = lastParagraphLineLengthMult;
    }

    public void setMinParagraphIndent(double minParagraphIndent) {
        this.minParagraphIndent = minParagraphIndent;
    }

    public void setParagraphLineIndentMultiplier(double paragraphLineIndentMultiplier) {
        this.paragraphLineIndentMultiplier = paragraphLineIndentMultiplier;
    }
    
}
