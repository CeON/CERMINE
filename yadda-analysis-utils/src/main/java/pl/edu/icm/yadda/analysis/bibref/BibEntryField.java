package pl.edu.icm.yadda.analysis.bibref;

/**
 * BibEntry field.
 *
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class BibEntryField {

    private String text;
    private int startIndex;
    private int endIndex;

    public BibEntryField(String text) {
        this(text, -1, -1);
    }

    public BibEntryField(String text, int startIndex, int endIndex) {
        this.text = text;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
    }

    public String getText() {
        return text;
    }

    public void setText(String value) {
        this.text = value;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public int getEndIndex() {
        return endIndex;
    }

    public void setIndexes(int startIndex, int endIndex) {
        this.startIndex = startIndex;
        this.endIndex = endIndex;
    }

}
