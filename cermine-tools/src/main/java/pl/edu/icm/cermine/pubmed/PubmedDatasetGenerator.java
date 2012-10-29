package pl.edu.icm.cermine.pubmed;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import pl.edu.icm.cermine.PdfBxStructureExtractor;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.structure.model.BxDocument;
import pl.edu.icm.cermine.structure.model.BxZone;

public class PubmedDatasetGenerator {
	private static class Levenshtein {
		public Integer compare(List<String> s1, List<String> s2)
	    {
	        int retval = 0;
	        final int n = s1.size();
	        final int m = s2.size();
	        if (n == 0) {
	            retval = m;
	        } else if (m == 0) {
	            retval = n;
	        } else {
	            retval = compare(s1, n, s2, m);
	        }
	        return retval;
	    }

		private int compare(List<String> s1, final int n, List<String> s2, final int m) {
			int matrix[][] = new int[n + 1][m + 1];
			for (int i = 0; i <= n; i++) {
				matrix[i][0] = i;
			}
			for (int i = 0; i <= m; i++) {
				matrix[0][i] = i;
			}

			for (int i = 1; i <= n; i++) {
				String s1i = s1.get(i - 1);
				for (int j = 1; j <= m; j++) {
					String s2j = s2.get(j - 1);
					final int cost = s1i.equals(s2j) ? 0 : 1;
					matrix[i][j] = min3(matrix[i - 1][j] + 1,
							matrix[i][j - 1] + 1, matrix[i - 1][j - 1] + cost);
				}
			}
			return matrix[n][m];
		}

		private int min3(final int a, final int b, final int c) {
			return Math.min(Math.min(a, b), c);
		}
	}

	private static <T> List<T> slice(List<T> list, int index, int count) {
		List<T> result = new ArrayList<T>();
		if (index >= 0 && index < list.size()) {
			int end = index + count < list.size() ? index + count : list.size();
			for (int i = index; i < end; i++) {
				result.add(list.get(i));
			}
		}
		return result;
	}
	
	private static Integer min3(Integer int1, Integer int2, Integer int3) {
		return Math.min(int1, Math.min(int2, int3));
	}
	
	private static List<String> tokenize(String text) {
		List<String> roughRet = new ArrayList<String>(Arrays.asList(text.split(" |,|\\.|&|;|:|\\-")));
		List<String> ret = new ArrayList<String>();
		for(String candidate: roughRet) {
			if(!candidate.isEmpty()) {
				ret.add(candidate);
			}
		}
		return ret;
	}

	private static String joinStrings(List<String> strings) {
		StringBuilder ret = new StringBuilder();
		for(String str: strings) {
			ret.append(str).append(" ");
		}
		return ret.toString();
	}
	
	private static Integer levenshteinDistance(List<String> tokens1, List<String> tokens2) {
		Integer length1 = tokens1.size();
		Integer length2 = tokens2.size();
		Integer distance = 0;
		if(length1 == 0) {
			return length2;
		}
		else if(length2 == 0) {
			return length1;
		}
		else {
			if(!tokens1.get(0).equals(tokens2.get(0))) {
				distance = 1;
			}
			return min3(	levenshteinDistance(slice(tokens1, 1, length1-1), tokens2) + 1,
							levenshteinDistance(tokens1, slice(tokens2, 1, length2-1)) + 1,
							levenshteinDistance(slice(tokens1, 1, length1-1), slice(tokens2, 1, length2-1)) + distance
						);
		}
	}

	private static List<String> extractTextAsList(Node node) {
		List<String> ret = new ArrayList<String>();
		
		if(node == null) {
			return ret;
		}
		if(node.getChildNodes().getLength() == 0) {
			ret.add(node.getNodeValue());
		} else {
			for(Integer childIdx=0; childIdx < node.getChildNodes().getLength(); ++childIdx) {
				ret.addAll(extractTextAsList(node.getChildNodes().item(childIdx)));
			}
		}
		return ret;
	}

