package pl.edu.icm.yadda.analysis.textr.transformers;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import pl.edu.icm.yadda.analysis.textr.model.BxBounds;
import pl.edu.icm.yadda.analysis.textr.model.BxChunk;
import pl.edu.icm.yadda.analysis.textr.model.BxLine;
import pl.edu.icm.yadda.analysis.textr.model.BxPage;
import pl.edu.icm.yadda.analysis.textr.model.BxWord;
import pl.edu.icm.yadda.analysis.textr.model.BxZone;
import pl.edu.icm.yadda.analysis.textr.model.BxZoneLabel;
import pl.edu.icm.yadda.analysis.textr.tools.BxBoundsBuilder;
import pl.edu.icm.yadda.analysis.textr.tools.BxModelUtils;
import pl.edu.icm.yadda.metadata.transformers.IMetadataReader;
import pl.edu.icm.yadda.metadata.transformers.MetadataFormat;
import pl.edu.icm.yadda.metadata.transformers.MetadataModel;
import pl.edu.icm.yadda.metadata.transformers.TransformationException;

/**
 * Reads BxDocument model pages from TrueViz format.
 *
 * @author kura
 * @author krusek
 */
public class TrueVizToBxDocumentReader implements IMetadataReader<BxPage> {
    
    private static final Logger log = LoggerFactory.getLogger(TrueVizToBxDocumentReader.class);

    @Override
    public MetadataFormat getSourceFormat() {
        return TrueVizUtils.TRUEVIZ_FORMAT;
    }

    @Override
    public MetadataModel<BxPage> getTargetModel() {
        return BxDocumentTransformers.MODEL;
    }

    @Override
    public List<BxPage> read(String string, Object... hints) throws TransformationException {
        return read(new StringReader(string), hints);
    }

    @Override
    public List<BxPage> read(Reader reader, Object... hints) throws TransformationException {
        try {
            Document doc = TrueVizUtils.newDocumentBuilder().parse(new InputSource(reader));
            List<BxPage> pages = new ArrayList<BxPage>();

            if ("Page".equalsIgnoreCase(doc.getDocumentElement().getTagName())) {
                pages.add(parsePageNode(doc.getDocumentElement()));
            } else if ("Document".equalsIgnoreCase(doc.getDocumentElement().getTagName())) {
                for (Element pageElement : getChildren("Page", doc.getDocumentElement())) {
                    pages.add(parsePageNode(pageElement));
                }
            }

            return pages;
        } catch (IOException ex) {
            throw new TransformationException(ex);
        } catch (ParserConfigurationException ex) {
            throw new TransformationException(ex);
        } catch (SAXException ex) {
            throw new TransformationException(ex);
        }
    }

    private ArrayList<Element> getChildren(String name, Element el) {
         ArrayList<Element> list=new ArrayList<Element>();
         NodeList nl=el.getChildNodes();
         for (int i=0; i<nl.getLength();i++) {
             Node n=nl.item(i);
            if (n instanceof Element) {
                 Element e=(Element) n;
                 if (e.getTagName().equalsIgnoreCase(name)){
                    list.add(e);
                }
            }
        }
        return list;
    }




    private BxBounds parseElementContainingVertexes(Element el) {
        ArrayList<Element> vs = getChildren("Vertex",el);
        BxBoundsBuilder builder = new BxBoundsBuilder();
        for (Element v : vs) {
            double x = Double.parseDouble(v.getAttribute("x"));
            double y = Double.parseDouble(v.getAttribute("y"));
            builder.expand(x, y);
        }
        return builder.getBounds();
    }

    private BxChunk parseCharacterElement(Element charE) {
        BxBounds bou = null;
        String text = null;
        if (!getChildren("CharacterCorners",charE).isEmpty()) {
            bou = (parseElementContainingVertexes(getChildren("CharacterCorners",charE).get(0)));
        }
        if (!(getChildren("GT_Text",charE).isEmpty())) {
            text = getChildren("GT_Text",charE).get(0).getAttribute("Value");
        }
        return new BxChunk(bou, text);
    }

    private BxWord parseWordElement(Element wordE) {
        BxWord word = new BxWord();
        if (!(getChildren("WordCorners",wordE).isEmpty())) {
            word.setBounds(parseElementContainingVertexes(getChildren("WordCorners",wordE).get(0)));
        }

        List<Element> e = getChildren("Character",wordE);
        for (Element caE : e) {
            BxChunk ch = parseCharacterElement(caE);
            word.addChunks(ch);
        }
        return word;
    }

    private BxLine parseLineElement(Element lineE) {
        BxLine line = new BxLine();
         if (!(getChildren("LineCorners",lineE).isEmpty()))  {
            line.setBounds(parseElementContainingVertexes(getChildren("LineCorners",lineE).get(0)));
        }
        List<Element> e = getChildren("Word",lineE);
        for (Element we : e) {
            BxWord wo = parseWordElement(we);
            line.addWord(wo);
        }
        return line;
    }

    private BxZoneLabel parseClassification(Element elClassicfication) {
        ArrayList<Element> eli=getChildren("Category",elClassicfication);
        Element catEl = eli.isEmpty()?null:eli.get(0);
        if (catEl == null) {
            eli=getChildren("Type",elClassicfication);
            catEl = eli.isEmpty()?null:eli.get(0);
        }
        if (catEl == null) {
            return null;
        }
        String val = catEl.getAttribute("Value");
        if (val == null) {
            return null;
        }
        try {
            return BxZoneLabel.valueOf(val.toUpperCase());
        } catch (IllegalArgumentException ex) {
            return BxZoneLabel.UNKNOWN;
        }
    }

    private BxZone parseZoneNode(Element zoneE) {
        BxZone zone = new BxZone();
        if (!getChildren("Classification",zoneE).isEmpty()) {
            zone.setLabel(parseClassification(getChildren("Classification",zoneE).get(0)));
        }
        if (!getChildren("ZoneCorners",zoneE).isEmpty()) {
            zone.setBounds(parseElementContainingVertexes(getChildren("ZoneCorners",zoneE).get(0)));
        }
        List<Element> e = getChildren("Line",zoneE);
        for (Element lin : e) {
            BxLine li = parseLineElement(lin);
            zone.addLine(li);
        }
        return zone;

    }

    private BxPage parsePageNode(Element elem) {
        BxPage page = new BxPage();

        List<Element> e = getChildren("Zone", elem);
        for (Element zo : e) {
            BxZone zon = parseZoneNode(zo);
            page.addZone(zon);
        }
        BxBoundsBuilder.setBounds(page);
        BxModelUtils.sortZonesYX(page);
        
        return page;
    }
}
