package pl.edu.icm.yadda.analysis.textr.transformers;

import java.io.StringWriter;
import java.io.Writer;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import pl.edu.icm.yadda.analysis.textr.model.BxBounds;
import pl.edu.icm.yadda.analysis.textr.model.BxChunk;
import pl.edu.icm.yadda.analysis.textr.model.BxLine;
import pl.edu.icm.yadda.analysis.textr.model.BxPage;
import pl.edu.icm.yadda.analysis.textr.model.BxWord;
import pl.edu.icm.yadda.analysis.textr.model.BxZone;
import pl.edu.icm.yadda.metadata.transformers.IMetadataWriter;
import pl.edu.icm.yadda.metadata.transformers.MetadataFormat;
import pl.edu.icm.yadda.metadata.transformers.MetadataModel;
import pl.edu.icm.yadda.metadata.transformers.TransformationException;

/**
 * Writes BxDocument model pages to TrueViz format.
 *
 * @author krusek
 */
public class BxDocumentToTrueVizWriter implements IMetadataWriter<BxPage> {

    private static final Properties OUTPUT_PROPERTIES = new Properties();

    static {
        OUTPUT_PROPERTIES.setProperty(OutputKeys.DOCTYPE_SYSTEM, "Trueviz.dtd");
        OUTPUT_PROPERTIES.setProperty(OutputKeys.INDENT, "yes");
    }

    private static final DecimalFormat FORMAT = new DecimalFormat("0.000", new DecimalFormatSymbols(Locale.US));

    @Override
    public MetadataModel<BxPage> getSourceModel() {
        return BxDocumentTransformers.MODEL;
    }

    @Override
    public MetadataFormat getTargetFormat() {
        return TrueVizUtils.TRUEVIZ_FORMAT;
    }

    private void appendProperty(Document doc, Element parent, String name, String value) {
        Element node = doc.createElement(name);
        node.setAttribute("Value", value);
        parent.appendChild(node);
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
        appendProperty(doc, node, "CharacterID", "");
        appendBounds(doc, node, "CharacterCorners", chunk.getBounds());
        appendProperty(doc, node, "CharacterNext", "");
        appendProperty(doc, node, "GT_Text", chunk.getText());
        parent.appendChild(node);
    }

    private void appendWord(Document doc, Element parent, BxWord word) {
        Element node = doc.createElement("Word");
        appendProperty(doc, node, "WordID", "");
        appendBounds(doc, node, "WordCorners", word.getBounds());
        appendProperty(doc, node, "WordNext", "");
        appendProperty(doc, node, "WordNumChars", "");
        for (BxChunk chunk: word.getChunks()) {
            appendCharacter(doc, node, chunk);
        }
        parent.appendChild(node);
    }

    private void appendLine(Document doc, Element parent, BxLine line) {
        Element node = doc.createElement("Line");
        appendProperty(doc, node, "LineID", "");
        appendBounds(doc, node, "LineCorners", line.getBounds());
        appendProperty(doc, node, "LineNext", "");
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
        appendProperty(doc, node, "ZoneID", "");
        appendBounds(doc, node, "ZoneCorners", zone.getBounds());
        appendProperty(doc, node, "ZoneNext", "");
        Element insetsNode = doc.createElement("ZoneInsets");
        insetsNode.setAttribute("Top", "");
        insetsNode.setAttribute("Bottom", "");
        insetsNode.setAttribute("Left", "");
        insetsNode.setAttribute("Right", "");
        node.appendChild(insetsNode);
        appendProperty(doc, node, "ZoneLines", "");
        if (zone.getLabel() != null) {
            appendClassification(doc, node, zone.getLabel().toString(), "");
        }
        for (BxLine line: zone.getLines()) {
            appendLine(doc, node, line);
        }
        parent.appendChild(node);
    }

    private void appendPage(Document doc, Element parent, BxPage page) {
        Element node = doc.createElement("Page");
        appendProperty(doc, node, "PageID", "");
        appendProperty(doc, node, "PageType", "");
        appendProperty(doc, node, "PageNumber", "");
        appendProperty(doc, node, "PageColumns", "");
        appendProperty(doc, node, "PageNext", "");
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

    @Override
    public String write(List<BxPage> objects, Object... hints) throws TransformationException {
        StringWriter sw = new StringWriter();
        write(sw, objects, hints);
        return sw.toString();
    }

    @Override
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
