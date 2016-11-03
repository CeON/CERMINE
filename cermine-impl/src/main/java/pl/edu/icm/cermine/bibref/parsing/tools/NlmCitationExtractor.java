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

package pl.edu.icm.cermine.bibref.parsing.tools;

import java.io.IOException;
import java.util.*;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Text;
import org.jdom.filter.Filter;
import org.jdom.input.SAXBuilder;
import org.xml.sax.InputSource;
import pl.edu.icm.cermine.bibref.parsing.model.Citation;
import pl.edu.icm.cermine.bibref.parsing.model.CitationToken;
import pl.edu.icm.cermine.bibref.parsing.model.CitationTokenLabel;

/**
 * Citation extractor from NLM xml-s.
 *
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public final class NlmCitationExtractor {

    public static final List<String> TAGS_CITATION = Arrays.asList(
            "mixed-citation", "citation", "element-citation");

    public static final String KEY_TEXT = "text";

    private static final Map<String, CitationTokenLabel> TAGS_LABEL_MAP = new HashMap<String, CitationTokenLabel>();

    static {
        TAGS_LABEL_MAP.put("article-title",   CitationTokenLabel.ARTICLE_TITLE);
        TAGS_LABEL_MAP.put("conf-name",       CitationTokenLabel.CONF);
        TAGS_LABEL_MAP.put("named-content",   CitationTokenLabel.CONTENT);
        TAGS_LABEL_MAP.put("edition",         CitationTokenLabel.TEXT);
        TAGS_LABEL_MAP.put("given-names",     CitationTokenLabel.GIVENNAME);
        TAGS_LABEL_MAP.put("issue",           CitationTokenLabel.ISSUE);
        TAGS_LABEL_MAP.put("fpage",           CitationTokenLabel.PAGEF);
        TAGS_LABEL_MAP.put("lpage",           CitationTokenLabel.PAGEL);
        TAGS_LABEL_MAP.put("publisher-loc",   CitationTokenLabel.TEXT);
        TAGS_LABEL_MAP.put("publisher-name",  CitationTokenLabel.TEXT);
        TAGS_LABEL_MAP.put("sc",              CitationTokenLabel.SC);
        TAGS_LABEL_MAP.put("series",          CitationTokenLabel.SERIES);
        TAGS_LABEL_MAP.put("source",          CitationTokenLabel.SOURCE);
        TAGS_LABEL_MAP.put("surname",         CitationTokenLabel.SURNAME);
        TAGS_LABEL_MAP.put("text",            CitationTokenLabel.TEXT);
        TAGS_LABEL_MAP.put("uri",             CitationTokenLabel.URI);
        TAGS_LABEL_MAP.put("volume",          CitationTokenLabel.VOLUME);
        TAGS_LABEL_MAP.put("volume-series",   CitationTokenLabel.VOLUME_SERIES);
        TAGS_LABEL_MAP.put("year",            CitationTokenLabel.YEAR);
    }

    private NlmCitationExtractor() {}
    
    public static List<Citation> extractCitations(InputSource source) throws JDOMException, IOException {
        SAXBuilder builder = new SAXBuilder("org.apache.xerces.parsers.SAXParser");
        builder.setValidation(false);
        builder.setFeature("http://xml.org/sax/features/validation", false);
        builder.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
        builder.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        Document doc = builder.build(source);

        Iterator mixedCitations = doc.getDescendants(new Filter() {

            @Override
            public boolean matches(Object object) {
                return object instanceof Element && TAGS_CITATION.contains(((Element) object).getName());
            }
        });

        List<Citation> citationSet = new ArrayList<Citation>();

        while (mixedCitations.hasNext()) {
            Citation citation = new Citation();
            readElement((Element) mixedCitations.next(), citation);
            citationSet.add(citation);
        }
        return citationSet;
    }

    private static void readElement(Element element, Citation citation) {
        for (Object content : element.getContent()) {
            if (content instanceof Text) {
                String contentText = ((Text) content).getText();
                if (!contentText.matches("^[\\s]*$")) {
                    for (CitationToken token : CitationUtils.stringToCitation(contentText).getTokens()) {
                        token.setStartIndex(token.getStartIndex() + citation.getText().length());
                        token.setEndIndex(token.getEndIndex() + citation.getText().length());
                        token.setLabel(TAGS_LABEL_MAP.get(KEY_TEXT));
                        citation.addToken(token);
                    }
                    citation.appendText(contentText);
                } else {
                    citation.appendText(" ");
                }
            } else if (content instanceof Element) {
                Element contentElement = (Element) content;
                String contentElementName = contentElement.getName();
                if (TAGS_LABEL_MAP.containsKey(contentElementName)) {
                    for (CitationToken token : CitationUtils.stringToCitation(contentElement.getValue()).getTokens()) {
                        token.setStartIndex(token.getStartIndex() + citation.getText().length());
                        token.setEndIndex(token.getEndIndex() + citation.getText().length());
                        token.setLabel(TAGS_LABEL_MAP.get(contentElementName));
                        citation.addToken(token);
                    }
                    citation.appendText(contentElement.getValue());
                } else {
                    readElement(contentElement, citation);
                }
            }
        }
    }
    
}
