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

import pl.edu.icm.cermine.tools.distance.CosineDistance;
import pl.edu.icm.cermine.tools.distance.SmithWatermanDistance;
import pl.edu.icm.cermine.tools.XMLTools;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.*;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import org.apache.commons.lang.StringUtils;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.DOMOutputter;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import pl.edu.icm.cermine.evaluation.tools.*;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.exception.TransformationException;
import pl.edu.icm.cermine.tools.TextUtils;

/**
 * @author Pawel Szostek (p.szostek@icm.edu.pl)
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public final class FinalMetadataExtractionEvaluation {

    private static class PrecisionRecall {

        public int correct;
        public int expected;
        public int extracted;
        
        public double precision;
        public double recall;
        
        public PrecisionRecall() {
            correct = 0;
            expected = 0;
            extracted = 0;
            precision = -1.;
            recall = -1.;
        }
        
        public PrecisionRecall buildForSingle(List<MetadataSingle> metadataList) {
            for (MetadataSingle metadata : metadataList) {
                correct += metadata.correctSize();
                expected += metadata.expectedSize();
                extracted += metadata.extractedSize();
            }
            return this;
        }
        
        public PrecisionRecall buildForList(List<MetadataList> metadataList) {
            int precisions = 0;
            int recalls = 0;
            for (MetadataList metadata : metadataList) {
                if (metadata.getPrecision() != null) {
                    precision += metadata.getPrecision();
                    precisions++;
                }
                if (metadata.getRecall() != null) {
                    recall += metadata.getRecall();
                    recalls++;
                }
            }
            precision /= precisions;
            recall /= recalls;
            return this;
        }
        
        public PrecisionRecall buildForRelation(List<MetadataRelation> metadataList) {
            int precisions = 0;
            int recalls = 0;
            for (MetadataRelation metadata : metadataList) {
                if (metadata.getPrecision() != null) {
                    precision += metadata.getPrecision();
                    precisions++;
                }
                if (metadata.getRecall() != null) {
                    recall += metadata.getRecall();
                    recalls++;
                }
            }
            precision /= precisions;
            recall /= recalls;
            return this;
        }

        @Override
        public String toString() {
            return "PrecisionRecall{" + "correct=" + correct + ", expected=" + expected + ", extracted=" + extracted + '}';
        }
        
        public void print(String name) {
            System.out.println(name + ": " + toString());
            System.out.printf(name + ": precision: %4.2f" + ", recall: %4.2f" + ", F1: %4.2f\n\n",
                    calculatePrecision() == null ? -1. : 100 * calculatePrecision(), 
                    calculateRecall() == null ? -1. : 100 * calculateRecall(), 
                    calculateF1() == null ? -1. : 100 * calculateF1());
        }

        public Double calculateRecall() {
            if (recall != -1.) {
                return recall;
            }
            if (expected == 0) {
                return null;
            }
            return (double) correct / expected;
        }
        
        public Double calculatePrecision() {
            if (precision != -1.) {
                return precision;
            }
            if (extracted == 0) {
                return null;
            }
            return (double) correct / extracted;
        }
        
        public Double calculateF1() {
            Double prec = calculatePrecision();
            Double rec = calculateRecall();
            if (prec == null || rec == null) {
                return null;
            }
            return 2 * prec * rec / (prec + rec);
        }
    }

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
        List<MetadataSingle> issns = new ArrayList<MetadataSingle>();
        
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
            MetadataSingle title = readMetadataSingle(originalNlm, "/article/front/article-meta//article-title",
                                                        extractedNlm, "/article/front/article-meta//article-title");
            titles.add(title);
            title.correct = false;
            String expTitleNorm = title.expectedValue.replaceAll("[^a-zA-Z0-9]", "");
            String extrTitleNorm = title.extractedValue.replaceAll("[^a-zA-Z0-9]", "");
            if (compareStringsSW(title.expectedValue, title.extractedValue) >= 0.9 ||
                        (!expTitleNorm.isEmpty() && expTitleNorm.equals(extrTitleNorm))) {
                title.correct = true;
            }
            title.print("title");         

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
                author = author.replaceAll("[^a-zA-Z ]", "");
                expectedAuthors.add(author);
            }
            
            List<Node> extractedAuthorNodes = XMLTools.extractNodes(extractedNlm, "/article/front/article-meta/contrib-group/contrib[@contrib-type='author'][string-name]");

            List<String> extractedAuthors = new ArrayList<String>();
            for (Node authorNode : extractedAuthorNodes) {
                List<String> names = XMLTools.extractChildrenTextFromNode(authorNode, "string-name");
                if (names.isEmpty()) {
                    continue;
                }
                extractedAuthors.add(names.get(0).replaceAll("[^a-zA-Z ]", ""));
            }

            MetadataList author = new MetadataList(expectedAuthors, extractedAuthors);
            authors.add(author);
            author.print("author");
            
            
            // Affiliations
            Set<String> expectedAffiliationsSet = Sets.newHashSet(XMLTools.extractTextAsList(originalNlm, "/article/front/article-meta//aff"));
            Set<String> extractedAffiliationsSet = Sets.newHashSet(XMLTools.extractTextAsList(extractedNlm, "/article/front/article-meta//aff"));
            List<String> expectedAffiliations = Lists.newArrayList(expectedAffiliationsSet);
            List<String> extractedAffiliations = Lists.newArrayList(extractedAffiliationsSet);
            MetadataList affiliation = new MetadataList(expectedAffiliations, extractedAffiliations);
            affiliations.add(affiliation);
            affiliation.print("affiliation");
            
            
            // Author - Affiliation relation
            MetadataRelation authorAffiliation = new MetadataRelation();
            
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
            MetadataList email = readMetadataList(originalNlm, "/article/front/article-meta/contrib-group/contrib[@contrib-type='author']//email",
                                                    extractedNlm, "/article/front/article-meta/contrib-group/contrib[@contrib-type='author']//email");
            emails.add(email);
            email.print("email");
            
            
            // Author - Email relations
            MetadataRelation authorEmail = new MetadataRelation();
            
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
            
            
            // Abstract
            MetadataSingle abstrakt = readMetadataSingle(originalNlm, "/article/front/article-meta/abstract",
                                                        extractedNlm, "/article/front/article-meta/abstract");
            abstracts.add(abstrakt);
            abstrakt.correct = compareStringsSW(abstrakt.expectedValue, abstrakt.extractedValue) >= 0.9;
            abstrakt.print("abstract");
            
            
            // Keywords
            MetadataList keyword = readMetadataList(originalNlm, "/article/front/article-meta//kwd",
                                                    extractedNlm, "/article/front/article-meta/kwd-group/kwd");
            keywords.add(keyword);
            keyword.print("keywords");
            
            
            // Journal title
            MetadataSingle journal = readMetadataSingle(originalNlm, "/article/front/journal-meta//journal-title",
                                                        extractedNlm, "/article/front/journal-meta/journal-title-group/journal-title");
            journals.add(journal);
            journal.correct = false;
            if (journal.hasExpected() && journal.hasExtracted()
                    && isSubsequence(journal.expectedValue.replaceAll("[^a-zA-Z]", "").toLowerCase(), journal.extractedValue.replaceAll("[^a-zA-Z]", "").toLowerCase())) {
                journal.correct = true;
            }
            journal.print("journal title");
            
            
            // Volume
            MetadataSingle volume = readMetadataSingle(originalNlm, "/article/front/article-meta/volume",
                                                        extractedNlm, "/article/front/article-meta/volume");
            volumes.add(volume);
            volume.print("volume");
            
            
            // Issue            
            MetadataSingle issue = readMetadataSingle(originalNlm, "/article/front/article-meta/issue",
                                                        extractedNlm, "/article/front/article-meta/issue");
            issues.add(issue);
            issue.print("issue");

            
            // Pages range
            MetadataSingle fPage = readMetadataSingle(originalNlm, "/article/front/article-meta/fpage",
                                                    extractedNlm, "/article/front/article-meta/fpage");
            MetadataSingle lPage = readMetadataSingle(originalNlm, "/article/front/article-meta/lpage",
                                                    extractedNlm, "/article/front/article-meta/lpage");
            String expRange = fPage.hasExpected() && lPage.hasExpected() ?
                    fPage.expectedValue + "--" + lPage.expectedValue : "";
            String extrRange = fPage.hasExtracted() && lPage.hasExtracted() ?
                    fPage.extractedValue + "--" + lPage.extractedValue : "";
            MetadataSingle pageRange = new MetadataSingle(expRange, extrRange);
            pageRanges.add(pageRange);
            pageRange.print("pages");
            
            
            // Publication date
            List<String> expectedPubDate = XMLTools.extractTextAsList(originalNlm, "/article/front/article-meta/pub-date");
            expectedPubDate = removeLeadingZerosFromDate(expectedPubDate);
            List<String> extractedPubDate = XMLTools.extractTextAsList(extractedNlm, "/article/front/article-meta/pub-date");
            extractedPubDate = removeLeadingZerosFromDate(extractedPubDate);
            
            MetadataSingle year = new MetadataSingle(StringUtils.join(expectedPubDate, "---"),
                    StringUtils.join(extractedPubDate, "---"));
            year.correct = DateComparator.yearsMatch(expectedPubDate, extractedPubDate);
            years.add(year);
            year.print("year");
            
            
            // DOI
            MetadataSingle doi = readMetadataSingle(originalNlm, "/article/front/article-meta/article-id[@pub-id-type='doi']",
                                                        extractedNlm, "/article/front/article-meta/article-id[@pub-id-type='doi']");
            dois.add(doi);
            doi.print("DOI");
            
            
            // Journal ISSN
            MetadataSingle issn = readMetadataSingle(originalNlm, "/article/front/journal-meta/issn[@pub-type='ppub']",
                                                        extractedNlm, "/article/front/journal-meta/issn[@pub-type='ppub']");
            issns.add(issn);
            issn.print("ISSN");
            
        }
      
        System.out.println("==== Summary (" + iter.size() + " docs)====");
        
        PrecisionRecall titlePR = new PrecisionRecall().buildForSingle(titles);
        titlePR.print("Title");

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
        
        PrecisionRecall abstractPR = new PrecisionRecall().buildForSingle(abstracts);
        abstractPR.print("Abstract");
        
        PrecisionRecall keywordsPR = new PrecisionRecall().buildForList(keywords);
        keywordsPR.print("Keywords");
        
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
        
        PrecisionRecall issnPR = new PrecisionRecall().buildForSingle(issns);
        issnPR.print("ISSN");

        List<PrecisionRecall> results = Lists.newArrayList(
                titlePR, authorsPR, affiliationsPR, emailsPR, abstractPR, 
                keywordsPR, journalPR, volumePR, issuePR, pageRangePR, yearPR,
                doiPR/*, issnPR*/);
        
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

    private static double calculatePrecision(List<String> expected, List<String> extracted) {
        if (extracted.isEmpty()) {
            return .0;
        }
        int correct = 0;
        CosineDistance cos = new CosineDistance();
        
        List<String> tmp = new ArrayList<String>(expected);
        external:
        for (String partExt : extracted) {
            for (String partExp : tmp) {
                if (cos.compare(TextUtils.tokenize(partExt), TextUtils.tokenize(partExp))+0.001 > Math.sqrt(2) / 2) {
                    ++correct;
                    tmp.remove(partExp);
                    continue external;
                }
            }
        }
        return (double) correct / extracted.size();
    }

    private static double calculateRecall(List<String> expected, List<String> extracted) {
        int correct = 0;
        CosineDistance cos = new CosineDistance();
        List<String> tmp = new ArrayList<String>(expected);
        external:
        for (String partExt : extracted) {
            internal:
            for (String partExp : tmp) {
                if (cos.compare(TextUtils.tokenize(partExt), TextUtils.tokenize(partExp))+0.001 > Math.sqrt(2) / 2) {
                    ++correct;
                    tmp.remove(partExp);
                    continue external;
                }
            }
        }
        return (double) correct / expected.size();
    }

    private static double compareStringsSW(String expectedText, String extractedText) {
        List<String> expectedTokens = TextUtils.tokenize(expectedText.trim());
        List<String> extractedTokens = TextUtils.tokenize(extractedText.trim());
        SmithWatermanDistance distanceFunc = new SmithWatermanDistance(.0, .0);
        double distance = distanceFunc.compare(expectedTokens, extractedTokens);
        return 2*distance / (double) (expectedTokens.size()+extractedTokens.size());
    }

    static List<String> removeLeadingZerosFromDate(List<String> strings) {
        List<String> ret = new ArrayList<String>();
        for (String string : strings) {
            String[] parts = string.split("\\s");
            if (parts.length > 1) {
                List<String> newDate = new ArrayList<String>();
                for (String part : parts) {
                    newDate.add(part.replaceFirst("^0+(?!$)", ""));
                }
                ret.add(StringUtils.join(newDate, " "));
            } else {
                ret.add(string);
            }
        }
        return ret;
    }

    static boolean isSubsequence(String str, String sub) {
        if (sub.isEmpty()) {
            return true;
        }
        if (str.isEmpty()) {
            return false;
        }
        if (str.charAt(0) == sub.charAt(0)) {
            return isSubsequence(str.substring(1), sub.substring(1));
        }
        return isSubsequence(str.substring(1), sub);
    }
    
    static org.w3c.dom.Document elementToW3CDocument(org.jdom.Element elem) throws JDOMException {
        org.jdom.Document metaDoc = new org.jdom.Document();
        metaDoc.setRootElement(elem);
        org.jdom.output.DOMOutputter domOutputter = new DOMOutputter();
        return domOutputter.output(metaDoc);
    }

    static String outputDoc(Document document) throws IOException, TransformerException {
        OutputFormat format = new OutputFormat(document);
        format.setLineWidth(65);
        format.setIndenting(true);
        format.setIndent(2);
        Writer out = new StringWriter();
        XMLSerializer serializer = new XMLSerializer(out, format);
        serializer.serialize(document);
        return out.toString();
    }
    
    private MetadataSingle readMetadataSingle(org.w3c.dom.Document originalNlm, String originalXPath, 
            org.w3c.dom.Document extractedNlm, String extractedXPath) throws XPathExpressionException {
        String expected = XMLTools.extractTextFromNode(originalNlm, originalXPath).trim();
        String extracted = XMLTools.extractTextFromNode(extractedNlm, extractedXPath).trim();
        return new MetadataSingle(expected, extracted);
    }

    private MetadataList readMetadataList(org.w3c.dom.Document originalNlm, String originalXPath, 
            org.w3c.dom.Document extractedNlm, String extractedXPath) throws XPathExpressionException {
        List<String> expected = XMLTools.extractTextAsList(originalNlm, originalXPath);
        List<String> extracted = XMLTools.extractTextAsList(extractedNlm, extractedXPath);
        return new MetadataList(expected, extracted);
    }
    
    private static class MetadataSingle {
        private String expectedValue;
        private String extractedValue;
        private Boolean correct;

        public MetadataSingle(String expectedValue, String extractedValue) {
            this.expectedValue = expectedValue;
            this.extractedValue = extractedValue;
            this.correct = null;
        }

        public boolean isCorrect() {
            return correct == null ? expectedValue.equals(extractedValue) : correct;
        }
        
        public boolean hasExpected() {
            return expectedValue != null && !expectedValue.isEmpty();
        }
        
        public boolean hasExtracted() {
            return extractedValue != null && !extractedValue.isEmpty();
        }
        
        public int expectedSize() {
            return expectedValue == null || expectedValue.isEmpty() ? 0 : 1;
        }
        
        public int extractedSize() {
            return extractedValue == null || extractedValue.isEmpty() ? 0 : 1;
        }
        
        public int correctSize() {
            return hasExpected() && hasExtracted() && isCorrect() ? 1 : 0;
        }
    
        public void print(String name) {
            System.out.println("");
            System.out.println("Expected " + name + ": " + expectedValue);
            System.out.println("Extracted " + name + ": " + extractedValue);
            System.out.println("Correct: " + (isCorrect() ? "yes" : "no"));
        }
        
    }
    
    private static class MetadataList {
        private List<String> expectedValue;
        private List<String> extractedValue;
        private Double precision = -1.;
        private Double recall = -1.;

        public MetadataList(List<String> expectedValue, List<String> extractedValue) {
            this.expectedValue = expectedValue;
            this.extractedValue = extractedValue;
            this.precision = -1.;
            this.recall = -1.;
        }

        public boolean hasExpected() {
            return expectedValue != null && !expectedValue.isEmpty();
        }
        
        public boolean hasExtracted() {
            return extractedValue != null && !extractedValue.isEmpty();
        }
        
        public Double getPrecision() {
            if (precision != -1.) {
                return precision;
            }
            if (!hasExtracted()) {
                return null;
            }
            return calculatePrecision(expectedValue, extractedValue);
        }
        
        public Double getRecall() {
            if (recall != -1.) {
                return recall;
            }
            if (!hasExpected()) {
                return null;
            }
            return calculateRecall(expectedValue, extractedValue);
        }
        
        public void print(String name) {
            System.out.println("");
            System.out.println("Expected " + name + ":");
            for (String expected : expectedValue) {
                System.out.println("    "+expected);
            }
            System.out.println("Extracted " + name + ":");
            for (String extracted : extractedValue) {
                System.out.println("    "+extracted);
            }
            System.out.printf("Precision: %4.2f\n", getPrecision());
            System.out.printf("Recall: %4.2f\n", getRecall());
        }
        
    }
    
    private static class MetadataRelation {
        private Set<StringRelation> expectedValue = new HashSet<StringRelation>();
        private Set<StringRelation> extractedValue = new HashSet<StringRelation>();

        public void addExpected(StringRelation relation) {
            expectedValue.add(relation);
        }
        
        public void addExtracted(StringRelation relation) {
            extractedValue.add(relation);
        }
        
        public boolean hasExpected() {
            return expectedValue != null && !expectedValue.isEmpty();
        }
        
        public boolean hasExtracted() {
            return extractedValue != null && !extractedValue.isEmpty();
        }
        
        public Double getPrecision() {
            if (!hasExtracted()) {
                return null;
            }
            int correct = 0;
            CosineDistance cos = new CosineDistance();
            
            List<StringRelation> tmp = new ArrayList<StringRelation>(expectedValue);
            external:
            for (StringRelation partExt : extractedValue) {
                for (StringRelation partExp : tmp) {
                    if (cos.compare(TextUtils.tokenize(partExt.element1), TextUtils.tokenize(partExp.element1))+0.001 > Math.sqrt(2) / 2
                        && cos.compare(TextUtils.tokenize(partExt.element2), TextUtils.tokenize(partExp.element2))+0.001 > Math.sqrt(2) / 2) {
                        ++correct;
                        tmp.remove(partExp);
                        continue external;
                    }
                }
            }
            return (double) correct / extractedValue.size();
        }
        
        public Double getRecall() {
            if (!hasExpected()) {
                return null;
            }
            int correct = 0;
            CosineDistance cos = new CosineDistance();
            List<StringRelation> tmp = new ArrayList<StringRelation>(expectedValue);
            external:
            for (StringRelation partExt : extractedValue) {
                internal:
                for (StringRelation partExp : tmp) {
                    if (cos.compare(TextUtils.tokenize(partExt.element1), TextUtils.tokenize(partExp.element1))+0.001 > Math.sqrt(2) / 2
                        && cos.compare(TextUtils.tokenize(partExt.element2), TextUtils.tokenize(partExp.element2))+0.001 > Math.sqrt(2) / 2) {
                        ++correct;
                        tmp.remove(partExp);
                        continue external;
                    }
                }
            }
            return (double) correct / expectedValue.size();
        }
        
        public void print(String name) {
            System.out.println("");
            System.out.println("Expected " + name + ":");
            for (StringRelation expected : expectedValue) {
                System.out.println("    "+expected);
            }
            System.out.println("Extracted " + name + ":");
            for (StringRelation extracted : extractedValue) {
                System.out.println("    "+extracted);
            }
            System.out.printf("Precision: %4.2f\n", getPrecision());
            System.out.printf("Recall: %4.2f\n", getRecall());
        }
        
    }

    private static class StringRelation {
        private String element1;
        private String element2;

        public StringRelation(String element1, String element2) {
            this.element1 = element1;
            this.element2 = element2;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final StringRelation other = (StringRelation) obj;
            if ((this.element1 == null) ? (other.element1 != null) : !this.element1.equals(other.element1)) {
                return false;
            }
            if ((this.element2 == null) ? (other.element2 != null) : !this.element2.equals(other.element2)) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 67 * hash + (this.element1 != null ? this.element1.hashCode() : 0);
            hash = 67 * hash + (this.element2 != null ? this.element2.hashCode() : 0);
            return hash;
        }

        @Override
        public String toString() {
            return element1 + " --- " + element2;
        }
        
    }
    
}
