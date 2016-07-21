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

package pl.edu.icm.cermine.metadata.transformers;

import java.util.ArrayList;
import java.util.List;
import org.jdom.Element;
import pl.edu.icm.cermine.exception.TransformationException;
import pl.edu.icm.cermine.metadata.affiliation.tools.CountryISOCodeFinder;
import pl.edu.icm.cermine.metadata.model.*;
import pl.edu.icm.cermine.parsing.model.Token;
import pl.edu.icm.cermine.tools.XMLTools;
import pl.edu.icm.cermine.tools.transformers.ModelToModelConverter;

/**
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class MetadataToNLMConverter implements ModelToModelConverter<DocumentMetadata, Element> {

    @Override
    public Element convert(DocumentMetadata source, Object... hints) throws TransformationException {
        Element metadata = new Element(TAG_ARTICLE);
        Element front = new Element(TAG_FRONT);
        front.addContent(convertJournalMetadata(source));
        front.addContent(convertArticleMetadata(source));
        metadata.addContent(front);
        return metadata;
    }

    @Override
    public List<Element> convertAll(List<DocumentMetadata> source, Object... hints) throws TransformationException {
        List<Element> elements = new ArrayList<Element>(source.size());
        for (DocumentMetadata metadata : source) {
            elements.add(convert(metadata));
        }
        return elements;
    }
    
    private Element convertJournalMetadata(DocumentMetadata source) {
        Element metadata = new Element(TAG_JOURNAL_META);

        String[] journalPath = new String[]{TAG_JOURNAL_TITLE_GROUP, TAG_JOURNAL_TITLE};
        addElement(metadata, journalPath, source.getJournal());
        addElement(metadata, TAG_ISSN, source.getJournalISSN(), TAG_PUB_TYPE, ATTR_VALUE_PUB_TYPE_PPUB);
        String[] publisherPath = new String[]{TAG_PUBLISHER, TAG_PUBLISHER_NAME};
        addElement(metadata, publisherPath, source.getPublisher());
       
        return metadata;
    }
    
    private Element convertArticleMetadata(DocumentMetadata source) throws TransformationException {
        Element metadata = new Element(TAG_ARTICLE_META);

        String[] titlePath = new String[]{TAG_TITLE_GROUP, TAG_ARTICLE_TITLE};
        addElement(metadata, titlePath, source.getTitle());
        
        for (String idType : source.getIdTypes()) {
            addElement(metadata, TAG_ARTICLE_ID, source.getId(idType), ATTR_PUB_ID_TYPE, idType);
        }

        if (!source.getAuthors().isEmpty() || !source.getEditors().isEmpty()
                || !source.getAffiliations().isEmpty()) {
            Element contributors = new Element(TAG_CONTRIB_GROUP);
            for (DocumentAuthor author : source.getAuthors()) {
                contributors.addContent(convertAuthor(author));
            }
            for (DocumentAuthor editor : source.getEditors()) {
                contributors.addContent(convertEditor(editor));
            }
            for (DocumentAffiliation affiliation : source.getAffiliations()) {
                contributors.addContent(convertAffiliation(affiliation));
            }
            metadata.addContent(contributors);
        }
       
        String[] abstractPath = new String[]{TAG_ABSTRACT, TAG_P};
        addElement(metadata, abstractPath, source.getAbstrakt());
       
        if (!source.getKeywords().isEmpty()) {
            Element keywords = new Element(TAG_KWD_GROUP);
            for (String keyword : source.getKeywords()) {
                addElement(keywords, TAG_KWD, keyword);
            }
            metadata.addContent(keywords);
        }

        addElement(metadata, TAG_VOLUME, source.getVolume());
        addElement(metadata, TAG_ISSUE, source.getIssue());
        addElement(metadata, TAG_FPAGE, source.getFirstPage());
        addElement(metadata, TAG_LPAGE, source.getLastPage());
        
        DocumentDate pubDate = source.getDate(DocumentDate.DATE_PUBLISHED);
        if (pubDate != null) {
            metadata.addContent(convertDate(TAG_PUB_DATE, DocumentDate.DATE_PUBLISHED, pubDate));
        }
        
        DocumentDate accDate = source.getDate(DocumentDate.DATE_ACCEPTED);
        DocumentDate recDate = source.getDate(DocumentDate.DATE_RECEIVED);
        DocumentDate revDate = source.getDate(DocumentDate.DATE_REVISED);
        if (accDate != null || recDate != null || revDate != null) {
            Element history = new Element(TAG_HISTORY);
            if (accDate != null) {
                history.addContent(convertDate(TAG_DATE, ATTR_VALUE_DATE_ACCEPTED, accDate));
            }
            if (recDate != null) {
                history.addContent(convertDate(TAG_DATE, ATTR_VALUE_DATE_TYPE_RECEIVED, recDate));
            }
            if (revDate != null) {
                history.addContent(convertDate(TAG_DATE, ATTR_VALUE_DATE_TYPE_REVISED, revDate));
            }
            metadata.addContent(history);
        }
        
        return metadata;
    }

    private Element convertAuthor(DocumentAuthor author) {
        Element contributor = new Element(TAG_CONTRIB);
        contributor.setAttribute(ATTR_CONTRIB_TYPE, ATTR_VALUE_CONTRIB_AUTHOR);

        addElement(contributor, TAG_STRING_NAME, author.getName());
        for (String email: author.getEmails()) {
            addElement(contributor, TAG_EMAIL, email);
        }

        for (DocumentAffiliation aff : author.getAffiliations()) {
            Element affRef = createElement(TAG_XREF, aff.getId());
            affRef.setAttribute(ATTR_REF_TYPE, ATTR_VALUE_REF_TYPE_AFF);
            affRef.setAttribute(ATTR_RID, aff.getId());
            contributor.addContent(affRef);
        }
                
        return contributor;
    }
    
    public Element convertAffiliation(DocumentAffiliation affiliation) throws TransformationException {
        Element aff = new Element(TAG_AFFILIATION);
        aff.setAttribute(ATTR_ID, affiliation.getId());
        addElement(aff, TAG_LABEL, affiliation.getId());
        for (Token<AffiliationLabel> token : affiliation.getTokens()) {
            switch (token.getLabel()) {
                case TEXT:
                    aff.addContent(token.getText());
                    break;
                case COUN:
                    CountryISOCodeFinder finder = new CountryISOCodeFinder();
                    String isoCode = finder.getCountryISOCode(token.getText());
                    if (isoCode == null) {
                        addElement(aff, TAG_COUNTRY, token.getText());
                    } else {
                        addElement(aff, TAG_COUNTRY, token.getText(), ATTR_COUNTRY, isoCode);
                    }   break;
                case INST:
                    addElement(aff, TAG_INSTITUTION, token.getText());
                    break;
                case ADDR:
                    addElement(aff, TAG_ADDRESS, token.getText());
                    break;
                default:
                    break;
            }
        }
        return aff;
    }
    
    private Element convertEditor(DocumentAuthor editor) {
        Element contributor = new Element(TAG_CONTRIB);
        contributor.setAttribute(ATTR_CONTRIB_TYPE, ATTR_VALUE_CONTRIB_EDITOR);
        addElement(contributor, TAG_STRING_NAME, editor.getName());
        return contributor;
    }
    
    private Element convertDate(String name, String type, DocumentDate date) {
        Element historyDate = new Element(name);
        if (!type.equals(DocumentDate.DATE_PUBLISHED)) {
            historyDate.setAttribute(ATTR_DATE_TYPE, type);
        }
        addElement(historyDate, TAG_DAY, date.getDay());
        addElement(historyDate, TAG_MONTH, date.getMonth());
        addElement(historyDate, TAG_YEAR, date.getYear());
        return historyDate;
    }

    private void addElement(Element parent, String[] names, String text) {
        if (text != null && names.length != 0) {
            Element prev = parent;
            for (String name : names) {
                Element element = new Element(name);
                prev.addContent(element);
                prev = element;
            }
            prev.setText(XMLTools.removeInvalidXMLChars(text));
        }
    }
    
    private void addElement(Element parent, String name, String text) {
        if (text != null) {
            Element element = createElement(name, text);
            parent.addContent(element);
        }
    }

    private void addElement(Element parent, String name, String text, String attrName, String attrValue) {
        if (text != null) {
            Element element = createElement(name, text);
            element.setAttribute(attrName, attrValue);
            parent.addContent(element);
        }
    }
    
    private Element createElement(String name, String text) {
        Element element = new Element(name);
        element.setText(XMLTools.removeInvalidXMLChars(text));
        return element;
    }
       

    private static final String TAG_ABSTRACT                    = "abstract";
    private static final String TAG_ADDRESS                     = "addr-line";
    private static final String TAG_AFFILIATION                 = "aff";
    private static final String TAG_ARTICLE                     = "article";
    private static final String TAG_ARTICLE_ID                  = "article-id";
    private static final String TAG_ARTICLE_META                = "article-meta";
    private static final String TAG_ARTICLE_TITLE               = "article-title";
    private static final String TAG_CONTRIB                     = "contrib";
    private static final String TAG_CONTRIB_GROUP               = "contrib-group";
    private static final String TAG_COUNTRY                     = "country";
    private static final String TAG_DATE                        = "date";
    private static final String TAG_DAY                         = "day";
    private static final String TAG_EMAIL                       = "email";
    private static final String TAG_FPAGE                       = "fpage";
    private static final String TAG_FRONT                       = "front";
    private static final String TAG_HISTORY                     = "history";
    private static final String TAG_INSTITUTION                 = "institution";
    private static final String TAG_ISSN                        = "issn";
    private static final String TAG_ISSUE                       = "issue";
    private static final String TAG_JOURNAL_META                = "journal-meta";
    private static final String TAG_JOURNAL_TITLE               = "journal-title";
    private static final String TAG_JOURNAL_TITLE_GROUP         = "journal-title-group";
    private static final String TAG_KWD                         = "kwd";
    private static final String TAG_KWD_GROUP                   = "kwd-group";
    private static final String TAG_LABEL                       = "label";
    private static final String TAG_LPAGE                       = "lpage";
    private static final String TAG_MONTH                       = "month";
    private static final String TAG_P                           = "p";
    private static final String TAG_PUB_DATE                    = "pub-date";
    private static final String TAG_PUB_TYPE                    = "pub-type";
    private static final String TAG_PUBLISHER                   = "publisher";
    private static final String TAG_PUBLISHER_NAME              = "publisher-name";
    private static final String TAG_STRING_NAME                 = "string-name";
    private static final String TAG_TITLE_GROUP                 = "title-group";
    private static final String TAG_VOLUME                      = "volume";
    private static final String TAG_YEAR                        = "year";
    private static final String TAG_XREF                        = "xref";

    private static final String ATTR_CONTRIB_TYPE               = "contrib-type";
    private static final String ATTR_COUNTRY                    = "country";
    private static final String ATTR_DATE_TYPE                  = "date-type";
    private static final String ATTR_ID                         = "id";
    private static final String ATTR_PUB_ID_TYPE                = "pub-id-type";
    private static final String ATTR_REF_TYPE                   = "ref-type";
    private static final String ATTR_RID                        = "rid";
    
    private static final String ATTR_VALUE_DATE_ACCEPTED        = "accepted";
    private static final String ATTR_VALUE_CONTRIB_AUTHOR       = "author";
    private static final String ATTR_VALUE_CONTRIB_EDITOR       = "editor";
    private static final String ATTR_VALUE_PUB_TYPE_PPUB        = "ppub";
    private static final String ATTR_VALUE_DATE_TYPE_RECEIVED   = "received";
    private static final String ATTR_VALUE_REF_TYPE_AFF         = "aff";
    private static final String ATTR_VALUE_DATE_TYPE_REVISED    = "revised";
           
}
