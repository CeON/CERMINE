package pl.edu.icm.cermine.evaluation;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import pl.edu.icm.cermine.evaluation.tools.SmithWatermanDistance;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.DOMOutputter;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPath;

import pl.edu.icm.cermine.PdfNLMMetadataExtractor;
import pl.edu.icm.cermine.evaluation.tools.CosineDistance;
import pl.edu.icm.cermine.evaluation.tools.PdfNlmIterator;
import pl.edu.icm.cermine.evaluation.tools.PdfNlmPair;
import pl.edu.icm.cermine.evaluation.tools.XMLTools;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.exception.TransformationException;

import org.apache.commons.lang.StringUtils;
/**
 *
 * @author Pawel Szostek (p.szostek@icm.edu.pl)
 */

public final class FinalEffectEvaluator {
	
	private static class CorrectAllPair {
		public CorrectAllPair() {
			correct = 0;
			all = 0;
		}
		public Integer correct;
		public Integer all;
		
		@Override
		public String toString() {
			return "[Correct: " + correct + ", all: " + all + "]";
		}
	}

    public static void main(String[] args) throws AnalysisException, IOException, TransformationException, ParserConfigurationException, SAXException,JDOMException, XPathExpressionException, TransformerException {
        if (args.length < 1) {
            System.out.println("Usage: FinalEffectEvaluator <input dir>");
            return;
        }
        PdfNLMMetadataExtractor metadataExtractor = new PdfNLMMetadataExtractor();
        
        javax.xml.parsers.DocumentBuilderFactory dbf = javax.xml.parsers.DocumentBuilderFactory.newInstance();
        dbf.setValidating(false);
        dbf.setFeature("http://xml.org/sax/features/namespaces", false);
        dbf.setFeature("http://xml.org/sax/features/validation", false);
        dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
        dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);

        javax.xml.parsers.DocumentBuilder documentBuilder = null;

    	try {
    		documentBuilder = dbf.newDocumentBuilder();
    	}
    	catch (javax.xml.parsers.ParserConfigurationException ex) {
    		ex.printStackTrace();
    		System.exit(1);
    	}

