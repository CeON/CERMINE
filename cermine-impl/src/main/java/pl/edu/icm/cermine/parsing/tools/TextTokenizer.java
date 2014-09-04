package pl.edu.icm.cermine.parsing.tools;

import java.util.List;

import pl.edu.icm.cermine.parsing.model.Token;

public abstract class TextTokenizer<L, T extends Token<L>> {
	public abstract List<T> tokenize(String text);
}
