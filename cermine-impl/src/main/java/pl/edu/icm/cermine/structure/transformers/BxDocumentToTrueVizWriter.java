package pl.edu.icm.cermine.structure.transformers;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import pl.edu.icm.cermine.exception.TransformationException;
import pl.edu.icm.cermine.structure.model.*;

/**
 * Writes BxDocument model pages to TrueViz format.
 *
 * @author krusek
 */
public class BxDocumentToTrueVizWriter {

    private static final Properties OUTPUT_PROPERTIES = new Properties();

    static {
        OUTPUT_PROPERTIES.setProperty(OutputKeys.DOCTYPE_SYSTEM, "Trueviz.dtd");
        OUTPUT_PROPERTIES.setProperty(OutputKeys.INDENT, "yes");
    }

    private static final DecimalFormat FORMAT = new DecimalFormat("0.000", new DecimalFormatSymbols(Locale.US));

    public static final Map<BxZoneLabel, String> ZONE_LABEL_MAP = new EnumMap<BxZoneLabel, String>(BxZoneLabel.class);
    static {
        ZONE_LABEL_MAP.put(BxZoneLabel.GEN_METADATA,          "gen_metadata");
        ZONE_LABEL_MAP.put(BxZoneLabel.GEN_BODY,              "gen_body");
        ZONE_LABEL_MAP.put(BxZoneLabel.GEN_REFERENCES,        "gen_references");
        ZONE_LABEL_MAP.put(BxZoneLabel.GEN_OTHER,             "gen_other");
        ZONE_LABEL_MAP.put(BxZoneLabel.MET_ABSTRACT,          "abstract");
        ZONE_LABEL_MAP.put(BxZoneLabel.MET_AFFILIATION,       "affiliation");
        ZONE_LABEL_MAP.put(BxZoneLabel.MET_AUTHOR,            "author");
        ZONE_LABEL_MAP.put(BxZoneLabel.MET_BIB_INFO,          "bib_info");
        ZONE_LABEL_MAP.put(BxZoneLabel.MET_BIOGRAPHY, "biography");
        ZONE_LABEL_MAP.put(BxZoneLabel.MET_CORRESPONDENCE,    "correspondence");
        ZONE_LABEL_MAP.put(BxZoneLabel.MET_ACCESS_DATA,    "access_data");
        ZONE_LABEL_MAP.put(BxZoneLabel.MET_DATES,             "dates");
        ZONE_LABEL_MAP.put(BxZoneLabel.MET_EDITOR,            "editor");
        ZONE_LABEL_MAP.put(BxZoneLabel.MET_KEYWORDS,          "keywords");
        ZONE_LABEL_MAP.put(BxZoneLabel.MET_TITLE,             "title");
        ZONE_LABEL_MAP.put(BxZoneLabel.MET_TYPE,              "type");
        ZONE_LABEL_MAP.put(BxZoneLabel.MET_COPYRIGHT,              "copyright");
        ZONE_LABEL_MAP.put(BxZoneLabel.BODY_CONTRIBUTION,          "contribution");
        ZONE_LABEL_MAP.put(BxZoneLabel.BODY_ATTACHMENT,          "attachment");
        ZONE_LABEL_MAP.put(BxZoneLabel.BODY_ACKNOWLEDGMENT,          "acknowledgment");
        ZONE_LABEL_MAP.put(BxZoneLabel.BODY_GLOSSARY,          "glossary");
        ZONE_LABEL_MAP.put(BxZoneLabel.BODY_CONFLICT_STMT,          "conflict_statement");
        ZONE_LABEL_MAP.put(BxZoneLabel.BODY_CONTENT,          "body_content");
        ZONE_LABEL_MAP.put(BxZoneLabel.BODY_HEADING,          "heading");
        ZONE_LABEL_MAP.put(BxZoneLabel.BODY_EQUATION,         "equation");
        ZONE_LABEL_MAP.put(BxZoneLabel.BODY_EQUATION_LABEL,   "equation_label");
        ZONE_LABEL_MAP.put(BxZoneLabel.BODY_FIGURE,           "figure");
        ZONE_LABEL_MAP.put(BxZoneLabel.BODY_FIGURE_CAPTION,   "figure_caption");
        ZONE_LABEL_MAP.put(BxZoneLabel.BODY_TABLE,            "table");
        ZONE_LABEL_MAP.put(BxZoneLabel.BODY_TABLE_CAPTION,    "table_caption");
        ZONE_LABEL_MAP.put(BxZoneLabel.BODY_ATTACHMENT,    		"attachment");
        ZONE_LABEL_MAP.put(BxZoneLabel.OTH_PAGE_NUMBER,       "page_number");
        ZONE_LABEL_MAP.put(BxZoneLabel.OTH_UNKNOWN ,          "unknown");
        ZONE_LABEL_MAP.put(BxZoneLabel.REFERENCES,          "references");
    }
    
