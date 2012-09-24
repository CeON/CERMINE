package pl.edu.icm.yadda.analysis.metadata.extraction.enhancers;

import com.google.common.collect.Sets;
import java.util.*;
import java.util.regex.Pattern;
import org.jdom.Element;
import pl.edu.icm.yadda.analysis.textr.model.*;

/**
 *
 * @author krusek
 */
public class AffiliationGeometricEnhancer extends AbstractSimpleEnhancer {

    private static final Pattern SKIPPED_LINE_PATTERN = Pattern.compile(
            "Email:.+",
            Pattern.CASE_INSENSITIVE);
    private static final double EPSILON = 1e-3;

    private final Set<String> headers = Sets.newHashSet("author affiliations", "author details");

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
    protected boolean enhanceMetadata(BxDocument document, Element metadata) {
        boolean enhanced = false;
        for (BxPage page : filterPages(document)) {
            Processor processor = new Processor();
            for (BxZone zone : filterZones(page)) {
                boolean firstLine = true;
                for (BxLine line : zone.getLines()) {
                    if (firstLine) {
                        firstLine = false;
                        if (headers.contains(line.toText().toLowerCase())) {
                            continue;
                        }
                    }
                    if (SKIPPED_LINE_PATTERN.matcher(line.toText()).matches()) {
                        continue;
                    }
                    for (BxWord word : line.getWords()) {
                        Iterator<BxChunk> iterator = word.getChunks().iterator();
                        if (iterator.hasNext()) {
                            BxChunk chunk = iterator.next();
                            processor.addText(chunk.toText());
                            double bottom = chunk.getBounds().getY() + chunk.getBounds().getHeight();
                            while (iterator.hasNext()) {
                                chunk = iterator.next();
                                double currentBottom = chunk.getBounds().getY() + chunk.getBounds().getHeight();
                                if (eq(bottom, currentBottom)) {
                                    processor.addText(chunk.toText());
                                } else {
                                    bottom = currentBottom;
                                    processor.nextPart();
                                    processor.addText(chunk.getText());
                                }
                            }
                            processor.endWord();
                        }
                    }
                }
                processor.endZone();
            }
            Map<String, String> affiliations = processor.fetchAffiliations();
            if (!affiliations.isEmpty()) {
                for(Map.Entry<String, String> entry : affiliations.entrySet()) {
                    String text = entry.getValue();
                    text = text.replaceFirst("[Cc]orresponding [Aa]uthor.*$", "");
                    text = text.replaceFirst(" and$", "").replaceFirst("\\S+@.*$", "").replaceFirst("[Ee]mails?:.*$", "");
                    text = text.replaceFirst("[Ee]-[Mm]ails?:.*$", "").trim().replaceFirst("[\\.,;]$", "");
                    Enhancers.setAffiliation(metadata, entry.getKey(), text);
                    enhanced = true;
                }
            }
        }
        Enhancers.cleanAffiliations(metadata);
        return enhanced;
    }

    private static boolean eq(double first, double second) {
        return Math.abs(first - second) <= EPSILON;
    }

    private static class Processor {

        private static final Pattern NONAFFILIATION_PATTERN = Pattern.compile(
                "Correspondence:.+|Contributed equally",
                Pattern.CASE_INSENSITIVE);

        private Map<String, String> affiliations = new HashMap<String, String>();
        int nextIndex = 1;
        private boolean firstPart = true;
        private String affiliationRef = "";
        private StringBuilder affiliationBuilder = new StringBuilder();
        private StringBuilder partBuilder = new StringBuilder();

        private void endAffiliation() {
            if (affiliationBuilder.length() > 0) {
                String text = affiliationBuilder.toString();
                if (!NONAFFILIATION_PATTERN.matcher(text).matches()) {
                    if (isIndex(affiliationRef) || affiliationRef.equals("")) {
                        affiliations.put(affiliationRef, text);
                    }
                }
                affiliationBuilder.setLength(0);
            }
        }

        private boolean isIndex(String text) {
            return Enhancers.isAffiliationIndex(text);
        }

        private boolean isNextIndex(String text) {
            try {
                return isIndex(text) &&  Integer.parseInt(text) == nextIndex;
            } catch (NumberFormatException e) {
                return false;
            }
        }

        private String canonizeRef(String text) {
            if (isNextIndex(text)) {
                nextIndex++;
                return text;
            } else if(text.equals("*") || text.equals("†")) {
                return text;
            }
            return null;
        }

        private void endPart() {
            if (firstPart) {
                String refText = canonizeRef(partBuilder.toString());
                if (refText != null) {
                    endAffiliation();
                    affiliationRef = refText;
                } else {
                    if (affiliationBuilder.length() > 0) {
                        affiliationBuilder.append(' ');
                    }
                    affiliationBuilder.append(partBuilder);
                }
            } else {
                affiliationBuilder.append(partBuilder);
            }
            partBuilder.setLength(0);
        }

        public void endWord() {
            endPart();
            firstPart = true;
        }

        public void endZone() {
            endAffiliation();
        }

        public void addText(String text) {
            partBuilder.append(text);
        }

        public void nextPart() {
            endPart();
            firstPart = false;
        }

        public Map<String, String> fetchAffiliations() {
            endAffiliation();
            return affiliations;
        }
    }
}
