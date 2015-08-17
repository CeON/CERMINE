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
import org.xml.sax.SAXException;
import pl.edu.icm.cermine.evaluation.tools.MetadataRelation.StringRelation;
import pl.edu.icm.cermine.evaluation.tools.*;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.exception.TransformationException;

/**
 * @author Pawel Szostek (p.szostek@icm.edu.pl)
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public final class FinalMetadataExtractionEvaluation {

    public void evaluate(NlmIterator iter) throws AnalysisException, IOException, TransformationException, ParserConfigurationException, SAXException, JDOMException, XPathExpressionException, TransformerException {

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

        List<MetadataSingle> titles = new ArrayList<MetadataSingle>();
        List<MetadataList> authors = new ArrayList<MetadataList>();
        List<MetadataList> affiliations = new ArrayList<MetadataList>();
        List<MetadataRelation> authorsAffiliations = new ArrayList<MetadataRelation>();
        List<MetadataList> emails = new ArrayList<MetadataList>();
        List<MetadataRelation> authorsEmails = new ArrayList<MetadataRelation>();
        List<MetadataSingle> abstracts = new ArrayList<MetadataSingle>();
        List<MetadataList> keywords = new ArrayList<MetadataList>();
        List<MetadataSingle> journals = new ArrayList<MetadataSingle>();
        List<MetadataSingle> volumes = new ArrayList<MetadataSingle>();
        List<MetadataSingle> issues = new ArrayList<MetadataSingle>();
        List<MetadataSingle> pageRanges = new ArrayList<MetadataSingle>();
        List<MetadataSingle> years = new ArrayList<MetadataSingle>();
        List<MetadataSingle> dois = new ArrayList<MetadataSingle>();
        
        int i = 0;
        for (NlmPair pair : iter) {
            i++;
            System.out.println("");
            System.out.println(">>>>>>>>> "+i);
            
            System.out.println(pair.getExtractedNlm().getPath());

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
                                                        extractedNlm, "/article/front/article-meta//article-title");
            title.setComp(EvaluationUtils.swComparator);
            titles.add(title);
            title.print("title");

            
            // Abstract
            MetadataSingle abstrakt = new MetadataSingle(originalNlm, "/article/front/article-meta/abstract",
                                                        extractedNlm, "/article/front/article-meta/abstract");
            abstrakt.setComp(EvaluationUtils.swComparator);
            abstracts.add(abstrakt);
            abstrakt.print("abstract");
            
            
            // Keywords
            MetadataList keyword = new MetadataList(originalNlm, "/article/front/article-meta//kwd",
                                                    extractedNlm, "/article/front/article-meta/kwd-group/kwd");
            keywords.add(keyword);
            keyword.print("keywords");
            
            
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
            
            List<Node> extractedAuthorNodes = XMLTools.extractNodes(extractedNlm, "/article/front/article-meta/contrib-group/contrib[@contrib-type='author'][string-name]");

            List<String> extractedAuthors = new ArrayList<String>();
            for (Node authorNode : extractedAuthorNodes) {
                List<String> names = XMLTools.extractChildrenTextFromNode(authorNode, "string-name");
                if (names.isEmpty()) {
                    continue;
                }
                extractedAuthors.add(names.get(0));
            }

            MetadataList author = new MetadataList(expectedAuthors, extractedAuthors);
            author.setComp(EvaluationUtils.authorComparator);
            authors.add(author);
            author.print("author");
            
            
            // Affiliations
            Set<String> expectedAffiliationsSet = Sets.newHashSet(XMLTools.extractTextAsList(originalNlm, "/article/front/article-meta//aff"));
            Set<String> extractedAffiliationsSet = Sets.newHashSet(XMLTools.extractTextAsList(extractedNlm, "/article/front/article-meta//aff"));
            List<String> expectedAffiliations = Lists.newArrayList(expectedAffiliationsSet);
            List<String> extractedAffiliations = Lists.newArrayList(extractedAffiliationsSet);
            MetadataList affiliation = new MetadataList(expectedAffiliations, extractedAffiliations);
            affiliation.setComp(EvaluationUtils.cosineComparator());
            affiliations.add(affiliation);
            affiliation.print("affiliation");
            
            
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
            
            List<Node> extractedAffiliationNodes = XMLTools.extractNodes(extractedNlm, "/article/front/article-meta//aff[@id]");
            Map<String, String> extractedAffiliationMap = new HashMap<String, String>();
            for (Node extractedAffiliationNode : extractedAffiliationNodes) {
                String id = extractedAffiliationNode.getAttributes().getNamedItem("id").getNodeValue();
                String aff = XMLTools.extractTextFromNode(extractedAffiliationNode);
                extractedAffiliationMap.put(id, aff);
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
            
            for (Node extractedAuthorNode : extractedAuthorNodes) {
                String authorName = extractedAuthors.get(extractedAuthorNodes.indexOf(extractedAuthorNode));
                List<Node> xrefs = XMLTools.extractChildrenNodesFromNode(extractedAuthorNode, "xref");
                for (Node xref : xrefs) {
                    if ("aff".equals(xref.getAttributes().getNamedItem("ref-type").getNodeValue())) {
                        String affId = xref.getAttributes().getNamedItem("rid").getNodeValue();
                        for (String id : affId.split(" ")) {
                            String aff = extractedAffiliationMap.get(id);
                            if (aff != null) {
                                authorAffiliation.addExtracted(new StringRelation(authorName, aff));
                            }
                        }
                    }
                }
            }
            
            authorsAffiliations.add(authorAffiliation);
            authorAffiliation.print("author - affiliation");
            
            
            // Email addresses
            MetadataList email = new MetadataList(originalNlm, "/article/front/article-meta/contrib-group/contrib[@contrib-type='author']//email",
                                                    extractedNlm, "/article/front/article-meta/contrib-group/contrib[@contrib-type='author']//email");
            email.setComp(EvaluationUtils.emailComparator);
            emails.add(email);
            email.print("email");
            
            
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
            for (Node extractedAuthorNode : extractedAuthorNodes) {
                String authorName = extractedAuthors.get(extractedAuthorNodes.indexOf(extractedAuthorNode));
                
                for (String emailAddress : XMLTools.extractChildrenTextFromNode(extractedAuthorNode, "email")) {
                    authorEmail.addExtracted(new StringRelation(authorName, emailAddress));
                }
            }
            authorsEmails.add(authorEmail);
            authorEmail.print("author - email");
            
            
            // Journal title
            MetadataSingle journal = new MetadataSingle(originalNlm, "/article/front/journal-meta//journal-title",
                                                        extractedNlm, "/article/front/journal-meta/journal-title-group/journal-title");
            journal.setComp(EvaluationUtils.journalComparator);
            journals.add(journal);
            journal.print("journal title");
            
            
            // Volume
            MetadataSingle volume = new MetadataSingle(originalNlm, "/article/front/article-meta/volume",
                                                        extractedNlm, "/article/front/article-meta/volume");
            volumes.add(volume);
            volume.print("volume");
            
            
            // Issue            
            MetadataSingle issue = new MetadataSingle(originalNlm, "/article/front/article-meta/issue",
                                                        extractedNlm, "/article/front/article-meta/issue");
            issues.add(issue);
            issue.print("issue");

            
            // Pages range
            MetadataSingle fPage = new MetadataSingle(originalNlm, "/article/front/article-meta/fpage",
                                                    extractedNlm, "/article/front/article-meta/fpage");
            MetadataSingle lPage = new MetadataSingle(originalNlm, "/article/front/article-meta/lpage",
                                                    extractedNlm, "/article/front/article-meta/lpage");
            String expRange = fPage.hasExpected() && lPage.hasExpected() ?
                    fPage.getExpectedValue() + "--" + lPage.getExpectedValue() : "";
            String extrRange = fPage.hasExtracted() && lPage.hasExtracted() ?
                    fPage.getExtractedValue() + "--" + lPage.getExtractedValue() : "";
            MetadataSingle pageRange = new MetadataSingle(expRange, extrRange);
            pageRanges.add(pageRange);
            pageRange.print("pages");
            
            
            // Publication date
            List<String> expectedPubDate = XMLTools.extractTextAsList(originalNlm, "/article/front/article-meta/pub-date");
            expectedPubDate = EvaluationUtils.removeLeadingZerosFromDate(expectedPubDate);
            List<String> extractedPubDate = XMLTools.extractTextAsList(extractedNlm, "/article/front/article-meta/pub-date");
            extractedPubDate = EvaluationUtils.removeLeadingZerosFromDate(extractedPubDate);
            
            MetadataSingle year = new MetadataSingle(StringUtils.join(expectedPubDate, "---"),
                    StringUtils.join(extractedPubDate, "---"));
            year.setComp(EvaluationUtils.yearComparator);
            years.add(year);
            year.print("year");
            
            
            // DOI
            MetadataSingle doi = new MetadataSingle(originalNlm, "/article/front/article-meta/article-id[@pub-id-type='doi']",
                                                        extractedNlm, "/article/front/article-meta/article-id[@pub-id-type='doi']");
            dois.add(doi);
            doi.print("DOI");
        }
      
        System.out.println("==== Summary (" + iter.size() + " docs)====");
        
        PrecisionRecall titlePR = new PrecisionRecall().buildForSingle(titles);
        titlePR.print("Title");

        PrecisionRecall abstractPR = new PrecisionRecall().buildForSingle(abstracts);
        abstractPR.print("Abstract");
        
        PrecisionRecall keywordsPR = new PrecisionRecall().buildForList(keywords);
        keywordsPR.print("Keywords");
        
        PrecisionRecall authorsPR = new PrecisionRecall().buildForList(authors);
        authorsPR.print("Authors");

        PrecisionRecall affiliationsPR = new PrecisionRecall().buildForList(affiliations);
        affiliationsPR.print("Affiliations");
        
        PrecisionRecall authorsAffiliationsPR = new PrecisionRecall().buildForRelation(authorsAffiliations);
        authorsAffiliationsPR.print("Author - affiliation");
        
        PrecisionRecall emailsPR = new PrecisionRecall().buildForList(emails);
        emailsPR.print("Emails");

        PrecisionRecall authorsEmailsPR = new PrecisionRecall().buildForRelation(authorsEmails);
        authorsEmailsPR.print("Author - email");
        
        PrecisionRecall journalPR = new PrecisionRecall().buildForSingle(journals);
        journalPR.print("Journal");

        PrecisionRecall volumePR = new PrecisionRecall().buildForSingle(volumes);
        volumePR.print("Volume");

        PrecisionRecall issuePR = new PrecisionRecall().buildForSingle(issues);
        issuePR.print("Issue");

        PrecisionRecall pageRangePR = new PrecisionRecall().buildForSingle(pageRanges);
        pageRangePR.print("Pages");
        
        PrecisionRecall yearPR = new PrecisionRecall().buildForSingle(years);
        yearPR.print("Year");
        
        PrecisionRecall doiPR = new PrecisionRecall().buildForSingle(dois);
        doiPR.print("DOI");

        List<PrecisionRecall> results = Lists.newArrayList(
                titlePR, authorsPR, affiliationsPR, emailsPR, abstractPR, 
                keywordsPR, journalPR, volumePR, issuePR, pageRangePR, yearPR,
                doiPR);
        
        double avgPrecision = 0;
        double avgRecall = 0;
        double avgF1 = 0;
        for (PrecisionRecall result : results) {
            avgPrecision += result.calculatePrecision();
            avgRecall += result.calculateRecall();
            avgF1 += result.calculateF1();
        }
        avgPrecision /= results.size();
        avgRecall /= results.size();
        avgF1 /= results.size();
  
        System.out.printf("Average precision\t\t%4.2f\n", 100 * avgPrecision);
        System.out.printf("Average recall\t\t%4.2f\n", 100 * avgRecall);
        System.out.printf("Average F1 score\t\t%4.2f\n", 100 * avgF1);
    }

    public static void main(String[] args) throws AnalysisException, IOException, TransformationException, ParserConfigurationException, SAXException, JDOMException, XPathExpressionException, TransformerException {
        if (args.length != 3) {
            System.out.println("Usage: FinalMetadataExtractionEvaluation <input dir> <orig extension> <extract extension>");
            return;
        }
        String directory = args[0];
        String origExt = args[1];
        String extrExt = args[2];

        FinalMetadataExtractionEvaluation e = new FinalMetadataExtractionEvaluation();
        NlmIterator iter = new NlmIterator(directory, origExt, extrExt);
        e.evaluate(iter);
    }
    
}