	private static String extractTextFromNodes(Node node) {
		StringBuilder ret = new StringBuilder();
		
		if(node == null) {
			return "";
		}
		if(node.getChildNodes().getLength() == 0) {
			return node.getNodeValue() + " ";
		} else {
			for(Integer childIdx=0; childIdx < node.getChildNodes().getLength(); ++childIdx) {
				ret.append(extractTextFromNodes(node.getChildNodes().item(childIdx)));
			}
		}
		return ret.toString();
	}

	public static void main(String[] args) throws AnalysisException, ParserConfigurationException, SAXException, IOException, XPathExpressionException {
		if(args.length != 2) {
			System.err.println("Usage: <pubmed .nxml path> <corresponding .pdf path>");
			System.exit(1);
		}
		String nxmlPath = args[0];
		String pdfPath = args[1];
		
		XPath xpath = XPathFactory.newInstance().newXPath();
		
		InputStream pdfStream = new FileInputStream(pdfPath);
		InputStream nxmlStream = new FileInputStream(nxmlPath);

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setValidating(false);
		dbf.setFeature("http://xml.org/sax/features/namespaces", false);
		dbf.setFeature("http://xml.org/sax/features/validation", false);
		dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
		dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
		
		DocumentBuilder builder = dbf.newDocumentBuilder();
		Document domDoc = builder.parse(nxmlStream);

		PdfBxStructureExtractor structureExtractor = new PdfBxStructureExtractor();
		BxDocument bxDoc = structureExtractor.extractStructure(pdfStream);
		
		//abstract
		Node abstractNode = (Node) xpath.evaluate("/article/front/article-meta/abstract", domDoc, XPathConstants.NODE);
		String abstractString = extractTextFromNodes(abstractNode);
		//title
		String titleString = (String) xpath.evaluate("/article/front/article-meta/title-group/article-title", domDoc, XPathConstants.STRING);
		
		//journal title
		String journalTitleString = (String) xpath.evaluate("/article/front/journal-meta/journal-title", domDoc, XPathConstants.STRING);
		//journal publisher
		String journalPublisherString = (String) xpath.evaluate("/article/front/journal-meta/publisher/publisher-name", domDoc, XPathConstants.STRING); 
		//journal issn
		String journalISSNString = (String) xpath.evaluate("/article/front/journal-meta/issn", domDoc, XPathConstants.STRING);   
		
		String articleTypeString = null;
		//article type
		Object articleTypeResult = xpath.evaluate("/article/@article-type", domDoc, XPathConstants.NODESET);
		NodeList articleTypeProducts = (NodeList) articleTypeResult;
		if(articleTypeProducts.getLength() > 0) {
			articleTypeString = articleTypeProducts.item(0).getNodeValue();
		}
		
		//received date
		String receivedString = (String) xpath.evaluate("/article/front/article-meta/history/date[@date-type='received']", domDoc, XPathConstants.STRING);
		//accepted date
		String acceptedString = (String) xpath.evaluate("/article/front/article-meta/history/date[@date-type='accepted']", domDoc, XPathConstants.STRING);
		
		//publication date
		String pubdateString = null;
		if(((NodeList)xpath.evaluate("/article/front/article-meta/pub-date", domDoc, XPathConstants.NODESET)).getLength() > 1) {
			Node pubdateNode = (Node) xpath.evaluate("/article/front/article-meta/pub-date[@pub-type='epub']", domDoc, XPathConstants.NODE);
			pubdateString = extractTextFromNodes(pubdateNode);
		} else {
			Node pubdateNode = (Node) xpath.evaluate("/article/front/article-meta/pub-date[@pub-type='collection']", domDoc, XPathConstants.NODE);
			pubdateString = extractTextFromNodes(pubdateNode);
		}
		
		//keywords
		Node keywordsNode = (Node) xpath.evaluate("/article/front/article-meta/kwd-group", domDoc, XPathConstants.NODE);
		String keywordsString = extractTextFromNodes(keywordsNode);
		//DOI
		String doiString = (String) xpath.evaluate("/article/front/article-meta/article-id[@pub-id-type='doi']", domDoc, XPathConstants.STRING);
		//volume
		String volumeString = (String) xpath.evaluate("/article/front/article-meta/article-id/volume", domDoc, XPathConstants.STRING);
		//issue
		String issueString = (String) xpath.evaluate("/article/front/article-meta/article-id/issue", domDoc, XPathConstants.STRING);
		//first page
		String firstPageString = (String) xpath.evaluate("/article/front/article-meta/article-id/fpage", domDoc, XPathConstants.STRING);
		//last page
		String lastPageString = (String) xpath.evaluate("/article/front/article-meta/article-id/lpage", domDoc, XPathConstants.STRING);
		
		List<String> authorNames = new ArrayList<String>();
		List<String> authorEmails = new ArrayList<String>();
		List<String> authorAffiliation = new ArrayList<String>();
		List<String> editorNames = new ArrayList<String>();
		
		//editors
		NodeList editorsResult = (NodeList)xpath.evaluate("/article/front/article-meta/contrib-group/contrib[@contrib-type='editor']/name", domDoc, XPathConstants.NODESET);
		
		for(int nodeIdx=0; nodeIdx < editorsResult.getLength(); ++nodeIdx) {
			String name = (String)xpath.evaluate("given-names", editorsResult.item(nodeIdx), XPathConstants.STRING);
			String surname = (String)xpath.evaluate("surname", editorsResult.item(nodeIdx), XPathConstants.STRING);
			editorNames.add(name + " " + surname);
		}
		
		//author names
		NodeList authorsResult = (NodeList)xpath.evaluate("/article/front/article-meta/contrib-group/contrib[@contrib-type='author']", domDoc, XPathConstants.NODESET);
		for(int nodeIdx=0; nodeIdx < authorsResult.getLength(); ++nodeIdx) {
			Node curNode = authorsResult.item(nodeIdx);
			String name = (String)xpath.evaluate("name/given-names", curNode, XPathConstants.STRING);
			String surname = (String)xpath.evaluate("name/surname", curNode, XPathConstants.STRING);
			
			String email;
			try {
				email = (String)xpath.evaluate("address/email", curNode, XPathConstants.STRING);
			} catch (XPathExpressionException e) {
				email = "";
			}
			if(email.isEmpty()) {
				try {
					email  = (String)xpath.evaluate("email", curNode, XPathConstants.STRING);
				} catch (XPathExpressionException e) {
					//yaaay, probably there is no e-mail at all! => do nothing
				}
			}
			authorEmails.add(email);
			
			authorNames.add(name + " " + surname);
		}
		
		//authors' affiliations
		NodeList affResult = (NodeList)xpath.evaluate("/article/front/article-meta/aff", domDoc, XPathConstants.NODESET);
		for(int nodeIdx=0; nodeIdx < affResult.getLength(); ++nodeIdx) {
			String text = affResult.item(nodeIdx).getTextContent();
			authorAffiliation.add(text);
		}
		
		//article body
		System.out.println("body");
		Node bodyNode = (Node) xpath.evaluate("/article/body", domDoc, XPathConstants.NODE);
		List<String> bodyStrings = extractTextAsList(bodyNode);
//				new ArrayList<String>();
//		for(Integer bodyNodeIdx = 0; bodyNodeIdx < bodyNode.getChildNodes().getLength(); ++bodyNodeIdx) {
//			bodyStrings.add(extractTextFromNodes(bodyNode.getChildNodes().item(bodyNodeIdx)));
//		}
		
		//references
		NodeList refParentNode = (NodeList) xpath.evaluate("/article/back/ref-list", domDoc, XPathConstants.NODESET);
		List<String> refStrings = new ArrayList<String>();
		for(Integer refIdx = 0; refIdx < refParentNode.getLength(); ++refIdx) {
			refStrings.add(extractTextFromNodes(refParentNode.item(refIdx)));
		}
		
		
		System.out.println("journalTitle: " + journalTitleString);
		System.out.println("journalPublisher: " + journalPublisherString);
		System.out.println("journalISSNPublisher: " + journalISSNString);
		
		System.out.println("articleType: " + articleTypeString);
		System.out.println("received: " + receivedString);
		System.out.println("accepted: " + acceptedString);
		System.out.println("pubdate: " + pubdateString);
		
		System.out.println("title: " + titleString);
		System.out.println("abstract: " + abstractString);
		
		System.out.println("authorEmails: " + authorEmails);
		System.out.println("authorNames: " + authorNames);
		
		System.out.println("keywords: " + keywordsString);
		System.out.println("DOI: " + doiString);
		System.out.println("volume: " + volumeString);
		System.out.println("issue: " + issueString);
		System.out.println("firstPage: " + firstPageString);
		System.out.println("lastPage: " + lastPageString);
		
		System.out.println("body: " + bodyStrings);
		
		System.out.println("ref: " + refStrings.size() + " " + refStrings);
		/*
		#MET_TITLE
		/article/front/article-meta/title-group/article-title 
		
		#MET_DATES
		/article/front/article-meta/history/date[@date-type='received']/
		
		/article/front/article-meta/history/date[@date-type='accepted']/
		
		/article/front/article-meta/pub-date/
		
		#MET_ABSTRACT
		/article/front/article-meta/abstract/p 
		
		#MET_KEYWORDS
		/article/front/article-meta/kwd-group/kwd 
		
		#MET_BIB_INFO
		/article/front/journal-meta/journal-title
		/article/front/journal-meta/publisher/publisher-name
		/article/front/journal-meta/issn[@pub-type='ppub'] 
		/article/front/article-meta/article-id[@pub-id-type='doi']
		/article/front/article-meta/volume 
		/article/front/article-meta/issue 
		/article/front/article-meta/fpage
		/article/front/article-meta/lpage 
		
		#MET_TYPE
		/article/@article-type
		
		#MET_EDITOR
		/article/front/article-meta/contrib-group/contrib[@contrib-type='editor']/name/given-names
		/article/front/article-meta/contrib-group/contrib[@contrib-type='editor']/name/surname
		/article/front/article-meta/contrib-group/contrib[@contrib-type='editor']/email
		
		#MET_AUTHOR
		/article/front/article-meta/contrib-group/contrib[@contrib-type='author']/name/given-names
		/article/front/article-meta/contrib-group/contrib[@contrib-type='author']/name/surname
		
		#MET_CORRESPONDENCE
		/article/front/article-meta/contrib-group/contrib[@contrib-type='author']/email
		/article/front/article-meta/contrib-group/contrib[@contrib-type='author']/address/email
		
		#MET_AFFILIATION
		/article/front/article-meta/contrib-group/contrib[@contrib-type='author']/aff
		/article/front/article-meta/contrib-group/contrib[@contrib-type='editor']/aff
		
		#GEN_REFERENCES
		/article/back/ref-list
			/article/back/ref-list/ref/mixed-citation/article-title (article title)
    		/article/back/ref-list/ref/mixed-citation/edition (edition)
    		/article/back/ref-list/ref/mixed-citation/publisher-name (publisher name)
    		/article/back/ref-list/ref/mixed-citation/publisher-loc (publisher location)
    		/article/back/ref-list/ref/mixed-citation/series (series)
    		/article/back/ref-list/ref/mixed-citation/source (journal title)
    		/article/back/ref-list/ref/mixed-citation/uri (URI)
    		/article/back/ref-list/ref/mixed-citation/volume (volume)
    		/article/back/ref-list/ref/mixed-citation/year (year of publication)
    		/article/back/ref-list/ref/mixed-citation/issue (issue)
    		/article/back/ref-list/ref/mixed-citation/fpage (first page)
    		/article/back/ref-list/ref/mixed-citation/lpage (last page)
    		/article/back/ref-list/ref/mixed-citation/string-name (author)
    		/article/back/ref-list/ref/mixed-citation/string-name/given-names (author given names)
    		/article/back/ref-list/ref/mixed-citation/string-name/surname (author surname) 
		
		#GEN_BODY
		/article/body
		 
		 */
	Levenshtein lev = new Levenshtein();
	Double max = 0.0;
	BxZone bestZone = null;
	for(BxZone zone: bxDoc.asZones()) {
		Integer sim = lev.compare(tokenize(abstractString), tokenize(zone.toText()));
//		if(sim > max) {
//			bestZone = zone;
//			max = sim;
//		}
//		if(sim > 0.2) {
			System.out.println(sim + zone.toText() + "\n");
//		}
	}
	
	}
}
