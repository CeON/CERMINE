package pl.edu.icm.cermine.metadata.affiliations.features;

import pl.edu.icm.cermine.metadata.affiliations.model.AffiliationLabel;
import pl.edu.icm.cermine.metadata.affiliations.model.AffiliationToken;
import pl.edu.icm.cermine.metadata.affiliations.tools.AffiliationTokenizer;
import pl.edu.icm.cermine.parsing.features.DictionaryFeature;

public class AffiliationDictionaryFeature extends
DictionaryFeature<AffiliationLabel, AffiliationToken> {
	public AffiliationDictionaryFeature(String FeatureString, String dictionaryFileName,
			boolean useLowerCase) {
		super(FeatureString, dictionaryFileName, useLowerCase, new AffiliationTokenizer());
	}

}
