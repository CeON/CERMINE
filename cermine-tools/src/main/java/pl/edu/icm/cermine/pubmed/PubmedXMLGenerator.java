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
package pl.edu.icm.cermine.pubmed;

import pl.edu.icm.cermine.tools.SmartHashMap;
import com.google.common.collect.Lists;
import java.io.*;
import java.util.Map.Entry;
import java.util.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import pl.edu.icm.cermine.ContentExtractor;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.exception.TransformationException;
import pl.edu.icm.cermine.metadata.zoneclassification.tools.ZoneLocaliser;
import pl.edu.icm.cermine.structure.model.*;
import pl.edu.icm.cermine.structure.transformers.BxDocumentToTrueVizWriter;
import pl.edu.icm.cermine.tools.TextUtils;
import pl.edu.icm.cermine.tools.distance.CosineDistance;
import pl.edu.icm.cermine.tools.distance.SmithWatermanDistance;

/**
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class PubmedXMLGenerator {

    private static class LabelTrio {

        private final BxZoneLabel label;
        private final Double alignment;
        private final List<String> entryTokens;

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((label == null) ? 0 : label.hashCode());
            result = prime * result
                    + ((alignment == null) ? 0 : alignment.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            LabelTrio other = (LabelTrio) obj;
            if (label != other.label) {
                return false;
            }
            if (alignment == null) {
                if (other.alignment != null) {
                    return false;
                }
            } else if (!alignment.equals(other.alignment)) {
                return false;
            }
            return true;
        }

        public LabelTrio(BxZoneLabel label, List<String> tokens, Double similarity) {
            this.alignment = similarity;
            this.label = label;
            this.entryTokens = tokens;
        }
    };

    private boolean verbose = false;

    private void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    private void printlnVerbose(String string) {
        if (verbose) {
            System.out.println(string);
        }
    }

    private void printVerbose(String string) {
        if (verbose) {
            System.out.print(string);
        }
    }

    public BxDocument generateTrueViz(InputStream pdfStream, InputStream nlmStream)
            throws AnalysisException, ParserConfigurationException, SAXException, IOException, XPathExpressionException, TransformationException {
        XPath xpath = XPathFactory.newInstance().newXPath();
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setValidating(false);
        dbf.setFeature("http://xml.org/sax/features/namespaces", false);
        dbf.setFeature("http://xml.org/sax/features/validation", false);
        dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
        dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);

        DocumentBuilder builder = dbf.newDocumentBuilder();
        Document domDoc = builder.parse(nlmStream);

        ContentExtractor extractor = new ContentExtractor();
        extractor.setPDF(pdfStream);
        BxDocument bxDoc = extractor.getBxDocument();

        List<BxZone> zones = Lists.newArrayList(bxDoc.asZones());

        Integer bxDocLen = zones.size();

        SmartHashMap entries = new SmartHashMap();

        //abstract
        Node abstractNode = (Node) xpath.evaluate("/article/front/article-meta/abstract", domDoc, XPathConstants.NODE);
        String abstractString = XMLTools.extractTextFromNode(abstractNode);
        entries.putIf("Abstract " + abstractString, BxZoneLabel.MET_ABSTRACT);
        entries.putIf("Abstract", BxZoneLabel.MET_ABSTRACT);

        //title
        String titleString = (String) xpath.evaluate("/article/front/article-meta/title-group/article-title", domDoc, XPathConstants.STRING);
        entries.putIf(titleString, BxZoneLabel.MET_TITLE);
        String subtitleString = (String) xpath.evaluate("/article/front/article-meta/title-group/article-subtitle", domDoc, XPathConstants.STRING);
        entries.putIf(subtitleString, BxZoneLabel.MET_TITLE);

        //journal title
        String journalTitleString = (String) xpath.evaluate("/article/front/journal-meta/journal-title", domDoc, XPathConstants.STRING);
        if (journalTitleString == null || journalTitleString.isEmpty()) {
            journalTitleString = (String) xpath.evaluate("/article/front/journal-meta/journal-title-group/journal-title", domDoc, XPathConstants.STRING);
        }
        entries.putIf(journalTitleString, BxZoneLabel.MET_BIB_INFO);

        //journal publisher
        String journalPublisherString = (String) xpath.evaluate("/article/front/journal-meta/publisher/publisher-name", domDoc, XPathConstants.STRING);
        entries.putIf(journalPublisherString, BxZoneLabel.MET_BIB_INFO);
        String journalPublisherIdString = (String) xpath.evaluate("/article/front/journal-meta/journal-id[@journal-id-type='publisher-id']", domDoc, XPathConstants.STRING);
        entries.putIf(journalPublisherIdString, BxZoneLabel.MET_BIB_INFO);

        //journal issn
        String journalISSNString = (String) xpath.evaluate("/article/front/journal-meta/issn", domDoc, XPathConstants.STRING);
        entries.putIf(journalISSNString, BxZoneLabel.MET_BIB_INFO);

        //copyright/permissions
        String permissionsString = XMLTools.extractTextFromNode((Node) xpath.evaluate("/article/front/article-meta/permissions", domDoc, XPathConstants.NODE));
        entries.putIf(permissionsString, BxZoneLabel.MET_COPYRIGHT);

        //license
        Node licenseNode = (Node) xpath.evaluate("/article/front/article-meta/license", domDoc, XPathConstants.NODE);
        String licenseString = (String) XMLTools.extractTextFromNode(licenseNode);
        entries.putIf(licenseString, BxZoneLabel.MET_COPYRIGHT);

        //article type
        NodeList articleTypeNodes = (NodeList) xpath.evaluate("/article/@article-type", domDoc, XPathConstants.NODESET);
        List<String> articleTypeStrings = XMLTools.extractTextAsList(articleTypeNodes);
        Node articleTypeNode = (Node) xpath.evaluate("/article/front/article-meta/article-categories/subj-group", domDoc, XPathConstants.NODE);
        articleTypeStrings.add(XMLTools.extractTextFromNode(articleTypeNode));

        entries.putIf(articleTypeStrings, BxZoneLabel.MET_TYPE);

        //received date
        List<String> receivedDate = XMLTools.extractChildrenAsTextList((Node) xpath.evaluate("/article/front/article-meta/history/date[@date-type='received']", domDoc, XPathConstants.NODE));
        if (!receivedDate.isEmpty() && receivedDate.size() >= 3) {
            for (String date : TextUtils.produceDates(receivedDate)) {
                entries.putIf(date, BxZoneLabel.MET_DATES);
            }
        }

        //accepted date
        List<String> acceptedDate = XMLTools.extractChildrenAsTextList((Node) xpath.evaluate("/article/front/article-meta/history/date[@date-type='accepted']", domDoc, XPathConstants.NODE));
        if (!acceptedDate.isEmpty() && acceptedDate.size() >= 3) {
            for (String date : TextUtils.produceDates(acceptedDate)) {
                entries.putIf(date, BxZoneLabel.MET_DATES);
            }
        }

        //publication date
        List<String> pubdateString;
        if (((NodeList) xpath.evaluate("/article/front/article-meta/pub-date", domDoc, XPathConstants.NODESET)).getLength() > 1) {
            Node pubdateNode = (Node) xpath.evaluate("/article/front/article-meta/pub-date[@pub-type='epub']", domDoc, XPathConstants.NODE);
            pubdateString = XMLTools.extractChildrenAsTextList(pubdateNode);
        } else {
            Node pubdateNode = (Node) xpath.evaluate("/article/front/article-meta/pub-date[@pub-type='collection']", domDoc, XPathConstants.NODE);
            pubdateString = XMLTools.extractChildrenAsTextList(pubdateNode);
        }
        if (pubdateString != null && pubdateString.size() >= 3) {
            for (String date : TextUtils.produceDates(pubdateString)) {
                entries.putIf(date, BxZoneLabel.MET_DATES);
            }
        }
        if (pubdateString != null) {
            pubdateString.clear();
        }
        if (((NodeList) xpath.evaluate("/article/front/article-meta/pub-date", domDoc, XPathConstants.NODESET)).getLength() > 1) {
            Node pubdateNode = (Node) xpath.evaluate("/article/front/article-meta/pub-date[@pub-type='ppub']", domDoc, XPathConstants.NODE);
            pubdateString = XMLTools.extractChildrenAsTextList(pubdateNode);
        }
        if (pubdateString != null && pubdateString.size() >= 3) {
            for (String date : TextUtils.produceDates(pubdateString)) {
                entries.putIf(date, BxZoneLabel.MET_DATES);
            }
        }

        String extLink = (String) xpath.evaluate("/article/front/article-meta/ext-link[@ext-link-type='uri']/xlink:href", domDoc, XPathConstants.STRING);
        printlnVerbose(extLink);
        entries.putIf(extLink, BxZoneLabel.MET_ACCESS_DATA);

        //keywords
        Node keywordsNode = (Node) xpath.evaluate("/article/front/article-meta/kwd-group", domDoc, XPathConstants.NODE);
        String keywordsString = XMLTools.extractTextFromNode(keywordsNode);
        entries.putIf(keywordsString, BxZoneLabel.MET_KEYWORDS);

        //DOI
        String doiString = (String) xpath.evaluate("/article/front/article-meta/article-id[@pub-id-type='doi']", domDoc, XPathConstants.STRING);
        entries.putIf("DOI " + doiString, BxZoneLabel.MET_BIB_INFO);

        //volume
        String volumeString = (String) xpath.evaluate("/article/front/article-meta/volume", domDoc, XPathConstants.STRING);
        entries.putIf("volume " + volumeString, BxZoneLabel.MET_BIB_INFO);
        entries.putIf("vol " + volumeString, BxZoneLabel.MET_BIB_INFO);

        //issue
        String issueString = (String) xpath.evaluate("/article/front/article-meta/issue", domDoc, XPathConstants.STRING);
        entries.putIf("number " + issueString, BxZoneLabel.MET_BIB_INFO);

        entries.putIf("journal", BxZoneLabel.MET_BIB_INFO);
        entries.putIf("et al", BxZoneLabel.MET_BIB_INFO);

        List<String> authorNames = new ArrayList<String>();
        List<String> authorEmails = new ArrayList<String>();
        List<String> authorAffiliations = new ArrayList<String>();
        List<String> editors = new ArrayList<String>();

        //pages
        String fPage = (String) xpath.evaluate("/article/front/article-meta/fpage", domDoc, XPathConstants.STRING);
        String lPage = (String) xpath.evaluate("/article/front/article-meta/lpage", domDoc, XPathConstants.STRING);
        entries.putIf("pages " + fPage + " " + lPage, BxZoneLabel.MET_BIB_INFO);
        entries.putIf("pp " + fPage + " " + lPage, BxZoneLabel.MET_BIB_INFO);
        entries.putIf(fPage, BxZoneLabel.MET_BIB_INFO);
        entries.putIf(lPage, BxZoneLabel.MET_BIB_INFO);
        entries.putIf(lPage, BxZoneLabel.OTH_PAGE_NUMBER);
        entries.putIf(lPage, BxZoneLabel.OTH_PAGE_NUMBER);
        try {
            int f = Integer.valueOf(fPage);
            int l = Integer.valueOf(lPage);
            while (f < l) {
                f++;
                entries.putIf(String.valueOf(f), BxZoneLabel.OTH_PAGE_NUMBER);
            }
        } catch (NumberFormatException ex) {
        }

        entries.putIf("page of", BxZoneLabel.OTH_PAGE_NUMBER);

        //editors
        NodeList editorNodes = (NodeList) xpath.evaluate("/article/front/article-meta/contrib-group/contrib[@contrib-type='editor']", domDoc, XPathConstants.NODESET);
        for (int nodeIdx = 0; nodeIdx < editorNodes.getLength(); ++nodeIdx) {
            String editorString = XMLTools.extractTextFromNode(editorNodes.item(nodeIdx));
            editors.add(editorString);
        }
        entries.putIf(TextUtils.joinStrings(editors), BxZoneLabel.MET_EDITOR);

        NodeList authorsResult = (NodeList) xpath.evaluate("/article/front/article-meta/contrib-group/contrib[@contrib-type='author']", domDoc, XPathConstants.NODESET);
        for (int nodeIdx = 0; nodeIdx < authorsResult.getLength(); ++nodeIdx) {
            Node curNode = authorsResult.item(nodeIdx);

            //author names
            String name = (String) xpath.evaluate("name/given-names", curNode, XPathConstants.STRING);
            String surname = (String) xpath.evaluate("name/surname", curNode, XPathConstants.STRING);

            //author affiliation
            List<String> aff = XMLTools.extractTextAsList((NodeList) xpath.evaluate("/article/front/article-meta/contrib-group/aff", domDoc, XPathConstants.NODESET));

            //author correspondence
            String email;
            try {
                email = (String) xpath.evaluate("address/email", curNode, XPathConstants.STRING);
            } catch (XPathExpressionException e) {
                email = "";
            }
            if (email.isEmpty()) {
                try {
                    email = (String) xpath.evaluate("email", curNode, XPathConstants.STRING);
                } catch (XPathExpressionException e) {
                    //yaaay, probably there is no e-mail at all! => do nothing
                }
            }
            if (!email.isEmpty()) {
                authorEmails.add(email);
            }
            if (!aff.isEmpty()) {
                authorAffiliations.addAll(aff);
            }
            authorNames.add(name + " " + surname);
        }
        entries.putIf(TextUtils.joinStrings(authorNames), BxZoneLabel.MET_AUTHOR);

        //authors' affiliations
        NodeList affNodes = (NodeList) xpath.evaluate("/article/front/article-meta/aff", domDoc, XPathConstants.NODESET);
        authorAffiliations.addAll(XMLTools.extractTextAsList(affNodes));
        entries.putIf(authorAffiliations, BxZoneLabel.MET_AFFILIATION);

        //correspondence again
        NodeList correspNodes = (NodeList) xpath.evaluate("/article/front/article-meta/author-notes/corresp", domDoc, XPathConstants.NODESET);
        authorEmails.add(XMLTools.extractTextFromNodes(correspNodes));
        entries.putIf(authorEmails, BxZoneLabel.MET_CORRESPONDENCE);

        //author notes
        Node notesNode = (Node) xpath.evaluate("/article/front/article-meta/author-notes/corresp/fn", domDoc, XPathConstants.NODE);
        String notesString = XMLTools.extractTextFromNode(notesNode);
        entries.putIf(notesString, BxZoneLabel.MET_CORRESPONDENCE);
        notesString = XMLTools.extractTextFromNode((Node) xpath.evaluate("/article/back/notes", domDoc, XPathConstants.NODE));

        //article body
        NodeList paragraphNodes = (NodeList) xpath.evaluate("/article/body//p", domDoc, XPathConstants.NODESET);
        List<String> paragraphStrings = XMLTools.extractTextAsList(paragraphNodes);
        entries.putIf(paragraphStrings, BxZoneLabel.BODY_CONTENT);

        NodeList appNodes = (NodeList) xpath.evaluate("/article/back/app-group//p", domDoc, XPathConstants.NODESET);
        String appStrings = XMLTools.extractTextFromNodes(appNodes);
        entries.putIf(appStrings, BxZoneLabel.BODY_CONTENT);

        //section titles
        NodeList sectionTitleNodes = (NodeList) xpath.evaluate("/article/body//title", domDoc, XPathConstants.NODESET);
        List<String> sectionTitles = XMLTools.extractTextAsList(sectionTitleNodes);
        entries.putIf(sectionTitles, BxZoneLabel.BODY_CONTENT);

        NodeList appTitleNodes = (NodeList) xpath.evaluate("/article/back/app-group//title", domDoc, XPathConstants.NODESET);
        List<String> appTitles = XMLTools.extractTextAsList(appTitleNodes);
        entries.putIf(appTitles, BxZoneLabel.BODY_CONTENT);

        //figures
        NodeList figureNodes = (NodeList) xpath.evaluate("/article/floats-wrap//fig", domDoc, XPathConstants.NODESET);
        List<String> figureStrings = XMLTools.extractTextAsList(figureNodes);

        figureNodes = (NodeList) xpath.evaluate("/article/floats-group//fig", domDoc, XPathConstants.NODESET);
        figureStrings.addAll(XMLTools.extractTextAsList(figureNodes));

        figureNodes = (NodeList) xpath.evaluate("/article/back//fig", domDoc, XPathConstants.NODESET);
        figureStrings.addAll(XMLTools.extractTextAsList(figureNodes));

        figureNodes = (NodeList) xpath.evaluate("/article/body//fig", domDoc, XPathConstants.NODESET);
        figureStrings.addAll(XMLTools.extractTextAsList(figureNodes));

        figureNodes = (NodeList) xpath.evaluate("/article/back/app-group//fig", domDoc, XPathConstants.NODESET);
        figureStrings.addAll(XMLTools.extractTextAsList(figureNodes));

        entries.putIf(figureStrings, BxZoneLabel.BODY_FIGURE);

        //tables
        List<String> tableCaptions = new ArrayList<String>();
        List<String> tableBodies = new ArrayList<String>();
        List<String> tableFootnotes = new ArrayList<String>();

        //tableNodes
        NodeList tableNodes = (NodeList) xpath.evaluate("/article//table-wrap", domDoc, XPathConstants.NODESET);

        for (Integer nodeIdx = 0; nodeIdx < tableNodes.getLength(); ++nodeIdx) {
            Node tableNode = tableNodes.item(nodeIdx);

            String caption = (String) xpath.evaluate("caption", tableNode, XPathConstants.STRING);
            tableCaptions.add(caption);

            String body = XMLTools.extractTextFromNode((Node) xpath.evaluate("table", tableNode, XPathConstants.NODE));
            tableBodies.add(body);

            List<String> footnotes = XMLTools.extractTextAsList((NodeList) xpath.evaluate("table-wrap-foot/fn", tableNode, XPathConstants.NODESET));
            tableFootnotes.addAll(footnotes);

            entries.putIf(caption, BxZoneLabel.BODY_TABLE);
            entries.putIf(body, BxZoneLabel.BODY_TABLE);
            entries.putIf(footnotes, BxZoneLabel.BODY_TABLE);
        }

        //financial disclosure
        String financialDisclosure = XMLTools.extractTextFromNode((Node) xpath.evaluate("/article//fn[@fn-type='financial-disclosure']", domDoc, XPathConstants.NODE));
        entries.putIf(financialDisclosure, BxZoneLabel.BODY_ACKNOWLEDGMENT);

        //conflict
        String conflictString = XMLTools.extractTextFromNode((Node) xpath.evaluate("/article//fn[@fn-type='conflict']", domDoc, XPathConstants.NODE));
        entries.putIf(conflictString, BxZoneLabel.BODY_CONFLICT_STMT);

        //copyright
        String copyrightString = XMLTools.extractTextFromNode((Node) xpath.evaluate("/article/front/article-meta/permissions/copyright-statement", domDoc, XPathConstants.NODE));
        entries.putIf(copyrightString, BxZoneLabel.MET_COPYRIGHT);

        //acknowledgment
        String acknowledgement = XMLTools.extractTextFromNode((Node) xpath.evaluate("/article/back/ack", domDoc, XPathConstants.NODE));
        entries.putIf(acknowledgement, BxZoneLabel.BODY_ACKNOWLEDGMENT);

        acknowledgement = XMLTools.extractTextFromNode((Node) xpath.evaluate("/article/back/fn-group/fn", domDoc, XPathConstants.NODE));
        entries.putIf(acknowledgement, BxZoneLabel.BODY_CONFLICT_STMT);

        //glossary
        String glossary = XMLTools.extractTextFromNode((Node) xpath.evaluate("/article/back/glossary", domDoc, XPathConstants.NODE));
        entries.putIf(glossary, BxZoneLabel.BODY_GLOSSARY);

        //formula
        NodeList formulaNodes = (NodeList) xpath.evaluate("/article/body//disp-formula", domDoc, XPathConstants.NODESET);
        for (int nodeIdx = 0; nodeIdx < formulaNodes.getLength(); ++nodeIdx) {
            Node curFormulaNode = formulaNodes.item(nodeIdx);
            String label = (String) xpath.evaluate("label", curFormulaNode);
            entries.putIf(label, BxZoneLabel.BODY_EQUATION);

            NodeList curNodeChildren = curFormulaNode.getChildNodes();
            List<String> formulaParts = new ArrayList<String>();
            for (int childIdx = 0; childIdx < curNodeChildren.getLength(); ++childIdx) {
                Node curChild = curNodeChildren.item(childIdx);
                if (curChild.getNodeName().equals("label")) {
                    continue;
                }
                formulaParts.add(XMLTools.extractTextFromNode(curChild));
            }
            entries.putIf(TextUtils.joinStrings(formulaParts), BxZoneLabel.BODY_EQUATION);
        }

        //references
        List<String> refStrings = new ArrayList<String>();
        Node refParentNode = (Node) xpath.evaluate("/article/back/ref-list", domDoc, XPathConstants.NODE);
        if (refParentNode != null) {
            for (Integer refIdx = 0; refIdx < refParentNode.getChildNodes().getLength(); ++refIdx) {
                refStrings.add(XMLTools.extractTextFromNode(refParentNode.getChildNodes().item(refIdx)));
            }
        }
        entries.putIf(TextUtils.joinStrings(refStrings), BxZoneLabel.REFERENCES);
        entries.put("references", BxZoneLabel.REFERENCES);

        Set<String> allBibInfos = new HashSet<String>();
        for (Entry<String, BxZoneLabel> entry : entries.entrySet()) {
            if (BxZoneLabel.MET_BIB_INFO.equals(entry.getValue())) {
                allBibInfos.addAll(Arrays.asList(entry.getKey().split(" ")));
            }
        }
        entries.put(StringUtils.join(allBibInfos, " "), BxZoneLabel.MET_BIB_INFO);

        printlnVerbose("journalTitle: " + journalTitleString);
        printlnVerbose("journalPublisher: " + journalPublisherString);
        printlnVerbose("journalISSNPublisher: " + journalISSNString);

        printlnVerbose("articleType: " + articleTypeStrings);
        printlnVerbose("received: " + receivedDate);
        printlnVerbose("accepted: " + acceptedDate);
        printlnVerbose("pubdate: " + pubdateString);
        printlnVerbose("permissions: " + permissionsString);
        printlnVerbose("license: " + licenseString);

        printlnVerbose("title: " + titleString);
        printlnVerbose("abstract: " + abstractString);

        printlnVerbose("authorEmails: " + authorEmails);
        printlnVerbose("authorNames: " + authorNames);
        printlnVerbose("authorAff: " + authorAffiliations);
        printlnVerbose("authorNotes: " + notesString);
        printlnVerbose("editor: " + editors);

        printlnVerbose("keywords: " + keywordsString);
        printlnVerbose("DOI: " + doiString);
        printlnVerbose("volume: " + volumeString);
        printlnVerbose("issue: " + issueString);
        printlnVerbose("financial dis.: " + financialDisclosure);

        printlnVerbose("paragraphs: " + paragraphStrings);
        printlnVerbose("section titles: " + sectionTitles);

        printlnVerbose("tableBodies: " + tableBodies);
        printlnVerbose("tableCaptions: " + tableCaptions);
        printlnVerbose("tableFootnotes: " + tableFootnotes);

        printlnVerbose("figures: " + figureStrings);
        printlnVerbose("acknowledgement: " + acknowledgement);

        printlnVerbose("ref: " + refStrings.size() + " " + refStrings);

        SmithWatermanDistance smith = new SmithWatermanDistance(.1, 0.1);
        CosineDistance cos = new CosineDistance();

        //index: (zone,entry)
        List<List<LabelTrio>> swLabelSim = new ArrayList<List<LabelTrio>>(bxDocLen);
        List<List<LabelTrio>> cosLabProb = new ArrayList<List<LabelTrio>>(bxDocLen);
        for (Integer i = 0; i < bxDocLen; ++i) {
            swLabelSim.add(new ArrayList<LabelTrio>());
            cosLabProb.add(new ArrayList<LabelTrio>());
        }

        //iterate over entries
        for (Entry<String, BxZoneLabel> entry : entries.entrySet()) {
            List<String> entryTokens = TextUtils.tokenize(entry.getKey());
            printlnVerbose("--------------------");
            printlnVerbose(entry.getValue() + " " + entry.getKey() + "\n");
            //iterate over zones
            for (Integer zoneIdx = 0; zoneIdx < bxDocLen; ++zoneIdx) {
                BxZone curZone = zones.get(zoneIdx);
                List<String> zoneTokens = TextUtils.tokenize(
                        TextUtils.removeOrphantSpaces(
                                TextUtils.cleanLigatures(
                                        curZone.toText().toLowerCase())));

                Double smithSim;
                Double cosSim;
                if (curZone.toText().contains("www.biomedcentral.com")) {
                    //ignore
                    smithSim = 0.;
                    cosSim = 0.;
                } else {
                    smithSim = smith.compare(entryTokens, zoneTokens);
                    cosSim = cos.compare(entryTokens, zoneTokens);
                }
                printlnVerbose(smithSim + " " + zones.get(zoneIdx).toText() + "\n\n");
                swLabelSim.get(zoneIdx).add(new LabelTrio(entry.getValue(), entryTokens, smithSim));
                cosLabProb.get(zoneIdx).add(new LabelTrio(entry.getValue(), entryTokens, cosSim));
            }
        }

        printlnVerbose("===========================");
        for (BxPage page : bxDoc) {
            for (BxZone zone : page) {
                Integer zoneIdx = zones.indexOf(zone);
                BxZone curZone = zones.get(zoneIdx);
                String zoneText = TextUtils.removeOrphantSpaces(curZone.toText().toLowerCase());
                List<String> zoneTokens = TextUtils.tokenize(zoneText);
                Boolean valueSet = false;

                Collections.sort(swLabelSim.get(zoneIdx), new Comparator<LabelTrio>() {

                    @Override
                    public int compare(LabelTrio t1, LabelTrio t2) {
                        Double simDif = t1.alignment / t1.entryTokens.size() - t2.alignment / t2.entryTokens.size();
                        if (Math.abs(simDif) < 0.0001) {
                            return t2.entryTokens.size() - t1.entryTokens.size();
                        }
                        if (simDif > 0) {
                            return 1;
                        } else {
                            return -1;
                        }
                    }
                });
                Collections.reverse(swLabelSim.get(zoneIdx));

                List<String> entryTokens = swLabelSim.get(zoneIdx).get(0).entryTokens;
                if (Math.max(zoneTokens.size(), entryTokens.size()) > 0
                        && Math.min(zoneTokens.size(), entryTokens.size()) / Math.max(zoneTokens.size(), (double) entryTokens.size()) > 0.7
                        && swLabelSim.get(zoneIdx).get(0).alignment / entryTokens.size() > 0.7) {
                    curZone.setLabel(swLabelSim.get(zoneIdx).get(0).label);
                    valueSet = true;
                    printVerbose("0 ");
                }

                if (!valueSet) {
                    Collections.sort(swLabelSim.get(zoneIdx), new Comparator<LabelTrio>() {

                        @Override
                        public int compare(LabelTrio t1, LabelTrio t2) {
                            Double simDif = t1.alignment - t2.alignment;
                            if (Math.abs(simDif) < 0.0001) {
                                return t2.entryTokens.size() - t1.entryTokens.size();
                            }
                            if (simDif > 0) {
                                return 1;
                            } else {
                                return -1;
                            }
                        }
                    });
                    Collections.reverse(swLabelSim.get(zoneIdx));
                    printlnVerbose("-->" + swLabelSim.get(zoneIdx).get(0).alignment / zoneTokens.size());
                    if (swLabelSim.get(zoneIdx).get(0).alignment / zoneTokens.size() > 0.5) {
                        curZone.setLabel(swLabelSim.get(zoneIdx).get(0).label);
                        valueSet = true;
                        printVerbose("1 ");
                    }
                }

                if (!valueSet) {
                    Map<BxZoneLabel, Double> cumulated = new EnumMap<BxZoneLabel, Double>(BxZoneLabel.class);
                    for (LabelTrio trio : swLabelSim.get(zoneIdx)) {
                        if (cumulated.containsKey(trio.label)) {
                            cumulated.put(trio.label, cumulated.get(trio.label) + trio.alignment / Math.max(zoneTokens.size(), trio.entryTokens.size()));
                        } else {
                            cumulated.put(trio.label, trio.alignment / Math.max(zoneTokens.size(), trio.entryTokens.size()));
                        }
                    }
                    Double max = Double.NEGATIVE_INFINITY;
                    BxZoneLabel bestLabel = null;
                    for (Entry<BxZoneLabel, Double> entry : cumulated.entrySet()) {
                        if (entry.getValue() > max) {
                            max = entry.getValue();
                            bestLabel = entry.getKey();
                        }
                    }
                    if (max >= 0.5) {
                        curZone.setLabel(bestLabel);
                        printVerbose("2 ");
                        valueSet = true;
                    }
                }

                if (!valueSet) {
                    Collections.sort(swLabelSim.get(zoneIdx), new Comparator<LabelTrio>() {

                        @Override
                        public int compare(LabelTrio t1, LabelTrio t2) {
                            Double simDif = t1.alignment / t1.entryTokens.size() - t2.alignment / t2.entryTokens.size();
                            if (Math.abs(simDif) < 0.001) {
                                return t2.entryTokens.size() - t1.entryTokens.size();
                            }
                            if (simDif > 0) {
                                return 1;
                            } else {
                                return -1;
                            }
                        }
                    });
                    Collections.reverse(swLabelSim.get(zoneIdx));
                    List<LabelTrio> l = swLabelSim.get(zoneIdx);

                    BxZoneLabel best = null;
                    int bestScore = 0;
                    for (LabelTrio lt : l) {
                        int i = 0;
                        for (String zt : zoneTokens) {
                            if (lt.entryTokens.contains(zt)) {
                                i++;
                            }
                        }
                        if (i > bestScore && i > 1) {
                            best = lt.label;
                            bestScore = i;
                        }
                    }
                    if (best != null) {
                        curZone.setLabel(best);
                        valueSet = true;
                    } else {
                        for (LabelTrio lt : l) {
                            int i = 0;
                            for (String zt : zoneTokens) {
                                for (String j : lt.entryTokens) {
                                    if (zt.replaceAll("[^0-9a-zA-Z,;\\.!\\?]", "").equals(j.replaceAll("[^0-9a-zA-Z,;\\.!\\?]", ""))) {
                                        i++;
                                        break;
                                    }
                                }
                            }
                            if (i > bestScore && i > 1) {
                                best = lt.label;
                                bestScore = i;
                            }
                        }
                    }

                    if (best != null) {
                        curZone.setLabel(best);
                        valueSet = true;
                    }
                }
                if (!valueSet) {
                    curZone.setLabel(null);
                }
                printlnVerbose(zone.getLabel() + " " + zone.toText() + "\n");
            }
            Map<BxZone, ZoneLocaliser> zoneLocMap = new HashMap<BxZone, ZoneLocaliser>();
            Set<BxZone> unlabeledZones = new HashSet<BxZone>();
            for (BxZone zone : page) {
                if (zone.getLabel() == null) {
                    unlabeledZones.add(zone);
                    zoneLocMap.put(zone, new ZoneLocaliser(zone));
                }
            }
            Integer lastNumberOfUnlabeledZones;
            do {
                lastNumberOfUnlabeledZones = unlabeledZones.size();
                infereLabels(unlabeledZones, zoneLocMap);
                infereLabels(unlabeledZones, zoneLocMap);
            } while (lastNumberOfUnlabeledZones != unlabeledZones.size());
        }
        printlnVerbose("=>=>=>=>=>=>=>=>=>=>=>=>=>=");

        return bxDoc;
    }

    private void infereLabels(Set<BxZone> unlabeledZones, Map<BxZone, ZoneLocaliser> zoneLocMap) {
        Set<BxZone> toBeRemoved = new HashSet<BxZone>();
        for (BxZone zone : unlabeledZones) {
            if (zone.getLabel() == null) {
                ZoneLocaliser loc = zoneLocMap.get(zone);
                if ((loc.getLeftZone() != null && loc.getRightZone() != null)
                        && (loc.getLeftZone().getLabel() == loc.getRightZone().getLabel())) {
                    zone.setLabel(loc.getLeftZone().getLabel());
                    printVerbose("3 ");
                    toBeRemoved.add(zone);
                } else if ((loc.getLowerZone() != null && loc.getUpperZone() != null)
                        && (loc.getLowerZone().getLabel() == loc.getUpperZone().getLabel())) {
                    zone.setLabel(loc.getLowerZone().getLabel());
                    printVerbose("3 ");
                    toBeRemoved.add(zone);
                } else if (zone.hasNext() && zone.hasPrev()
                        && zone.getPrev().getLabel() == zone.getNext().getLabel()) {
                    zone.setLabel(zone.getPrev().getLabel());
                    printVerbose("3 ");
                    toBeRemoved.add(zone);
                }
            }
        }
        for (BxZone zone : toBeRemoved) {
            zoneLocMap.remove(zone);
        }
        unlabeledZones.removeAll(toBeRemoved);
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: <pubmed directory>");
            System.exit(1);
        }

        File dir = new File(args[0]);
        for (File pdfFile : FileUtils.listFiles(dir, new String[]{"pdf"}, true)) {
            try {
                String pdfPath = pdfFile.getPath();
                String nxmlPath = TextUtils.getNLMPath(pdfPath);

                File xmlFile = new File(TextUtils.getTrueVizPath(nxmlPath));
                if (xmlFile.exists()) {
                    continue;
                }

                System.out.print(pdfPath + " ");

                InputStream pdfStream = new FileInputStream(pdfPath);
                InputStream nxmlStream = new FileInputStream(nxmlPath);

                PubmedXMLGenerator datasetGenerator = new PubmedXMLGenerator();
                datasetGenerator.setVerbose(false);
                BxDocument bxDoc = datasetGenerator.generateTrueViz(pdfStream, nxmlStream);

                int keys = 0;
                Set<BxZoneLabel> set = EnumSet.noneOf(BxZoneLabel.class);
                int total = 0;
                int known = 0;
                for (BxZone z : bxDoc.asZones()) {
                    total++;
                    if (z.getLabel() != null) {
                        known++;
                        if (z.getLabel().isOfCategoryOrGeneral(BxZoneLabelCategory.CAT_METADATA)) {
                            set.add(z.getLabel());
                        }
                        if (BxZoneLabel.REFERENCES.equals(z.getLabel())) {
                            keys = 1;
                        }
                    }
                }

                if (set.contains(BxZoneLabel.MET_AFFILIATION)) {
                    keys++;
                }
                if (set.contains(BxZoneLabel.MET_AUTHOR)) {
                    keys++;
                }
                if (set.contains(BxZoneLabel.MET_BIB_INFO)) {
                    keys++;
                }
                if (set.contains(BxZoneLabel.MET_TITLE)) {
                    keys++;
                }
                int coverage = 0;
                if (total > 0) {
                    coverage = known * 100 / total;
                }
                System.out.print(coverage + " " + set.size() + " " + keys);

                FileWriter fstream = new FileWriter(
                        TextUtils.getTrueVizPath(nxmlPath).replace(".xml", "." + coverage + ".cxml"));
                BufferedWriter out = new BufferedWriter(fstream);
                BxDocumentToTrueVizWriter writer = new BxDocumentToTrueVizWriter();
                out.write(writer.write(Lists.newArrayList(bxDoc)));
                out.close();

                System.out.println(" done");
            } catch (AnalysisException e) {
                e.printStackTrace();
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (XPathExpressionException e) {
                e.printStackTrace();
            } catch (TransformationException e) {
                e.printStackTrace();
            }
        }
    }

}
