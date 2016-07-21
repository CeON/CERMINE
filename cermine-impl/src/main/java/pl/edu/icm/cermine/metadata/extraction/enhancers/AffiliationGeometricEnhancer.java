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

import com.google.common.collect.Sets;
import java.util.*;
import java.util.regex.Pattern;
import pl.edu.icm.cermine.metadata.model.DocumentAuthor;
import pl.edu.icm.cermine.metadata.model.DocumentMetadata;
import pl.edu.icm.cermine.structure.model.*;

/**
 * @author Krzysztof Rusek
 */
public class AffiliationGeometricEnhancer extends AbstractSimpleEnhancer {

    private static final Pattern SKIPPED_LINE_PATTERN = Pattern.compile(
            ".*(Email|Correspondence|Contributed equally|Dated|This work|Electronic address|The authors|Index|Received|Draft):?.*",
            Pattern.CASE_INSENSITIVE);
    private static final Pattern EMAIL_SIMPLE_LINE_PATTERN = Pattern.compile(
            "\\S+@[^,\\s]+",
            Pattern.CASE_INSENSITIVE);
    private static final Pattern EMAIL_LINE_PATTERN = Pattern.compile(
            "[\\{\\[]([^,\\s]+, ?)+[^,\\s]+ ?[\\}\\]]?@\\S+",
            Pattern.CASE_INSENSITIVE);
       
    private static final Pattern FULL_INDEX_PATTERN = Pattern.compile("\\d{1,2}|\\*|∗|⁎|†|‡|§|\\(..?\\)|\\{|¶|\\[..?\\]|\\+|\\||⊥|\\^|#|α|β|λ|ξ|ψ|[a-f]|¹|²|³");
    private static final Pattern SIMPLE_INDEX_PATTERN = Pattern.compile("\\*|∗|⁎|†|‡|§|\\{|¶|\\+|\\||⊥|\\^|#|α|β|λ|ξ|ψ|¹|²|³");
        

    private final Set<String> headers = Sets.newHashSet("authoraffiliations", "authordetails", "affiliations");

    public AffiliationGeometricEnhancer() {
        setSearchedZoneLabels(BxZoneLabel.MET_AFFILIATION);
    }

    public void setHeaders(Collection<String> headers) {
        this.headers.clear();
        for (String header : headers) {
            this.headers.add(header.toLowerCase());
        }
    }

    @Override
    protected Set<EnhancedField> getEnhancedFields() {
        return EnumSet.of(EnhancedField.AFFILIATION);
    }

