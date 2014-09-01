package pl.edu.icm.cermine.affparse.dictfeatures;

public class Institution extends DictionaryFeature {

	// NOTE: This feature was used during the TRAINING DATA enhancement stage.
	// Enabling it in the parser may lead to misleadingly high performance in tests.
	
	@Override
	protected String getFeatureString() {
		return "Institution";
	}

	@Override
	protected String getDictionaryFileName() {
		return "institution_keywords.txt";
	}

}
