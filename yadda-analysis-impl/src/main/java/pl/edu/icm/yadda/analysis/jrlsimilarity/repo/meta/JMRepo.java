package pl.edu.icm.yadda.analysis.jrlsimilarity.repo.meta;

import pl.edu.icm.yadda.analysis.jrlsimilarity.repo.meta.connector.JMRepoConnector;


/**
 * Interface for 
 * @author  Michał Siemiończyk michsiem@icm.edu.pl
 */
interface JMRepo<T> {
	/**
	 * @uml.property  name="connector"
	 * @uml.associationEnd  
	 */
	public T getConnector();
}