    @Override
    protected boolean enhanceMetadata(BxDocument document, DocumentMetadata metadata) {
        Set<String> indexes = new HashSet<String>();
        for (DocumentAuthor author : metadata.getAuthors()) {
            indexes.addAll(author.getAffiliationRefs());
        }
        
        boolean enhanced = false;
        for (BxPage page : filterPages(document)) {
            Processor processor = new Processor();
            for (BxZone zone : filterZones(page)) {
                if (zone.getY() > page.getHeight() / 2 && zone.hasPrev() && zone.getPrev().toText().equals("Keywords")) {
                    continue;
                }
                boolean firstLine = true;
                for (BxLine line : zone) {
                    if (firstLine) {
                        firstLine = false;
                        if (headers.contains(line.toText().toLowerCase().replaceAll("[^0-9a-zA-Z]", ""))) {
                            continue;
                        }
                    }
                    if (SKIPPED_LINE_PATTERN.matcher(line.toText()).matches()
                            || EMAIL_SIMPLE_LINE_PATTERN.matcher(line.toText()).matches()
                            || EMAIL_LINE_PATTERN.matcher(line.toText()).matches()) {
                        continue;
                    }
                    
                    double meanY = 0;
                    double meanH = 0;
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
                        
                    for (BxWord word : line) {
                        Iterator<BxChunk> iterator = word.iterator();
                        while (iterator.hasNext()) {
                            BxChunk chunk = iterator.next();
                            double chunkY = chunk.getY();
                            double chunkH = chunk.getHeight();
                            if (SIMPLE_INDEX_PATTERN.matcher(chunk.toText()).matches() ||
                                Math.abs(chunkY-meanY)+ Math.abs(meanH-chunkH) > 2 ||
                                (indexes.contains(chunk.toText()) && chunk.getParent().childrenCount() < 3 
                                    && chunk.getParent().equals(chunk.getParent().getParent().getFirstChild()))) {
                                processor.addAffIndex(chunk.toText());
                            } else {
                                processor.addText(chunk.toText());
                            }
                        }
                        processor.endWord();
                    }
                }
                processor.endZone();
            }
            Map<String, String> affiliations = processor.fetchAffiliations();
            if (!affiliations.isEmpty()) {
                for(Map.Entry<String, String> entry : affiliations.entrySet()) {
                    String text = entry.getValue();
                    text = text.trim()
                            .replaceFirst("[Cc]orresponding [Aa]uthor.*$", "").trim()
                            .replaceFirst("[Cc]opyright is held by.*$", "").trim()
                            .replaceFirst("Full list of author information.*$", "").trim()
                            .replaceFirst("\\(?(January|February|March|April|May|June|July|August|September|October|November|December) .*$", "").trim()
                            .replaceFirst(" and$", "").trim()
                            .replaceFirst("\\S+@.*$", "").trim()
                            .replaceFirst("\\(?[Ee]mails?:.*$", "").trim()
                            .replaceFirst("\\(?[Ee]- *[Mm]ails?:.*$", "").trim()
                            .replaceFirst("http://.*$", "").trim()
                            .replaceFirst("www\\..*$", "").trim()
                            .replaceFirst("Acknowledgements.*$", "").trim()
                            .replaceFirst("[\\.,;]$", "").trim()
                            .replaceFirst("^[-\\)]", "").trim()
                            .replaceAll("(?<=[a-z])- (?=[a-z])", "");
                    if (text.isEmpty() || !text.matches(".*[A-Z].*") || !text.matches(".*[a-z].*") || text.length() < 12) {
                        continue;
                    }
                    String index = entry.getKey();
                    if (index.startsWith("aff-")) {
                        index = "";
                    }
                    if (index.isEmpty()) {
                        if (text.matches("^[1-9]\\. .*") && indexes.contains(text.substring(0, 1))) {
                            while (!text.isEmpty()) {
                                index = text.substring(0, 1);
                                text = text.replaceFirst("^[1-9]\\. *", "").trim();
                                String t = text.replaceFirst(" *[1-9]\\. .*$", "");
                                String rest = text.substring(t.length()).trim();
                                if (!rest.isEmpty() && indexes.contains(rest.substring(0, 1))) {
                                    if (t.length() > 20) {
                                        metadata.setAffiliationByIndex(index, t);
                                    }
                                    text = rest;
                                } else {
                                    if (text.length() > 20) {
                                        metadata.setAffiliationByIndex(index, text);
                                    }
                                    text = "";
                                }
                            }
                        } else {
                            metadata.addAffiliationToAllAuthors(text);
                        }
                    } else {
                        metadata.setAffiliationByIndex(index, text);
                    }
                    enhanced = true;
                }
            }
        }

        return enhanced;
    }

    private static class Processor {

        private static final Pattern NONAFFILIATION_PATTERN = Pattern.compile(
                "Correspondence:.+|Contributed equally",
                Pattern.CASE_INSENSITIVE);

        private final Map<String, String> affiliations = new HashMap<String, String>();
        private String affiliationRef = "";
        private final StringBuilder affiliationBuilder = new StringBuilder();
        private int emptyIndex = 100;

        private void endAffiliation() {
            if (affiliationBuilder.length() > 0) {
                String text = affiliationBuilder.toString();
                if (!NONAFFILIATION_PATTERN.matcher(text).matches()
                        && (isIndex(affiliationRef) || affiliationRef.isEmpty())) {
                    if (affiliationRef.isEmpty()) {
                        affiliationRef = "aff-"+emptyIndex;
                        emptyIndex++;
                    }
                    affiliations.put(affiliationRef, text);
                }
                affiliationBuilder.setLength(0);
                affiliationRef = "";
            }
        }

        private boolean isIndex(String text) {
            return FULL_INDEX_PATTERN.matcher(text).matches();
        }

        public void endWord() {
            affiliationBuilder.append(" ");
        }

        public void endZone() {
            endAffiliation();
        }

        public void addText(String text) {
            affiliationBuilder.append(text);
        }

        public Map<String, String> fetchAffiliations() {
            endAffiliation();
            return affiliations;
        }

        private void addAffIndex(String toText) {
            endAffiliation();
            affiliationRef += toText;
        }
    }
}
