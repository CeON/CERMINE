package pl.edu.icm.cermine.affparse.dictfeatures;

public class StopWord extends DictionaryFeature {

	@Override
	protected String getFeatureString() {
		return "StopWordMulti"; // TODO change to StopWord
	}

	@Override
	protected String getDictionaryFileName() {
		return "stop_words_multilang.txt";
	}

}
