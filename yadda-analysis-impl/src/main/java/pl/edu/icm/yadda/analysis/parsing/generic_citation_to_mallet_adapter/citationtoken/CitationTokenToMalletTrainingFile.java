package pl.edu.icm.yadda.analysis.parsing.generic_citation_to_mallet_adapter.citationtoken;

import pl.edu.icm.yadda.analysis.parsing.generic_citation_to_mallet_adapter.AnyTagClassToMalletTrainingFile;

public class CitationTokenToMalletTrainingFile {	
	public static void execute(StringBuffer out, Object[] tokens, int max_distance){
		AnyTagClassToMalletTrainingFile.execute(out, tokens, CitationTokenTextGetter.getInstance(), CitationTokenLabelGetter.getInstance(), max_distance);
	}
}
