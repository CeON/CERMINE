package pl.edu.icm.yadda.analysis.relations.pj.clues_occurence;

import java.util.HashSet;

import pl.edu.icm.yadda.analysis.relations.constants.RelConstants;


public class Feature4CoClassif extends AggregateDisambiguator{

	@Override
	public String id() {
		return "co-classification-clue";
	}

	@Override
	protected void initializeQuery(String c1, String c2) {
		askWho = "msc";
		
		queryString[0] = "" +
		" 			SELECT msc " +
	    " 			FROM " +
	    "				{doc1} <"+RelConstants.RL_HAS_CONTRIBUTOR+"> {<"+c1+">}," +
	    "				{doc1} <"+RelConstants.RL_CATEGORY_MSC+"> {msc}" +	    
		"";
	    
	    
		queryString[1] = "" +
		" 			SELECT msc " +
	    " 			FROM " +
	    "				{doc1} <"+RelConstants.RL_HAS_CONTRIBUTOR+"> {<"+c2+">}," +
	    "				{doc1} <"+RelConstants.RL_CATEGORY_MSC+"> {msc}" +	    
		"";
	}

	@Override
	protected String postprocess(String prefix, HashSet<String> emails) {
		emails.add(prefix);
		return prefix;
	}
}
