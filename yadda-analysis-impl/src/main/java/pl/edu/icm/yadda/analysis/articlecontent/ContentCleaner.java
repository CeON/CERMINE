package pl.edu.icm.yadda.analysis.articlecontent;

import java.util.ArrayList;
import java.util.List;
import pl.edu.icm.yadda.analysis.articlecontent.model.BxDocContentStructure;
import pl.edu.icm.yadda.analysis.articlecontent.model.BxDocContentStructure.BxDocContentPart;
import pl.edu.icm.yadda.analysis.textr.model.BxLine;

/**
 *
 * @author Dominika Tkaczyk
 */
public class ContentCleaner {
    
    private double paragraphLineIndentMultiplier = 0.5;
    
    private double minParagraphIndent = 5;
    
    private double lastParagraphLineLengthMult = 0.8;
    
    private double firstParagraphLineMinScore = 3;

    public void cleanupContent(BxDocContentStructure contentStructure) {
        for (BxDocContentPart contentPart : contentStructure.getParts()) {
            
            List<BxLine> headerLines = contentPart.getHeaderLines();
            String headerText = "";
            for (BxLine headerLine : headerLines) {
                String lineText = headerLine.toText();
                if (lineText.endsWith("-")) {
                    lineText = lineText.substring(0, lineText.length()-1);
                    if (lineText.lastIndexOf(" ") < 0) {
                        headerText += lineText;
                    } else {
                        headerText += lineText.substring(0, lineText.lastIndexOf(" "));
                        headerText += " ";
                        headerText += lineText.substring(lineText.lastIndexOf(" ")+1);
                    }
                } else {
                    headerText += lineText;
                    headerText += " ";
                }
            }
            contentPart.setCleanHeaderText(cleanLigatures(headerText.trim()));
            
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
                if (next != null) {
                    if (line.getX() > next.getX() && line.getX() - next.getX() < paragraphLineIndentMultiplier * maxLen 
                            && line.getX() - next.getX() > minParagraphIndent) {
                        score++;
                    }
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
                    if (lineText.lastIndexOf(" ") < 0) {
                        contentText += lineText;
                    } else {
                        contentText += lineText.substring(0, lineText.lastIndexOf(" "));
                        contentText += "\n";
                        contentText += lineText.substring(lineText.lastIndexOf(" ")+1);
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
    
}
