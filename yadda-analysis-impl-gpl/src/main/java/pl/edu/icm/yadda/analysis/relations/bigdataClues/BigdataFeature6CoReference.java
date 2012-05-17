package pl.edu.icm.yadda.analysis.relations.bigdataClues;

import java.util.HashSet;

import pl.edu.icm.yadda.analysis.relations.constants.RelConstants;


public class BigdataFeature6CoReference extends BigdataDisambiguator{
	
	protected void initializeQuery(String c1, String c2) {
		askWho = "doc_r_id";
		
		queryString[0] = "" +
		" 			SELECT doc_r_id " +
	    " 			FROM " +
	    "				{doc1} <"+RelConstants.RL_HAS_CONTRIBUTOR+"> {<"+c1+">}," +
	    "				{doc1} <"+RelConstants.RL_REFERENCES+"> {doc1r}," +
	    "				{doc1r} <"+RelConstants.RL_IS_DOCUMENT+"> {doc_r_id}" +
		"";
	    
	    
		queryString[1] = "" +
		" 			SELECT doc_r_id " +
	    " 			FROM " +
	    "				{doc2} <"+RelConstants.RL_HAS_CONTRIBUTOR+"> {<"+c2+">}," +
	    "				{doc2} <"+RelConstants.RL_REFERENCES+"> {doc2r}," +
	    "				{doc2r} <"+RelConstants.RL_IS_DOCUMENT+"> {doc_r_id}" +	    
		"";
		
	}

	@Override
	protected void initializeGraph(String c) {
		graphString = "" +
		"construct *  \n" +
	    " 			FROM " +
	    "				{doc2} <"+RelConstants.RL_HAS_CONTRIBUTOR+"> {<"+c+">}," +
	    "				{doc2} <"+RelConstants.RL_REFERENCES+"> {doc2r}," +
	    "				{doc2r} <"+RelConstants.RL_IS_DOCUMENT+"> {doc_r_id}" +	    	    
		"";
	}	
	
	@Override
	public String id() {
		return "co-reference-clue";
	}

	@Override
	protected String postprocess(String prefix, HashSet<String> emails) {
		emails.add(prefix);
		return prefix;
	}
}
