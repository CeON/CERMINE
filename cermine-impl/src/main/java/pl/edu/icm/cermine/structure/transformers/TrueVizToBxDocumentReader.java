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

import com.google.common.collect.Lists;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import pl.edu.icm.cermine.exception.TransformationException;
import pl.edu.icm.cermine.structure.model.*;
import pl.edu.icm.cermine.structure.tools.BxBoundsBuilder;
import pl.edu.icm.cermine.structure.tools.BxModelUtils;

/**
 * Reads BxDocument model pages from TrueViz format.
 *
 * @author Kuba Jurkiewicz
 * @author Krzysztof Rusek
 * @author Pawel Szostek
 */
public class TrueVizToBxDocumentReader {

    //set while parsing xml
    private boolean areIdsSet;
    public static final Map<String, BxZoneLabel> ZONE_LABEL_MAP = new HashMap<String, BxZoneLabel>();

    static {
        ZONE_LABEL_MAP.put("abstract", BxZoneLabel.MET_ABSTRACT);
        ZONE_LABEL_MAP.put("access_data", BxZoneLabel.MET_ACCESS_DATA);
        ZONE_LABEL_MAP.put("acknowledgment", BxZoneLabel.BODY_ACKNOWLEDGMENT);
        ZONE_LABEL_MAP.put("affiliation", BxZoneLabel.MET_AFFILIATION);
        ZONE_LABEL_MAP.put("attachment", BxZoneLabel.BODY_ATTACHMENT);
        ZONE_LABEL_MAP.put("author", BxZoneLabel.MET_AUTHOR);
        ZONE_LABEL_MAP.put("author_title", BxZoneLabel.MET_TITLE);
        ZONE_LABEL_MAP.put("bib_info", BxZoneLabel.MET_BIB_INFO);
        ZONE_LABEL_MAP.put("biography", BxZoneLabel.MET_BIOGRAPHY);
        ZONE_LABEL_MAP.put("body", BxZoneLabel.BODY_CONTENT);
        ZONE_LABEL_MAP.put("body_content", BxZoneLabel.BODY_CONTENT);
        ZONE_LABEL_MAP.put("category", BxZoneLabel.MET_CATEGORY);
        ZONE_LABEL_MAP.put("contribution", BxZoneLabel.BODY_CONTRIBUTION);
        ZONE_LABEL_MAP.put("conflict_statement", BxZoneLabel.BODY_CONFLICT_STMT);
        ZONE_LABEL_MAP.put("copyright", BxZoneLabel.MET_COPYRIGHT);
        ZONE_LABEL_MAP.put("correspondence", BxZoneLabel.MET_CORRESPONDENCE);
        ZONE_LABEL_MAP.put("dates", BxZoneLabel.MET_DATES);
        ZONE_LABEL_MAP.put("editor", BxZoneLabel.MET_EDITOR);
        ZONE_LABEL_MAP.put("equation", BxZoneLabel.BODY_EQUATION);
        ZONE_LABEL_MAP.put("equation_label", BxZoneLabel.BODY_EQUATION_LABEL);
        ZONE_LABEL_MAP.put("figure", BxZoneLabel.BODY_FIGURE);
        ZONE_LABEL_MAP.put("figure_caption", BxZoneLabel.BODY_FIGURE_CAPTION);
        ZONE_LABEL_MAP.put("glossary", BxZoneLabel.BODY_GLOSSARY);
        ZONE_LABEL_MAP.put("junk", BxZoneLabel.BODY_JUNK);
        ZONE_LABEL_MAP.put("heading", BxZoneLabel.BODY_HEADING);
        ZONE_LABEL_MAP.put("keywords", BxZoneLabel.MET_KEYWORDS);
        ZONE_LABEL_MAP.put("page_number", BxZoneLabel.OTH_PAGE_NUMBER);
        ZONE_LABEL_MAP.put("references", BxZoneLabel.REFERENCES);
        ZONE_LABEL_MAP.put("table", BxZoneLabel.BODY_TABLE);
        ZONE_LABEL_MAP.put("table_caption", BxZoneLabel.BODY_TABLE_CAPTION);
        ZONE_LABEL_MAP.put("terms", BxZoneLabel.MET_TERMS);
        ZONE_LABEL_MAP.put("title", BxZoneLabel.MET_TITLE);
        ZONE_LABEL_MAP.put("type", BxZoneLabel.MET_TYPE);
        ZONE_LABEL_MAP.put("unknown", BxZoneLabel.OTH_UNKNOWN);
    }

    public List<BxPage> read(String string, Object... hints) throws TransformationException {
        return read(new StringReader(string), hints);
    }

