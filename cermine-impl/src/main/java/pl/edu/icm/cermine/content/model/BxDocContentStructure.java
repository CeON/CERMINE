package pl.edu.icm.cermine.content.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import pl.edu.icm.cermine.structure.model.BxLine;
import pl.edu.icm.cermine.structure.model.BxPage;
import pl.edu.icm.cermine.tools.classification.features.FeatureVector;
import pl.edu.icm.cermine.tools.classification.features.FeatureVectorBuilder;

/**
 *
 * @author Dominika Tkaczyk
 */
public class BxDocContentStructure {
    
    private List<BxLine> firstHeaderLines = new ArrayList<BxLine>();
    private Map<BxLine, BxDocContentPart> parts = new HashMap<BxLine, BxDocContentPart>();


    public void addFirstHeaderLine(BxPage page, BxLine headerLine) {
        firstHeaderLines.add(headerLine);
        parts.put(headerLine, new BxDocContentPart(page, headerLine));
    }

    public void addContentLine(BxLine headerLine, BxLine contentLine) {
        if (parts.get(headerLine) != null) {
            parts.get(headerLine).addContentLine(contentLine);
        }
    }

    public FeatureVector[] getFirstHeaderFeatureVectors(FeatureVectorBuilder builder) {
        FeatureVector[] fvs = new FeatureVector[parts.size()];
        int i = 0;
        for (BxLine header : firstHeaderLines) {
            fvs[i] = parts.get(header).getFirstHeaderFeatureVector(builder);
            i++;
        }
        return fvs;
    }

    public void setHeaderLevelIds(int[] ids) {
        int i = 0;
        for (BxLine header : firstHeaderLines) {
            parts.get(header).setLevelId(ids[i]);
            i++;
        }
    }

    public List<BxLine> getFirstHeaderLines() {
        return firstHeaderLines;
    }

    public boolean containsFirstHeaderLine(BxLine headerLine) {
        return firstHeaderLines.contains(headerLine);
    }

    public void addAdditionalHeaderLine(BxLine headerLine, BxLine additionalLine) {
        parts.get(headerLine).markLineAsHeader(additionalLine);
    }

    public List<BxDocContentPart> getParts() {
        List<BxDocContentPart> sortedParts = new ArrayList<BxDocContentPart>();
        for (BxLine header : firstHeaderLines) {
            sortedParts.add(parts.get(header));
        }
        return sortedParts;
    }

    public int getTopHeaderLevelId() {
        if (firstHeaderLines == null || firstHeaderLines.isEmpty()) {
            return -1;
        }
        return parts.get(firstHeaderLines.get(0)).getLevelId();
    }


    public static class BxDocContentPart {
        
        private int levelId;
        
        private BxPage page;

        private BxLine firstHeaderLine;
        private List<BxLine> headerLines = new ArrayList<BxLine>();
        private String cleanHeaderText;

        private List<BxLine> contentLines = new ArrayList<BxLine>();
        private List<String> cleanContentTexts = new ArrayList<String>();

        
        public BxDocContentPart(BxPage page, BxLine firstHeaderLine) {
            this.page = page;
            this.firstHeaderLine = firstHeaderLine;
            this.headerLines.add(firstHeaderLine);
        }

        public int getLevelId() {
            return levelId;
        }

        public void setLevelId(int levelId) {
            this.levelId = levelId;
        }

        public BxLine getFirstHeaderLine() {
            return firstHeaderLine;
        }

        public void setFirstHeaderLine(BxLine firstHeaderLine) {
            this.firstHeaderLine = firstHeaderLine;
        }

        public FeatureVector getFirstHeaderFeatureVector(FeatureVectorBuilder builder) {
            return builder.getFeatureVector(firstHeaderLine, page);
        }

        public List<String> getCleanContentTexts() {
            return cleanContentTexts;
        }

        public void setCleanContentTexts(List<String> cleanContentTexts) {
            this.cleanContentTexts = cleanContentTexts;
        }

        public String getCleanHeaderText() {
            return cleanHeaderText;
        }

        public void setCleanHeaderText(String cleanHeaderText) {
            this.cleanHeaderText = cleanHeaderText;
        }

        public List<BxLine> getContentLines() {
            return contentLines;
        }

        public void setContentLines(List<BxLine> contentLines) {
            this.contentLines = contentLines;
        }

        public List<BxLine> getHeaderLines() {
            return headerLines;
        }

        public void setHeaderLines(List<BxLine> headerLines) {
            this.headerLines = headerLines;
        }

        private void addContentLine(BxLine line) {
            contentLines.add(line);
        }

        private void markLineAsHeader(BxLine headerLine) {
            headerLines.add(headerLine);
            contentLines.remove(headerLine);
        }

    }
}
