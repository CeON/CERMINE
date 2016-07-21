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

package pl.edu.icm.cermine.metadata.affiliation.tools;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import pl.edu.icm.cermine.metadata.model.AffiliationLabel;
import pl.edu.icm.cermine.metadata.model.DocumentAffiliation;
import pl.edu.icm.cermine.parsing.model.Token;
import pl.edu.icm.cermine.parsing.tools.NLMParsableStringExtractor;

/**
 * NLM string extractor suitable for extracting document affiliations.
 * 
 * @author Bartosz Tarnawski
 */
public class NLMAffiliationExtractor extends
        NLMParsableStringExtractor<AffiliationLabel, Token<AffiliationLabel>, DocumentAffiliation> {

	private static final AffiliationTokenizer TOKENIZER = new AffiliationTokenizer();
	
    @Override
	protected List<String> getTags() {
		return TAGS_AFFILIATION;
	}

    @Override
	protected String getKeyText() {
		return KEY_TEXT;
	}

    @Override
	protected Map<String, AffiliationLabel> getTagLabelMap() {
		return TAGS_LABEL_MAP;
	}

    @Override
	protected DocumentAffiliation createParsableString() {
		return new DocumentAffiliation("");
	}

    @Override
	protected DocumentAffiliation createParsableString(String text) {
		DocumentAffiliation instance = new DocumentAffiliation(text);
		instance.setTokens(TOKENIZER.tokenize(instance.getRawText()));
		return instance;
	}

    private static final List<String> TAGS_AFFILIATION = Arrays.asList("aff");

    private static final String KEY_TEXT = "text";

    private static final Map<String, AffiliationLabel> TAGS_LABEL_MAP =
    		new HashMap<String, AffiliationLabel>();

    static {
        TAGS_LABEL_MAP.put("addr-line",   	AffiliationLabel.ADDR);
        TAGS_LABEL_MAP.put("institution",   AffiliationLabel.INST);
        TAGS_LABEL_MAP.put("country",   	AffiliationLabel.COUN);
        TAGS_LABEL_MAP.put("author",   		AffiliationLabel.AUTH);
        TAGS_LABEL_MAP.put("text",          AffiliationLabel.TEXT);
    }
}
