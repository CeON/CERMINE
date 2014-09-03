package pl.edu.icm.cermine.affparse.dictfeatures;

public class City extends DictionaryFeature {

	public City(boolean useLowerCase) {
		super(useLowerCase);
	}

	@Override
	protected String getFeatureString() {
		return "City";
	}

	@Override
	protected String getDictionaryFileName() {
		return "cities.txt";
	}

}
