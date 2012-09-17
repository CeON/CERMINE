package pl.edu.icm.yadda.analysis.bibref.parsing.model;

/**
 * Citation token.
 *
 * @author Dominika Tkaczyk (dtkaczyk@icm.edu.pl)
 */
public class CitationToken {

    private String text;
    private CitationTokenLabel label;
    private int startIndex;
    private int endIndex;

    public CitationToken(String text, int startIndex, int endIndex, CitationTokenLabel label) {
        this.text = text;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.label = label;
    }

    public CitationToken(String text, int startIndex, int endIndex) {
        this(text, startIndex, endIndex, CitationTokenLabel.TEXT);
    }

    public CitationTokenLabel getLabel() {
        return label;
    }

    public void setLabel(CitationTokenLabel label) {
        this.label = label;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void appendText(String text) {
        this.text += text;
    }

    public int getEndIndex() {
        return endIndex;
    }

    public void setEndIndex(int endIndex) {
        this.endIndex = endIndex;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }

}
