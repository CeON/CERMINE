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

import java.util.*;
import org.jdom.Element;
import pl.edu.icm.cermine.bibref.model.BibEntry;
import pl.edu.icm.cermine.bibref.model.BibEntryField;
import pl.edu.icm.cermine.exception.TransformationException;
import pl.edu.icm.cermine.tools.XMLTools;
import pl.edu.icm.cermine.tools.transformers.ModelToModelConverter;

/**
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class BibEntryToNLMConverter implements ModelToModelConverter<BibEntry, Element> {

    private static final Map<String, String> BIBENTRY_TO_NLM = new HashMap<String, String>();

    static {
        BIBENTRY_TO_NLM.put(BibEntry.FIELD_TITLE,     "article-title");
        BIBENTRY_TO_NLM.put(BibEntry.FIELD_CONTENTS,  "named-content");
        BIBENTRY_TO_NLM.put(BibEntry.FIELD_EDITION,   "edition");
        BIBENTRY_TO_NLM.put(BibEntry.FIELD_PUBLISHER, "publisher-name");
        BIBENTRY_TO_NLM.put(BibEntry.FIELD_LOCATION,  "publisher-loc");
        BIBENTRY_TO_NLM.put(BibEntry.FIELD_SERIES,    "series");
        BIBENTRY_TO_NLM.put(BibEntry.FIELD_JOURNAL,   "source");
        BIBENTRY_TO_NLM.put(BibEntry.FIELD_URL,       "uri");
        BIBENTRY_TO_NLM.put(BibEntry.FIELD_VOLUME,    "volume");
        BIBENTRY_TO_NLM.put(BibEntry.FIELD_YEAR,      "year");
        BIBENTRY_TO_NLM.put(BibEntry.FIELD_NUMBER,    "issue");
    }
   

    @Override
    public Element convert(BibEntry entry, Object... hints) throws TransformationException {
        Element element = new Element("mixed-citation");
     
        Map<BibEntryField, String> fieldKeyMap = new HashMap<BibEntryField, String>();
        
        String text = entry.getText();
        List<BibEntryField> fields = new ArrayList<BibEntryField>();
        for (String key : entry.getFieldKeys()) {
            fields.addAll(entry.getAllFields(key));
            for (BibEntryField field : entry.getAllFields(key)) {
                fieldKeyMap.put(field, key);
            }
        }
        Collections.sort(fields, new Comparator<BibEntryField>() {

            @Override
            public int compare(BibEntryField t1, BibEntryField t2) {
                return Integer.valueOf(t1.getStartIndex()).compareTo(t2.getStartIndex());
            }
        
        });
        
        int lastIndex = 0;
        for (BibEntryField field : fields) {
            if (field.getStartIndex() < 0) {
                continue;
            }

            String fieldText = text.substring(field.getStartIndex(), field.getEndIndex());
            if (field.getStartIndex() != lastIndex) {
                element.addContent(XMLTools.removeInvalidXMLChars(text.substring(lastIndex, field.getStartIndex())));
            }
            
            if (BIBENTRY_TO_NLM.get(fieldKeyMap.get(field)) != null) {
                Element fieldElement = new Element(BIBENTRY_TO_NLM.get(fieldKeyMap.get(field)));
                fieldElement.setText(XMLTools.removeInvalidXMLChars(fieldText));
                element.addContent(fieldElement);
            } else if (BibEntry.FIELD_PAGES.equals(fieldKeyMap.get(field))) {
                if (!field.getText().contains("--")) {
                    Element firstPageElement = new Element("fpage");
                    firstPageElement.setText(XMLTools.removeInvalidXMLChars(field.getText()));
                    element.addContent(firstPageElement);
                } else {
                    String firstPage = field.getText().replaceAll("--.*", "");
                    String lastPage = field.getText().replaceAll(".*--", "");

                    int firstPageIndex = fieldText.indexOf(firstPage);
                    int lastPageIndex = fieldText.indexOf(lastPage, firstPageIndex + firstPage.length());
                    
                    element.addContent(XMLTools.removeInvalidXMLChars(fieldText.substring(0, firstPageIndex)));
                
                    Element firstPageElement = new Element("fpage");
                    firstPageElement.setText(XMLTools.removeInvalidXMLChars(firstPage));
                    element.addContent(firstPageElement);
                
                    element.addContent(XMLTools.removeInvalidXMLChars(fieldText.substring(firstPageIndex + firstPage.length(), lastPageIndex)));
                
                    Element lastPageElement = new Element("lpage");
                    lastPageElement.setText(XMLTools.removeInvalidXMLChars(lastPage));
                    element.addContent(lastPageElement);
                }
            } else if (BibEntry.FIELD_AUTHOR.equals(fieldKeyMap.get(field))) {
                if (!field.getText().contains(", ")) {
                    Element nameElement = new Element("string-name");
                    Element firstElement = new Element("surname");
                    firstElement.setText(XMLTools.removeInvalidXMLChars(field.getText()));
                    nameElement.addContent(firstElement);
                    element.addContent(nameElement);
                } else {               
                    String surname = field.getText().replaceAll(", .*", "");
                    String givenname = field.getText().replaceAll(".*, ", "");
                    int surnameIndex = fieldText.indexOf(surname);
                    int givennameIndex = fieldText.indexOf(givenname, surnameIndex + surname.length());
                    if (givennameIndex < 0) {
                        givennameIndex = fieldText.indexOf(givenname);
                        surnameIndex = fieldText.indexOf(surname, givennameIndex + givenname.length());
                    }
                
                    String firstText = surname;
                    String firstLabel = "surname";
                    int firstIndex = surnameIndex;
                    String secondText = givenname;
                    String secondLabel = "given-names";
                    int secondIndex = givennameIndex;
                
                    if (secondIndex < firstIndex) {
                        firstText = givenname;
                        firstLabel = "given-names";
                        firstIndex = givennameIndex;
                        secondText = surname;
                        secondLabel = "surname";
                        secondIndex = surnameIndex;
                    }

                    Element nameElement = new Element("string-name");
                    
                    nameElement.addContent(XMLTools.removeInvalidXMLChars(fieldText.substring(0, firstIndex)));
                
                    Element firstElement = new Element(firstLabel);
                    firstElement.setText(XMLTools.removeInvalidXMLChars(firstText));
                    nameElement.addContent(firstElement);
                
                    nameElement.addContent(XMLTools.removeInvalidXMLChars(fieldText.substring(firstIndex + firstText.length(), secondIndex)));
                
                    Element lastElement = new Element(secondLabel);
                    lastElement.setText(XMLTools.removeInvalidXMLChars(secondText));
                    nameElement.addContent(lastElement);
                
                    nameElement.addContent(XMLTools.removeInvalidXMLChars(fieldText.substring(secondIndex + secondText.length(), fieldText.length())));
                    element.addContent(nameElement);
                }
            }
            lastIndex = field.getEndIndex();
        }
        if (lastIndex < text.length()) {
            element.addContent(XMLTools.removeInvalidXMLChars(text.substring(lastIndex, text.length())));
        }
       
        return element;
    }

    @Override
    public List<Element> convertAll(List<BibEntry> source, Object... hints) throws TransformationException {
        List<Element> elements = new ArrayList<Element>(source.size());
        for (BibEntry entry: source) {
            elements.add(convert(entry, hints));
        }
        return elements;
    }
    
}
