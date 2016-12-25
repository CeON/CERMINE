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

package pl.edu.icm.cermine.content.cleaning;

import java.util.ArrayList;
import java.util.List;
import pl.edu.icm.cermine.content.model.BxContentStructure;
import pl.edu.icm.cermine.content.model.BxContentStructure.BxDocContentPart;
import pl.edu.icm.cermine.structure.model.BxLine;

/**
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
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
    
    public void cleanupContent(BxContentStructure contentStructure) {
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
    
    public static String cleanOther(String str) {
        if (str == null) {
            return null;
        }
        return str.replaceAll("[’‘]", "'")
                  .replaceAll("[–]", "-")  // EN DASH \u2013
                  .replaceAll("[—]", "-"); // EM DASH \u2014
    }
    
    public static String cleanLigatures(String str) {
        if (str == null) {
            return null;
        }
        return str.replaceAll("\uFB00", "ff")
                  .replaceAll("\uFB01", "fi")
                  .replaceAll("\uFB02", "fl")
                  .replaceAll("\uFB03", "ffi")
                  .replaceAll("\uFB04", "ffl")
                  .replaceAll("\uFB05", "ft")
                  .replaceAll("\uFB06", "st")
                  .replaceAll("\u00E6", "ae")
                  .replaceAll("\u0153", "oe");
    }
    
    public static String clean(String str) {
        if (str == null) {
            return null;
        }
        return cleanOther(cleanLigatures(str));
    }
    
    public static String cleanHyphenationAndBreaks(String str) {
        if (str == null) {
            return null;
        }
        return cleanHyphenation(str).replaceAll("\n", " ");
    }
    
    public static String cleanHyphenation(String str) {
        if (str == null) {
            return null;
        }
        str = str.replaceAll(" +", " ").replaceAll("^ +", "").replaceAll(" +$", "");
        String hyphenList = "\u002D\u00AD\u2010\u2011\u2012\u2013\u2014\u2015\u207B\u208B\u2212-";
        String[] lines = str.split("\n");
        for (int i = 0; i < lines.length; i++) {
            lines[i] = lines[i].replaceAll("^ +", "").replaceAll(" +$", "");
        }
        StringBuilder sb = new StringBuilder();
        int i = 0;
        while (i < lines.length) {
            String line = lines[i];
            if (i + 1 == lines.length) {
                sb.append(line);
                break;
            }
            String next = lines[i+1];
            if (line.matches("^.*["+hyphenList+"]$")) {
                line = line.substring(0, line.length()-1);
                sb.append(line);
                int idx = next.indexOf(' ');
                if (idx < 0) {
                    sb.append(next);
                    i++;
                } else {
                    sb.append(next.substring(0, idx));
                    lines[i+1] = next.substring(idx+1);                   
                }
            } else {
                sb.append(line);
            }
            sb.append("\n");
            i++;
        }
        return sb.toString().trim();
    }
    
    public static String cleanAll(String str) {
        if (str == null) {
            return null;
        }
        return clean(cleanHyphenation(str));
    }
    
    public static String cleanAllAndBreaks(String str) {
        if (str == null) {
            return null;
         }
        return clean(cleanHyphenationAndBreaks(str));
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
