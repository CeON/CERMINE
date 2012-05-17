package pl.edu.icm.yadda.analysis.relations.pj.clues_occurence;

import java.util.HashSet;

import pl.edu.icm.yadda.analysis.relations.constants.RelConstants;


public class Feature5CoKeywordPhrase extends AggregateDisambiguator{
	
	protected void initializeQuery(String c1, String c2) {
		askWho = "k";
		
		queryString[0] = "" +
		" 			SELECT k " +
	    " 			FROM " +
	    "				{doc1} <"+RelConstants.RL_HAS_CONTRIBUTOR+"> {<"+c1+">}," +
	    "				{doc1} <"+RelConstants.RL_TAG+"> {t1}," +
	    "				{t1} <"+RelConstants.RL_TEXT+"> {k}";
	    
	    
		queryString[1] = "" +
		" 			SELECT k " +
	    " 			FROM " +
	    "				{doc2} <"+RelConstants.RL_HAS_CONTRIBUTOR+"> {<"+c2+">}," +
	    "				{doc2} <"+RelConstants.RL_TAG+"> {t2}," +
	    "				{t2} <"+RelConstants.RL_TEXT+"> {k}" +  
		"";
		
	}

	@Override
	protected String postprocess(String prefix, HashSet<String> emails) {
		emails.add(prefix);
		return prefix;
	}

	@Override
	public String id() {
		return "co-keyword-phrase-clue";
	}
}
