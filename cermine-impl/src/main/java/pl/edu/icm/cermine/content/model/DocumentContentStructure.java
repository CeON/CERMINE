package pl.edu.icm.cermine.content.model;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Dominika Tkaczyk
 */
public class DocumentContentStructure {

    private DocumentHeader header;
    private List<DocumentParagraph> paragraphs;
    private List<DocumentContentStructure> parts = new ArrayList<DocumentContentStructure>();
    private DocumentContentStructure parent;

    
    public DocumentContentStructure() {
        parts = new ArrayList<DocumentContentStructure>();
        paragraphs = new ArrayList<DocumentParagraph>();
    }

    //parent

    public DocumentContentStructure getParent() {
        return parent;
    }
    
    public void setParents() {
        for (DocumentContentStructure part : parts) {
            part.parent = this;
            part.setParents();
        }
    }
    
    //parts
    
    public List<DocumentContentStructure> getParts() {
        return parts;
    }

    public void setParts(List<DocumentContentStructure> parts) {
        this.parts = parts;
        for (DocumentContentStructure dcp : parts) {
            dcp.parent = this;
        }
    }
    
    public void addPart(DocumentContentStructure part) {
        parts.add(part);
    }
    
    //headers
    
    public DocumentHeader getHeader() {
        return header;
    }

    public void setHeader(DocumentHeader header) {
        this.header = header;
    }
    
    public List<String> getAllHeaderTexts() {
        List<String> headers = new ArrayList<String>();
        if (header != null) {
            headers.add(header.getText());
        }
        for (DocumentContentStructure part : parts) {
            headers.addAll(part.getAllHeaderTexts());
        }
        return headers;
    }
    
    public int getAllHeaderCount() {
        int sum = (header == null) ? 0 : 1;
        for (DocumentContentStructure part : parts) {
            sum += part.getAllHeaderCount();
        }
        return sum;
    }
        
    public boolean containsHeaderText(String headerText) {
        if (header != null && headerText.equals(header.getText())) {
            return true;
        }
        for (DocumentContentStructure part : parts) {
            if (part.containsHeaderText(headerText)) {
                return true;
            }
        }
        return false;
    }

    public boolean containsHeaderFirstLineText(String lineText) {
        if (header != null) {
            String[] lines = header.getText().split("\n");
            if (lineText.equals(lines[0])) {
                return true;
            }
        }
        for (DocumentContentStructure part : parts) {
            if (part.containsHeaderFirstLineText(lineText)) {
                return true;
            }
        }
        return false;
    }
  
    public DocumentHeader getPrevHeader(DocumentHeader header) {
        DocumentContentStructure prev = null;
        for (DocumentContentStructure part : parts) {
            if (part.containsHeader(header)) {
                return prev == null ? null : prev.getHeader();
            }
            prev = part;
        }
        return null;
    }
    
    public boolean containsHeader(DocumentHeader header) {
        if (header.equals(this.header)) {
            return true;
        }
        for (DocumentContentStructure part : parts) {
            if (part.containsHeader(header)) {
                return true;
            }
        }
        return false;
    }

    public List<DocumentHeader> getHeaders() {
        List<DocumentHeader> headers = new ArrayList<DocumentHeader>();
        if (header != null) {
            headers.add(header);
        }
        for (DocumentContentStructure part : parts) {
            headers.addAll(part.getHeaders());
        }
        return headers;
    }
    
    //paragraphs

    public List<DocumentParagraph> getParagraphs() {
        return paragraphs;
    }
    
    public List<String> getAllParagraphTexts() {
        List<String> pars = new ArrayList<String>();
        for (DocumentParagraph p : paragraphs) {
            pars.add(p.getText());
        }

        for (DocumentContentStructure part : parts) {
            pars.addAll(part.getAllParagraphTexts());
        }
        return pars;
    }
    
    public List<DocumentParagraph> getAllParagraphs() {
        List<DocumentParagraph> pars = new ArrayList<DocumentParagraph>();
        if (paragraphs != null) {
            pars.addAll(this.paragraphs);
        }

        for (DocumentContentStructure part : parts) {
            pars.addAll(part.getAllParagraphs());
        }
        return pars;
    }
    
    public int getAllParagraphCount() {
        int sum = paragraphs.size();
        for (DocumentContentStructure part : parts) {
            sum += part.getAllParagraphCount();
        }
        return sum;
    }
    
    public void addParagraph(DocumentParagraph paragraph) {
        paragraphs.add(paragraph);
    }
    
    //printing

    public void printHeaders() {
        if (header != null) {
            for (int i = 1; i < header.getLevel(); i++) {
                System.out.print("\t");
            }
            System.out.println(header.getLevel() + " " + header.getText());
        }
        for (DocumentContentStructure dcp : parts) {
            dcp.printHeaders();
        }
        System.out.println("");
    }

    public void print() {
        if (header != null) {
            for (int i = 1; i < header.getLevel(); i++) {
                System.out.print("\t");
            }
            System.out.println(header.getLevel() + " " + header.getText());
        }
        System.out.println("");
        for (DocumentParagraph p : paragraphs) {
            System.out.println("[" + p.getText() + "]");
        }
        System.out.println("");
        for (DocumentContentStructure dcp : parts) {
            dcp.print();
        }
        System.out.println("");
    }
    
}