    private void appendProperty(Document doc, Element parent, String name, String value) {
        Element node = doc.createElement(name);
        node.setAttribute("Value", value);
        parent.appendChild(node);
    }
    
    private void appendPropertyIfNotNull(Document doc, Element parent, String name, String value) {
        if(value == null) {
        	appendProperty(doc, parent, name, "");
        } else {
        	appendProperty(doc, parent, name, value);
        }
    }
    
    private void appendVertex(Document doc, Element parent, double x, double y) {
        Element node = doc.createElement("Vertex");
        node.setAttribute("x", FORMAT.format(x));
        node.setAttribute("y", FORMAT.format(y));
        parent.appendChild(node);
    }

    private void appendBounds(Document doc, Element parent, String name, BxBounds bounds) {
        if (bounds == null) {
            bounds = new BxBounds();
        }
        Element node = doc.createElement(name);
        appendVertex(doc, node, bounds.getX(), bounds.getY());
        appendVertex(doc, node, bounds.getX() + bounds.getWidth(), bounds.getY());
        appendVertex(doc, node, bounds.getX() + bounds.getWidth(), bounds.getY() + bounds.getHeight());
        appendVertex(doc, node, bounds.getX(), bounds.getY() + bounds.getHeight());
        parent.appendChild(node);
    }

    private void appendCharacter(Document doc, Element parent, BxChunk chunk) {
        Element node = doc.createElement("Character");
        appendPropertyIfNotNull(doc, node, "CharacterID", chunk.getId());
        appendBounds(doc, node, "CharacterCorners", chunk.getBounds());
        appendPropertyIfNotNull(doc, node, "CharacterNext", chunk.getNextId());
        appendProperty(doc, node, "GT_Text", chunk.getText());
        parent.appendChild(node);
    }

    private void appendWord(Document doc, Element parent, BxWord word) {
        Element node = doc.createElement("Word");
        appendPropertyIfNotNull(doc, node, "WordID", word.getId());
        appendBounds(doc, node, "WordCorners", word.getBounds());
        appendPropertyIfNotNull(doc, node, "WordNext", word.getNextId());
        appendProperty(doc, node, "WordNumChars", "");
        for (BxChunk chunk: word.getChunks()) {
            appendCharacter(doc, node, chunk);
        }
        parent.appendChild(node);
    }

    private void appendLine(Document doc, Element parent, BxLine line) {
        Element node = doc.createElement("Line");
        appendPropertyIfNotNull(doc, node, "LineID", line.getId());
        appendBounds(doc, node, "LineCorners", line.getBounds());
        appendPropertyIfNotNull(doc, node, "LineNext", line.getNextId());
        appendProperty(doc, node, "LineNumChars", "");
        for (BxWord word: line.getWords()) {
            appendWord(doc, node, word);
        }
        parent.appendChild(node);
    }

    private void appendClassification(Document doc, Element parent, String category, String type) {
        Element node = doc.createElement("Classification");
        appendProperty(doc, node, "Category", category);
        appendProperty(doc, node, "Type", type);
        parent.appendChild(node);
    }

