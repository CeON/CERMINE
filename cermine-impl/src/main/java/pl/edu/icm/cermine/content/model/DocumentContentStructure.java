package pl.edu.icm.cermine.content.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.jdom.Element;
import pl.edu.icm.cermine.content.model.BxDocContentStructure.BxDocContentPart;

/**
 *
 * @author Dominika Tkaczyk
 */
public class DocumentContentStructure {

    private List<DocumentContentPart> parts = new ArrayList<DocumentContentPart>();

    
    public List<String> getHeaderTexts() {
        List<String> headers = new ArrayList<String>();
        if (parts != null) {
            for (DocumentContentPart part : parts) {
                headers.addAll(part.getHeaderTexts());
            }
        }
        return headers;
    }

    public List<String> getHeaderLineTexts() {
        List<String> lines = new ArrayList<String>();
        if (parts != null) {
            for (DocumentContentPart part : parts) {
                lines.addAll(part.getHeaderLineTexts());
            }
        }
        return lines;
    }

    public int getHeaderCount() {
        int sum = 0;
        for (DocumentContentPart part : parts) {
            sum += part.getHeaderCountRec();
        }
        return sum;
    }
    
    public int getHeaderLineCount() {
        int sum = 0;
        for (DocumentContentPart part : parts) {
            sum += part.getHeaderLineCountRec();
        }
        return sum;
    }
    
