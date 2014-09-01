package pl.edu.icm.cermine.affparse.dictfeatures;

public class StateCode extends DictionaryFeature {

	@Override
	protected String getFeatureString() {
		return "StateCode";
	}

	@Override
	protected String getDictionaryFileName() {
		return "state_codes.txt";
	}

}
