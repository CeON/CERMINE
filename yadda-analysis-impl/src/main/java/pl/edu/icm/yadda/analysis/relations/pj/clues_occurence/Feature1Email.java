package pl.edu.icm.yadda.analysis.relations.pj.clues_occurence;

import java.util.HashSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.edu.icm.yadda.analysis.relations.constants.RelConstants;


public class Feature1Email extends AggregateDisambiguator{

	private static final Logger log = LoggerFactory.getLogger(Feature1Email.class);
	
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
	public String id() {
		return "full-email-clue";
	}


	@Override
	protected String postprocess(String prefix, HashSet<String> emails) {
		emails.add(prefix.toLowerCase());
		return prefix;
	}
}
