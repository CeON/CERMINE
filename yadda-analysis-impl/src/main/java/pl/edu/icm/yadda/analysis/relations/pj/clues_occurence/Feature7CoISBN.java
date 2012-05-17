package pl.edu.icm.yadda.analysis.relations.pj.clues_occurence;

import java.util.HashSet;

import pl.edu.icm.yadda.analysis.relations.constants.RelConstants;


public class Feature7CoISBN extends AggregateDisambiguator{

	@Override
	public String id() {
		return "co-isbn";
	}

	@Override
	protected void initializeQuery(String c1, String c2) {
		askWho = "isbn";
		
		queryString[0] = "" +
		" 			SELECT isbn " +
	    " 			FROM " +
	    "				{doc1} <"+RelConstants.RL_HAS_CONTRIBUTOR+"> {<"+c1+">}," +
	    "				{doc1} <"+RelConstants.RL_HAS_ISBN+"> {isbn}" +	    
		"";
	    
	    
		queryString[1] = "" +
		" 			SELECT isbn " +
	    " 			FROM " +
	    "				{doc2} <"+RelConstants.RL_HAS_CONTRIBUTOR+"> {<"+c2+">}," +
	    "				{doc2} <"+RelConstants.RL_HAS_ISBN+"> {isbn}" +	    
		"";
	}

	@Override
	protected String postprocess(String prefix, HashSet<String> emails) {
		emails.add(prefix);
		return prefix;
	}
}
