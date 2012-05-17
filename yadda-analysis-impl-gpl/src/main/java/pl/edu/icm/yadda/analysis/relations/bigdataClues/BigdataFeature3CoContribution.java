package pl.edu.icm.yadda.analysis.relations.bigdataClues;

import java.util.HashSet;

import pl.edu.icm.yadda.analysis.relations.constants.RelConstants;


public class BigdataFeature3CoContribution extends BigdataDisambiguator{
	
	protected void initializeQuery(String c1, String c2) {
		askWho = "sur";
		
		queryString[0] = "" +
		" 			SELECT sur " +
	    " 			FROM " +
	    "				{doc1} <"+RelConstants.RL_HAS_CONTRIBUTOR+"> {<"+c1+">}," +
	    "				{doc1} <"+RelConstants.RL_HAS_CONTRIBUTOR+"> {cAa}," +
	    "				{cAa} <"+RelConstants.RL_SURNAME+"> {sur}" +
	    "           WHERE " +
	    "			cAa != <"+c1+">	" +
	    "";
	    
	    
		queryString[1] = "" +
		" 			SELECT sur " +
	    " 			FROM " +
	    "				{doc2} <"+RelConstants.RL_HAS_CONTRIBUTOR+"> {<"+c2+">}," +
	    "				{doc2} <"+RelConstants.RL_HAS_CONTRIBUTOR+"> {cBa}," +
	    "				{cBa} <"+RelConstants.RL_SURNAME+"> {sur}" +		    
	    "           WHERE " +
	    "			cBa != <"+c2+">	" +
		"";
		
	}

	@Override
	protected void initializeGraph(String c) {
		graphString = "" +
		"construct *  \n" +
		" 			FROM " +
	    "				{doc1} <"+RelConstants.RL_HAS_CONTRIBUTOR+"> {<"+c+">}," +
	    "				{doc1} <"+RelConstants.RL_HAS_CONTRIBUTOR+"> {cAa}," +
	    "				{cAa} <"+RelConstants.RL_SURNAME+"> {sur}" +
	    "           WHERE " +
	    "			cAa != <"+c+">	" +
		"";
	}
	
	@Override
	public String id() {
		return "co-contribution-clue";
	}

	@Override
	protected String postprocess(String prefix, HashSet<String> emails) {
		emails.add(prefix);
		return prefix;
	}
}
