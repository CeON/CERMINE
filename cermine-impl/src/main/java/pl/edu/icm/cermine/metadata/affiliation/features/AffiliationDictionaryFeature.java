package pl.edu.icm.cermine.metadata.affiliation.features;

import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.metadata.affiliation.tools.AffiliationTokenizer;
import pl.edu.icm.cermine.metadata.model.AffiliationToken;
import pl.edu.icm.cermine.parsing.features.KeywordFeatureCalculator;


/**
 * Keyword feature calculator suitable for processing affiliations.
 * 
 * @author Bartosz Tarnawski
 */
public class AffiliationDictionaryFeature extends KeywordFeatureCalculator<AffiliationToken> {
	
	/**
	 * @param FeatureString the string which will be added to the matching tokens' features lists
	 * @param dictionaryFileName the name of the dictionary to be used (must be a package resource)
	 * @param caseSensitive whether dictionary lookups should be case sensitive
	 * @throws AnalysisException
	 */
	public AffiliationDictionaryFeature(String FeatureString, String dictionaryFileName,
			boolean caseSensitive) throws AnalysisException {
		super(FeatureString, dictionaryFileName, caseSensitive, new AffiliationTokenizer());
	}

}
