package pl.edu.icm.yadda.analysis.bibref.parsing.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a citation as a sequence of citation tokens.
 *
 * @author Dominika Tkaczyk (dtkaczyk@icm.edu.pl)
 */
public class Citation {

    private String text;
    private List<CitationToken> tokens;

    public Citation(String text, List<CitationToken> tokens) {
        this.text = text;
        this.tokens = tokens;
    }

    public Citation(String text) {
        this(text, new ArrayList<CitationToken>());
    }

    public Citation() {
        this("");
    }

    public String getText() {
        return text;
    }

    public List<CitationToken> getTokens() {
        return tokens;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void addToken(CitationToken token) {
        tokens.add(token);
    }

    public void appendText(String text) {
        this.text += text;
    }

}
