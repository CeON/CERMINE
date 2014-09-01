package pl.edu.icm.cermine.affparse.dictfeatures;

public class Address extends DictionaryFeature {

	@Override
	protected String getFeatureString() {
		return "Address";
	}

	@Override
	protected String getDictionaryFileName() {
		return "address_keywords.txt";
	}

}
