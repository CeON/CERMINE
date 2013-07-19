package pl.edu.icm.cermine.structure.transformers;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.*;
import javax.activation.UnsupportedDataTypeException;
import javax.xml.parsers.ParserConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import pl.edu.icm.cermine.exception.TransformationException;
import pl.edu.icm.cermine.structure.model.*;

/**
 * Reads BxDocument model pages from Marg format.
 *
 * @author kura
 * @author krusek
 */
public class MargToTextrImporter {

    private static final Logger log = LoggerFactory.getLogger(MargToTextrImporter.class);
    private static final Map<String, BxZoneLabel> ZONE_LABEL_MAP = new HashMap<String, BxZoneLabel>();

    static {
        ZONE_LABEL_MAP.put("abstract", BxZoneLabel.MET_ABSTRACT);
        ZONE_LABEL_MAP.put("affiliation", BxZoneLabel.MET_AFFILIATION);
        ZONE_LABEL_MAP.put("author", BxZoneLabel.MET_AUTHOR);
        ZONE_LABEL_MAP.put("title", BxZoneLabel.MET_TITLE);
    }

    public List<BxPage> read(String string, Object... hints) throws TransformationException {
        return read(new StringReader(string), hints);
    }

    public List<BxPage> read(Reader reader, Object... hints) throws TransformationException {
        List<BxPage> pages = new ArrayList<BxPage>();
        try {
            pages.add(importSource(new InputSource(reader)));
        } catch (IOException ex) {
            throw new TransformationException(ex);
        } catch (ParserConfigurationException ex) {
            throw new TransformationException(ex);
        } catch (SAXException ex) {
            throw new TransformationException(ex);
        }
        return pages;
    }

    private static class ComparablePair<X extends Comparable, Y extends Comparable> implements Comparable {

        private X o1;
        private Y o2;

        public X getO1() {
            return o1;
        }

        public void setO1(X o1) {
            this.o1 = o1;
        }

        public Y getO2() {
            return o2;
        }

        public void setO2(Y o2) {
            this.o2 = o2;
        }

        public ComparablePair(X o1, Y o2) {
            this.o1 = o1;
            this.o2 = o2;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final ComparablePair<X, Y> other = (ComparablePair<X, Y>) obj;
            if (this.o1 != other.o1 && (this.o1 == null || !this.o1.equals(other.o1))) {
                return false;
            }
            if (this.o2 != other.o2 && (this.o2 == null || !this.o2.equals(other.o2))) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 47 * hash + (this.o1 != null ? this.o1.hashCode() : 0);
            hash = 47 * hash + (this.o2 != null ? this.o2.hashCode() : 0);
            return hash;
        }

        @Override
        public int compareTo(Object o) {
            if (o == null) {
                throw new NullPointerException();
            }
            if (getClass() != o.getClass()) {
                throw new UnsupportedOperationException("different classes");
            }
            final ComparablePair<X, Y> other = (ComparablePair<X, Y>) o;
            if (this.o1 != other.o1 && (this.o1 == null || !this.o1.equals(other.o1))) {
                if (this.o1 == null) {
                    return -1;
                }
                return this.o1.compareTo(other.o1);
            }
            if (this.o2 != other.o2 && (this.o2 == null || !this.o2.equals(other.o2))) {
                if (this.o2 == null) {
                    return -1;
                }
                return this.o2.compareTo(other.o2);
            }
            return 0;
        }
    }

    private List<Element> getChildren(String name, Element el) {
        ArrayList<Element> list = new ArrayList<Element>();
        NodeList nl = el.getChildNodes();
        for (int i = 0; i < nl.getLength(); i++) {
            Node n = nl.item(i);
            if (n instanceof Element) {
                Element e = (Element) n;
                if (e.getTagName().equalsIgnoreCase(name)) {
                    list.add(e);
                }
            }
        }
        return list;
    }

    private BxBounds parseElementContainingVertexes(Element el) {
        List<Element> vs = getChildren("Vertex", el);
        if (vs.isEmpty()) {
            return null;
        }
        List<ComparablePair<Integer, Integer>> list = new ArrayList<ComparablePair<Integer, Integer>>();
        int minx = Integer.MAX_VALUE;
        int maxx = Integer.MIN_VALUE;
        int miny = Integer.MAX_VALUE;
        int maxy = Integer.MIN_VALUE;
        for (Element v : vs) {
            int x = Integer.parseInt(v.getAttribute("x"));
            if (x < minx) {
                minx = x;
            }
            if (x > maxx) {
                maxx = x;
            }
            int y = Integer.parseInt(v.getAttribute("y"));
            if (y < miny) {
                miny = y;
            }
            if (y > maxy) {
                maxy = y;
            }
            list.add(new ComparablePair<Integer, Integer>(x, y));
        }
        Collections.sort(list);
        BxBounds ret = new BxBounds(minx, miny, maxx - minx, maxy - miny);
        if (ret.getHeight() == 0 || ret.getWidth() == 0) {
            log.warn("problems with height or width points are:");
            for (ComparablePair<Integer, Integer> pa : list) {
                log.warn("\t" + pa.o1 + " , " + pa.o2);

            }
        }
        return ret;
    }