    public boolean containsHeaderText(String headerText) {
        for (DocumentContentPart part : parts) {
            if (part.containsHeaderText(headerText)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean containsHeaderFirstLineText(String lineText) {
        for (DocumentContentPart part : parts) {
            if (part.containsHeaderFirstLineText(lineText)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean containsHeaderLineText(String lineText) {
        for (DocumentContentPart part : parts) {
            if (part.containsHeaderLineText(lineText)) {
                return true;
            }
        }
        return false;
    }
    
    public List<String> getParagraphTexts() {
        List<String> pars = new ArrayList<String>();
        if (parts != null) {
            for (DocumentContentPart part : parts) {
                pars.addAll(part.getParagraphTextsRec());
            }
        }
        return pars;
    }
    
    public List<Paragraph> getParagraphs() {
        List<Paragraph> pars = new ArrayList<Paragraph>();
        if (parts != null) {
            for (DocumentContentPart part : parts) {
                pars.addAll(part.getParagraphsRec());
            }
        }
        return pars;
    }
    
    public int getParagraphCount() {
        int sum = 0;
        for (DocumentContentPart part : parts) {
            sum += part.getParagraphCountRec();
        }
        return sum;
    }
    
    public int getParagraphNumber(Paragraph par) {
        int num = 0;
        int partNum;
        for (DocumentContentPart dcp : parts) {
            partNum = dcp.getParagraphNumber(par);
            if (partNum < 0) {
                num += dcp.getParagraphCountRec();
            } else {
                return num + partNum + 1;
            }
        }
        return -1;
    }
    
    public Element toXML() {
        Element element = new Element("content");
        for (DocumentContentPart part : parts) {
            element.addContent(part.toXML());
        }
        return element;
    }

    public void build(BxDocContentStructure contentStructure) {
        int topClusterNum = contentStructure.getTopHeaderLevelId();
        if (topClusterNum < 0) {
            return;
        }
        List<BxDocContentPart> contentParts = contentStructure.getParts();
        List<BxDocContentPart> sectionParts = new ArrayList<BxDocContentPart>();
        for (BxDocContentPart contentPart : contentParts) {
            if (contentPart.getLevelId() == topClusterNum && !sectionParts.isEmpty()) {
                DocumentContentPart dcp = new DocumentContentPart();
                dcp.buildFromBxDocContent(sectionParts, 1);
                parts.add(dcp);
                sectionParts.clear();
            }
            sectionParts.add(contentPart);
        }
        if (!sectionParts.isEmpty()) {
            DocumentContentPart dcp = new DocumentContentPart();
            dcp.buildFromBxDocContent(sectionParts, 1);
            parts.add(dcp);
        }
    }
    
    public void build(List<Element> elements) {
        parts = new ArrayList<DocumentContentPart>();
        List<Element> partElements = new ArrayList<Element>();
        String topClusterName = elements.get(0).getName();

        for (Element element : elements) {
            if (element.getName().startsWith("h") && topClusterName.equals(element.getName())
                    && !partElements.isEmpty()) {
                DocumentContentPart dcp = new DocumentContentPart();
                dcp.buildFromXML(partElements, 1);
                parts.add(dcp);
                partElements.clear();
            }
            partElements.add(element);
        }

        if (!partElements.isEmpty()) {
            DocumentContentPart dcp = new DocumentContentPart();
            dcp.buildFromXML(partElements, 1);
            parts.add(dcp);
        }
    }

    public void printHeaders() {
        for (DocumentContentPart part : parts) {
            part.printHeaders();
        }
    }
    
    public void print() {
        for (DocumentContentPart part : parts) {
            part.print();
        }
    }
    
        
    public static class DocumentContentPart {

        private Header header;

        private List<Paragraph> paragraphs;
               
        private List<DocumentContentPart> parts = new ArrayList<DocumentContentPart>();
        
        private DocumentContentPart parent;

      
        public Header getHeader() {
            return header;
        }

        public void setHeader(Header header) {
            this.header = header;
        }
        
        public List<String> getHeaderTexts() {
            List<String> headers = new ArrayList<String>();
            headers.add(header.getText());
            if (parts != null) {
                for (DocumentContentPart part : parts) {
                    headers.addAll(part.getHeaderTexts());
                }
            }
            return headers;
        }
         
        public List<String> getHeaderLineTexts() {
            List<String> lines = new ArrayList<String>();
            lines.addAll(Arrays.asList(header.getText().split("\n")));
            if (parts != null) {
                for (DocumentContentPart part : parts) {
                    lines.addAll(part.getHeaderLineTexts());
                }
            }
            return lines;
        }
        
        public int getHeaderCountRec() {
            int sum = 1;
            for (DocumentContentPart part : parts) {
                sum += part.getHeaderCountRec();
            }
            return sum;
        }
        
        public int getHeaderLineCountRec() {
            int sum = 0;
            if (header != null) {
                sum = header.getText().split("\n").length;
            }
            for (DocumentContentPart part : parts) {
                sum += part.getHeaderLineCountRec();
            }
            return sum;
        }
        
        public boolean containsHeaderText(String headerText) {
            if (headerText.equals(header.getText())) {
                return true;
            }
            for (DocumentContentPart part : parts) {
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
            for (DocumentContentPart part : parts) {
                if (part.containsHeaderFirstLineText(lineText)) {
                    return true;
                }
            }
            return false;
        }
        
        public boolean containsHeaderLineText(String lineText) {
            String[] lines = {};
            if (header != null) {
                lines = header.getText().split("\n");
            }
            if (Arrays.asList(lines).contains(lineText)) {
                return true;
            }
            for (DocumentContentPart part : parts) {
                if (part.containsHeaderLineText(lineText)) {
                    return true;
                }
            }
            return false;
        }
        
        public List<String> getParagraphTextsRec() {
            List<String> pars = new ArrayList<String>();
            for (Paragraph p : paragraphs) {
                pars.add(p.getText());
            }
            
            if (parts != null) {
                for (DocumentContentPart part : parts) {
                    pars.addAll(part.getParagraphTextsRec());
                }
            }
            return pars;
        }
        
        public List<Paragraph> getParagraphsRec() {
            List<Paragraph> pars = new ArrayList<Paragraph>();
            pars.addAll(this.paragraphs);
            
            if (parts != null) {
                for (DocumentContentPart part : parts) {
                    pars.addAll(part.getParagraphsRec());
                }
            }
            return pars;
        }
        
        public int getParagraphCountRec() {
            int sum = paragraphs.size();
            for (DocumentContentPart part : parts) {
                sum += part.getParagraphCountRec();
            }
            return sum;
        }
        
        public int getParagraphNumber(Paragraph paragraph) {
            return paragraphs.indexOf(paragraph);
        }

        public List<DocumentContentPart> getParts() {
            return parts;
        }
        
        public void setParts(List<DocumentContentPart> parts) {
            this.parts = parts;
            for (DocumentContentPart dcp : parts) {
                dcp.parent = this;
            }
        }

        public DocumentContentPart getParent() {
            return parent;
        }
        
        public List<Element> toXML() {
            List<Element> elements = new ArrayList<Element>();
            Element element = new Element("h"+header.getLevel());
            elements.add(element);
            element.setText(header.getText());
            for (Paragraph par : paragraphs) {
                Element b = new Element("p");
                b.setText(par.getText());
                elements.add(b);
            }
            for (DocumentContentPart part : parts) {
                elements.addAll(part.toXML());
            }
            return elements;
        }
        
        public void buildFromXML(List<Element> elements, int level) {
            if (elements.isEmpty()) {
                return;
            }

            paragraphs = new ArrayList<Paragraph>();
            parts = new ArrayList<DocumentContentPart>();

            List<Element> partElements = new ArrayList<Element>();
            String topHeaderName = null;

            for (Element element : elements) {
                if (header == null) {
                    header = new Header(level, element.getValue(), this);
                } else if (topHeaderName == null && element.getName().startsWith("h")) {
                    topHeaderName = element.getName();
                    partElements.add(element);
                } else if (topHeaderName == null && element.getName().equals("p")) {
                    paragraphs.add(new Paragraph(element.getValue(), this));
                } else {
                    if (element.getName().startsWith("h") && topHeaderName.equals(element.getName())
                            && !partElements.isEmpty()) {
                        DocumentContentPart dcp = new DocumentContentPart();
                        dcp.parent = this;
                        dcp.buildFromXML(partElements, level + 1);
                        parts.add(dcp);
                        partElements.clear();
                    }
                    partElements.add(element);
                }
            }

            if (!partElements.isEmpty()) {
                DocumentContentPart dcp = new DocumentContentPart();
                dcp.parent = this;
                dcp.buildFromXML(partElements, level + 1);
                parts.add(dcp);
            }
        }

        private void buildFromBxDocContent(List<BxDocContentPart> contentParts, int level) {
            if (contentParts.isEmpty()) {
                return;
            }

            header = new Header(level, contentParts.get(0).getCleanHeaderText(), this);
            paragraphs = new ArrayList<Paragraph>();
            for (String s : contentParts.get(0).getCleanContentTexts()) {
                paragraphs.add(new Paragraph(s, this));
            }
            
            parts = new ArrayList<DocumentContentPart>();
            
            contentParts.remove(0);
            if (contentParts.isEmpty()) {
                return;
            }
            
            int topClusterNum = contentParts.get(0).getLevelId();
            List<BxDocContentPart> sectionContentParts = new ArrayList<BxDocContentPart>();
            
            for (BxDocContentPart contentPart : contentParts) {
                if (contentPart.getLevelId() == topClusterNum && !sectionContentParts.isEmpty()) {
                    DocumentContentPart dcp = new DocumentContentPart();
                    dcp.parent = this;
                    dcp.buildFromBxDocContent(sectionContentParts, level+1);
                    parts.add(dcp);
                    sectionContentParts.clear();
                }
                sectionContentParts.add(contentPart);
            }
            if (!sectionContentParts.isEmpty()) {
                DocumentContentPart dcp = new DocumentContentPart();
                dcp.parent = this;
                dcp.buildFromBxDocContent(sectionContentParts, level + 1);
                parts.add(dcp);
            }
        }
        
        public void printHeaders() {
            for (int i = 1; i < header.getLevel(); i++) {
                System.out.print("\t");
            }
            System.out.println(header.getLevel() + " " + header.getText());
            for (DocumentContentPart dcp : parts) {
                dcp.printHeaders();
            }
            System.out.println("");
        }
        
        public void print() {
            for (int i = 1; i < header.getLevel(); i++) {
                System.out.print("\t");
            }
            System.out.println(header.getLevel() + " " + header.text);
            System.out.println(""); 
            for (Paragraph p : paragraphs) { 
                System.out.println("[" + p.getText() + "]"); 
            } 
            System.out.println("");
            for (DocumentContentPart dcp : parts) {
                dcp.print();
            }
            System.out.println("");
        }

    }
    
    public static class Header {
        private int level;
        private String text;
        private DocumentContentPart part;

        public Header(int level, String text, DocumentContentPart part) {
            this.level = level;
            this.text = text;
            this.part= part;
        }

        private Header() {
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

        public DocumentContentPart getPart() {
            return part;
        }

        public void setPart(DocumentContentPart part) {
            this.part = part;
        }
        
    }
    
    public static class Paragraph {
        
        private String text;
        private DocumentContentPart part;

        public Paragraph(String text, DocumentContentPart part) {
            this.text = text;
            this.part = part;
        }

        
        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public DocumentContentPart getPart() {
            return part;
        }

        public void setPart(DocumentContentPart part) {
            this.part = part;
        }

    }
}
