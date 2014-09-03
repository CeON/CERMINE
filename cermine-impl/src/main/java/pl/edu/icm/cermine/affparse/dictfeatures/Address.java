package pl.edu.icm.cermine.affparse.dictfeatures;

public class Address extends DictionaryFeature {

	public Address(boolean useLowerCase) {
		super(useLowerCase);
	}

	@Override
	protected String getFeatureString() {
		return "Address";
	}

	@Override
	protected String getDictionaryFileName() {
		return "address_keywords.txt";
	}

}
