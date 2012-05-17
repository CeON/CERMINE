package pl.edu.icm.yadda.analysis.relations.pj.clues_occurence;

import java.util.HashSet;

import pl.edu.icm.yadda.analysis.relations.constants.RelConstants;


public class Feature6CoReference extends AggregateDisambiguator{
	
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
	    "           WHERE " +
	    "			doc1 != doc2	" +
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
