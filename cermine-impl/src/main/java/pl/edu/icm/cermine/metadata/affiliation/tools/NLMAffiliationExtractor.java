package pl.edu.icm.cermine.metadata.affiliation.tools;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import pl.edu.icm.cermine.metadata.model.AffiliationLabel;
import pl.edu.icm.cermine.metadata.model.AffiliationToken;
import pl.edu.icm.cermine.metadata.model.DocumentAffiliation;
import pl.edu.icm.cermine.parsing.tools.NLMParsableStringExtractor;

/**
 * NLM string extractor suitable for extracting document affiliations.
 * 
 * @author Bartosz Tarnawski
 */
public class NLMAffiliationExtractor extends
NLMParsableStringExtractor<AffiliationLabel, AffiliationToken, DocumentAffiliation> {

	private static final AffiliationTokenizer tokenizer = new AffiliationTokenizer();
	
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
        TAGS_LABEL_MAP.put("author",   		AffiliationLabel.AUTHOR);
        TAGS_LABEL_MAP.put("text",          AffiliationLabel.TEXT);
    }
}
