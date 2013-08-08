/**
 * This file is part of CERMINE project.
 * Copyright (c) 2011-2013 ICM-UW
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

package pl.edu.icm.cermine.metadata.extraction.enhancers;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jdom.Element;
import pl.edu.icm.cermine.structure.model.*;

/**
 * Author enhancer.
 *
 * This enhancer should be invoked after affiliation enhancer.
 *
 * @author krusek
 * @author Dominika Tkaczyk
 */
public class AuthorEnhancer extends AbstractSimpleEnhancer {

    public AuthorEnhancer() {
        setSearchedZoneLabels(EnumSet.of(BxZoneLabel.MET_AUTHOR));
    }

    @Override
    protected boolean enhanceMetadata(BxDocument document, Element metadata) {
        boolean enhanced = false;
        for (BxPage page : filterPages(document)) {
            for (BxZone zone : filterZones(page)) {
                List<BxChunk> chunks = new ArrayList<BxChunk>();
                for (BxLine l : zone.getLines()) {
                    for (BxWord w : l.getWords()) {
                        for (BxChunk ch : w.getChunks()) {
                            chunks.add(ch);
                        }
                        chunks.add(new BxChunk(null, " "));
                    }
                }
                
                Pattern white = Pattern.compile("(\\s+)(.*)");
                Pattern simpleRef = Pattern.compile("(\\d+|\\*|⁎|†|‡)(.*)");
                Pattern title = Pattern.compile("(MD\\b|Prof.\\b|MS\\b|PhD\\b|Phd\\b|MPH\\b|RD\\b|LD\\b)(.*)");
                Pattern separator = Pattern.compile("(,|;|&|•|·|Æ)(.*)");
                Pattern andSeparator = Pattern.compile("(and\\b)(.*)");
                
                boolean afterSep = true;
                int index = 0;
                String text = zone.toText().replaceAll("\n", " ");
                String author = "";
                List<String> refs = new ArrayList<String>();
                boolean auth = false;
                
                while (!text.isEmpty()) {
                    Matcher whiteMatcher = white.matcher(text);
                    Matcher simpleRefMatcher = simpleRef.matcher(text);
                    Matcher titleMatcher = title.matcher(text);
                    Matcher separatorMatcher = separator.matcher(text);
                    Matcher andSeparatorMatcher = andSeparator.matcher(text);
                    if (whiteMatcher.matches()) {
                        index += whiteMatcher.group(1).length();
                        text = whiteMatcher.group(2);
                        afterSep = true;
                        author += whiteMatcher.group(1);
                    } else if (separatorMatcher.matches()) {
                        index += separatorMatcher.group(1).length();
                        text = separatorMatcher.group(2);
                        afterSep = true;
                        auth = false;
                    } else if (afterSep && andSeparatorMatcher.matches()) {
                        index += andSeparatorMatcher.group(1).length();
                        text = andSeparatorMatcher.group(2);
                        afterSep = true;
                        auth = false;
                    } else if (afterSep && titleMatcher.matches()) {
                        index += titleMatcher.group(1).length();
                        text = titleMatcher.group(2);
                        afterSep = true;
                    } else if (simpleRefMatcher.matches()) {
                        index += simpleRefMatcher.group(1).length();
                        text = simpleRefMatcher.group(2);
                        afterSep = true;
                        refs.add(simpleRefMatcher.group(1));
                        auth = false;
                    } else {
                        double chunkY = chunks.get(index).getY();
                        double chunkH = chunks.get(index).getHeight();
                        BxLine line = chunks.get(index).getParent().getParent();
                        double meanY = 0;
                        double meanH = 0;
                        int total = 0;
                        for (BxWord w : line.getWords()) {
                            for (BxChunk ch : w.getChunks()) {
                                meanY += ch.getY();
                                meanH += ch.getHeight();
                                total++;
                            }
                        }
                        meanY /= total;
                        meanH /= total;
        
                        if ((chunks.get(index).toText().matches("[a-f]") 
                                || chunks.get(index).toText().matches("[^a-zA-Z0-9]"))
                                && Math.abs(chunkY-meanY)+ Math.abs(meanH-chunkH) > 2) {
                            index += 1;
                            afterSep = true;
                            refs.add(text.substring(0, 1));
                            text = text.substring(1);
                            auth = false;
                        } else {
                           if (!auth && !author.trim().isEmpty()) {
                               Enhancers.addAuthor(metadata, author.trim(), refs);
                                author = "";
                                refs.clear();
                            }
                            auth = true;
                            author += text.substring(0, 1);
                            index++;
                            text = text.substring(1);
                            afterSep = false;
                        }
                    }
                }
                if (!author.isEmpty()) {
                    Enhancers.addAuthor(metadata, author.trim(), refs);
                }
                
                enhanced = true;
            }
            if (enhanced) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected Set<EnhancedField> getEnhancedFields() {
        return EnumSet.of(EnhancedField.AUTHORS);
    }

}
