/**
 * This file is part of CERMINE project.
 * Copyright (c) 2011-2016 ICM-UW
 *
 * CERMINE is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * CERMINE is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with CERMINE. If not, see <http://www.gnu.org/licenses/>.
 */

package pl.edu.icm.cermine.structure.transformers;

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
 * @author Krzysztof Rusek
 */
public class BxDocumentToTrueVizWriter {

    public static final String MINIMAL_OUTPUT_SIZE = "MINIMAL_OUTPUT_SIZE";
    
    private static final Properties OUTPUT_PROPERTIES = new Properties();

    static {
        OUTPUT_PROPERTIES.setProperty(OutputKeys.DOCTYPE_SYSTEM, "Trueviz.dtd");
        OUTPUT_PROPERTIES.setProperty(OutputKeys.INDENT, "yes");
    }

    public static final Map<BxZoneLabel, String> ZONE_LABEL_MAP = new EnumMap<BxZoneLabel, String>(BxZoneLabel.class);
    static {
        ZONE_LABEL_MAP.put(BxZoneLabel.GEN_METADATA,          "gen_metadata");
        ZONE_LABEL_MAP.put(BxZoneLabel.GEN_BODY,              "gen_body");
        ZONE_LABEL_MAP.put(BxZoneLabel.GEN_REFERENCES,        "gen_references");
        ZONE_LABEL_MAP.put(BxZoneLabel.GEN_OTHER,             "gen_other");
        ZONE_LABEL_MAP.put(BxZoneLabel.MET_ABSTRACT,          "abstract");
        ZONE_LABEL_MAP.put(BxZoneLabel.MET_AFFILIATION,       "affiliation");
        ZONE_LABEL_MAP.put(BxZoneLabel.MET_AUTHOR,            "author");
        ZONE_LABEL_MAP.put(BxZoneLabel.MET_TITLE_AUTHOR,      "author_title");
        ZONE_LABEL_MAP.put(BxZoneLabel.MET_BIB_INFO,          "bib_info");
        ZONE_LABEL_MAP.put(BxZoneLabel.MET_BIOGRAPHY,		  "biography");
        ZONE_LABEL_MAP.put(BxZoneLabel.MET_CATEGORY,		  "category");
        ZONE_LABEL_MAP.put(BxZoneLabel.MET_TERMS,             "terms");
        ZONE_LABEL_MAP.put(BxZoneLabel.MET_CORRESPONDENCE,    "correspondence");
        ZONE_LABEL_MAP.put(BxZoneLabel.MET_ACCESS_DATA,    	  "access_data");
        ZONE_LABEL_MAP.put(BxZoneLabel.MET_DATES,             "dates");
        ZONE_LABEL_MAP.put(BxZoneLabel.MET_EDITOR,            "editor");
        ZONE_LABEL_MAP.put(BxZoneLabel.MET_KEYWORDS,          "keywords");
        ZONE_LABEL_MAP.put(BxZoneLabel.MET_TITLE,             "title");
        ZONE_LABEL_MAP.put(BxZoneLabel.MET_TYPE,              "type");
        ZONE_LABEL_MAP.put(BxZoneLabel.MET_COPYRIGHT,         "copyright");
        ZONE_LABEL_MAP.put(BxZoneLabel.BODY_CONTRIBUTION,     "contribution");
        ZONE_LABEL_MAP.put(BxZoneLabel.BODY_ATTACHMENT,       "attachment");
        ZONE_LABEL_MAP.put(BxZoneLabel.BODY_ACKNOWLEDGMENT,   "acknowledgment");
        ZONE_LABEL_MAP.put(BxZoneLabel.BODY_GLOSSARY,         "glossary");
        ZONE_LABEL_MAP.put(BxZoneLabel.BODY_CONFLICT_STMT,    "conflict_statement");
        ZONE_LABEL_MAP.put(BxZoneLabel.BODY_CONTENT,          "body_content");
        ZONE_LABEL_MAP.put(BxZoneLabel.BODY_HEADING,          "heading");
        ZONE_LABEL_MAP.put(BxZoneLabel.BODY_EQUATION,         "equation");
        ZONE_LABEL_MAP.put(BxZoneLabel.BODY_EQUATION_LABEL,   "equation_label");
        ZONE_LABEL_MAP.put(BxZoneLabel.BODY_FIGURE,           "figure");
        ZONE_LABEL_MAP.put(BxZoneLabel.BODY_FIGURE_CAPTION,   "figure_caption");
        ZONE_LABEL_MAP.put(BxZoneLabel.BODY_TABLE,            "table");
        ZONE_LABEL_MAP.put(BxZoneLabel.BODY_TABLE_CAPTION,    "table_caption");
        ZONE_LABEL_MAP.put(BxZoneLabel.BODY_ATTACHMENT,    	  "attachment");
        ZONE_LABEL_MAP.put(BxZoneLabel.BODY_JUNK,             "junk");
        ZONE_LABEL_MAP.put(BxZoneLabel.OTH_PAGE_NUMBER,       "page_number");
        ZONE_LABEL_MAP.put(BxZoneLabel.OTH_UNKNOWN ,          "unknown");
        ZONE_LABEL_MAP.put(BxZoneLabel.REFERENCES,            "references");
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
    
    private void appendVertex(Document doc, Element parent, double x, double y, Object... hints) {
        DecimalFormat format = new DecimalFormat("0.000", new DecimalFormatSymbols(Locale.US));
        if (Arrays.asList(hints).contains(MINIMAL_OUTPUT_SIZE)) {
            format = new DecimalFormat("0.0", new DecimalFormatSymbols(Locale.US));
        }
        Element node = doc.createElement("Vertex");
        node.setAttribute("x", format.format(x));
        node.setAttribute("y", format.format(y));
        parent.appendChild(node);
    }

    private void appendBounds(Document doc, Element parent, String name, BxBounds bounds, Object... hints) {
        if (bounds == null) {
            bounds = new BxBounds();
        }
        Element node = doc.createElement(name);
        appendVertex(doc, node, bounds.getX(), bounds.getY(), hints);
        if (!Arrays.asList(hints).contains(MINIMAL_OUTPUT_SIZE)) {
            appendVertex(doc, node, bounds.getX() + bounds.getWidth(), bounds.getY(), hints);
        }
        appendVertex(doc, node, bounds.getX() + bounds.getWidth(), bounds.getY() + bounds.getHeight(), hints);
        if (!Arrays.asList(hints).contains(MINIMAL_OUTPUT_SIZE)) {
            appendVertex(doc, node, bounds.getX(), bounds.getY() + bounds.getHeight(), hints);
        }
        parent.appendChild(node);
    }

    private void appendCharacter(Document doc, Element parent, BxChunk chunk, Object... hints) {
        Element node = doc.createElement("Character");
        appendPropertyIfNotNull(doc, node, "CharacterID", chunk.getId());
        appendBounds(doc, node, "CharacterCorners", chunk.getBounds(), hints);
        appendPropertyIfNotNull(doc, node, "CharacterNext", chunk.getNextId());
        Element font = doc.createElement("Font");
        font.setAttribute("Size", "");
        font.setAttribute("Spacing", "");
        font.setAttribute("Style", "");
        font.setAttribute("Type", chunk.getFontName());
        node.appendChild(font);
        appendProperty(doc, node, "GT_Text", chunk.toText());
        parent.appendChild(node);
    }

    private void appendWord(Document doc, Element parent, BxWord word, Object... hints) {
        Element node = doc.createElement("Word");
        appendPropertyIfNotNull(doc, node, "WordID", word.getId());
        appendBounds(doc, node, "WordCorners", word.getBounds(), hints);
        appendPropertyIfNotNull(doc, node, "WordNext", word.getNextId());
        appendProperty(doc, node, "WordNumChars", "");
        for (BxChunk chunk : word) {
            appendCharacter(doc, node, chunk, hints);
        }
        parent.appendChild(node);
    }

    private void appendLine(Document doc, Element parent, BxLine line, Object... hints) {
        Element node = doc.createElement("Line");
        appendPropertyIfNotNull(doc, node, "LineID", line.getId());
        appendBounds(doc, node, "LineCorners", line.getBounds(), hints);
        appendPropertyIfNotNull(doc, node, "LineNext", line.getNextId());
        appendProperty(doc, node, "LineNumChars", "");
        for (BxWord word : line) {
            appendWord(doc, node, word, hints);
        }
        parent.appendChild(node);
    }

    private void appendClassification(Document doc, Element parent, String category, String type) {
        Element node = doc.createElement("Classification");
        appendProperty(doc, node, "Category", category);
        appendProperty(doc, node, "Type", type);
        parent.appendChild(node);
    }

    private void appendZone(Document doc, Element parent, BxZone zone, Object... hints) throws TransformationException {
        Element node = doc.createElement("Zone");
        appendPropertyIfNotNull(doc, node, "ZoneID", zone.getId());
        appendBounds(doc, node, "ZoneCorners", zone.getBounds(), hints);
        appendPropertyIfNotNull(doc, node, "ZoneNext", zone.getNextId());
        Element insetsNode = doc.createElement("ZoneInsets");
        insetsNode.setAttribute("Top", "");
        insetsNode.setAttribute("Bottom", "");
        insetsNode.setAttribute("Left", "");
        insetsNode.setAttribute("Right", "");
        node.appendChild(insetsNode);
        appendProperty(doc, node, "ZoneLines", "");
        if (zone.getLabel() != null) {
            if (ZONE_LABEL_MAP.get(zone.getLabel()) != null && !ZONE_LABEL_MAP.get(zone.getLabel()).isEmpty()) {
                appendClassification(doc, node, ZONE_LABEL_MAP.get(zone.getLabel()).toUpperCase(), "");
            } else {
            	throw new TransformationException("Writing down an unknown zone label: " + zone.getLabel());
            }
        }
        for (BxLine line : zone) {
            appendLine(doc, node, line, hints);
        }
        parent.appendChild(node);
    }

    private void appendPage(Document doc, Element parent, BxPage page, Object... hints) throws TransformationException {
        Element node = doc.createElement("Page");
        appendPropertyIfNotNull(doc, node, "PageID", page.getId());
        appendProperty(doc, node, "PageType", "");
        appendProperty(doc, node, "PageNumber", "");
        appendProperty(doc, node, "PageColumns", "");
        appendPropertyIfNotNull(doc, node, "PageNext", page.getNextId());
        appendProperty(doc, node, "PageZones", "");
        for (BxZone zone : page) {
            appendZone(doc, node, zone, hints);
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

    private Document createDocument(List<BxPage> pages, Object... hints) throws ParserConfigurationException, TransformationException {
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
            appendPage(doc, root, page, hints);
        }
        doc.appendChild(root);

        return doc;
    }

    public String write(List<BxPage> objects, Object... hints) throws TransformationException {
        StringWriter sw = new StringWriter();
        write(sw, objects, hints);
        sw.flush();
        return sw.toString();
    }

    public void write(Writer writer, List<BxPage> objects, Object... hints) throws TransformationException {
        try {
            Document doc = createDocument(objects, hints);
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
