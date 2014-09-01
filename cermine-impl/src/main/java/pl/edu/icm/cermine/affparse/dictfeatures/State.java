package pl.edu.icm.cermine.affparse.dictfeatures;

public class State extends DictionaryFeature {

	@Override
	protected String getFeatureString() {
		return "State";
	}

	@Override
	protected String getDictionaryFileName() {
		return "states.txt";
	}

}
