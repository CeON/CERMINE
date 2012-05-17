package pl.edu.icm.yadda.analysis.relations.pj.clues;

import java.util.LinkedList;

import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.edu.icm.yadda.analysis.relations.constants.RelConstants;
import pl.edu.icm.yadda.analysis.relations.pj.auxil.PJDisambiguator;

/**
 * Disambiguator granting contributors who use only same languages (excluding English).
 * Result (similarity factor) is in [-1,1] 
 *   
 * Contributor A and B contribute write in langsA and langsB.
 * Let's subtract English from both of sets. 
 * Then, if either set langsA or langsB is empty result is 0.
 * Otherwise result is calculated from following equation:
 * (i_common * rewardForMatch + i_diff * penaltyForLackOfLang - i_sum*penaltyForLackOfLang) 
 * /(i_sum*rewardForMatch - i_sum*penaltyForLackOfLang)) *2 -1;
 * where:
 * i_sum is number of distinct elements (langA) AND (langB)
 * i_diff is number of distinct elements (langA-langB) AND (langB-langA)
 * i_common is number of distinct elements (langA) INTERSECTION (langB)
 * penaltyForLackOfLang = -1;
 * rewardForMatch = 2;	

 * @return similarity factor in [-1,1]
 * 
 * @author pdendek
 *
 */
public class PJNotStrictLanguageClue extends PJDisambiguator{
    private static final Logger log = LoggerFactory.getLogger(PJNotStrictLanguageClue.class);
	
	double penaltyForLackOfLang = -1;
	double rewardForMatch = 2;	
	
	public double getPenaltyForLackOfLang() {
		return penaltyForLackOfLang;
	}

	public void setPenaltyForLackOfLang(double penaltyForLackOfLang) {
		this.penaltyForLackOfLang = penaltyForLackOfLang;
	}
	
	public double getPointsForMatch() {
		return rewardForMatch;
	}

	public void setPointsForMatch(double pointsForMatch) {
		this.rewardForMatch = pointsForMatch;
	}
	
	@Override
	public String id() {
		return "language-clue-not-strict";
	}
	protected double checkIfSimilar(RepositoryConnection conn, String contribA, String contribB) 
	throws RepositoryException, MalformedQueryException, QueryEvaluationException {
		
		LinkedList<String> langA = new LinkedList<String>();
		LinkedList<String> langB = new LinkedList<String>();
		
		String zeroQueryString = "" +
		  "Select distinct lan  \n" +
		  "from \n" +
		  "{doc} <"+RelConstants.RL_HAS_CONTRIBUTOR+"> {<"+contribA+">}, \n" +
		  "{doc} <"+RelConstants.RL_LANGUAGE+"> {lan} \n" +
		  "";
		
		String firstQueryString = "" +
		  "Select distinct lan  \n" +
		  "from \n" +
		  "{doc} <"+RelConstants.RL_HAS_CONTRIBUTOR+"> {<"+contribB+">}, \n" +
		  "{doc} <"+RelConstants.RL_LANGUAGE+"> {lan} \n" +
		  "";
		
		TupleQuery query = null;
		TupleQueryResult res = null;
		
		query = conn.prepareTupleQuery(QueryLanguage.SERQL, zeroQueryString);
	    res = query.evaluate();
		while(res.hasNext()){
			langA.add(res.next().getValue("lan").toString());
		}
		res.close();
		query = conn.prepareTupleQuery(QueryLanguage.SERQL, firstQueryString);
	    res = query.evaluate();
	    while(res.hasNext()){
			langB.add(res.next().getValue("lan").toString());
		}
	    res.close();
	    langA.remove("english");
	    langB.remove("english");
	    
	    @SuppressWarnings("unchecked")
		LinkedList<String> intersection = (LinkedList<String>) langA.clone();
	    intersection.retainAll(langB);
	    
	    @SuppressWarnings("unchecked")
		LinkedList<String> summation = (LinkedList<String>) langA.clone();
	    /* instead of appending all:
	    summation.addAll(langB);
	    add if they are not in the list*/
	    for(String i: langB){
	    	if(!summation.contains(i))
	    		summation.add(new String(i));
	    }
	    
	    double i_diff = summation.size()-intersection.size();
	    double i_common = intersection.size();
	    double i_sum = summation.size();
	    
	    return 
	    (//count points, add absolut of down bound to make lowest value 0
	    (i_common * rewardForMatch + i_diff * penaltyForLackOfLang - i_sum*penaltyForLackOfLang)
	    / //divide result to get result from [0,1] range
	    (i_sum*rewardForMatch - i_sum*penaltyForLackOfLang)
	    )
	    *2 //multiply by two to get range [0,2]
	    -1;//Subtract 1 to get range [-1,1]
	}
}
