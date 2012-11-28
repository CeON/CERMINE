package pl.edu.icm.cermine.content.model;

/**
 * Document header class.
 *
 * @author Dominika Tkaczyk
 */
public class DocumentHeader {

    private int level;
    private String text;
    private DocumentContentStructure contentStructure;

    public DocumentHeader(int level, String text, DocumentContentStructure contentStructure) {
        this.level = level;
        this.text = text;
        this.contentStructure = contentStructure;
    }

    private DocumentHeader() {
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public DocumentContentStructure getContentStructure() {
        return contentStructure;
    }

    public void setContentStructure(DocumentContentStructure contentStructure) {
        this.contentStructure = contentStructure;
    }

}
