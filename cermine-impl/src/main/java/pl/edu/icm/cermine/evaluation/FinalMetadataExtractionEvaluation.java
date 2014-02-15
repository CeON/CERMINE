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
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
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

/**
 *
 * @author Pawel Szostek (p.szostek@icm.edu.pl)
 */

/*
 * TODO
 * 
 * 2 references
 */
public final class FinalMetadataExtractionEvaluation {

    private boolean verbose = false;

    public FinalMetadataExtractionEvaluation(boolean verbose) {
        this.verbose = verbose;
    }

    private void printVerbose(String text) {
        if (verbose) {
            System.out.println(text);
        }
    }

    private static class PrecissonRecall {

        public int correct;
        public int expected;
        public int extracted;
        
        public PrecissonRecall() {
            correct = 0;
            expected = 0;
            extracted = 0;
        }

        @Override
        public String toString() {
            return "PrecissonRecall{" + "correct=" + correct + ", expected=" + expected + ", extracted=" + extracted + '}';
        }

        public Double calculateRecall() {
            if (expected == 0) {
                return null;
            } else {
                return (double) correct / expected;
            }
        }
        
        public Double calculatePrecission() {
            if (extracted == 0) {
                return null;
            } else {
                return (double) correct / extracted;
            }
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

        PrecissonRecall issn = new PrecissonRecall();
        PrecissonRecall doi = new PrecissonRecall();
        PrecissonRecall volume = new PrecissonRecall();
        PrecissonRecall issue = new PrecissonRecall();
        PrecissonRecall pages = new PrecissonRecall();
        PrecissonRecall dateYear = new PrecissonRecall();
        PrecissonRecall dateFull = new PrecissonRecall();
        PrecissonRecall journalTitle = new PrecissonRecall();

        List<Double> abstractRates = new ArrayList<Double>(iter.size());
        List<Double> titleRates = new ArrayList<Double>(iter.size());

        List<Double> keywordPrecisions = new ArrayList<Double>(iter.size());
        List<Double> keywordRecalls = new ArrayList<Double>(iter.size());

        List<Double> authorsPrecisions = new ArrayList<Double>(iter.size());
        List<Double> authorsRecalls = new ArrayList<Double>(iter.size());
        
        List<Double> affPrecisions = new ArrayList<Double>(iter.size());
        List<Double> affRecalls = new ArrayList<Double>(iter.size());

        int ii = 0;
        for (NlmPair pair : iter) {
            ii++;
            System.out.println("");
            printVerbose(">>>>>>>>> "+ii);
            
            printVerbose(pair.getExtractedNlm().getPath());

            org.w3c.dom.Document originalNlm = documentBuilder.parse(new FileInputStream(pair.getOriginalNlm()));
            org.w3c.dom.Document extractedNlm = documentBuilder.parse(new FileInputStream(pair.getExtractedNlm()));

            String expectedTitle = XMLTools.extractTextFromNode(originalNlm, "/article/front/article-meta//article-title");
            String extractedTitle = XMLTools.extractTextFromNode(extractedNlm, "/article/front/article-meta/title-group/article-title");
           
            List<Node> expectedAuthorsNodes = XMLTools.extractNodes(originalNlm, "/article/front/article-meta/contrib-group/contrib[@contrib-type='author']/name");
            
            List<String> expectedAuthors = new ArrayList<String>();
            for (Node authorNode : expectedAuthorsNodes) {
                List<String> givenNames = XMLTools.extractChildrenTextFromNode(authorNode, "given-names");
                List<String> surnames = XMLTools.extractChildrenTextFromNode(authorNode, "surname");
                String author = StringUtils.join(givenNames, " ")+" "+StringUtils.join(surnames, " ");
                author = author.replaceAll("[^a-zA-Z ]", "");
                expectedAuthors.add(author);
            }
            
            List<String> extractedAuthors1 = XMLTools.extractTextAsList(extractedNlm, "/article/front/article-meta/contrib-group/contrib[@contrib-type='author']/string-name");
            List<String> extractedAuthors = new ArrayList<String>();
            for (String author : extractedAuthors1) {
                extractedAuthors.add(author.replaceAll("[^a-zA-Z ]", ""));
            }

            List<String> expectedKeywords = XMLTools.extractTextAsList(originalNlm, "/article/front/article-meta//kwd");
            List<String> extractedKeywords = XMLTools.extractTextAsList(extractedNlm, "/article/front/article-meta/kwd-group/kwd");

            String expectedJournalTitle = XMLTools.extractTextFromNode(originalNlm, "/article/front/journal-meta//journal-title");
            String extractedJournalTitle = XMLTools.extractTextFromNode(extractedNlm, "/article/front/journal-meta/journal-title-group/journal-title");

            String expectedAbstract = XMLTools.extractTextFromNode(originalNlm, "/article/front/article-meta/abstract");
            String extractedAbstract = XMLTools.extractTextFromNode(extractedNlm, "/article/front/article-meta/abstract");

            String expectedDoi = XMLTools.extractTextFromNode(originalNlm, "/article/front/article-meta/article-id[@pub-id-type='doi']");
            String extractedDoi = XMLTools.extractTextFromNode(extractedNlm, "/article/front/article-meta/article-id[@pub-id-type='doi']");

            String expectedISSN = XMLTools.extractTextFromNode(originalNlm, "/article/front/journal-meta/issn[@pub-type='ppub']");
            String extractedISSN = XMLTools.extractTextFromNode(extractedNlm, "/article/front/journal-meta/issn[@pub-type='ppub']");

            String expectedVolume = XMLTools.extractTextFromNode(originalNlm, "/article/front/article-meta/volume");
            String extractedVolume = XMLTools.extractTextFromNode(extractedNlm, "/article/front/article-meta/volume");

            String expectedIssue = XMLTools.extractTextFromNode(originalNlm, "/article/front/article-meta/issue");
            String extractedIssue = XMLTools.extractTextFromNode(extractedNlm, "/article/front/article-meta/issue");

            String expectedFPage = XMLTools.extractTextFromNode(originalNlm, "/article/front/article-meta/fpage");
            String extractedFPage = XMLTools.extractTextFromNode(extractedNlm, "/article/front/article-meta/fpage");

            String expectedLPage = XMLTools.extractTextFromNode(originalNlm, "/article/front/article-meta/lpage");
            String extractedLPage = XMLTools.extractTextFromNode(extractedNlm, "/article/front/article-meta/lpage");
            
            List<String> expectedPubDate = XMLTools.extractTextAsList(originalNlm, "/article/front/article-meta/pub-date");
            expectedPubDate = removeLeadingZerosFromDate(expectedPubDate);
            List<String> extractedPubDate = XMLTools.extractTextAsList(extractedNlm, "/article/front/article-meta/pub-date");
            extractedPubDate = removeLeadingZerosFromDate(extractedPubDate);
            
            Set<String> expectedAffiliationsSet = Sets.newHashSet(XMLTools.extractTextAsList(originalNlm, "/article/front/article-meta//aff"));
            Set<String> extractedAffiliationsSet = Sets.newHashSet(XMLTools.extractTextAsList(extractedNlm, "/article/front/article-meta//aff"));
            List<String> expectedAffiliations = Lists.newArrayList(expectedAffiliationsSet);
            List<String> extractedAffiliations = Lists.newArrayList(extractedAffiliationsSet);

           
            //equality measures
            if (!expectedVolume.isEmpty()) {
                if (expectedVolume.equals(extractedVolume)) {
                    ++volume.correct;
                }
                ++volume.expected;
            }
            if (!extractedVolume.isEmpty()) {
                volume.extracted++;
            }
            if (!expectedIssue.isEmpty()) {
                if (expectedIssue.equals(extractedIssue)) {
                    ++issue.correct;
                }
                ++issue.expected;
            }
            if (!extractedIssue.isEmpty()) {
                issue.extracted++;
            }
            if (!expectedISSN.isEmpty()) {
                if (extractedISSN.equals(expectedISSN)) {
                    ++issn.correct;
                }
                ++issn.expected;
            }
            if (!extractedISSN.isEmpty()) {
                issn.extracted++;
            }
            if (!expectedDoi.isEmpty()) {
                if (expectedDoi.equals(extractedDoi)) {
                    ++doi.correct;
                }
                ++doi.expected;
            }
            if (!extractedDoi.isEmpty()) {
                doi.extracted++;
            }
            if (!expectedFPage.isEmpty() && !expectedLPage.isEmpty()) {
                if (expectedFPage.equals(extractedFPage) && expectedLPage.equals(extractedLPage)) {
                    ++pages.correct;
                }
                ++pages.expected;
            }
            if (!extractedFPage.isEmpty() && !extractedLPage.isEmpty()) {
                pages.extracted++;
            }

            if (!expectedPubDate.isEmpty()) {
                Boolean yearsMatch = DateComparator.yearsMatch(expectedPubDate, extractedPubDate);
                if (yearsMatch != null) {
                    if (yearsMatch) {
                        ++dateYear.correct;
                    }
                    ++dateYear.expected;
                }
            }
            if (!extractedPubDate.isEmpty()) {
                dateYear.extracted++;
                dateFull.extracted++;
            }

            //Smith-Waterman distance measures
            if (expectedAbstract.length() > 0) {
                abstractRates.add(compareStringsSW(expectedAbstract, extractedAbstract));
            } else {
                abstractRates.add(null);
            }
            if (expectedTitle.length() > 0) {
                titleRates.add(compareStringsSW(expectedTitle, extractedTitle));
            } else {
                titleRates.add(null);
            }
            if (!expectedJournalTitle.isEmpty()) {
                journalTitle.expected++;
            }
            if (!extractedJournalTitle.isEmpty()) {
                journalTitle.extracted++;
                if (isSubsequence(expectedJournalTitle.replaceAll("[^a-zA-Z]", "").toLowerCase(), extractedJournalTitle.replaceAll("[^a-zA-Z]", "").toLowerCase())) {
                    journalTitle.correct++;
                }
            }
            
            //precision + recall
            if (expectedAuthors.size() > 0) {
                authorsRecalls.add(calculateRecall(expectedAuthors, extractedAuthors));
            } else {
                authorsRecalls.add(null);
            }
            if (extractedAuthors.size() > 0) {
                authorsPrecisions.add(calculatePrecision(expectedAuthors, extractedAuthors));
            } else {
                authorsPrecisions.add(null);
            }
            if (expectedKeywords.size() > 0) {
                keywordRecalls.add(calculateRecall(expectedKeywords, extractedKeywords));
            } else {
                keywordRecalls.add(null);
            }
            if (extractedKeywords.size() > 0) {
                keywordPrecisions.add(calculatePrecision(expectedKeywords, extractedKeywords));
            } else {
                keywordPrecisions.add(null);
            }
            if (expectedAffiliations.size() > 0) {
                affRecalls.add(calculateRecall(expectedAffiliations, extractedAffiliations));
            } else {
                affRecalls.add(null);
            }
            if (extractedAffiliations.size() > 0) {
                affPrecisions.add(calculatePrecision(expectedAffiliations, extractedAffiliations));
            } else {
                affPrecisions.add(null);
            }

            System.out.println("");
            printVerbose(">>> Expected authors: ");
            for (String author : expectedAuthors) {
                printVerbose(author);
            }

            System.out.println("");
            printVerbose(">>> Extracted authors: ");
            for (String author : extractedAuthors) {
                printVerbose(author);
            }

            System.out.println("");
            printVerbose(">>> Expected keywords: ");
            for (String keyword : expectedKeywords) {
                printVerbose(keyword);
            }

            System.out.println("");
            printVerbose(">>> Extracted keywords: ");
            for (String keyword : extractedKeywords) {
                printVerbose(keyword);
            }

            printVerbose(">>> Expected journal title: " + expectedJournalTitle);
            printVerbose(">>> Extracted journal title: " + extractedJournalTitle);

            printVerbose(">>> Expected article title: " + expectedTitle);
            printVerbose(">>> Extracted article title: " + extractedTitle);

            printVerbose(">>> Expected article abstract: " + expectedAbstract);
            printVerbose(">>> Extracted article abstract: " + extractedAbstract);

            printVerbose(">>> Expected doi: " + expectedDoi);
            printVerbose(">>> Extracted doi: " + extractedDoi);
            
            printVerbose(">>> Expected issn: " + expectedISSN);
            printVerbose(">>> Extracted issn: " + extractedISSN);

            printVerbose(">>> Expected volume: " + expectedVolume);
            printVerbose(">>> Extracted volume: " + extractedVolume);
            
            printVerbose(">>> Expected issue: " + expectedIssue);
            printVerbose(">>> Extracted issue: " + extractedIssue);
            
            printVerbose(">>> Expected pages: " + expectedFPage + " " + expectedLPage);
            printVerbose(">>> Extracted pages: " + extractedFPage + " " + extractedLPage);
            
            printVerbose(">>> Expected date: ");
            for (String date : expectedPubDate) {
                printVerbose(date);
            }

            printVerbose(">>> Extracted date: ");
            for (String date : extractedPubDate) {
                printVerbose(date);
            }
            printVerbose(">>> Expected affs: ");
            for (String aff : expectedAffiliations) {
                printVerbose(aff);
            }

            printVerbose(">>> Extracted affs: ");
            for (String aff : extractedAffiliations) {
                printVerbose(aff);
            }
            
            
            printVerbose("abstract " + abstractRates.get(abstractRates.size()-1));
            printVerbose("title " + titleRates.get(titleRates.size()-1));
            printVerbose("journal title " + journalTitle);
      
            System.out.println("");
            printVerbose("authors precission " + authorsPrecisions.get(authorsPrecisions.size()-1));
            printVerbose("authors recall " + authorsRecalls.get(authorsPrecisions.size()-1));
          
            System.out.println("");
            printVerbose("aff precission " + affPrecisions.get(affPrecisions.size()-1));
            printVerbose("aff recall " + affRecalls.get(affPrecisions.size()-1));
          
            System.out.println("");
            printVerbose("keywords precission " + keywordPrecisions.get(keywordPrecisions.size()-1));
            printVerbose("keywords recall " + keywordRecalls.get(keywordPrecisions.size()-1));

            printVerbose("date years" + dateYear);
            printVerbose("doi" + doi);
            printVerbose("issn" + issn);
            printVerbose("volume" + volume);
            printVerbose("issue" + issue);
            printVerbose("pages" + pages);
        }
        
        Double value;
        System.out.println("==== Summary (" + iter.size() + " docs)====");
        if ((value = calculateAverage(abstractRates)) != null) {
            System.out.printf("abstract avg (SW) \t\t%4.2f\n", 100 * value);
        }
        if ((value = calculateAverage(titleRates)) != null) {
            System.out.printf("title avg (SW) \t\t\t%4.2f\n", 100 * value);
        }
        if ((value = journalTitle.calculatePrecission()) != null) {
            System.out.printf("journal title precission\t\t%4.2f\n", 100 * value);
        }
        if ((value = journalTitle.calculateRecall()) != null) {
            System.out.printf("journal title recall\t\t%4.2f\n", 100 * value);
        }
        if ((value = calculateAverage(authorsPrecisions)) != null) {
            System.out.printf("authors precision avg (EQ)\t%4.2f\n", 100 * value);
        }
        if ((value = calculateAverage(authorsRecalls)) != null) {
            System.out.printf("authors recall avg (EQ)\t\t%4.2f\n", 100 * value);
        }
        if ((value = calculateAverage(affPrecisions)) != null) {
            System.out.printf("aff precision avg (EQ)\t%4.2f\n", 100 * value);
        }
        if ((value = calculateAverage(affRecalls)) != null) {
            System.out.printf("aff recall avg (EQ)\t\t%4.2f\n", 100 * value);
        }
        if ((value = calculateAverage(keywordPrecisions)) != null) {
            System.out.printf("keywords precision avg (EQ)\t%4.2f\n", 100 * value);
        }
        if ((value = calculateAverage(keywordRecalls)) != null) {
            System.out.printf("keywords recall avg (EQ)\t%4.2f\n", 100 * value);
        }
        if ((value = dateYear.calculatePrecission()) != null) {
            System.out.printf("date year precission avg\t\t%4.2f\n", 100 * value);
        }
        if ((value = dateYear.calculateRecall()) != null) {
            System.out.printf("date year recall avg\t\t%4.2f\n", 100 * value);
        }
        if ((value = doi.calculatePrecission()) != null) {
            System.out.printf("doi precission\t\t%4.2f\n", 100 * value);
        }
        if ((value = doi.calculateRecall()) != null) {
            System.out.printf("doi recall\t\t%4.2f\n", 100 * value);
        }
        if ((value = issn.calculatePrecission()) != null) {
            System.out.printf("issn precission\t\t%4.2f\n", 100 * value);
        }
        if ((value = issn.calculateRecall()) != null) {
            System.out.printf("issn recall\t\t%4.2f\n", 100 * value);
        }
        if ((value = volume.calculatePrecission()) != null) {
            System.out.printf("volume precission\t\t%4.2f\n", 100 * value);
        }
        if ((value = volume.calculateRecall()) != null) {
            System.out.printf("volume recall\t\t%4.2f\n", 100 * value);
        }
        if ((value = issue.calculatePrecission()) != null) {
            System.out.printf("issue precission\t\t%4.2f\n", 100 * value);
        }
        if ((value = issue.calculateRecall()) != null) {
            System.out.printf("issue recall\t\t%4.2f\n", 100 * value);
        }
        if ((value = pages.calculatePrecission()) != null) {
            System.out.printf("pages precission avg\t\t%4.2f\n", 100 * value);
        }
        if ((value = pages.calculateRecall()) != null) {
            System.out.printf("pages recall avg\t\t%4.2f\n", 100 * value);
        }
    }

