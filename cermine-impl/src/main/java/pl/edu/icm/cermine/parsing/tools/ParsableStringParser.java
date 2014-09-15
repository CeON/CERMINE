package pl.edu.icm.cermine.parsing.tools;

import org.jdom.Element;

import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.exception.TransformationException;
import pl.edu.icm.cermine.parsing.model.ParsableString;

/**
 * Generic parser, processes an instance of ParsableString by generating and tagging
 * its tokens.
 * 
 * @author Bartosz Tarnawski
 *
 * @param <PS> parsable string type
 */
public abstract class ParsableStringParser<PS extends ParsableString<?>> {
	
	/**
	 * Sets the token list of the parsable string so that their labels
	 * determine the tagging of its text content.
	 * @param text the parsable string instance to parse
	 * @throws AnalysisException 
	 */
	public abstract void parse(PS text) throws AnalysisException;
	
	/**
	 * @param text string to parse
	 * @return XML Element with the tagged text in NLM format
	 * @throws TransformationException 
	 * @throws AnalysisException 
	 */
	public abstract Element parseString(String text) throws AnalysisException, TransformationException;
}
