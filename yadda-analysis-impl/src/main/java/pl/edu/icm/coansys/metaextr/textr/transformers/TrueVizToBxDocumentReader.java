package pl.edu.icm.coansys.metaextr.textr.transformers;

import pl.edu.icm.coansys.metaextr.textr.model.BxLine;
import pl.edu.icm.coansys.metaextr.textr.model.BxZone;
import pl.edu.icm.coansys.metaextr.textr.model.BxChunk;
import pl.edu.icm.coansys.metaextr.textr.model.Indexable;
import pl.edu.icm.coansys.metaextr.textr.model.BxPage;
import pl.edu.icm.coansys.metaextr.textr.model.BxZoneLabel;
import pl.edu.icm.coansys.metaextr.textr.model.BxWord;
import pl.edu.icm.coansys.metaextr.textr.model.BxDocument;
import pl.edu.icm.coansys.metaextr.textr.model.BxBounds;
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
import pl.edu.icm.coansys.metaextr.TransformationException;
import pl.edu.icm.coansys.metaextr.textr.tools.BxBoundsBuilder;

/**
 * Reads BxDocument model pages from TrueViz format.
 *
 * @author kura
 * @author krusek
 * @author pszostek
 */
public class TrueVizToBxDocumentReader {
    
    private boolean areIdsSet;