    public static void main(String[] args) throws AnalysisException, IOException, TransformationException, ParserConfigurationException, SAXException, JDOMException, XPathExpressionException, TransformerException {
        if (args.length != 3) {
            System.out.println("Usage: FinalMetadataExtractionEvaluation <input dir> <orig extension> <extract extension>");
            return;
        }
        boolean verbose = true;
        String directory = args[0];
        String origExt = args[1];
        String extrExt = args[2];

        FinalMetadataExtractionEvaluation e = new FinalMetadataExtractionEvaluation(verbose);
        NlmIterator iter = new NlmIterator(directory, origExt, extrExt);
        e.evaluate(iter);
    }

    private static Double calculateAverage(List<Double> values) {
        int all = 0;
        double sum = .0;
        for (Double value : values) {
            if (value != null) {
                ++all;
                sum += value;
            }
        }
        return sum / all;
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
                if (cos.compare(StringTools.tokenize(partExt), StringTools.tokenize(partExp))+0.001 > Math.sqrt(2) / 2) {
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
                if (cos.compare(StringTools.tokenize(partExt), StringTools.tokenize(partExp))+0.001 > Math.sqrt(2) / 2) {
                    ++correct;
                    tmp.remove(partExp);
                    continue external;
                }
            }
        }
        return (double) correct / expected.size();
    }

    private static double compareStringsSW(String expectedText, String extractedText) {
        List<String> expectedTokens = StringTools.tokenize(expectedText);
        List<String> extractedTokens = StringTools.tokenize(extractedText);
        SmithWatermanDistance distanceFunc = new SmithWatermanDistance(.0, .0);
        double distance = distanceFunc.compare(expectedTokens, extractedTokens);
        return distance / (double) expectedTokens.size();
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
        OutputFormat format = new OutputFormat(document); //document is an instance of org.w3c.dom.Document
        format.setLineWidth(65);
        format.setIndenting(true);
        format.setIndent(2);
        Writer out = new StringWriter();
        XMLSerializer serializer = new XMLSerializer(out, format);
        serializer.serialize(document);
        return out.toString();
    }
}
