package pl.edu.icm.yadda.analysis.jrlsimilarity.repo.sim;

import pl.edu.icm.yadda.analysis.jrlsimilarity.repo.sim.connector.SimRepoConnector;

/**
 * Interface which stands for Journal Similarity Repository functionalities.
 * Journal Similarity Repository is queried via user Api and it is built
 * at the end of process in  {@link JournalSimilarityRepoWritingNode}.
 * Classes that implement SimRepo should be built on a singleton - manner.
 * 
 * 
 * @author Michał Siemiończyk michsiem@icm.edu.pl
 *
 */
public interface SimRepo {
	
	public SimRepoConnector getConnector();
 
	
}
