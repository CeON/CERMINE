package pl.edu.icm.yadda.analysis.bibref;

/**
 * Extractor of bibliographic references from plain text.
 * 
 * @author Lukasz Bolikowski (bolo@icm.edu.pl)
 *
 */
public interface IBibReferenceExtractor {
	/**
	 * Extracts bibliographic references from plain text.
	 * 
	 * @param text Plain text of a document.
	 * @return Array of bibliographic references, possibly empty.
	 */
	public String[] extractBibReferences(String text);
}
