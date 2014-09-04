package pl.edu.icm.cermine.affparse.dictfeatures;

import pl.edu.icm.cermine.affparse.model.AffiliationLabel;
import pl.edu.icm.cermine.affparse.model.AffiliationToken;
import pl.edu.icm.cermine.affparse.tools.AffiliationNormalizer;
import pl.edu.icm.cermine.affparse.tools.AffiliationTokenizer;

public class AffiliationDictionaryFeature extends
DictionaryFeature<AffiliationLabel, AffiliationToken> {
	public AffiliationDictionaryFeature(String FeatureString, String dictionaryFileName,
			boolean useLowerCase) {
		super(FeatureString, dictionaryFileName, useLowerCase,
				new AffiliationTokenizer(), new AffiliationNormalizer());
	}

}
