package pl.edu.icm.cermine.pubmed;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
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

public class PubmedDatasetGenerator {
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
		String abstractString = (String) xpath.evaluate("/article/front/article-meta/abstract", domDoc, XPathConstants.STRING);
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
			pubdateString = (String) xpath.evaluate("/article/front/article-meta/pub-date[@pub-type='epub']", domDoc, XPathConstants.STRING);
		} else {
			pubdateString = (String) xpath.evaluate("/article/front/article-meta/pub-date[@pub-type='collection']", domDoc, XPathConstants.STRING);
		}
		
		//keywords
		String keywordsString = (String) xpath.evaluate("/article/front/article-meta/kwd-group/kwd", domDoc, XPathConstants.STRING);
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
					//do nothing
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
		String bodyString = (String)xpath.evaluate("/article/body", domDoc, XPathConstants.STRING);
		
		String refString = (String)xpath.evaluate("/article/back/ref-list", domDoc, XPathConstants.STRING);
		
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
		
		System.out.println("body: " + bodyString);
		
		System.out.println("ref: " + refString);
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
		
		
	}
}
