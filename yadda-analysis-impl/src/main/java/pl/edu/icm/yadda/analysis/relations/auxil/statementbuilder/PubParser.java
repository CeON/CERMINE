package pl.edu.icm.yadda.analysis.relations.auxil.statementbuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.edu.icm.yadda.common.YaddaException;
import pl.edu.icm.yadda.parsing.ICitationParser;
import pl.edu.icm.yadda.parsing.regexpparser.RegexpReferenceParser2;
import pl.edu.icm.yadda.tools.content.IAuthorParser;
import pl.edu.icm.yadda.tools.content.RegexpAuthorParser;

public class PubParser {
	private static final Logger log = LoggerFactory.getLogger(PubParser.class);
	
	private static ICitationParser citationParser = null;
	private static IAuthorParser authorParser = null;
	final static String regexpAuthorParserProp = "pl/edu/icm/yadda/tools/content/config/authorParser.properties";

	public static synchronized IAuthorParser getAuthorParser() throws YaddaException{
		return getRegexpAuthorParser();
	}
	
	public static synchronized ICitationParser getCitationParser() throws YaddaException{
		return getRegexpCitationParser();
	}
	
	private static synchronized IAuthorParser getRegexpAuthorParser() throws YaddaException {
		if(authorParser == null) authorParser = 
			new RegexpAuthorParser
			(regexpAuthorParserProp);
		return authorParser;
	}

	private static synchronized ICitationParser getRegexpCitationParser() throws YaddaException{
		try {
			if(citationParser==null)
			citationParser = new RegexpReferenceParser2("pl/edu/icm/yadda/tools/content/config/referenceParser.properties");
		} catch (YaddaException e) {
			citationParser = null;
			log.error(e.toString());
			for(StackTraceElement s: e.getStackTrace())
				log.error(s.toString());
		}
		return citationParser;
	}
	
}
