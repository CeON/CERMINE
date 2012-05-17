package pl.edu.icm.yadda.analysis.relations;

import pl.edu.icm.yadda.analysis.relations.constants.RelConstants;

public class DummyInterpreter implements DisambiguationInterpreter{

	public double interpretResult(Disambiguator disambiguator,
			double result) {
		return result;
	}
	public String id(){
		return RelConstants.NS_FEATURE_INTERPRETER+"dummy-interpreter";
	}
}
