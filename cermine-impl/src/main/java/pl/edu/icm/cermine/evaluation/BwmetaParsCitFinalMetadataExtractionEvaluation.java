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
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import pl.edu.icm.cermine.evaluation.tools.*;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.exception.TransformationException;

/**
 * @author Pawel Szostek (p.szostek@icm.edu.pl)
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public final class BwmetaParsCitFinalMetadataExtractionEvaluation {

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

        List<MetadataList> titles = new ArrayList<MetadataList>();
        List<MetadataList> authors = new ArrayList<MetadataList>();
        List<MetadataList> affiliations = new ArrayList<MetadataList>();
        List<MetadataList> emails = new ArrayList<MetadataList>();
        List<MetadataList> abstracts = new ArrayList<MetadataList>();
        List<MetadataList> keywords = new ArrayList<MetadataList>();
        
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
            MetadataList title = new MetadataList(originalNlm, "/bwmeta/element/name[not(@type)]",
                                                        extractedNlm, "//algorithm[@name='ParsHed']//title");
            title.setComp(EvaluationUtils.swComparator);
            titles.add(title);
            title.print("title");

            
            // Authors
            List<Node> expectedAuthorNodes = XMLTools.extractNodes(originalNlm, "/bwmeta/element/contributor[@role='author']");
            
            List<String> expectedAuthors = new ArrayList<String>();
            for (Node authorNode : expectedAuthorNodes) {
                List<Node> names = XMLTools.extractChildrenNodesFromNode(authorNode, "name");
                if (names.isEmpty()) {
                    continue;
                }
                for (Node n : names) {
                    if (n.getAttributes().getNamedItem("type") != null
                            && n.getAttributes().getNamedItem("type").getTextContent().equals("canonical")) {
                        expectedAuthors.add(n.getTextContent());//.replaceAll("[^a-zA-Z]", ""));
                        break;
                    }
                }
            }

            List<Node> extractedAuthorNodes = XMLTools.extractNodes(extractedNlm, "//algorithm[@name='ParsHed']//author");

            List<String> extractedAuthors = new ArrayList<String>();
            for (Node authorNode : extractedAuthorNodes) {
                String author = XMLTools.extractTextFromNode(authorNode);
                extractedAuthors.add(author);
            }

            MetadataList author = new MetadataList(expectedAuthors, extractedAuthors);
            author.setComp(EvaluationUtils.authorComparator);
            authors.add(author);
            author.print("author");
            
            
            // Affiliations
            Set<String> expectedAffiliationsSet = Sets.newHashSet(XMLTools.extractTextAsList(originalNlm, "/bwmeta/element/affiliation/text"));
            Set<String> extractedAffiliationsSet = Sets.newHashSet(XMLTools.extractTextAsList(extractedNlm, "//algorithm[@name='ParsHed']//affiliation"));
            List<String> expectedAffiliations = Lists.newArrayList(expectedAffiliationsSet);
            List<String> extractedAffiliations = Lists.newArrayList(extractedAffiliationsSet);
            MetadataList affiliation = new MetadataList(expectedAffiliations, extractedAffiliations);
            affiliation.setComp(EvaluationUtils.cosineComparator());
            affiliations.add(affiliation);
            affiliation.print("affiliation");
            
           
            // Email addresses
            MetadataList email = new MetadataList(originalNlm, "/bwmeta/element/contributor[@role='author']/attribute[@key='contact-email']/value",
                                                    extractedNlm, "//algorithm[@name='ParsHed']//email");
            email.setComp(EvaluationUtils.emailComparator);
            emails.add(email);
            email.print("email");
            

            // Abstract
            MetadataList abstrakt = new MetadataList(originalNlm, "/bwmeta/element/description[@type='abstract']",
                                                        extractedNlm, "//algorithm[@name='ParsHed']//abstract");
            abstrakt.setComp(EvaluationUtils.swComparator);
            abstracts.add(abstrakt);
            abstrakt.print("abstract");
            
            
            // Keywords
            MetadataList keyword = new MetadataList(originalNlm, "/bwmeta/element/tags[@type='keyword']/tag",
                                                    extractedNlm, "//algorithm[@name='ParsHed']//keyword");
            keywords.add(keyword);
            keyword.print("keywords");
        }
      
        System.out.println("==== Summary (" + iter.size() + " docs)====");
        
        PrecisionRecall titlePR = new PrecisionRecall().buildForList(titles);
        titlePR.print("Title");

        PrecisionRecall abstractPR = new PrecisionRecall().buildForList(abstracts);
        abstractPR.print("Abstract");
        
        PrecisionRecall keywordsPR = new PrecisionRecall().buildForList(keywords);
        keywordsPR.print("Keywords");
        
        PrecisionRecall authorsPR = new PrecisionRecall().buildForList(authors);
        authorsPR.print("Authors");

        PrecisionRecall affiliationsPR = new PrecisionRecall().buildForList(affiliations);
        affiliationsPR.print("Affiliations");

        PrecisionRecall emailsPR = new PrecisionRecall().buildForList(emails);
        emailsPR.print("Emails");

        List<PrecisionRecall> results = Lists.newArrayList(
                titlePR, authorsPR, affiliationsPR, emailsPR, abstractPR, 
                keywordsPR);
        
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

        BwmetaParsCitFinalMetadataExtractionEvaluation e = new BwmetaParsCitFinalMetadataExtractionEvaluation();
        NlmIterator iter = new NlmIterator(directory, origExt, extrExt);
        e.evaluate(iter);
    }

}
