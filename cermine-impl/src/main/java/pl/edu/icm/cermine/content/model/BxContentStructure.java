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

package pl.edu.icm.cermine.content.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import pl.edu.icm.cermine.structure.model.BxLine;
import pl.edu.icm.cermine.structure.model.BxPage;
import pl.edu.icm.cermine.tools.classification.general.FeatureVector;
import pl.edu.icm.cermine.tools.classification.general.FeatureVectorBuilder;

/**
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class BxContentStructure {
    
    private final List<BxLine> firstHeaderLines = new ArrayList<BxLine>();
    private final Map<BxLine, BxDocContentPart> parts = new HashMap<BxLine, BxDocContentPart>();

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
        
        private final BxPage page;

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
