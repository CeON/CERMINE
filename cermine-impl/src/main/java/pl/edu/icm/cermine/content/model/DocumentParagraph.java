package pl.edu.icm.cermine.content.model;

/**
 * Document paragraph class.
 *
 * @author Dominika Tkaczyk
 */
public class DocumentParagraph {

    private String text;
    private DocumentContentStructure contentStructure;

    public DocumentParagraph(String text, DocumentContentStructure contentStructure) {
        this.text = text;
        this.contentStructure = contentStructure;
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
