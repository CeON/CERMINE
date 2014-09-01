package pl.edu.icm.cermine.affparse.dictfeatures;

public class City extends DictionaryFeature {

	@Override
	protected String getFeatureString() {
		return "City";
	}

	@Override
	protected String getDictionaryFileName() {
		return "cities.txt";
	}

}