    public List<BxPage> read(Reader reader, Object... hints) throws TransformationException {
        try {
            areIdsSet = true;
            Document doc = TrueVizUtils.newDocumentBuilder().parse(new InputSource(reader));
            List<BxPage> pages = new ArrayList<BxPage>();

            if ("Page".equalsIgnoreCase(doc.getDocumentElement().getTagName())) {
                pages.add(parsePageNode(doc.getDocumentElement()));
            } else if ("Document".equalsIgnoreCase(doc.getDocumentElement().getTagName())) {
                for (Element pageElement : getChildren("Page", doc.getDocumentElement())) {
                    BxPage page = parsePageNode(pageElement);
                    pages.add(page);
                }
            }
            setIdsAndLinkPages(pages);
            if (areIdsSet) {
                linkAndReorderOtherElements(pages);
            }
            for (BxPage page : pages) {
                BxModelUtils.setParents(page);
            }
            return pages;
        } catch (IOException ex) {
        	System.err.println(ex.getMessage());
            throw new TransformationException(ex);
        } catch (ParserConfigurationException ex) {
        	System.err.println(ex.getMessage());
            throw new TransformationException(ex);
        } catch (SAXException ex) {
        	System.err.println(ex.getMessage());
            throw new TransformationException(ex);
        }
    }

    protected <A extends Indexable<A>> List<A> reorderList(List<A> list) {
    	if(list.isEmpty()) {
    		return list;
    	}
        Map<String, A> elems = new HashMap<String, A>();
        List<A> ordered = new ArrayList<A>(list.size());
        for (A elem : list) {
            elems.put(elem.getId(), elem);
        }

        A start = null;
        for (A elem : list) {
            if (elem.getPrev() == null) {//first element at all
                start = elem;
                break;
            }
        }
        //maybe we are somewhere in the middle of the document
        if(start == null) {
        	for(A elem : list) {
        		if (!elems.keySet().contains(elem.getPrev().getId())) {
        			start = elem;
        			break;
        		}
        	}
        }
        //there is not previous element..
        if (start == null) {
        	for(A elem : list) {
        		System.out.println(elem.getPrev());
        	}
            throw new IllegalStateException("Start element not found");
        }
        do {
            ordered.add(start);
            if (!start.hasNext()) { //last element at all
                break;
            }
            start = start.getNext();
        } while (elems.keySet().contains(start.getId()));

        if (ordered.size() != list.size()) {
            throw new IllegalStateException("Output list size doesn't match the input one: " + ordered.size() + " " + list.size());
        }
        return ordered;
    }

    /**
     * A generic function for linking objects together (setting *Next and *Prev) It is a assumed, that all Id's and
     * NextId's are set before.
     *
     * @param list is a list of elements to be connected
     */
    private <A extends Indexable<A>> void linkGenericImpl(List<A> list) {
        Map<String, A> indicesMap = new HashMap<String, A>();
        for (A elem : list) {
            indicesMap.put(elem.getId(), elem);
        }
        for (A elem : list) {
            String nextId = elem.getNextId();
            if (nextId.equals("-1") || list.indexOf(elem) == list.size()-1) { /*
                 * there is no next element
                 */
                elem.setNext(null);
            } else {
                A next = indicesMap.get(nextId);
                if (next == null) {
                    throw new RuntimeException("No matching element found for \"" + nextId + "\"");
                }
                //link with the next element
                elem.setNext(next);
                //link with the previous element
                next.setPrev(elem);
            }
        }
    }

    /*
     * assumes that nextIds are set for all objects
     */
    private void linkAndReorderOtherElements(List<BxPage> pages) {
        BxDocument temp = new BxDocument();
        temp.setPages(pages);
        linkGenericImpl(Lists.newArrayList(temp.asZones()));
        linkGenericImpl(Lists.newArrayList(temp.asLines()));
        linkGenericImpl(Lists.newArrayList(temp.asWords()));
        linkGenericImpl(Lists.newArrayList(temp.asChunks()));
        for (BxPage page : pages) {
            for (BxZone zone : page) {
                for (BxLine line : zone) {
                    for (BxWord word : line) {
                        word.setChunks(reorderList(Lists.newArrayList(word)));
                    }
                    line.setWords(reorderList(Lists.newArrayList(line)));
                }
                zone.setLines(reorderList(Lists.newArrayList(zone)));
            }
            page.setZones(reorderList(Lists.newArrayList(page)));
        }
    }