    public static final Map<String, BxZoneLabel> zoneLabelMap = new HashMap<String, BxZoneLabel>();
    static {
        zoneLabelMap.put("abstract",        BxZoneLabel.MET_ABSTRACT);
        zoneLabelMap.put("affiliation",     BxZoneLabel.MET_AFFILIATION);
        zoneLabelMap.put("author",          BxZoneLabel.MET_AUTHOR);
        zoneLabelMap.put("bib_info",        BxZoneLabel.MET_BIB_INFO);
        zoneLabelMap.put("body",            BxZoneLabel.BODY_CONTENT);
        zoneLabelMap.put("copyright",       BxZoneLabel.OTH_COPYRIGHT);
        zoneLabelMap.put("correspondence",  BxZoneLabel.MET_CORRESPONDENCE);
        zoneLabelMap.put("dates",           BxZoneLabel.MET_DATES);
        zoneLabelMap.put("editor",          BxZoneLabel.MET_EDITOR);
        zoneLabelMap.put("equation",        BxZoneLabel.BODY_EQUATION);
        zoneLabelMap.put("equation_label",  BxZoneLabel.BODY_EQUATION_LABEL);
        zoneLabelMap.put("figure",          BxZoneLabel.BODY_FIGURE);
        zoneLabelMap.put("figure_caption",  BxZoneLabel.BODY_FIGURE_CAPTION);
        zoneLabelMap.put("footer",          BxZoneLabel.OTH_FOOTER);
        zoneLabelMap.put("header",          BxZoneLabel.OTH_HEADER);
        zoneLabelMap.put("keywords",        BxZoneLabel.MET_KEYWORDS);
        zoneLabelMap.put("page_number",     BxZoneLabel.OTH_PAGE_NUMBER);
        zoneLabelMap.put("references",      BxZoneLabel.REFERENCES);
        zoneLabelMap.put("table",           BxZoneLabel.BODY_TABLE);
        zoneLabelMap.put("table_caption",   BxZoneLabel.BODY_TABLE_CAPTION);
        zoneLabelMap.put("title",           BxZoneLabel.MET_TITLE);
        zoneLabelMap.put("type",            BxZoneLabel.MET_TYPE);
        zoneLabelMap.put("unknown",         BxZoneLabel.OTH_UNKNOWN);
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
            if(areIdsSet) {
            	linkOtherElements(pages);
            	for(BxPage page: pages)
            		page.setSorted(true);
            } else {
            	for(BxPage page: pages)
            		page.setSorted(false);
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

    /** A generic function for linking objects together (setting *Next and *Prev)
     * It is a assumed, that all Id's and NextId's are set before. 
     * @param list is a list of elements to be connected 
     */
	private <A extends Indexable<A>> void linkGenericImpl(List<A> list) {
		Map<String, A> indicesMap = new HashMap<String, A>();
		for (A elem : list)
			indicesMap.put(elem.getId(), elem);
		for (A elem : list) {
			String nextId = elem.getNextId();
			if (nextId.equals(new String("-1"))) { /* there is no next element */
				elem.setNext(null);
			} else {
				A next = indicesMap.get(nextId);
				if (next == null)
					throw new RuntimeException("No matching element found for \"" + nextId + "\"");
				//link with the next element
				elem.setNext(next);
				//link with the previous element
				next.setPrev(elem);
			}
		}
	}
	
    /* assumes that nextIds are set for all objects */
	private void linkOtherElements(List<BxPage> pages) {
		BxDocument temp = new BxDocument();
		temp.setPages(pages);
		linkGenericImpl(temp.asZones());
		linkGenericImpl(temp.asLines());
		linkGenericImpl(temp.asWords());
		linkGenericImpl(temp.asChunks());
	}

	private void setIdsAndLinkPages(List<BxPage> pages) {
		if(pages.size() == 0)
			return;
		if(pages.size() == 1) {
			BxPage page = pages.get(0);
			page.setId("0");
			page.setNextId("-1");
			page.setNext(null);
			page.setPrev(null);
			return;
		}
		boolean arePageIdsSet = true;
		for(BxPage page: pages) {
			if(page.getNextId() == null || page.getId() == null) {
				arePageIdsSet = false;
				break;	
			}
		}
		if(arePageIdsSet) { /* Page IDs were set in the input file */
			linkGenericImpl(pages);
		} else { /* Page IDs were not set. We have to do it on our own */
			Integer idx;
			for(idx = 0; idx < pages.size()-1; ++idx) {
				pages.get(idx).setId(Integer.toString(idx));
				pages.get(idx).setNextId(Integer.toString(idx+1));
			}
			pages.get(pages.size()-1).setId(Integer.toString(idx));
			pages.get(pages.size()-1).setNextId("-1");
			linkGenericImpl(pages);
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

    /** Function for obtaining value for optional children (that can appear in the XML,
     * but doesn't have to).
     * 
     * @param name is a name of the node
     * @param el is the root node for the child
     * @return value of the child, if present and not empty. Otherwise equals to null
     */
	private String getOptionalChildValue(String name, Element el) {
		List<Element> children = getChildren(name, el);
		if(children.size() != 0) {
			String val = children.get(0).getAttribute("Value");
			if(val.equals(new String("")))
				return null;
			else
				return val;
		} else {
			return null;
		}
		
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
        
        BxChunk chunk = new BxChunk(bou, text);
        chunk.setId(getOptionalChildValue("CharacterId", charE));
    	chunk.setNextId(getOptionalChildValue("CharacterNext", charE));
    	
    	if(areIdsSet && (chunk.getId() == null || chunk.getNextId() == null))
    		areIdsSet = false;
    	
        return chunk;
    }

    private BxWord parseWordElement(Element wordE) {
        BxWord word = new BxWord();
        if (!(getChildren("WordCorners",wordE).isEmpty())) {
            word.setBounds(parseElementContainingVertexes(getChildren("WordCorners",wordE).get(0)));
        }

        word.setId(getOptionalChildValue("WordId", wordE));
    	word.setNextId(getOptionalChildValue("WordNext", wordE));
    	
    	if(areIdsSet && (word.getId() == null || word.getNextId() == null))
    		areIdsSet = false;
    	
        List<Element> e = getChildren("Character",wordE);
        for (Element caE : e) {
            BxChunk ch = parseCharacterElement(caE);
            ch.setContext(word);
            word.addChunks(ch);
        }
        return word;
    }

    private BxLine parseLineElement(Element lineE) {
        BxLine line = new BxLine();
         if (!(getChildren("LineCorners",lineE).isEmpty()))  {
            line.setBounds(parseElementContainingVertexes(getChildren("LineCorners",lineE).get(0)));
        }
         
        line.setId(getOptionalChildValue("LineId", lineE));
     	line.setNextId(getOptionalChildValue("LineNext", lineE));

    	if(areIdsSet && (line.getId() == null || line.getNextId() == null))
    		areIdsSet = false;

        List<Element> e = getChildren("Word",lineE);
        for (Element we : e) {
            BxWord wo = parseWordElement(we);
            wo.setContext(line);
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
        
        if (zoneLabelMap.containsKey(val.toLowerCase())) {
            return zoneLabelMap.get(val.toLowerCase());
        } else {
        	throw new IllegalArgumentException("Illegal class value in input file: " + val);
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

        zone.setId(getOptionalChildValue("ZoneId", zoneE));
    	zone.setNextId(getOptionalChildValue("ZoneNext", zoneE));

    	if(areIdsSet && (zone.getId() == null || zone.getNextId() == null))
    		areIdsSet = false;

        List<Element> e = getChildren("Line",zoneE);
        for (Element lin : e) {
            BxLine li = parseLineElement(lin);
            li.setContext(zone);
            zone.addLine(li);
        }
        return zone;

    }

    private BxPage parsePageNode(Element elem) {
    	BxPage page = new BxPage();
    	page.setId(getOptionalChildValue("PageId", elem));
        page.setNextId(getOptionalChildValue("PageNext", elem));
    	
        if(areIdsSet && (page.getId() == null || page.getNextId() == null))
    		areIdsSet = false;
        
        List<Element> e = getChildren("Zone", elem);
        for (Element zo : e) {
            BxZone zon = parseZoneNode(zo);
            zon.setContext(page);
            page.addZone(zon);
        }
        BxBoundsBuilder.setBounds(page);
        
        return page;
    }
}
