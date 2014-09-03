package pl.edu.icm.cermine.affparse.dictfeatures;

public class State extends DictionaryFeature {

	public State(boolean useLowerCase) {
		super(useLowerCase);
	}

	@Override
	protected String getFeatureString() {
		return "State";
	}

	@Override
	protected String getDictionaryFileName() {
		return "states.txt";
	}

}
