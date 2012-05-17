package pl.edu.icm.yadda.analysis.jrlsimilarity.metadata.keywords;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.edu.icm.yadda.analysis.jrlsimilarity.metadata.JournalMetaDataParser;
import pl.edu.icm.yadda.bwmeta.model.YElement;

/**
 * Class responsible for extracting keywords (references) meta-data from source
 * element.
 * @author Michał Siemiończyk michsiem@icm.edu.pl
 */
public class JournalKeywordsParser implements JournalMetaDataParser<YElement> {


	protected final Logger log = LoggerFactory.getLogger(this.getClass());
	

	private YElement sourceElement;
	

	private JournalKeywords keywords;
	


	@Override
	public boolean parse() {
		
		return false;
	}

	public JournalKeywordsParser(YElement yElem){
		this.sourceElement = yElem;
	}
	
	public JournalKeywords getKeywords() {
		return keywords;
	}

	@Override
	public void setSource(YElement sourceElement) {
		this.sourceElement = sourceElement;
		
	}

	public JournalKeywords getParsedData() {
		return this.keywords;
	}

}
