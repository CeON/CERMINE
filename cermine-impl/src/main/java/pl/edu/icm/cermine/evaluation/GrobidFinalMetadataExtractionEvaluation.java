/**
 * This file is part of CERMINE project.
 * Copyright (c) 2011-2013 ICM-UW
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

package pl.edu.icm.cermine.evaluation;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import org.apache.commons.lang.StringUtils;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import pl.edu.icm.cermine.evaluation.tools.MetadataRelation.StringRelation;
import pl.edu.icm.cermine.evaluation.tools.*;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.exception.TransformationException;

/**
 * @author Pawel Szostek (p.szostek@icm.edu.pl)
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public final class GrobidFinalMetadataExtractionEvaluation {

    public void evaluate(int mode, NlmIterator iter) throws AnalysisException, IOException, TransformationException, ParserConfigurationException, SAXException, JDOMException, XPathExpressionException, TransformerException {

        javax.xml.parsers.DocumentBuilderFactory dbf = javax.xml.parsers.DocumentBuilderFactory.newInstance();
        dbf.setValidating(false);
        dbf.setFeature("http://xml.org/sax/features/namespaces", false);
        dbf.setFeature("http://xml.org/sax/features/validation", false);
        dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
        dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);

        javax.xml.parsers.DocumentBuilder documentBuilder = dbf.newDocumentBuilder();

        SAXBuilder builder = new SAXBuilder("org.apache.xerces.parsers.SAXParser");
        builder.setValidation(false);
        builder.setFeature("http://xml.org/sax/features/validation", false);
        builder.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
        builder.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);

        List<ComparisonResult> titles = new ArrayList<ComparisonResult>();
        List<ComparisonResult> authors = new ArrayList<ComparisonResult>();
        List<ComparisonResult> affiliations = new ArrayList<ComparisonResult>();
        List<ComparisonResult> authorsAffiliations = new ArrayList<ComparisonResult>();
        List<ComparisonResult> emails = new ArrayList<ComparisonResult>();
        List<ComparisonResult> authorsEmails = new ArrayList<ComparisonResult>();
        List<ComparisonResult> abstracts = new ArrayList<ComparisonResult>();
        List<ComparisonResult> keywords = new ArrayList<ComparisonResult>();
        List<ComparisonResult> journals = new ArrayList<ComparisonResult>();
        List<ComparisonResult> volumes = new ArrayList<ComparisonResult>();
        List<ComparisonResult> issues = new ArrayList<ComparisonResult>();
        List<ComparisonResult> pageRanges = new ArrayList<ComparisonResult>();
        List<ComparisonResult> years = new ArrayList<ComparisonResult>();
        List<ComparisonResult> dois = new ArrayList<ComparisonResult>();
       
        if (mode == 1) {
            System.out.println("path,gro_title,gro_abstract,gro_keywords,"+
                "gro_authors,gro_affs,gro_autaff,gro_email,gro_autemail,gro_journal,gro_volume,gro_issue,"+
                "gro_pages,gro_year,gro_doi,one");
        }
        
        int i = 0;
        for (NlmPair pair : iter) {
            i++;

            if (mode == 0) {
                System.out.println("");
                System.out.println(">>>>>>>>> "+i);
                System.out.println(pair.getExtractedNlm().getPath());
            }
            if (mode == 1) {
                System.out.print(pair.getOriginalNlm().getPath()+",");
            }
            
            org.w3c.dom.Document originalNlm;
            org.w3c.dom.Document extractedNlm;
            try {
                originalNlm = documentBuilder.parse(new FileInputStream(pair.getOriginalNlm()));
                extractedNlm = documentBuilder.parse(new FileInputStream(pair.getExtractedNlm()));
            } catch (SAXException ex) {
                i--;
                continue;
            }
            
            // Document's title
            MetadataSingle title = new MetadataSingle(originalNlm, "/article/front/article-meta//article-title",
                                                        extractedNlm, "//teiHeader//titleStmt/title");
            title.setComp(EvaluationUtils.swComparator);
            titles.add(title);
            title.print(mode, "title");         

            
            // Abstract
            MetadataSingle abstrakt = new MetadataSingle(originalNlm, "/article/front/article-meta/abstract",
                                                        extractedNlm, "//teiHeader//abstract/p");
            abstrakt.setComp(EvaluationUtils.swComparator);
            abstracts.add(abstrakt);
            abstrakt.print(mode, "abstract");
            
            
            // Keywords
            MetadataList keyword = new MetadataList(originalNlm, "/article/front/article-meta//kwd",
                                                    extractedNlm, "//teiHeader//keywords//term");
            keywords.add(keyword);
            keyword.print(mode, "keywords");
            
            
            // Authors
            List<Node> expectedAuthorNodes = XMLTools.extractNodes(originalNlm, "/article/front/article-meta/contrib-group/contrib[@contrib-type='author'][name]");
            
            List<String> expectedAuthors = new ArrayList<String>();
            for (Node authorNode : expectedAuthorNodes) {
                List<Node> names = XMLTools.extractChildrenNodesFromNode(authorNode, "name");
                if (names.isEmpty()) {
                    continue;
                }
                Node name = names.get(0);
                List<String> givenNames = XMLTools.extractChildrenTextFromNode(name, "given-names");
                List<String> surnames = XMLTools.extractChildrenTextFromNode(name, "surname");
                String author = StringUtils.join(givenNames, " ")+" "+StringUtils.join(surnames, " ");
                expectedAuthors.add(author);
            }

            List<Node> extractedAuthorNodes = XMLTools.extractNodes(extractedNlm, "//teiHeader//sourceDesc/biblStruct//author/persName");

            List<String> extractedAuthors = new ArrayList<String>();
            for (Node authorNode : extractedAuthorNodes) {
                List<String> givenNames = XMLTools.extractChildrenTextFromNode(authorNode, "forename");
                List<String> surnames = XMLTools.extractChildrenTextFromNode(authorNode, "surname");
                String author = StringUtils.join(givenNames, " ")+" "+StringUtils.join(surnames, " ");
                extractedAuthors.add(author);
            }

            MetadataList author = new MetadataList(expectedAuthors, extractedAuthors);
            author.setComp(EvaluationUtils.authorComparator);
            authors.add(author);
            author.print(mode, "author");

    
            // Affiliations
            Set<String> expectedAffiliationsSet = Sets.newHashSet(XMLTools.extractTextAsList(originalNlm, "/article/front/article-meta//aff"));
            Set<String> extractedAffiliationsSet = Sets.newHashSet(XMLTools.extractTextAsList(extractedNlm, "//teiHeader//sourceDesc/biblStruct//author/affiliation"));
            List<String> expectedAffiliations = Lists.newArrayList(expectedAffiliationsSet);
            List<String> extractedAffiliations = Lists.newArrayList(extractedAffiliationsSet);
            MetadataList affiliation = new MetadataList(expectedAffiliations, extractedAffiliations);
            affiliation.setComp(EvaluationUtils.cosineComparator());
            affiliations.add(affiliation);
            affiliation.print(mode, "affiliation");
 

            // Author - Affiliation relation
            MetadataRelation authorAffiliation = new MetadataRelation();
            authorAffiliation.setComp1(EvaluationUtils.authorComparator);
            authorAffiliation.setComp2(EvaluationUtils.cosineComparator());

            List<Node> expectedAffiliationNodes = XMLTools.extractNodes(originalNlm, "/article/front/article-meta//aff[@id]");
            Map<String, String> expectedAffiliationMap = new HashMap<String, String>();
            for (Node expectedAffiliationNode : expectedAffiliationNodes) {
                String id = expectedAffiliationNode.getAttributes().getNamedItem("id").getNodeValue();
                String aff = XMLTools.extractTextFromNode(expectedAffiliationNode);
                expectedAffiliationMap.put(id, aff);
            }

            for (Node expectedAuthorNode : expectedAuthorNodes) {
                String authorName = expectedAuthors.get(expectedAuthorNodes.indexOf(expectedAuthorNode));
                List<Node> xrefs = XMLTools.extractChildrenNodesFromNode(expectedAuthorNode, "xref");
                for (Node xref : xrefs) {
                    if (xref.getAttributes() != null && xref.getAttributes().getNamedItem("ref-type") != null
                           && "aff".equals(xref.getAttributes().getNamedItem("ref-type").getNodeValue())) {
                        String affId = xref.getAttributes().getNamedItem("rid").getNodeValue();
                        for (String id : affId.split(" ")) {
                            String aff = expectedAffiliationMap.get(id);
                            if (aff != null) {
                                authorAffiliation.addExpected(new StringRelation(authorName, aff));
                            }
                        }
                    }
                }
            }

            extractedAuthorNodes = XMLTools.extractNodes(extractedNlm,
                    "//teiHeader//sourceDesc/biblStruct//author/persName");

            for (Node authorNode : extractedAuthorNodes) {

                List<String> givenNames = XMLTools.extractChildrenTextFromNode(authorNode, "forename");
                List<String> surnames = XMLTools.extractChildrenTextFromNode(authorNode, "surname");
                String a = StringUtils.join(givenNames, " ")+" "+StringUtils.join(surnames, " ");

                Node n = authorNode.getParentNode();
                NodeList nl = n.getChildNodes();
                for (int iu = 0; iu < nl.getLength(); iu++) {
                    Node aff = nl.item(iu);
                    if ("affiliation".equals(aff.getNodeName())) {
                        String aw = XMLTools.extractTextFromNode(aff);
                        authorAffiliation.addExtracted(new StringRelation(a, aw));
                    }
                }
           
            }

            authorsAffiliations.add(authorAffiliation);
            authorAffiliation.print(mode, "author - affiliation");

            double autF1 = 0;
            if (author.getPrecision() != null && author.getRecall() != null) {
                autF1 = 2./(1./author.getPrecision()+1./author.getRecall());
            }
            double affF1 = 0;
            if (affiliation.getPrecision() != null && affiliation.getRecall() != null) {
                affF1 = 2./(1./affiliation.getPrecision()+1./affiliation.getRecall());
            }
            double aufF1 = 0;
            if (authorAffiliation.getPrecision() != null && authorAffiliation.getRecall() != null) {
                aufF1 = 2./(1./authorAffiliation.getPrecision()+1./authorAffiliation.getRecall());
            }

      //      System.out.println("TOTL "+autF1+","+affF1+","+aufF1);

       
            // Email addresses
            MetadataList email = new MetadataList(originalNlm, "/article/front/article-meta/contrib-group/contrib[@contrib-type='author']//email",
                                                    extractedNlm, "//teiHeader//sourceDesc/biblStruct//author/email");
            email.setComp(EvaluationUtils.emailComparator);
            emails.add(email);
            email.print(mode, "email");

            
            // Author - Email relations
            MetadataRelation authorEmail = new MetadataRelation();
            authorEmail.setComp1(EvaluationUtils.authorComparator);
            authorEmail.setComp2(EvaluationUtils.emailComparator);
            
            for (Node expectedAuthorNode : expectedAuthorNodes) {
                String authorName = expectedAuthors.get(expectedAuthorNodes.indexOf(expectedAuthorNode));
                
                List<Node> addresses = XMLTools.extractChildrenNodesFromNode(expectedAuthorNode, "address");
                for (Node address : addresses) {
                    for (String emailAddress : XMLTools.extractChildrenTextFromNode(address, "email")) {
                        authorEmail.addExpected(new StringRelation(authorName, emailAddress));
                    }
                }
                for (String emailAddress : XMLTools.extractChildrenTextFromNode(expectedAuthorNode, "email")) {
                    authorEmail.addExpected(new StringRelation(authorName, emailAddress));
                }
            }
            
            
            extractedAuthorNodes = XMLTools.extractNodes(extractedNlm,
                    "//teiHeader//sourceDesc/biblStruct//author/persName");

            for (Node authorNode : extractedAuthorNodes) {

                List<String> givenNames = XMLTools.extractChildrenTextFromNode(authorNode, "forename");
                List<String> surnames = XMLTools.extractChildrenTextFromNode(authorNode, "surname");
                String a = StringUtils.join(givenNames, " ")+" "+StringUtils.join(surnames, " ");

                Node n = authorNode.getParentNode();
                NodeList nl = n.getChildNodes();
                for (int iu = 0; iu < nl.getLength(); iu++) {
                    Node aff = nl.item(iu);
                    if ("email".equals(aff.getNodeName())) {
                        String aw = XMLTools.extractTextFromNode(aff);
                        authorEmail.addExtracted(new StringRelation(a, aw));
                    }
                }
           
            }
                        
            authorsEmails.add(authorEmail);
            authorEmail.print(mode, "author - email");

                        
            // Journal title
            MetadataSingle journal = new MetadataSingle(originalNlm, "/article/front/journal-meta//journal-title",
                                                        extractedNlm, "//monogr/title[@level='j' and @type='main']");
            journal.setComp(EvaluationUtils.journalComparator);
            journals.add(journal);
            journal.print(mode, "journal title");

            
            // Volume
            MetadataSingle volume = new MetadataSingle(originalNlm, "/article/front/article-meta/volume",
                                                        extractedNlm, "//monogr/imprint/biblScope[@unit='volume']");
            volumes.add(volume);
            volume.print(mode, "volume");

            
            // Issue            
            MetadataSingle issue = new MetadataSingle(originalNlm, "/article/front/article-meta/issue",
                                                        extractedNlm, "//monogr/imprint/biblScope[@unit='issue']");
            issues.add(issue);
            issue.print(mode, "issue");

            
            // Pages range
            MetadataSingle fPage = new MetadataSingle(originalNlm, "/article/front/article-meta/fpage",
                                                    extractedNlm, "//monogr/imprint/biblScope[@unit='page']/@from");
            MetadataSingle lPage = new MetadataSingle(originalNlm, "/article/front/article-meta/lpage",
                                                    extractedNlm, "//monogr/imprint/biblScope[@unit='page']/@to");
            String expRange = fPage.hasExpected() && lPage.hasExpected() ?
                    fPage.getExpectedValue() + "--" + lPage.getExpectedValue() : "";
            String extrRange = fPage.hasExtracted() && lPage.hasExtracted() ?
                    fPage.getExtractedValue() + "--" + lPage.getExtractedValue() : "";
            MetadataSingle pageRange = new MetadataSingle(expRange, extrRange);
            pageRanges.add(pageRange);
            pageRange.print(mode, "pages");
            
            
            // Publication date
            List<String> expectedPubDate = XMLTools.extractTextAsList(originalNlm, "/article/front/article-meta/pub-date");
            expectedPubDate = EvaluationUtils.removeLeadingZerosFromDate(expectedPubDate);
            List<Node> extractedPubDates = XMLTools.extractNodes(extractedNlm, "//teiHeader//date[@type='published']");
            List<String> extractedPubDate = Lists.newArrayList();
            if (!extractedPubDates.isEmpty()) {
                Node pubDate = extractedPubDates.get(0);
                String date = pubDate.getTextContent();
                if (pubDate.getAttributes().getNamedItem("when") != null) {
                    date = pubDate.getAttributes().getNamedItem("when").getTextContent();
                }
                extractedPubDate = Lists.newArrayList(date.split("-"));
                extractedPubDate = EvaluationUtils.removeLeadingZerosFromDate(extractedPubDate);
            }
            
            MetadataSingle year = new MetadataSingle(StringUtils.join(expectedPubDate, "---"),
                    StringUtils.join(extractedPubDate, "---"));
            year.setComp(EvaluationUtils.yearComparator);
            years.add(year);
            year.print(mode, "year");
  
            
            // DOI
            MetadataSingle doi = new MetadataSingle(originalNlm, "/article/front/article-meta/article-id[@pub-id-type='doi']",
                                                        extractedNlm, "//teiHeader//idno[@type='DOI']");
            dois.add(doi);
            doi.print(mode, "DOI");
            
            if (mode == 1) {
                System.out.println("1");
            }
        }
        
        if (mode != 1) {
            System.out.println("==== Summary (" + iter.size() + " docs)====");
        
            PrecisionRecall titlePR = new PrecisionRecall().build(titles);
            titlePR.print("Title");

            PrecisionRecall abstractPR = new PrecisionRecall().build(abstracts);
            abstractPR.print("Abstract");
        
            PrecisionRecall keywordsPR = new PrecisionRecall().build(keywords);
            keywordsPR.print("Keywords");
        
            PrecisionRecall authorsPR = new PrecisionRecall().build(authors);
            authorsPR.print("Authors");

            PrecisionRecall affiliationsPR = new PrecisionRecall().build(affiliations);
            affiliationsPR.print("Affiliations");
            
            PrecisionRecall authorsAffiliationsPR = new PrecisionRecall().build(authorsAffiliations);
            authorsAffiliationsPR.print("Author - affiliation");
        
            PrecisionRecall emailsPR = new PrecisionRecall().build(emails);
            emailsPR.print("Emails");
     
            PrecisionRecall authorsEmailsPR = new PrecisionRecall().build(authorsEmails);
            authorsEmailsPR.print("Author - email");
        
            PrecisionRecall journalPR = new PrecisionRecall().build(journals);
            journalPR.print("Journal");

            PrecisionRecall volumePR = new PrecisionRecall().build(volumes);
            volumePR.print("Volume");

            PrecisionRecall issuePR = new PrecisionRecall().build(issues);
            issuePR.print("Issue");

            PrecisionRecall pageRangePR = new PrecisionRecall().build(pageRanges);
            pageRangePR.print("Pages");
 
            PrecisionRecall yearPR = new PrecisionRecall().build(years);
            yearPR.print("Year");
        
            PrecisionRecall doiPR = new PrecisionRecall().build(dois);
            doiPR.print("DOI");

            List<PrecisionRecall> results = Lists.newArrayList(
                titlePR, authorsPR, affiliationsPR, emailsPR, abstractPR, 
                keywordsPR, yearPR, doiPR);
        
            double avgPrecision = 0;
            double avgRecall = 0;
            double avgF1 = 0;
            for (PrecisionRecall result : results) {
                avgPrecision += result.getPrecision();
                avgRecall += result.getRecall();
                avgF1 += result.getF1();
            }
            avgPrecision /= results.size();
            avgRecall /= results.size();
            avgF1 /= results.size();
  
            System.out.printf("Average precision\t\t%4.2f\n", 100 * avgPrecision);
            System.out.printf("Average recall\t\t%4.2f\n", 100 * avgRecall);
            System.out.printf("Average F1 score\t\t%4.2f\n", 100 * avgF1);
        }
    }

    public static void main(String[] args) throws AnalysisException, IOException, TransformationException, ParserConfigurationException, SAXException, JDOMException, XPathExpressionException, TransformerException {
        if (args.length != 3 && args.length != 4) {
            System.out.println("Usage: FinalMetadataExtractionEvaluation <input dir> <orig extension> <extract extension>");
            return;
        }
        String directory = args[0];
        String origExt = args[1];
        String extrExt = args[2];
        int mode = 0;
        if (args.length == 4 && args[3].equals("csv")) {
            mode = 1;
        }
        if (args.length == 4 && args[3].equals("q")) {
            mode = 2;
        }

        GrobidFinalMetadataExtractionEvaluation e = new GrobidFinalMetadataExtractionEvaluation();
        NlmIterator iter = new NlmIterator(directory, origExt, extrExt);
        e.evaluate(mode, iter);
    }

}
