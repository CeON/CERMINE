package pl.edu.icm.yadda.analysis.jrlsimilarity.repo.meta;

import pl.edu.icm.yadda.analysis.jrlsimilarity.repo.meta.connector.XmlJMRepoConnector;

/**
 * An implementation of xml-files based journal metadata repository.
 * @author Michał Siemiończyk michsiem@icm.edu.pl
 *
 */
public class XmlJMRepo implements JMRepo{

	private static XmlJMRepo instance = null;
	
	public static XmlJMRepo getInstance(){
		if (instance == null) instance = new XmlJMRepo();
		return instance;
	}
	
	public XmlJMRepoConnector getConnector() {
		// TODO Auto-generated method stub
//		return null;
		return new XmlJMRepoConnector();
	}

}
