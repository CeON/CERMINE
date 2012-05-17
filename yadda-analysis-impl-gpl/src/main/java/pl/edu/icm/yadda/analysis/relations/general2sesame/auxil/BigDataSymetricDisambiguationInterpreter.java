package pl.edu.icm.yadda.analysis.relations.general2sesame.auxil;

import pl.edu.icm.yadda.analysis.relations.DisambiguationInterpreter;
import pl.edu.icm.yadda.analysis.relations.Disambiguator;
import pl.edu.icm.yadda.analysis.relations.bigdataClues.BigdataFeature1Email;
import pl.edu.icm.yadda.analysis.relations.constants.RelConstants;

public class BigDataSymetricDisambiguationInterpreter implements DisambiguationInterpreter{

	@Override
	public double interpretResult(Disambiguator disambiguator, double result) {
		
		if(disambiguator instanceof BigdataFeature1Email){
			if(result > 0) return 1.0;
			return 1;
		}
		if(result==-1)//-1 is equavalent of null
			return 0.0;
		if(result==0)//zero means "no connection"
			return -1;
		return 1;
	}
	public String id(){
		return RelConstants.NS_FEATURE_INTERPRETER+"symmetry-interpreter";
	}

}
