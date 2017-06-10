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

package pl.edu.icm.cermine.bibref.transformers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jdom.Element;
import org.jdom.Text;
import pl.edu.icm.cermine.bibref.model.BibEntry;
import pl.edu.icm.cermine.bibref.model.BibEntryFieldType;
import pl.edu.icm.cermine.bibref.model.BibEntryType;
import pl.edu.icm.cermine.exception.TransformationException;
import pl.edu.icm.cermine.tools.transformers.ModelToModelConverter;

/**
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class NLMToBibEntryConverter implements ModelToModelConverter<Element, BibEntry> {

    private static final Map<String, BibEntryFieldType> NLM_TO_BIBENTRY = 
            new HashMap<String, BibEntryFieldType>();

    static {
        NLM_TO_BIBENTRY.put("article-title",    BibEntryFieldType.TITLE);
        NLM_TO_BIBENTRY.put("named-content",    BibEntryFieldType.CONTENTS);
        NLM_TO_BIBENTRY.put("edition",          BibEntryFieldType.EDITION);
        NLM_TO_BIBENTRY.put("publisher-name",   BibEntryFieldType.PUBLISHER);
        NLM_TO_BIBENTRY.put("publisher-loc",    BibEntryFieldType.LOCATION);
        NLM_TO_BIBENTRY.put("series",           BibEntryFieldType.SERIES);
        NLM_TO_BIBENTRY.put("source",           BibEntryFieldType.JOURNAL);
        NLM_TO_BIBENTRY.put("uri",              BibEntryFieldType.URL);
        NLM_TO_BIBENTRY.put("volume",           BibEntryFieldType.VOLUME);
        NLM_TO_BIBENTRY.put("year",             BibEntryFieldType.YEAR);
        NLM_TO_BIBENTRY.put("issue",            BibEntryFieldType.NUMBER);
    }
    
    @Override
    public BibEntry convert(Element source, Object... hints) throws TransformationException {
        BibEntry bibEntry = new BibEntry(BibEntryType.ARTICLE);
        String text = source.getValue().trim().replaceAll("\\s+", " ");
        bibEntry.setText(text);
      
        Map<Element, Integer> fIndex = new HashMap<Element, Integer>();
        Map<Element, Integer> lIndex = new HashMap<Element, Integer>();
        
        List content = source.getContent();
        int index = 0;
        for (Object child : content) {
            if (child instanceof Text) {
                String chText = ((Text) child).getTextNormalize();
                int oldLength = text.length();
                text = text.substring(chText.length()).trim();
                index += (oldLength - text.length());
            } else if (child instanceof Element) {
                Element elChild = (Element) child;
                fIndex.put(elChild, index);
                String chText = elChild.getValue().trim().replaceAll("\\s+", " ");
                lIndex.put(elChild, index + chText.length());
                int oldLength = text.length();
                text = text.substring(chText.length()).trim();
                index += (oldLength - text.length());
            } else {
                throw new TransformationException("Unknown element class found: "+child.getClass().getName());
            }
        }
        
        List children = source.getChildren();
        for (int i = 0; i < children.size(); i++) {
            Element child = (Element) children.get(i);
            if (NLM_TO_BIBENTRY.containsKey(child.getName())) {
                bibEntry.addField(NLM_TO_BIBENTRY.get(child.getName()), child.getTextNormalize(), fIndex.get(child), lIndex.get(child));
            } else if ("pub-id".equals(child.getName())) {
                if ("doi".equals(child.getAttributeValue("pub-id-type"))) {
                  bibEntry.addField(BibEntryFieldType.DOI, child.getTextNormalize(), fIndex.get(child), lIndex.get(child));
                } else if ("pmid".equals(child.getAttributeValue("pub-id-type"))) {
                  bibEntry.addField(BibEntryFieldType.PMID, child.getTextNormalize(), fIndex.get(child), lIndex.get(child));
                }
            } else if ("fpage".equals(child.getName())) {
                String pages = child.getText();
                int lastIndex = lIndex.get(child);
                if (i + 1 < children.size()) {
                    Element nextChild = (Element) children.get(i+1);
                    if ("lpage".equals(nextChild.getName())) {
                        pages += "--";
                        pages += nextChild.getText();
                        lastIndex = lIndex.get(nextChild);
                    }
                }
                bibEntry.addField(BibEntryFieldType.PAGES, pages, fIndex.get(child), lastIndex);
            } else if ("string-name".equals(child.getName())) {
                String name = child.getValue().trim().replaceAll("\\s+", " ");
                if (child.getChild("surname") != null) {
                    name = child.getChildTextNormalize("surname");
                    if (child.getChild("given-names") != null) {
                        name += ", ";
                        name += child.getChildTextNormalize("given-names");
                    }
                }
                bibEntry.addField(BibEntryFieldType.AUTHOR, name, fIndex.get(child), lIndex.get(child));
            }
        }
        
        return bibEntry;
    }

    @Override
    public List<BibEntry> convertAll(List<Element> source, Object... hints) throws TransformationException {
        List<BibEntry> entries = new ArrayList<BibEntry>(source.size());
        for (Element element : source) {
            entries.add(convert(element, hints));
        }
        return entries;
    }
    
}
