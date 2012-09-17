package pl.edu.icm.yadda.analysis.bibref;

import pl.edu.icm.yadda.analysis.AnalysisException;

/**
 * Bibliographic reference parser.
 * 
 * @author Lukasz Bolikowski (bolo@icm.edu.pl)
 *
 * @param <T> Type of parsed reference.
 */
public interface BibReferenceParser<T> {
	
	/**
	 * Parses a text of a reference.
	 * 
	 * @param text Text of a bibliographic reference.
	 * @return Parsed reference, or <code>null</code>
	 * if the specified text couldn't be parsed.
	 */
	public T parseBibReference(String text) throws AnalysisException;

}
