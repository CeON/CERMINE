package pl.edu.icm.yadda.analysis.relations.bigdataClues;

import java.util.HashSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.edu.icm.yadda.analysis.relations.constants.RelConstants;


public class BigdataFeature9FullInitials extends BigdataDisambiguator{

	@SuppressWarnings("unused")
	private static final Logger log = LoggerFactory.getLogger(BigdataFeature9FullInitials.class);
	
	protected void initializeQuery(String c1, String c2) {
		
		askWho = "email";
		
		queryString[0] = "" +
		"Select distinct email  \n" +
		"from \n" +
		"{<"+c1+">} <"+RelConstants.RL_INITIALS+"> {email} \n" +
		"	WHERE email!=\"\" \n" +
		"";
	    
		queryString[1] = "" +
		"Select distinct email  \n" +
		  "from \n" +
		  "{<"+c2+">} <"+RelConstants.RL_INITIALS+"> {email} \n" +
		"	WHERE email!=\"\" \n" +
		"";
		
	}


	@Override
	protected void initializeGraph(String c) {
		graphString = "" +
		"construct *  \n" +
	    " 			FROM " +
	    "{<"+c+">} <"+RelConstants.RL_INITIALS+"> {email} \n" +
		"	WHERE email!=\"\" \n" +
		"";
	}	
	
	@Override
	public String id() {
		return "full-initials-clue";
	}


	@Override
	protected String postprocess(String prefix, HashSet<String> emails) {
		emails.add(prefix.toLowerCase());
		return prefix;
	}
}
