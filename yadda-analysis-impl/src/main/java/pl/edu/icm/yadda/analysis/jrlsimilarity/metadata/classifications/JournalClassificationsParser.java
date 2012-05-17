package pl.edu.icm.yadda.analysis.jrlsimilarity.metadata.classifications;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.edu.icm.yadda.analysis.jrlsimilarity.metadata.JournalMetaDataParser;
import pl.edu.icm.yadda.bwmeta.model.YElement;

/**
 * Class responsible for extracting classifications meta-data from source
 * element.
 * @author Michał Siemiończyk michsiem@icm.edu.pl
 *
 */
public class JournalClassificationsParser implements JournalMetaDataParser<YElement> {

	/**
	 * @uml.property  name="log"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	protected final Logger log = LoggerFactory.getLogger(this.getClass());
	
	/**
	 * @uml.property  name="sourceElement"
	 * @uml.associationEnd  
	 */
	private YElement sourceElement; 
	
	/**
	 * @uml.property  name="classifications"
	 * @uml.associationEnd  readOnly="true"
	 */
	private JournalClassifications classifications;
	

	@Override
	public boolean parse() {
		return false;
	}


	/**
	 * @return
	 * @uml.property  name="classifications"
	 */
	public JournalClassifications getClassifications() {
		return classifications;
	}

	@Override
	public void setSource(YElement sourceElement) {
		this.sourceElement = sourceElement;
	}

}
