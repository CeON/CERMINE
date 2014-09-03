package pl.edu.icm.cermine.affparse.dictfeatures;

public class Country extends DictionaryFeature {

	public Country(boolean useLowerCase) {
		super(useLowerCase);
	}

	@Override
	protected String getFeatureString() {
		return "Country";
	}

	@Override
	protected String getDictionaryFileName() {
		return "countries2.txt";
	}

}
