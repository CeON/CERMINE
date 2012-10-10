package pl.edu.icm.cermine.bibref;

import pl.edu.icm.cermine.exception.AnalysisException;

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
	T parseBibReference(String text) throws AnalysisException;

}
