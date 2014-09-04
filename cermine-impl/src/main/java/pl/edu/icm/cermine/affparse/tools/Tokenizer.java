package pl.edu.icm.cermine.affparse.tools;

import java.util.List;

import pl.edu.icm.cermine.affparse.model.Token;

public abstract class Tokenizer<L, T extends Token<L>> {
	public abstract List<T> tokenize(String text);
}
