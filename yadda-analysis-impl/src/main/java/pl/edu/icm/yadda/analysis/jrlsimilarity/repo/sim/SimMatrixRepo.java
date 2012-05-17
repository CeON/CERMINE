package pl.edu.icm.yadda.analysis.jrlsimilarity.repo.sim;

import pl.edu.icm.yadda.analysis.jrlsimilarity.repo.sim.connector.SimRepoConnector;

/**
 * An implementation of journal similarity repository based on matrix.
 * @author Michał Siemiończyk michsiem@icm.edu.pl
 *
 */
public class SimMatrixRepo implements SimRepo {

	private SimMatrixRepo instance;
	
	public static SimMatrixRepo getInstance(){
		return null;
		//TODO finish that
	}
	
	private SimMatrixRepo(){
		;
	}
	

	@Override
	public SimRepoConnector getConnector() {
		// TODO Auto-generated method stub
		return null;
	}

}
