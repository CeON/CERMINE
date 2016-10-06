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

package pl.edu.icm.cermine.metadata.extraction.enhancers;

import com.google.common.base.CharMatcher;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import pl.edu.icm.cermine.metadata.model.DocumentMetadata;
import pl.edu.icm.cermine.structure.model.*;

/**
 * Author enhancer.
 *
 * This enhancer should be invoked after affiliation enhancer.
 *
 * @author Krzysztof Rusek
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class AuthorEnhancer extends AbstractSimpleEnhancer {

    public AuthorEnhancer() {
        setSearchedZoneLabels(EnumSet.of(BxZoneLabel.MET_AUTHOR));
    }

    @Override
    protected boolean enhanceMetadata(BxDocument document, DocumentMetadata metadata) {
        boolean enhanced = false;
        for (BxPage page : filterPages(document)) {
            for (BxZone zone : filterZones(page)) {
                List<BxChunk> chunks = new ArrayList<BxChunk>();
                for (BxLine l : zone) {
                    for (BxWord w : l) {
                        for (BxChunk ch : w) {
                            chunks.add(ch);
                        }
                        chunks.add(new BxChunk(null, " "));
                    }
                }
                chunks.remove(chunks.size()-1);

                Pattern white = Pattern.compile("(\\s+)(.*)");
                Pattern simpleRef = Pattern.compile("(\\d+|\\*|∗|⁎|†|‡|§|\\(..?\\)|\\{|¶|\\[..?\\]|\\+|\\||⊥|\\^|¹|²|³|#|α|β|λ|ξ|ψ)(.*)");
                Pattern title = Pattern.compile("(MD|Prof.|PhD|Phd|MPH|RD|LD|BCh|BAO|PharmD|BSc|FRCP|PA-C|RAC|MBA|DrPH|MBChB|BM|RGN|BA|FCCP)([^a-zA-Z].*)");
                Pattern titleEnd = Pattern.compile("(MD|Prof.|PhD|Phd|MPH|RD|LD|BCh|BAO|PharmD|BSc|FRCP|PA-C|RAC|MBA|DrPH|MBChB|BM|RGN|BA|FCCP)");
                Pattern separator = Pattern.compile("(,|;|&|•|·|Æ)(.*)");
                Pattern andSeparator = Pattern.compile("(and|AND)\\b(.*)");
                Pattern andEndSeparator = Pattern.compile("(and|AND)");

                boolean afterSep = true;
                int index = 0;
                String text = zone.toText().replaceAll("\n", " ");
                String author = "";
                List<String> refs = new ArrayList<String>();
                boolean auth = false;

                if (text.toLowerCase().contains("vol") && text.toLowerCase().contains("no")) {
                    continue;
                }

                while (!text.isEmpty()) {
                    Matcher whiteMatcher = white.matcher(text);
                    Matcher simpleRefMatcher = simpleRef.matcher(text);
                    Matcher titleMatcher = title.matcher(text);
                    Matcher titleEndMatcher = titleEnd.matcher(text);
                    Matcher separatorMatcher = separator.matcher(text);
                    Matcher andSeparatorMatcher = andSeparator.matcher(text);
                    Matcher andEndSeparatorMatcher = andEndSeparator.matcher(text);
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
                    } else if (afterSep && andEndSeparatorMatcher.matches()) {
                        text = "";
                    } else if (afterSep && titleMatcher.matches()) {
                        index += titleMatcher.group(1).length();
                        text = titleMatcher.group(2);
                        afterSep = true;
                    } else if (afterSep && titleEndMatcher.matches()) {
                        text = "";
                    } else if (simpleRefMatcher.matches()) {
                        index += simpleRefMatcher.group(1).length();
                        text = simpleRefMatcher.group(2);
                        afterSep = true;
                        refs.add(simpleRefMatcher.group(1));
                        auth = false;
                    } else {
                        BxChunk chunk = chunks.get(index);
                        double chunkY = 0;
                        double chunkH = 0;
                        double meanY = 0;
                        double meanH = 0;
                        if (chunk.getBounds() != null) {
                            chunkY = chunk.getY();
                            chunkH = chunk.getHeight();
                            BxLine line = chunk.getParent().getParent();
                            int total = 0;
                            for (BxWord w : line) {
                                for (BxChunk ch : w) {
                                    meanY += ch.getY();
                                    meanH += ch.getHeight();
                                    total++;
                                }
                            }
                            meanY /= total;
                            meanH /= total;
                        }
                        if (chunk.toText().matches("[a-f]") && Math.abs(chunkY-meanY)+ Math.abs(meanH-chunkH) > 2) {
                            index += 1;
                            afterSep = true;
                            refs.add(text.substring(0, 1));
                            text = text.substring(1);
                            auth = false;
                        } else {
                            if (!auth && !author.trim().isEmpty()) {
                                author = CharMatcher.WHITESPACE.trimFrom(author);
                                if (!author.equalsIgnoreCase("article info") && author.matches(".*[a-zA-Z].*")
                                        && author.length() > 4 && author.length() < 50) {
                                    metadata.addAuthor(author, refs);
                                }
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
                if (!author.isEmpty() && !author.toLowerCase().endsWith("introduction")) {
                    author = CharMatcher.WHITESPACE.trimFrom(author);
                    if (!author.equalsIgnoreCase("article info") && author.matches(".*[a-zA-Z].*")
                            && author.length() > 4 && author.length() < 50) {
                        metadata.addAuthor(author, refs);
                    }
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
