package pl.edu.icm.cermine.metadata.affiliations.features;

import pl.edu.icm.cermine.metadata.affiliations.model.AffiliationToken;
import pl.edu.icm.cermine.metadata.affiliations.tools.AffiliationTokenizer;
import pl.edu.icm.cermine.parsing.features.KeywordFeatureCalculator;


/**
 * Keyword feature calculator suitable for processing affiliations.
 * 
 * @author Bartosz Tarnawski
 */
public class AffiliationDictionaryFeature extends KeywordFeatureCalculator<AffiliationToken> {
	
	public AffiliationDictionaryFeature(String FeatureString, String dictionaryFileName,
			boolean caseSensitive) {
		super(FeatureString, dictionaryFileName, caseSensitive, new AffiliationTokenizer());
	}

}
