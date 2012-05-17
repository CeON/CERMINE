package pl.edu.icm.yadda.analysis.parsing.generic_citation_to_mallet_adapter.citationtoken;

import pl.edu.icm.yadda.analysis.bibref.parsing.model.CitationToken;
import pl.edu.icm.yadda.analysis.parsing.generic_citation_to_mallet_adapter.ObjectOperator;

public class CitationTokenLabelGetter implements ObjectOperator{

	private static CitationTokenLabelGetter me;

	@Override
	public String execute(Object obj) {
		CitationToken given = (CitationToken) obj;
		return given.getLabel().toString();
	}

	public static ObjectOperator getInstance() {
		if(me==null) me = new CitationTokenLabelGetter();
		return me;
	}
}