    private void appendZone(Document doc, Element parent, BxZone zone) {
        Element node = doc.createElement("Zone");
        appendPropertyIfNotNull(doc, node, "ZoneID", zone.getId());
        appendBounds(doc, node, "ZoneCorners", zone.getBounds());
        appendPropertyIfNotNull(doc, node, "ZoneNext", zone.getNextId());
        Element insetsNode = doc.createElement("ZoneInsets");
        insetsNode.setAttribute("Top", "");
        insetsNode.setAttribute("Bottom", "");
        insetsNode.setAttribute("Left", "");
        insetsNode.setAttribute("Right", "");
        node.appendChild(insetsNode);
        appendProperty(doc, node, "ZoneLines", "");
        if (zone.getLabel() != null) {
            if (ZONE_LABEL_MAP.containsKey(zone.getLabel())) {
                appendClassification(doc, node, ZONE_LABEL_MAP.get(zone.getLabel()), "");
            } else {
                appendClassification(doc, node, zone.getLabel().toString(), "");
            }
        }
        for (BxLine line: zone.getLines()) {
            appendLine(doc, node, line);
        }
        parent.appendChild(node);
    }

    private void appendPage(Document doc, Element parent, BxPage page) {
        Element node = doc.createElement("Page");
        appendPropertyIfNotNull(doc, node, "PageID", page.getId());
        appendProperty(doc, node, "PageType", "");
        appendProperty(doc, node, "PageNumber", "");
        appendProperty(doc, node, "PageColumns", "");
        appendPropertyIfNotNull(doc, node, "PageNext", page.getNextId());
        appendProperty(doc, node, "PageZones", "");
        for (BxZone zone: page.getZones()) {
            appendZone(doc, node, zone);
        }
        parent.appendChild(node);
    }

    private void appendLanguage(Document doc, Element parent, String type, String script, String codeset) {
        Element node = doc.createElement("Language");
        node.setAttribute("Type", type);
        node.setAttribute("Script", script);
        node.setAttribute("Codeset", codeset);
        parent.appendChild(node);
    }

    private void appendFont(Document doc, Element parent, String type, String style, String spacing, String size) {
        Element node = doc.createElement("Font");
        node.setAttribute("Type", type);
        node.setAttribute("Style", style);
        node.setAttribute("Spacing", spacing);
        node.setAttribute("Size", size);
        parent.appendChild(node);
    }

    private Document createDocument(List<BxPage> pages) throws ParserConfigurationException {
        Document doc = TrueVizUtils.newDocumentBuilder().newDocument();
        Element root = doc.createElement("Document");
        appendProperty(doc, root, "DocID", "");
        appendProperty(doc, root, "DocTitle", "");
        appendProperty(doc, root, "DocPubName", "");
        appendProperty(doc, root, "DocVolNum", "");
        appendProperty(doc, root, "DocIssueNum", "");
        appendProperty(doc, root, "DocMargins", "");
        appendProperty(doc, root, "DocDate", "");
        appendProperty(doc, root, "DocPages", "");
        Element imageNode = doc.createElement("DocImage");
        appendProperty(doc, imageNode, "Name", "");
        appendProperty(doc, imageNode, "Format", "");
        appendProperty(doc, imageNode, "Depth", "");
        appendProperty(doc, imageNode, "Compression", "");
        appendProperty(doc, imageNode, "Capture", "");
        appendProperty(doc, imageNode, "Quality", "");
        root.appendChild(imageNode);
        appendLanguage(doc, root, "", "", "");
        appendFont(doc, root, "", "", "", "");
        appendProperty(doc, root, "ReadingDir", "");
        appendProperty(doc, root, "CharOrient", "");
        appendClassification(doc, root, "", "");
        appendProperty(doc, root, "GT_Text", "");

        for (BxPage page: pages) {
            appendPage(doc, root, page);
        }
        doc.appendChild(root);

        return doc;
    }

    public String write(List<BxPage> objects, Object... hints) throws TransformationException {
        StringWriter sw = new StringWriter();
        write(sw, objects, hints);
        return sw.toString();
    }

    public void write(Writer writer, List<BxPage> objects, Object... hints) throws TransformationException {
        try {
            Document doc = createDocument(objects);
            Transformer t = TransformerFactory.newInstance().newTransformer();
            t.setOutputProperties(OUTPUT_PROPERTIES);
            t.transform(new DOMSource(doc), new StreamResult(writer));
        } catch (TransformerException ex) {
            throw new TransformationException(ex);
        } catch (ParserConfigurationException ex) {
            throw new TransformationException(ex);
        }
    }
}
