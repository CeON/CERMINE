package pl.edu.icm.yadda.analysis.relations.pj.clues;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.openrdf.query.BindingSet;
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
 * Disambiguator granting contributors who use same words as tag-words.
 * Result (similarity factor) is between [-1,1] 
 *  
 * Contributor A and B use his own set of tag-phrase (tagPhraseSetA, tagPhraseSetB).
 * One can split phrases into words getting tagWordSetA, tagWordSetB.
 * Then, one can count elements which intersect between those sets (i_intersection).
 * Also, one can sum elements from both sets (i_summation).
 * Similarity factor is product of division intersection elements by summation elements.
 * To get result from [-1,1] above is multiplied by two and subtracted by one,
 * which gives following: (i_intersection/(double)i_summation)*2-1;
 * 
 * @return similarity factor in range [-1,1]
 * @update @return similarity factor in range {0,1}
 * 
 * @author pdendek
 *
 */
public class PJTagWordsClue extends PJDisambiguator{

	private static final Logger log = LoggerFactory.getLogger(PJTagWordsClue.class);
	
	@Override
	public String id() {
		return "tag-words";
	}

	
	protected double checkIfSimilar(RepositoryConnection conn, String contribA, String contribB) 
	throws RepositoryException, MalformedQueryException, QueryEvaluationException {
		LinkedList<String> langA = new LinkedList<String>();
		LinkedList<String> langB = new LinkedList<String>();
		LinkedList<LinkedList<String>> tagA = new LinkedList<LinkedList<String>>();
		LinkedList<LinkedList<String>> tagB = new LinkedList<LinkedList<String>>();
		
		
		String zeroQueryString = "" +
		  "Select distinct text, lan  \n" +
		  "from \n" +
		  "{doc} <"+RelConstants.RL_HAS_CONTRIBUTOR+"> {<"+contribA+">}, \n" +
		  "{doc} <"+RelConstants.RL_TAG+"> {tag}, \n" +
		  "{tag} <"+RelConstants.RL_TEXT+"> {text}, \n" +
		  "{tag} <"+RelConstants.RL_LANGUAGE+"> {lan} \n" +
		  "";
		
		String firstQueryString = "" +
        "Select distinct text, lan  \n" +
        "from \n" +
        "{doc} <"+RelConstants.RL_HAS_CONTRIBUTOR+"> {<"+contribB+">}, \n" +
        "{doc} <"+RelConstants.RL_TAG+"> {tag}, \n" +
        "{tag} <"+RelConstants.RL_TEXT+"> {text}, \n" +
        "{tag} <"+RelConstants.RL_LANGUAGE+"> {lan} \n" +
        "";
		
		TupleQuery query = null;
		TupleQueryResult res = null;
		BindingSet bs = null;
		String lang = null, tag = null;
		int i_langA = 0;
		int i_langB = 0;
		
		query = conn.prepareTupleQuery(QueryLanguage.SERQL, zeroQueryString);
	    res = query.evaluate();
	    
		while(res.hasNext()){
			i_langA++;
			bs = res.next();
			lang = bs.getValue("lan").toString();
			tag = bs.getValue("text").toString();
			
			if(langA.contains(lang)){
				((LinkedList<String>) tagA.get(langA.indexOf(lang))).addAll(Arrays.asList(tag.split(" ")));
			}else{
				LinkedList<String> list = new LinkedList<String>();
				list.addAll(Arrays.asList(tag.split(" ")));
				langA.add(lang);
				tagA.add(list);
			}
		}
		if(i_langA==0)return 0;
		
		
		query = conn.prepareTupleQuery(QueryLanguage.SERQL, firstQueryString);
	    res = query.evaluate();
	    
	    while(res.hasNext()){
	    	i_langB++;
			bs = res.next();
			lang = bs.getValue("lan").toString();
			tag = bs.getValue("text").toString();
			
			if(langB.contains(lang)){
				((LinkedList<String>) tagB.get(langB.indexOf(lang))).addAll(Arrays.asList(tag.split(" ")));
			}else{
				LinkedList<String> list = new LinkedList<String>();
				list.addAll(Arrays.asList(tag.split(" ")));
				langB.add(lang);
				tagB.add(list);
			}
		}
	    if(i_langB==0)return 0;
	    
	    List<String> langIntersection = (List<String>) langA.clone();
	    langIntersection.retainAll(langB);
	    
	    int i_intersection = 0;
	    int i_summation = 0;
	    
	    for(String currLang : langIntersection){
	    	LinkedList<String> lA = (LinkedList<String>) tagA.get(lang.indexOf(currLang));
	    	LinkedList<String> lB = (LinkedList<String>) tagB.get(lang.indexOf(currLang));
	    	
	    	LinkedList<String> intersection = (LinkedList<String>) lA.clone();
	    	intersection.retainAll(lB);
	    	LinkedList<String> summation = (LinkedList<String>) lA.clone();
	    	
	    	summation.addAll(lB);
	    	
	    	
	    	i_intersection+=intersection.size();
	    	i_summation+=summation.size();
	    }
	    
	    //if((i_intersection/(double)i_summation)*2-1<-0.5) return 0; //less then 25% tag-words
	    //else return 1;
	    if(i_summation == 0)
	        return 0;
	    return (i_intersection/(double)i_summation);
	}
	
	
}