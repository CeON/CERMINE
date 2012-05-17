package pl.edu.icm.yadda.analysis.relations.general2sesame.auxil;

import pl.edu.icm.yadda.analysis.relations.DisambiguationInterpreter;
import pl.edu.icm.yadda.analysis.relations.Disambiguator;
import pl.edu.icm.yadda.analysis.relations.constants.RelConstants;

public class NotNegativeDisambiguatorInterpreter implements DisambiguationInterpreter{

	@Override
	public double interpretResult(Disambiguator disambiguator, double result) {
		
//		if(disambiguator instanceof Example){
//			return result;
//		}
		
		if(result <= 0) return 0.0;
		return 1;
	}
	public String id(){
		return RelConstants.NS_FEATURE_INTERPRETER+"less-0-equal-0-otherwise-1-interpreter";
	}
	
//	private class Example{}
}
