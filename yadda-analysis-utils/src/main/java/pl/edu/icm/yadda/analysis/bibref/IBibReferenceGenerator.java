package pl.edu.icm.yadda.analysis.bibref;

import java.util.List;

/**
 * Generator of bibliographic references.
 * 
 * @author Lukasz Bolikowski (bolo@icm.edu.pl)
 *
 * @param <T> A type containing document metadata.
 */
public interface IBibReferenceGenerator<T> {

	/** 
	 * Returns a list of supported bibliographic reference formats.
	 * 
	 * @return a list of supported bibliographic reference formats.
	 */
	public List<String> getFormats();
	
	/**
	 * Generates a bibliographic reference in the specified format
	 * from the specified document metadata.
	 * 
	 * @param metadata Document metadata from which
	 * a bibliographic reference will be generated.
	 * @param format Format of bibliographic reference.
	 * The specified format must be one of the formats
	 * returned by {@link #getFormats()},
	 * otherwise {@link UnsupportedOperationException} will be thrown.
	 * @param options Additional generator-dependent options.
	 * @return Bibliographic reference.
	 * @throws UnsupportedOperationException if the specified format is not supported
	 * by this reference generator.
	 */
	public String toBibReference(T metadata, String format, Object... options) throws UnsupportedOperationException;
}
