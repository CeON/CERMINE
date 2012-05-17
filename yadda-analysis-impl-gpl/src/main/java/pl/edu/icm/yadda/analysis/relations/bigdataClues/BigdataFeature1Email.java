package pl.edu.icm.yadda.analysis.relations.bigdataClues;

import java.util.HashSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.edu.icm.yadda.analysis.relations.constants.RelConstants;


public class BigdataFeature1Email extends BigdataDisambiguator{

	@SuppressWarnings("unused")
	private static final Logger log = LoggerFactory.getLogger(BigdataFeature1Email.class);
	
	protected void initializeQuery(String c1, String c2) {
		
		askWho = "email";
		
		queryString[0] = "" +
		"Select distinct email  \n" +
		"from \n" +
		"{<"+c1+">} <"+RelConstants.RL_CONTACT_EMAIL+"> {email} \n" +
		"	WHERE email!=\"\" \n" +
		"";
	    
		queryString[1] = "" +
		"Select distinct email  \n" +
		  "from \n" +
		  "{<"+c2+">} <"+RelConstants.RL_CONTACT_EMAIL+"> {email} \n" +
		"	WHERE email!=\"\" \n" +
		"";
		
	}

	@Override
	protected void initializeGraph(String c) {
		graphString = "" +
		"construct *  \n" +
		  "from \n" +
		  "{<"+c+">} <"+RelConstants.RL_CONTACT_EMAIL+"> {email} \n" +
		"	WHERE email!=\"\" \n" +
		"";
	}
	

	@Override
	public String id() {
		return "full-email-clue";
	}


	@Override
	protected String postprocess(String prefix, HashSet<String> emails) {
		emails.add(prefix.toLowerCase());
		return prefix;
	}


	public String getWholeQuery() {
		return "" +
		"Select distinct c1,c2,s,email  \n" +
		"from \n" +
		"{c1} <"+RelConstants.RL_CONTACT_EMAIL+"> {email}, \n" +
		"{c2} <"+RelConstants.RL_CONTACT_EMAIL+"> {email}, \n" +
		"{c1} <"+RelConstants.RL_SURNAME+"> {s}, \n" +
		"{c2} <"+RelConstants.RL_SURNAME+"> {s} \n" +
		"	WHERE email!=\"\" \n" +
		"";
	}



}