    private void setIdsAndLinkPages(List<BxPage> pages) {
        if (pages.isEmpty()) {
            return;
        }
        if (pages.size() == 1) {
            BxPage page = pages.get(0);
            page.setId("0");
            page.setNextId("-1");
            page.setNext(null);
            page.setPrev(null);
            return;
        }
        boolean arePageIdsSet = true;
        for (BxPage page : pages) {
            if (page.getNextId() == null || page.getId() == null) {
                arePageIdsSet = false;
                break;
            }
        }
        if (arePageIdsSet) { /*
             * Page IDs were set in the input file
             */
            linkGenericImpl(pages);
        } else { /*
             * Page IDs were not set. We have to do it on our own
             */
            Integer idx;
            for (idx = 0; idx < pages.size() - 1; ++idx) {
                pages.get(idx).setId(Integer.toString(idx));
                pages.get(idx).setNextId(Integer.toString(idx + 1));
            }
            pages.get(pages.size() - 1).setId(Integer.toString(idx));
            pages.get(pages.size() - 1).setNextId("-1");
            linkGenericImpl(pages);
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

    /**
     * Function for obtaining value for optional children (that can appear in the XML, but doesn't have to).
     *
     * @param name is a name of the node
     * @param el is the root node for the child
     * @return value of the child, if present and not empty. Otherwise equals to null
     */
    private String getOptionalChildValue(String name, Element el) {
        List<Element> children = getChildren(name, el);
        if (!children.isEmpty()) {
            String val = children.get(0).getAttribute("Value");
            if (val.equals("")) {
                return null;
            } else {
                return val;
            }
        } else {
            return null;
        }

    }

    private BxBounds parseElementContainingVertexes(Element el) {
        List<Element> vs = getChildren("Vertex", el);
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
        if (!getChildren("CharacterCorners", charE).isEmpty()) {
            bou = (parseElementContainingVertexes(getChildren("CharacterCorners", charE).get(0)));
        }
        if (!(getChildren("GT_Text", charE).isEmpty())) {
            text = getChildren("GT_Text", charE).get(0).getAttribute("Value");
        }

        BxChunk chunk = new BxChunk(bou, text);
        chunk.setId(getOptionalChildValue("CharacterId", charE));
        chunk.setNextId(getOptionalChildValue("CharacterNext", charE));

        List<Element> fonts = getChildren("Font", charE);
        if (!fonts.isEmpty()) {
            chunk.setFontName(fonts.get(0).getAttribute("Type"));
        }
        
        if (areIdsSet && (chunk.getId() == null || chunk.getNextId() == null)) {
            areIdsSet = false;
        }

        return chunk;
    }

    private BxWord parseWordElement(Element wordE) {
        BxWord word = new BxWord();
        if (!(getChildren("WordCorners", wordE).isEmpty())) {
            word.setBounds(parseElementContainingVertexes(getChildren("WordCorners", wordE).get(0)));
        }

        word.setId(getOptionalChildValue("WordId", wordE));
        word.setNextId(getOptionalChildValue("WordNext", wordE));

        if (areIdsSet && (word.getId() == null || word.getNextId() == null)) {
            areIdsSet = false;
        }

        List<Element> e = getChildren("Character", wordE);
        for (Element caE : e) {
            BxChunk ch = parseCharacterElement(caE);
            ch.setParent(word);
            word.addChunk(ch);
        }
        return word;
    }

    private BxLine parseLineElement(Element lineE) {
        BxLine line = new BxLine();
        if (!(getChildren("LineCorners", lineE).isEmpty())) {
            line.setBounds(parseElementContainingVertexes(getChildren("LineCorners", lineE).get(0)));
        }

        line.setId(getOptionalChildValue("LineId", lineE));
        line.setNextId(getOptionalChildValue("LineNext", lineE));

        if (areIdsSet && (line.getId() == null || line.getNextId() == null)) {
            areIdsSet = false;
        }

        List<Element> e = getChildren("Word", lineE);
        for (Element we : e) {
            BxWord wo = parseWordElement(we);
            wo.setParent(line);
            line.addWord(wo);
        }
        return line;
    }

    private BxZoneLabel parseClassification(Element elClassicfication) throws TransformationException {
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
        if (val.isEmpty()) {
            return null;
        }
        if (val.isEmpty()) {
        	return BxZoneLabel.OTH_UNKNOWN;
        }

        if (ZONE_LABEL_MAP.containsKey(val.toLowerCase())) {
            return ZONE_LABEL_MAP.get(val.toLowerCase());
        } else {
            return BxZoneLabel.valueOf(val.toUpperCase());
        }
    }

    private BxZone parseZoneNode(Element zoneE) throws TransformationException {
        BxZone zone = new BxZone();
        zone.setLabel(BxZoneLabel.OTH_UNKNOWN);
        if (!getChildren("Classification", zoneE).isEmpty()) {
            zone.setLabel(parseClassification(getChildren("Classification", zoneE).get(0)));
        }
        if (!getChildren("ZoneCorners", zoneE).isEmpty()) {
            zone.setBounds(parseElementContainingVertexes(getChildren("ZoneCorners", zoneE).get(0)));
        }

        zone.setId(getOptionalChildValue("ZoneId", zoneE));
        zone.setNextId(getOptionalChildValue("ZoneNext", zoneE));

        if (areIdsSet && (zone.getId() == null || zone.getNextId() == null)) {
            areIdsSet = false;
        }

        List<Element> e = getChildren("Line", zoneE);
        for (Element lin : e) {
            BxLine li = parseLineElement(lin);
            li.setParent(zone);
            zone.addLine(li);
        }
        return zone;

    }

    private BxPage parsePageNode(Element elem) throws TransformationException {
        BxPage page = new BxPage();
        page.setId(getOptionalChildValue("PageId", elem));
        page.setNextId(getOptionalChildValue("PageNext", elem));

        if (areIdsSet && (page.getId() == null || page.getNextId() == null)) {
            areIdsSet = false;
        }

        List<Element> e = getChildren("Zone", elem);
        for (Element zo : e) {
            BxZone zon = parseZoneNode(zo);
            zon.setParent(page);
            page.addZone(zon);
        }
        BxBoundsBuilder.setBounds(page);

        return page;
    }
}
