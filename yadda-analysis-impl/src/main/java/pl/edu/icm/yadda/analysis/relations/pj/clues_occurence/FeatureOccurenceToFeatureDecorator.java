package pl.edu.icm.yadda.analysis.relations.pj.clues_occurence;

import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;

import pl.edu.icm.yadda.analysis.relations.pj.auxil.PJDisambiguator;


public class FeatureOccurenceToFeatureDecorator extends PJDisambiguator{

	private PJDisambiguator disambiguator;		
	public FeatureOccurenceToFeatureDecorator(PJDisambiguator inner){
		this.disambiguator = inner;
	}
	
	protected double checkIfSimilar(RepositoryConnection conn, String c1, String c2) 
	throws RepositoryException, MalformedQueryException, QueryEvaluationException {
		int retval = (int) this.disambiguator.analyze(c1, c2);
		if(retval == -1) return 0; //lack of clue argument
		if(retval == 0) return -1; //clue returns 0
		else return 1;
	}

	@Override
	public String id() {
		return disambiguator.id();
	}
}
