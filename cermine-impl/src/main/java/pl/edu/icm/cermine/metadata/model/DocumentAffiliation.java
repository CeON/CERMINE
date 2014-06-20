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

package pl.edu.icm.cermine.metadata.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import pl.edu.icm.cermine.metadata.tools.MetadataTools;

/**
 *
 * @author Dominika Tkaczyk
 */
public class DocumentAffiliation {

    private String id;
    
    private String index;
    
    private String rawText;
    
    private List<AffiliationToken> tokens = new ArrayList<AffiliationToken>();

    public DocumentAffiliation(String id, String rawText) {
        this(id, null, rawText);
    }
    
    public DocumentAffiliation(String id, String index, String rawText) {
        this.id = id;
        this.index = index;
        this.rawText = MetadataTools.clean(rawText);
        tokens.add(new AffiliationToken(0, this.rawText.length(), this.rawText));
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getRawText() {
        return rawText;
    }

    public void setRawText(String rawText) {
        this.rawText = rawText;
    }

    void clean() {
        index = MetadataTools.clean(index);
        rawText = MetadataTools.clean(rawText);
    }

    public void addCountry(int bIndex, int eIndex){
        addToken(bIndex, eIndex, TAG_COUNTRY);
    }
    
    private void addToken(int bIndex, int eIndex, String tag) {
        List<AffiliationToken> significantTokens = new ArrayList<AffiliationToken>();
        for (AffiliationToken token : tokens) {
            if ((bIndex >= token.startIndex && bIndex < token.endIndex)
                    || (eIndex >= token.startIndex && eIndex < token.endIndex)) {
                significantTokens.add(token);
            }
        }
        if (significantTokens.isEmpty()) {
            return;
        }
        tokens.removeAll(significantTokens);
        AffiliationToken first = significantTokens.get(0);
        AffiliationToken last = significantTokens.get(significantTokens.size()-1);
        tokens.add(new AffiliationToken(first.startIndex, bIndex, first.getTag(), rawText.substring(first.startIndex, bIndex)));
        tokens.add(new AffiliationToken(bIndex, eIndex, tag, rawText.substring(bIndex, eIndex)));
        tokens.add(new AffiliationToken(eIndex, last.endIndex, last.getTag(), rawText.substring(eIndex, last.endIndex)));
        
        Collections.sort(tokens, new Comparator<AffiliationToken>() {

            @Override
            public int compare(AffiliationToken t, AffiliationToken t1) {
                return Integer.valueOf(t.startIndex).compareTo(t1.startIndex);
            }
        });
    }

    public List<AffiliationToken> getTokens() {
        return tokens;
    }

        
    public static final String TAG_COUNTRY = "country";
    
    
    public static class AffiliationToken {
        private int startIndex;
        private int endIndex;
        private String tag;
        private String text;

        public AffiliationToken(int startIndex, int endIndex, String text) {
            this.startIndex = startIndex;
            this.endIndex = endIndex;
            this.text = text;
        }

        public AffiliationToken(int startIndex, int endIndex, String tag, String text) {
            this.startIndex = startIndex;
            this.endIndex = endIndex;
            this.tag = tag;
            this.text = text;
        }

        public String getTag() {
            return tag;
        }

        public String getText() {
            return text;
        }
    }
    
}
