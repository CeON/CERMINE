package pl.edu.icm.cermine.affparse.dictfeatures;

public class StateCode extends DictionaryFeature {

	public StateCode(boolean useLowerCase) {
		super(useLowerCase);
	}

	@Override
	protected String getFeatureString() {
		return "StateCode";
	}

	@Override
	protected String getDictionaryFileName() {
		return "state_codes.txt";
	}

}
