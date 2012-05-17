package pl.edu.icm.yadda.analysis.jrlsimilarity.metadata.citations;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import pl.edu.icm.yadda.analysis.jrlsimilarity.metadata.JournalMetaDataParser;
import pl.edu.icm.yadda.bwmeta.model.YElement;


/**
 * Class responsible for extracting citations (references) meta-data from source
 * element.
 * @author Michał Siemiończyk michsiem@icm.edu.pl
 */
public class JournalCitationsParser implements JournalMetaDataParser<YElement> {

	
	protected final Logger log = LoggerFactory.getLogger(this.getClass());
	
	
	private YElement sourceElement;
	

	private JournalCitations citations;
	

	@Override
	public boolean parse() {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * @return
	 * @uml.property  name="citations"
	 */
	public JournalCitations getCitations() {
		return citations;
	}

	@Override
	public void setSource(YElement sourceElement) {
		this.setSourceElement(sourceElement);
		
	}

	public void setSourceElement(YElement sourceElement) {
		this.sourceElement = sourceElement;
	}

	public YElement getSourceElement() {
		return sourceElement;
	}

}
