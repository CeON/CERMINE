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
import java.util.List;
import pl.edu.icm.cermine.content.cleaning.ContentCleaner;
import pl.edu.icm.cermine.metadata.tools.MetadataTools;
import pl.edu.icm.cermine.parsing.model.ParsableString;
import pl.edu.icm.cermine.parsing.model.Token;

/**
 * Represents a document affiliation as a parsable string.
 *
 * @author Dominika Tkaczyk
 * @author Bartosz Tarnawski
 */
public class DocumentAffiliation implements ParsableString<Token<AffiliationLabel>> {

    private String id;
    
    private String index;
    
    private String rawText;
    
    private List<Token<AffiliationLabel>> tokens;
    
    public DocumentAffiliation(String rawText) {
    	this("id", rawText);
    }

    public DocumentAffiliation(String id, String rawText) {
        this(id, null, rawText);
    }
    
    public DocumentAffiliation(String id, String index, String rawText) {
        this.id = id;
        this.index = ContentCleaner.clean(index);
        this.rawText = MetadataTools.cleanAndNormalize(rawText);
        this.tokens = new ArrayList<Token<AffiliationLabel>>();
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

    @Override
    public String getRawText() {
        return rawText;
    }

    public void setRawText(String rawText) {
        this.rawText = rawText;
    }
    
    @Override
	public List<Token<AffiliationLabel>> getTokens() {
		return tokens;
	}

    @Override
	public void setTokens(List<Token<AffiliationLabel>> tokens) {
		this.tokens = tokens;
	}

    @Override
	public void addToken(Token<AffiliationLabel> token) {
		this.tokens.add(token);
	}

    public void mergeTokens() {
        if (tokens == null || tokens.isEmpty()){
            return;
        }
        Token<AffiliationLabel> actToken = null;
        List<Token<AffiliationLabel>> newTokens = new ArrayList<Token<AffiliationLabel>>();
        for (Token<AffiliationLabel> token : tokens) {
            if (actToken == null){
                actToken = new Token<AffiliationLabel>(token.getText(), token.getStartIndex(), token.getEndIndex(), token.getLabel());
                
            } else if (actToken.getLabel().equals(token.getLabel())) {
                actToken.setEndIndex(token.getEndIndex());
            } else {
                newTokens.add(actToken);
                actToken = new Token<AffiliationLabel>(token.getText(), token.getStartIndex(), token.getEndIndex(), token.getLabel());
            }
        }
        newTokens.add(actToken);
        for (Token<AffiliationLabel> token : newTokens) {
            int i = newTokens.indexOf(token);
            if (i + 1 == newTokens.size()) {
                token.setEndIndex(rawText.length());
            } else {
                token.setEndIndex(newTokens.get(i+1).getStartIndex());
            }
            token.setText(rawText.substring(token.getStartIndex(), token.getEndIndex()));
        }
        tokens = newTokens;
    }
    
    @Override
	public void appendText(String text) {
		this.rawText += text;
	}

    @Override
    public void clean() {
        index = ContentCleaner.clean(index);
        rawText = ContentCleaner.cleanAllAndBreaks(rawText);
    }

}
