package pl.edu.icm.cermine.pubmed;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream.GetField;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.lang.ArrayUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import pl.edu.icm.cermine.PdfBxStructureExtractor;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.exception.TransformationException;
import pl.edu.icm.cermine.structure.model.BxDocument;
import pl.edu.icm.cermine.structure.model.BxZone;
import pl.edu.icm.cermine.structure.model.BxZoneLabel;
import pl.edu.icm.cermine.structure.transformers.BxDocumentToTrueVizWriter;

public class PubmedDatasetGenerator {
	private static class Pair<A,B> {
		public A first;
		public B second;
		public Pair(A first, B second) {
			this.first = first;
			this.second = second;
		}
	};

	private static class LabelProbability {

		public BxZoneLabel label;
		public Double probability;
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((label == null) ? 0 : label.hashCode());
			result = prime * result
					+ ((probability == null) ? 0 : probability.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			LabelProbability other = (LabelProbability) obj;
			if (label != other.label)
				return false;
			if (probability == null) {
				if (other.probability != null)
					return false;
			} else if (!probability.equals(other.probability))
				return false;
			return true;
		}

		public LabelProbability(BxZoneLabel label, Double probability) {
			this.probability = probability;
			this.label = label;
		}
	};

	private static void putIf(HashMap map, String string, BxZoneLabel label) {
		if(string != null && !string.isEmpty()) {
			map.put(string, label);
		}
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
	private static Double max(List<Double> values) {
		Double max = Double.NEGATIVE_INFINITY;
		for(Double val: values) {
			if(!val.isNaN()) {
				max = Math.max(max, val);
			} else {
				continue;
			}
		}
		return max;
	}

	private static String joinStrings(List<String> strings) {
		StringBuilder ret = new StringBuilder();
		for(String str: strings) {
			ret.append(str).append(" ");
		}
		return ret.toString();
	}
	
	private static String joinStrings(String[] strings) {
		return joinStrings(new ArrayList<String>(Arrays.asList(strings)));
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
	
	private static List<String> produceDates(List<String> date) {
		List<String> ret = new ArrayList<String>();
		String month = date.get(1);
		//@TODO
		/**
		 * 
		 * 
		 * 
		 * 
		 * 
		 * 
		 * 
		 * 
		 * 
		 * 
		 * 
		 * 
		 */
		return ret;
	}

	public static void main(String[] args) throws AnalysisException, ParserConfigurationException, SAXException, IOException, XPathExpressionException, TransformationException {
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
		
		
		HashMap<String, BxZoneLabel> entries = new HashMap<String, BxZoneLabel>();
		
		//abstract
		Node abstractNode = (Node) xpath.evaluate("/article/front/article-meta/abstract", domDoc, XPathConstants.NODE);
		String abstractString = extractTextFromNodes(abstractNode);
		putIf(entries, "Abstract " + abstractString, BxZoneLabel.MET_ABSTRACT);
		
		//title
		String titleString = (String) xpath.evaluate("/article/front/article-meta/title-group/article-title", domDoc, XPathConstants.STRING);
		putIf(entries, titleString, BxZoneLabel.MET_TITLE);
		
		//journal title
		String journalTitleString = (String) xpath.evaluate("/article/front/journal-meta/journal-title", domDoc, XPathConstants.STRING);
		putIf(entries, journalTitleString, BxZoneLabel.MET_BIB_INFO);

		//journal publisher
		String journalPublisherString = (String) xpath.evaluate("/article/front/journal-meta/publisher/publisher-name", domDoc, XPathConstants.STRING); 
		putIf(entries, journalPublisherString, BxZoneLabel.MET_BIB_INFO);
		
		//journal issn
		String journalISSNString = (String) xpath.evaluate("/article/front/journal-meta/issn", domDoc, XPathConstants.STRING);   
		putIf(entries, journalISSNString, BxZoneLabel.MET_BIB_INFO);
		
		//copyright
		String copyrightStatement = (String) xpath.evaluate("/article/front/article-meta/permissions/copyright-statement", domDoc, XPathConstants.STRING);
		String copyrightYear = (String) xpath.evaluate("/article/front/article-meta/permissions/copyright-year", domDoc, XPathConstants.STRING);
		String copyrightHolder = (String) xpath.evaluate("/article/front/article-meta/permissions/copyright-holder", domDoc, XPathConstants.STRING);
		String copyrightString = joinStrings(new String[]{copyrightStatement, copyrightYear, copyrightHolder});
		putIf(entries, copyrightString, BxZoneLabel.MET_BIB_INFO);
		
		//license
		Node licenseNode = (Node) xpath.evaluate("/article/front/article-meta/license", domDoc, XPathConstants.NODE);
		String licenseString = (String) extractTextFromNodes(licenseNode);
		putIf(entries, licenseString, BxZoneLabel.MET_BIB_INFO);
		
		String articleTypeString = null;
		//article type
		Object articleTypeResult = xpath.evaluate("/article/@article-type", domDoc, XPathConstants.NODESET);
		NodeList articleTypeProducts = (NodeList) articleTypeResult;
		if(articleTypeProducts.getLength() > 0) {
			articleTypeString = articleTypeProducts.item(0).getNodeValue();
		}
		if(articleTypeString != null && !articleTypeString.isEmpty()) {
			putIf(entries, articleTypeString, BxZoneLabel.MET_TYPE);
		}
		
		//received date
		List<String> receivedDate = extractTextAsList((Node) xpath.evaluate("/article/front/article-meta/history/date[@date-type='received']", domDoc, XPathConstants.NODE));
		System.out.println(receivedDate);
//		putIf(entries, receivedString, BxZoneLabel.MET_DATES);
		
		//accepted date
		String acceptedString = (String) xpath.evaluate("/article/front/article-meta/history/date[@date-type='accepted']", domDoc, XPathConstants.STRING);
		putIf(entries, acceptedString, BxZoneLabel.MET_DATES);
		
		//publication date
		String pubdateString = null;
		if(((NodeList)xpath.evaluate("/article/front/article-meta/pub-date", domDoc, XPathConstants.NODESET)).getLength() > 1) {
			Node pubdateNode = (Node) xpath.evaluate("/article/front/article-meta/pub-date[@pub-type='epub']", domDoc, XPathConstants.NODE);
			pubdateString = extractTextFromNodes(pubdateNode);
		} else {
			Node pubdateNode = (Node) xpath.evaluate("/article/front/article-meta/pub-date[@pub-type='collection']", domDoc, XPathConstants.NODE);
			pubdateString = extractTextFromNodes(pubdateNode);
		}
		if(pubdateString != null && !pubdateString.isEmpty()) {
			putIf(entries, pubdateString, BxZoneLabel.MET_DATES);
		}
		
		//keywords
		Node keywordsNode = (Node) xpath.evaluate("/article/front/article-meta/kwd-group", domDoc, XPathConstants.NODE);
		String keywordsString = extractTextFromNodes(keywordsNode);
		putIf(entries, keywordsString, BxZoneLabel.MET_KEYWORDS);
		
		//DOI
		String doiString = (String) xpath.evaluate("/article/front/article-meta/article-id[@pub-id-type='doi']", domDoc, XPathConstants.STRING);
		putIf(entries, doiString, BxZoneLabel.MET_BIB_INFO);

		//volume
		String volumeString = (String) xpath.evaluate("/article/front/article-meta/article-id/volume", domDoc, XPathConstants.STRING);
		putIf(entries, volumeString, BxZoneLabel.MET_BIB_INFO);

		//issue
		String issueString = (String) xpath.evaluate("/article/front/article-meta/article-id/issue", domDoc, XPathConstants.STRING);
		
		putIf(entries, issueString, BxZoneLabel.MET_BIB_INFO);

		//first page
		String firstPageString = (String) xpath.evaluate("/article/front/article-meta/article-id/fpage", domDoc, XPathConstants.STRING);
		//last page
		String lastPageString = (String) xpath.evaluate("/article/front/article-meta/article-id/lpage", domDoc, XPathConstants.STRING);
		
		List<String> authorNames = new ArrayList<String>();
		List<String> authorEmails = new ArrayList<String>();
		List<String> authorAffiliations = new ArrayList<String>();
		List<String> editorNames = new ArrayList<String>();
		
		//editors
		NodeList editorsResult = (NodeList)xpath.evaluate("/article/front/article-meta/contrib-group/contrib[@contrib-type='editor']/name", domDoc, XPathConstants.NODESET);
		
		for(int nodeIdx=0; nodeIdx < editorsResult.getLength(); ++nodeIdx) {
			String name = (String)xpath.evaluate("given-names", editorsResult.item(nodeIdx), XPathConstants.STRING);
			String surname = (String)xpath.evaluate("surname", editorsResult.item(nodeIdx), XPathConstants.STRING);
			editorNames.add(name + " " + surname);
		}
		putIf(entries, joinStrings(editorNames), BxZoneLabel.MET_EDITOR);
		
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
		if(!authorNames.isEmpty())
			entries.put(joinStrings(authorNames), BxZoneLabel.MET_AUTHOR);
		if(!authorEmails.isEmpty())
			entries.put(joinStrings(authorEmails), BxZoneLabel.MET_CORRESPONDENCE);
		
		//authors' affiliations
		NodeList affResult = (NodeList)xpath.evaluate("/article/front/article-meta/aff", domDoc, XPathConstants.NODESET);
		for(int nodeIdx=0; nodeIdx < affResult.getLength(); ++nodeIdx) {
			String text = affResult.item(nodeIdx).getTextContent();
			authorAffiliations.add(text);
		}
		putIf(entries, joinStrings(authorAffiliations), BxZoneLabel.MET_AFFILIATION);
		
		//article body
		Node bodyNode = (Node) xpath.evaluate("/article/body", domDoc, XPathConstants.NODE);
		List<String> bodyStrings = extractTextAsList(bodyNode);
//		for(String bodyString: bodyStrings) {
//			putIf(slp, bodyString, BxZoneLabel.GEN_BODY);
//		}
		putIf(entries, joinStrings(bodyStrings), BxZoneLabel.GEN_BODY);
		
		
		//references
		NodeList refParentNode = (NodeList) xpath.evaluate("/article/back/ref-list", domDoc, XPathConstants.NODESET);
		List<String> refStrings = new ArrayList<String>();
		for(Integer refIdx = 0; refIdx < refParentNode.getLength(); ++refIdx) {
			refStrings.add(extractTextFromNodes(refParentNode.item(refIdx)));
		}
		for(String refString: refStrings) {
			putIf(entries, "References " + refString, BxZoneLabel.GEN_REFERENCES);
		}
		
		System.out.println("journalTitle: " + journalTitleString);
		System.out.println("journalPublisher: " + journalPublisherString);
		System.out.println("journalISSNPublisher: " + journalISSNString);
		
		System.out.println("articleType: " + articleTypeString);
//		System.out.println("received: " + receivedString);
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
	
		LevenshteinDistance lev = new LevenshteinDistance();
		SmithWatermanDistance smith = new SmithWatermanDistance(1.33, 1.33);

		//index: (zone,entry)
		List<List<LabelProbability>> labelProbabilities = new ArrayList<List<LabelProbability>>(bxDoc.asZones().size());
		for(Integer i=0; i<bxDoc.asZones().size(); ++i) {
			labelProbabilities.add(new ArrayList<LabelProbability>());
		}

		//iterate over entries
		for(Entry<String, BxZoneLabel> entry: entries.entrySet()) {
			List<Double> thisEntryVSAllZones = new ArrayList<Double>(bxDoc.asZones().size());
			System.out.println("--------------------");
			System.out.println(entry.getKey() + "\n");
			//iterate over zones
			for(Integer zoneIdx=0; zoneIdx < bxDoc.asZones().size(); ++zoneIdx) {
				BxZone curZone = bxDoc.asZones().get(zoneIdx);
				Double sim = null;
				if(curZone.toText().contains("www.biomedcentral.com")) {
					//ignore
					sim = 0.;
				} else {
					sim = smith.compare(tokenize(entry.getKey()), tokenize(curZone.toText()));
				}
				thisEntryVSAllZones.add(sim);
			}
			//check the maximum
			Double max = max(thisEntryVSAllZones);
			for(Integer zoneIdx=0; zoneIdx < bxDoc.asZones().size(); ++zoneIdx) {
				BxZone curZone = bxDoc.asZones().get(zoneIdx);
				Integer curZoneTokens = tokenize(curZone.toText()).size();
				System.out.println(thisEntryVSAllZones.get(zoneIdx)/max + " " + bxDoc.asZones().get(zoneIdx).toText());
				labelProbabilities.get(zoneIdx).add(new LabelProbability(entry.getValue(), thisEntryVSAllZones.get(zoneIdx)/curZoneTokens));
			}
		}

		System.out.println("===========================");
		for(Integer zoneIdx=0; zoneIdx < labelProbabilities.size(); ++zoneIdx) {
			BxZone curZone = bxDoc.asZones().get(zoneIdx);
			BxZoneLabel bestLabel = null;
			Double max = Double.NEGATIVE_INFINITY;
			for(LabelProbability lp: labelProbabilities.get(zoneIdx)) {
				if(lp.probability > max) {
					max = lp.probability;
					bestLabel = lp.label;
				}
			}
			if(max > 0.1) {
				curZone.setLabel(bestLabel);
			} else {
				curZone.setLabel(null);
			}
			System.out.println(max + " " + curZone.getLabel() + " "+ curZone.toText());
		}
		FileWriter fstream = new FileWriter("pdg_out.xml");
		BufferedWriter out = new BufferedWriter(fstream);
		BxDocumentToTrueVizWriter writer = new BxDocumentToTrueVizWriter();
		out.write(writer.write(bxDoc.getPages()));
		out.close();

	}
}
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