    private BxChunk parseCharacterElement(Element charE) {
        BxBounds bou = null;
        String text = null;
        if (!getChildren("CharacterCorners", charE).isEmpty()) {
            bou = (parseElementContainingVertexes(getChildren("CharacterCorners", charE).get(0)));
        }
        if (!(getChildren("GT_Text", charE).isEmpty())) {
            text = getChildren("GT_Text", charE).get(0).getAttribute("Value");
        }
        return new BxChunk(bou, text);
    }

    private BxWord parseWordElement(Element wordE) {
        BxWord word = new BxWord();
        if (!(getChildren("WordCorners", wordE).isEmpty())) {
            word.setBounds(parseElementContainingVertexes(getChildren("WordCorners", wordE).get(0)));
        }

        List<Element> e = getChildren("Character", wordE);
        for (Element caE : e) {
            BxChunk ch = parseCharacterElement(caE);
            word.addChunks(ch);
        }
        return word;
    }

    private BxLine parseLineElement(Element lineE) {
        BxLine line = new BxLine();
        if (!(getChildren("LineCorners", lineE).isEmpty())) {
            line.setBounds(parseElementContainingVertexes(getChildren("LineCorners", lineE).get(0)));
        }
        List<Element> e = getChildren("Word", lineE);
        for (Element we : e) {
            BxWord wo = parseWordElement(we);
            line.addWord(wo);
        }
        return line;
    }

    private BxZoneLabel parseClassification(Element elClassicfication) {
        List<Element> eli = getChildren("Category", elClassicfication);
        Element catEl = eli.isEmpty() ? null : eli.get(0);
        if (catEl == null) {
            eli = getChildren("Type", elClassicfication);
            catEl = eli.isEmpty() ? null : eli.get(0);
        }
        if (catEl == null) {
            return null;
        }
        String val = catEl.getAttribute("Value");
        if (val == null) {
            return null;
        }
        if (ZONE_LABEL_MAP.containsKey(val.toLowerCase())) {
            return ZONE_LABEL_MAP.get(val.toLowerCase());
        }
        return BxZoneLabel.OTH_UNKNOWN;
    }

    private BxZone parseZoneNode(Element zoneE) {
        BxZone zone = new BxZone();
        if (!getChildren("Classification", zoneE).isEmpty()) {
            zone.setLabel(parseClassification(getChildren("Classification", zoneE).get(0)));
        }
        if (!getChildren("ZoneCorners", zoneE).isEmpty()) {
            zone.setBounds(parseElementContainingVertexes(getChildren("ZoneCorners", zoneE).get(0)));
        }
        List<Element> e = getChildren("Line", zoneE);
        for (Element lin : e) {
            BxLine li = parseLineElement(lin);
            zone.addLine(li);
        }
        return zone;

    }

    private BxPage parsePageNode(Element elem) {
        BxPage page = new BxPage();

        double minX = 0, minY = 0, maxX = 0, maxY = 0;
        boolean started = false;

        List<Element> e = getChildren("Zone", elem);
        for (Element zo : e) {
            BxZone zon = parseZoneNode(zo);
            page.addZone(zon);

            BxBounds zoneBounds = zon.getBounds();
            if (!started) {
                minX = zoneBounds.getX();
                minY = zoneBounds.getY();
                maxX = zoneBounds.getX() + zoneBounds.getWidth();
                maxY = zoneBounds.getY() + zoneBounds.getHeight();
                started = true;
            }

            if (zoneBounds.getX() < minX) {
                minX = zoneBounds.getX();
            }
            if (zoneBounds.getX() + zoneBounds.getWidth() > maxX) {
                maxX = zoneBounds.getX() + zoneBounds.getWidth();
            }
            if (zoneBounds.getY() < minY) {
                minY = zoneBounds.getY();
            }
            if (zoneBounds.getY() + zoneBounds.getHeight() > maxY) {
                maxY = zoneBounds.getY() + zoneBounds.getHeight();
            }
        }

        Collections.sort(page.getZones(), new Comparator() {

            @Override
            public int compare(Object t, Object t1) {
                BxZone z1 = (BxZone) t;
                BxZone z2 = (BxZone) t1;
                int ret = Double.compare(z1.getBounds().getY(), z2.getBounds().getY());
                if (ret == 0) {
                    ret = Double.compare(z1.getBounds().getX(), z2.getBounds().getX());
                }
                return ret;
            }
        });
        return page.setBounds(new BxBounds(minX, minY, maxX - minX, maxY - minY));
    }

    private BxPage importSource(InputSource source) throws IOException, ParserConfigurationException, SAXException {
        Document doc = TrueVizUtils.newDocumentBuilder().parse(source);

        if ("Page".equalsIgnoreCase(doc.getDocumentElement().getTagName())) {
            return parsePageNode(doc.getDocumentElement());
        }

        throw new UnsupportedDataTypeException("There were no example of this type contact kura for more info");
    }
}