        SAXBuilder builder = new SAXBuilder("org.apache.xerces.parsers.SAXParser");
        builder.setValidation(false);
        builder.setFeature("http://xml.org/sax/features/validation", false);
        builder.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
        builder.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);

        PdfNlmIterator iter = new PdfNlmIterator(args[0]);
        
        CorrectAllPair issn = new CorrectAllPair();
        CorrectAllPair doi = new CorrectAllPair();
        CorrectAllPair urn = new CorrectAllPair();
        CorrectAllPair volume = new CorrectAllPair();
        CorrectAllPair issue = new CorrectAllPair();
        CorrectAllPair pages = new CorrectAllPair();
        CorrectAllPair dates = new CorrectAllPair();

        List<Double> abstractRates = new ArrayList<Double>(iter.size());
        List<Double> titleRates = new ArrayList<Double>(iter.size());
        List<Double> journalTitleRates = new ArrayList<Double>(iter.size());
        
        List<Double> keywordPrecisions = new ArrayList<Double>(iter.size());
        List<Double> keywordRecalls = new ArrayList<Double>(iter.size());

        List<Double> authorsPrecisions = new ArrayList<Double>(iter.size());
        List<Double> authorsRecalls = new ArrayList<Double>(iter.size());


        for (PdfNlmPair pair : iter) {

        	org.w3c.dom.Document originalNlm = documentBuilder.parse(new FileInputStream(pair.getNlm()));

        	org.jdom.Element metaElement = metadataExtractor.extractMetadata(new FileInputStream(pair.getPdf()));
        	org.w3c.dom.Document extractedNlm = ElementToW3CDocument(metaElement);
        	
        	System.out.println(">>>>>>>>> " + pair.getPdf().getName());
            
//        	System.out.println(outputDoc(originalNlm));
//        	System.out.println(outputDoc(extractedNlm));
        	
            String expectedTitle = XMLTools.extractTextFromNode(originalNlm, "/article/front/article-meta/title-group/article-title");
            String extractedTitle = XMLTools.extractTextFromNode(extractedNlm, "/article/front/article-meta/title-group/article-title");
            
            List<String> expectedAuthors = XMLTools.extractTextAsList(originalNlm, "/article/front/article-meta/contrib-group/contrib//name");
            List<String> extractedAuthors = XMLTools.extractTextAsList(extractedNlm, "/article/front/article-meta/contrib-group/contrib[@contrib-type='author']/string-name");

            List<String> expectedKeywords = XMLTools.extractTextAsList(originalNlm, "/article/front/article-meta/kwd-group/kwd");
            List<String> extractedKeywords = XMLTools.extractTextAsList(extractedNlm, "/article/front/article-meta/kwd-group/kwd");
            
            String expectedJournalTitle = XMLTools.extractTextFromNode(originalNlm, "/article/front/journal-meta/journal-title-group/journal-title");
            String extractedJournalTitle = XMLTools.extractTextFromNode(extractedNlm, "/article/front/journal-meta/journal-title-group/journal-title");
            
            String expectedPublisherName = XMLTools.extractTextFromNode(originalNlm, "/article/front//publisher-name");
            String extractedPublisherName = XMLTools.extractTextFromNode(extractedNlm, "/article/front//publisher-name");

            String expectedAbstract = XMLTools.extractTextFromNode(originalNlm, "/article/front/article-meta/abstract");
            String extractedAbstract = XMLTools.extractTextFromNode(extractedNlm, "/article/front/article-meta/abstract");
            		
            String expectedDoi =  XMLTools.extractTextFromNode(originalNlm, "/article/front/article-meta/article-id[@pub-id-type='doi']");
            String extractedDoi =  XMLTools.extractTextFromNode(extractedNlm, "/article/front/article-meta/article-id[@pub-id-type='doi']");
            
            String expectedISSN =  XMLTools.extractTextFromNode(originalNlm, "/article/front/journal-meta/issn[@pub-type='ppub']");
            String extractedISSN =  XMLTools.extractTextFromNode(extractedNlm, "/article/front/journal-meta/issn[@pub-type='ppub']");
            
            String expectedURN = XMLTools.extractTextFromNode(originalNlm, "/article/front/article-meta/article-id[@pub-id-type='urn']");
            String extractedURN = XMLTools.extractTextFromNode(extractedNlm, "/article/front/article-meta/article-id[@pub-id-type='urn']");

            String expectedFPage = XMLTools.extractTextFromNode(originalNlm, "/article/front/article-meta/fpage");
            String extractedFPage = XMLTools.extractTextFromNode(extractedNlm, "/article/front/article-meta/fpage");
            
            String expectedLPage = XMLTools.extractTextFromNode(originalNlm, "/article/front/article-meta/lpage") ;
            String extractedLPage = XMLTools.extractTextFromNode(extractedNlm, "/article/front/article-meta/lpage") ;
            
            List<String> expectedPubDate = XMLTools.extractTextAsList(originalNlm, "/article/front/article-meta/pub-date");
            expectedPubDate = removeLeadingZerosFromDate(expectedPubDate);
            List<String> extractedPubDate = XMLTools.extractTextAsList(extractedNlm, "/article/front/article-meta/pub-date");
            extractedPubDate = removeLeadingZerosFromDate(extractedPubDate);
            
            
            //equality measures
            if(!expectedISSN.isEmpty()) {
            	if(extractedISSN.equals(expectedISSN)) {
            		++issn.correct;
            	}
            	++issn.all;
            }
            if(!expectedDoi.isEmpty()) {
	            if(expectedDoi.equals(extractedDoi)) {
	            	++doi.correct;
	            }
	            ++doi.all;
            }
            if(!expectedURN.isEmpty()) {
	            if(expectedURN.equals(extractedURN)) {
	            	++urn.correct;
	            }
	            ++urn.all;
            }
            if(!expectedFPage.isEmpty() && !expectedLPage.isEmpty()) {
                if(expectedFPage.equals(extractedFPage) && expectedLPage.equals(extractedLPage)) {
                	++pages.correct;
                }
                ++pages.all;
            }

            if(!expectedPubDate.isEmpty()) {
	            if(new CosineDistance().compare(expectedPubDate, extractedPubDate) > 0.95) {
	            	++dates.correct;
	            }
	            ++dates.all;
            }
            
            //Smith-Waterman distance measures
            if(expectedAbstract.length() > 0) {
            	abstractRates.add(compareStringsSW(expectedAbstract, extractedAbstract));
            } else {
            	abstractRates.add(null);
            }
            if(expectedTitle.length() > 0) {
            	titleRates.add(compareStringsSW(expectedTitle, extractedTitle));
            } else {
            	titleRates.add(null);
            }
            if(expectedJournalTitle.length() > 0) {
            	journalTitleRates.add(compareStringsSW(expectedTitle, extractedTitle));
            } else {
            	journalTitleRates.add(null);
            }
            
            //precision + recall
            if(expectedAuthors.size() > 0) {
            	authorsPrecisions.add(calculatePrecision(expectedAuthors, extractedAuthors));
            	authorsRecalls.add(calculateRecall(expectedAuthors, extractedAuthors));
            } else {
            	authorsPrecisions.add(null);
            	authorsRecalls.add(null);
            }
            if(expectedKeywords.size() > 0) {
            	keywordPrecisions.add(calculatePrecision(expectedKeywords, extractedKeywords));
            	keywordRecalls.add(calculateRecall(expectedKeywords, extractedKeywords));
            } else {
            	keywordPrecisions.add(null);
            	keywordRecalls.add(null);
            }
            
            System.out.println(">>> Expected authors: ");
            for(String author: expectedAuthors) {
            	System.out.println(author);
            }
            
            System.out.println(">>> Extracted authors: ");
            for(String author: extractedAuthors) {
            	System.out.println(author);
            }
            
            System.out.println(">>> Expected keywords: ");
            for(String keyword: expectedKeywords) {
            	System.out.println(keyword);
            }
            
            System.out.println(">>> Extracted keywords: ");
            for(String keyword: extractedKeywords) {
            	System.out.println(keyword);
            }
            
            System.out.println(">>> Expected journal title: " + expectedJournalTitle);
            System.out.println(">>> Extracted journal title: " + extractedJournalTitle);
            
            System.out.println(">>> Expected publisher name: " + expectedPublisherName);
            System.out.println(">>> Extracted publisher name: " + extractedPublisherName);
            
            System.out.println(">>> Expected article title: " + expectedTitle);
            System.out.println(">>> Extracted article title: " + extractedTitle);

            System.out.println(">>> Expected article abstract: " + expectedAbstract);
            System.out.println(">>> Extracted article abstract: " + extractedAbstract);
            
            System.out.println(">>> Expected doi: " + expectedDoi);
            System.out.println(">>> Extracted doi: " + extractedDoi);
            
            System.out.println(">>> Expected date: ");
            for(String date: expectedPubDate) {
            	System.out.println(date);
            }

            System.out.println(">>> Extracted date: ");
            for(String date: extractedPubDate) {
            	System.out.println(date);
            }
            System.out.println("abstract " + abstractRates);
            System.out.println("title " + titleRates);
            System.out.println("journal title " + journalTitleRates);
            System.out.println("namesP " + authorsPrecisions);
            System.out.println("namesR " + authorsRecalls);
            System.out.println("keywordsP " + keywordPrecisions);
            System.out.println("keywordsR " + keywordRecalls);
            System.out.println("dates" + dates);
            System.out.println("doi" + doi);
            System.out.println("URN" + urn);
            System.out.println("pages" + pages);
            
        }

    }
   
    private static Double calculatePrecision(List<String> expected, List<String> extracted) {
    	if(extracted.size() == 0) {
    		return .0;
    	}
    	Integer correct = 0;
    	CosineDistance cos = new CosineDistance();
    	external: for(String partExt: extracted) {
    		internal: for(String partExp: expected) {
    			if(cos.compare(tokenize(partExt), tokenize(partExp)) > Math.sqrt(2)/2) {
    				++correct;
    				continue external;
    			}
    		}
    	}
    	return (double)correct/extracted.size();
    }
   
    private static Double calculateRecall(List<String> expected, List<String> extracted) {
    	Integer correct = 0;
    	CosineDistance cos = new CosineDistance();
    	external: for(String partExt: extracted) {
    		internal: for(String partExp: expected) {
    			if(cos.compare(tokenize(partExt), tokenize(partExp)) > Math.sqrt(2)/2) {
    				++correct;
    				continue external;
    			}
    		}
    	}
    	return (double)correct/expected.size();
    }
    
    private static Double compareStringsSW(String expectedText, String extractedText) {
		List<String> expectedTokens = tokenize(expectedText);
		List<String> extractedTokens = tokenize(extractedText);
		SmithWatermanDistance distanceFunc = new SmithWatermanDistance(.0, .0);
		Double distance = distanceFunc.compare(expectedTokens, extractedTokens);
		return distance/(double)expectedTokens.size();
	}

	static List<String> removeLeadingZerosFromDate(List<String> strings) {
    	List<String> ret = new ArrayList<String>();
    	for(String string: strings) {
    		String[] parts = string.split("\\s");
    		if(parts.length > 1) {
	    		List<String> newDate = new ArrayList<String>();
	    		for(String part: parts) {
	    			newDate.add(part.replaceFirst("^0+(?!$)", ""));
	    		}
	    		ret.add(StringUtils.join(newDate, " "));
    		} else {
    			ret.add(string);
    		}
    	}
    	return ret;
    }
    
    static org.w3c.dom.Document ElementToW3CDocument(org.jdom.Element elem) throws JDOMException {
        org.jdom.Document metaDoc = new org.jdom.Document();
        metaDoc.setRootElement(elem);
        org.jdom.output.DOMOutputter domOutputter = new DOMOutputter();
        org.w3c.dom.Document document = domOutputter.output(metaDoc);
        return document;
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
      
      public static List<String> tokenize(String text) {
          List<String> roughRet = new ArrayList<String>(Arrays.asList(text.split(" |\n|,|\\. |&|;|:|\\-")));
          List<String> ret = new ArrayList<String>();
          for (String candidate : roughRet) {
              if (candidate.length() > 1) {
                  ret.add(candidate.toLowerCase());
              }
          }
          return ret;
      }
}
