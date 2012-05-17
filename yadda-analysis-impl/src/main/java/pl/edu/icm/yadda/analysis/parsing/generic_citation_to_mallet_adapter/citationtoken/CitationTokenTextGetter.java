package pl.edu.icm.yadda.analysis.parsing.generic_citation_to_mallet_adapter.citationtoken;

import pl.edu.icm.yadda.analysis.bibref.parsing.model.CitationToken;
import pl.edu.icm.yadda.analysis.parsing.generic_citation_to_mallet_adapter.ObjectOperator;

public class CitationTokenTextGetter implements ObjectOperator{
	private static CitationTokenTextGetter me = null;
	
	@Override
	public String execute(Object obj) {
		CitationToken given = (CitationToken) obj;
		return given.getText();
	}

	public static synchronized ObjectOperator getInstance() {
		if(me==null) me = new CitationTokenTextGetter();
		return me;
	}
}
