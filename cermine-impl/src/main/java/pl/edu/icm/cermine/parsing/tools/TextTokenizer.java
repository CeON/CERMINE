package pl.edu.icm.cermine.parsing.tools;

import java.util.List;
import pl.edu.icm.cermine.parsing.model.Token;

/**
 * Text tokenizer.
 * 
 * @author Bartosz Tarnawski
 * @param <T> token type
 */
public interface TextTokenizer<T extends Token<?>> {
	/**
	 * @param text
	 * @return list of tokens, a sequence of atomic parts of the text
	 */
	List<T> tokenize(String text);
}
