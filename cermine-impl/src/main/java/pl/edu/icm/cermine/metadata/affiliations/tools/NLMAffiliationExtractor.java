package pl.edu.icm.cermine.metadata.affiliations.tools;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pl.edu.icm.cermine.metadata.affiliations.model.AffiliationLabel;
import pl.edu.icm.cermine.metadata.affiliations.model.AffiliationToken;
import pl.edu.icm.cermine.metadata.model.DocumentAffiliation;
import pl.edu.icm.cermine.parsing.tools.NLMTokenizedStringExtractor;

/**
 * NLM string extractor suitable for extracting document affiliations.
 * 
 * @author Bartosz Tarnawski
 */
public class NLMAffiliationExtractor extends
NLMTokenizedStringExtractor<AffiliationLabel, AffiliationToken, DocumentAffiliation> {

	private static final AffiliationTokenizer tokenizer = new AffiliationTokenizer();
	
	protected List<String> getTags() {
		return TAGS_AFFILIATION;
	}

	protected String getKeyText() {
		return KEY_TEXT;
	}

	protected Map<String, AffiliationLabel> getTagLabelMap() {
		return TAGS_LABEL_MAP;
	}

	protected DocumentAffiliation createString() {
		return new DocumentAffiliation("");
	}

	protected DocumentAffiliation createString(String text) {
		DocumentAffiliation instance = new DocumentAffiliation(text);
		instance.setTokens(tokenizer.tokenize(instance.getRawText()));
		return instance;
	}

    private static final List<String> TAGS_AFFILIATION = Arrays.asList("aff");

    private static final String KEY_TEXT = "text";

    private static final Map<String, AffiliationLabel> TAGS_LABEL_MAP =
    		new HashMap<String, AffiliationLabel>();

    static {
        TAGS_LABEL_MAP.put("addr-line",   	AffiliationLabel.ADDRESS);
        TAGS_LABEL_MAP.put("institution",   AffiliationLabel.INSTITUTION);
        TAGS_LABEL_MAP.put("country",   	AffiliationLabel.COUNTRY);
        TAGS_LABEL_MAP.put("text",          AffiliationLabel.TEXT);
    }
}
