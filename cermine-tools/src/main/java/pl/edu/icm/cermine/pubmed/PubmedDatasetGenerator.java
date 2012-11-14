package pl.edu.icm.cermine.pubmed;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream.GetField;
import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
	private static class LabelTrio {

		public BxZoneLabel label;
		public Double alignment;
		public List<String> entryTokens;
		
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
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			LabelTrio other = (LabelTrio) obj;
			if (label != other.label)
				return false;
			if (alignment == null) {
				if (other.alignment != null)
					return false;
			} else if (!alignment.equals(other.alignment))
				return false;
			return true;
		}

		public LabelTrio(BxZoneLabel label, List<String> tokens, Double similarity) {
			this.alignment = similarity;
			this.label = label;
			this.entryTokens = tokens;
		}
	};
	
	private static String getFileCoreName(String path) {
		String[] parts = path.split("\\.");
		if(parts.length == 2) {
			return parts[0];
		} else {
			if(parts.length > 1) {
				StringBuilder ret = new StringBuilder();
				ret.append(parts[0]);
				for(Integer partIdx = 1; partIdx < parts.length-1; ++partIdx) {
					ret.append(".").append(parts[partIdx]);
				}
				return ret.toString();
			} else {
				return parts[0];
			}
		}
	}

	private static String getNLMPath(String pdfPath) {
		return getFileCoreName(pdfPath) + ".nxml";
	}
	
	private static String getTrueVizPath(String pdfPath) {
		return getFileCoreName(pdfPath) + ".xml";
	}
	
	private static String extractTextFromNode(Node node) {
		StringBuilder ret = new StringBuilder();
		
		if(node == null) {
			return "";
		}
		if(node.getChildNodes().getLength() == 0) {
			if(node.getNodeValue() != null) {
				return node.getNodeValue() + " ";
			} else {
				return "";
			}
		} else {
			for(Integer childIdx=0; childIdx < node.getChildNodes().getLength(); ++childIdx) {
				ret.append(extractTextFromNode(node.getChildNodes().item(childIdx)));
			}
		}
		return ret.toString();
	}
	
	private static String extractTextFromNodes(NodeList nodes) {
		StringBuilder ret = new StringBuilder();
		for(Integer nodeIdx = 0; nodeIdx < nodes.getLength(); ++nodeIdx) {
			Node node = nodes.item(nodeIdx);
			ret.append(extractTextFromNode(node));
		}
		return ret.toString();
	}

	private static List<String> extractTextAsList(NodeList nodes) {
		List<String> ret = new ArrayList<String>(); 
		for(Integer nodeIdx=0; nodeIdx < nodes.getLength(); ++nodeIdx) {
			ret.add(extractTextFromNode(nodes.item(nodeIdx)));
		}
		return ret;
	}

	private static Double max(List<Double> values) {
		Double max = Double.NEGATIVE_INFINITY;
		for(Double val: values) {
			if(!val.isNaN()) {
				max = Math.min(max, val);
			} else {
				continue;
			}
		}
		return max;
	}

	private static List<String> produceDates(List<String> date) {
		System.out.println(date);
		List<String> ret = new ArrayList<String>();
		Integer monthInt = Integer.valueOf(date.get(1));
		if(monthInt >= 1 && monthInt <= 12) {
		    DateFormatSymbols dfs = new DateFormatSymbols();
		    String[] months = dfs.getMonths();
		    String month = months[monthInt-1];
		    ret.add(StringTools.joinStrings(new String[]{date.get(0), month, date.get(2)}));
		    ret.add(StringTools.joinStrings(new String[]{date.get(0), month.substring(0, 3), date.get(2)}));
		}
		ret.add(StringTools.joinStrings(date));
		return ret;
	}

	private static List<String> extractTextAsList(Node node) {
		List<String> ret = new ArrayList<String>();
		
		if(node == null) {
			return ret;
		}
		if(node.getChildNodes().getLength() == 0 && node.getNodeValue() != null) {
			ret.add(node.getNodeValue());
		} else {
			for(Integer childIdx=0; childIdx < node.getChildNodes().getLength(); ++childIdx) {
				ret.addAll(extractTextAsList(node.getChildNodes().item(childIdx)));
			}
		}
		return ret;
	}

	private static Map<BxZoneLabel, Double> reduce(List<LabelTrio> sims) {
		HashMap<BxZoneLabel, Double> ret = new HashMap<BxZoneLabel, Double>();
		for(LabelTrio trio: sims) {
			if(ret.containsKey(trio.label)) {
				ret.put(trio.label, ret.get(trio.label) + trio.alignment/trio.entryTokens.size());
			} else {
				ret.put(trio.label, trio.alignment/trio.entryTokens.size());
			}
		}
		return ret;
	}
	
	public static void main(String[] args) throws AnalysisException, ParserConfigurationException, SAXException, IOException, XPathExpressionException, TransformationException {
		if(args.length != 1) {
			System.err.println("Usage: <pubmed .pdf path>");
			System.exit(1);
		}
		String pdfPath = args[0];
		String nxmlPath = getNLMPath(pdfPath);
		
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
		Integer bxDocLen = bxDoc.asZones().size();
		
		SmartHashMap entries = new SmartHashMap();
		
		//abstract
		Node abstractNode = (Node) xpath.evaluate("/article/front/article-meta/abstract", domDoc, XPathConstants.NODE);
		String abstractString = extractTextFromNode(abstractNode);
		entries.putIf("Abstract " + abstractString, BxZoneLabel.MET_ABSTRACT);
		
		//title
		String titleString = (String) xpath.evaluate("/article/front/article-meta/title-group/article-title", domDoc, XPathConstants.STRING);
		entries.putIf(titleString, BxZoneLabel.MET_TITLE);
		//journal title
		String journalTitleString = (String) xpath.evaluate("/article/front/journal-meta/journal-title", domDoc, XPathConstants.STRING);
		if(journalTitleString == null || journalTitleString.isEmpty()) {
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
		
		//copyright
		String copyrightStatement = (String) xpath.evaluate("/article/front/article-meta/permissions/copyright-statement", domDoc, XPathConstants.STRING);
		String copyrightYear = (String) xpath.evaluate("/article/front/article-meta/permissions/copyright-year", domDoc, XPathConstants.STRING);
		String copyrightHolder = (String) xpath.evaluate("/article/front/article-meta/permissions/copyright-holder", domDoc, XPathConstants.STRING);
		String copyrightString = StringTools.joinStrings(new String[]{copyrightStatement, copyrightYear, copyrightHolder});
		entries.putIf(copyrightString, BxZoneLabel.MET_BIB_INFO);
		
		//license
		Node licenseNode = (Node) xpath.evaluate("/article/front/article-meta/license", domDoc, XPathConstants.NODE);
		String licenseString = (String) extractTextFromNode(licenseNode);
		entries.putIf(licenseString, BxZoneLabel.MET_BIB_INFO);
		
		String articleTypeString = null;
		//article type
		Object articleTypeResult = xpath.evaluate("/article/@article-type", domDoc, XPathConstants.NODESET);
		NodeList articleTypeProducts = (NodeList) articleTypeResult;
		if(articleTypeProducts.getLength() > 0) {
			articleTypeString = articleTypeProducts.item(0).getNodeValue();
		}
		entries.putIf(articleTypeString, BxZoneLabel.MET_TYPE);
		
		//received date
		List<String> receivedDate = extractTextAsList((Node) xpath.evaluate("/article/front/article-meta/history/date[@date-type='received']", domDoc, XPathConstants.NODE));
		if(!receivedDate.isEmpty()) {
			for(String date: produceDates(receivedDate)) {
				entries.putIf(date, BxZoneLabel.MET_DATES);
			}
		}
		
		//accepted date
		List<String> acceptedDate = extractTextAsList((Node) xpath.evaluate("/article/front/article-meta/history/date[@date-type='accepted']", domDoc, XPathConstants.NODE));
		if(!acceptedDate.isEmpty()) {
			for(String date: produceDates(acceptedDate)) {
				entries.putIf(date, BxZoneLabel.MET_DATES);
			}
		}
		
		//publication date
		List<String> pubdateString = null;
		if(((NodeList)xpath.evaluate("/article/front/article-meta/pub-date", domDoc, XPathConstants.NODESET)).getLength() > 1) {
			Node pubdateNode = (Node) xpath.evaluate("/article/front/article-meta/pub-date[@pub-type='epub']", domDoc, XPathConstants.NODE);
			pubdateString = extractTextAsList(pubdateNode);
		} else {
			Node pubdateNode = (Node) xpath.evaluate("/article/front/article-meta/pub-date[@pub-type='collection']", domDoc, XPathConstants.NODE);
			pubdateString = extractTextAsList(pubdateNode);
		}
		if(pubdateString != null && !pubdateString.isEmpty()) {
			for(String date: produceDates(pubdateString)) {
				entries.putIf(date, BxZoneLabel.MET_DATES);
			}
		}
		
		//keywords
		Node keywordsNode = (Node) xpath.evaluate("/article/front/article-meta/kwd-group", domDoc, XPathConstants.NODE);
		String keywordsString = extractTextFromNode(keywordsNode);
		entries.putIf(keywordsString, BxZoneLabel.MET_KEYWORDS);
		
		//DOI
		String doiString = (String) xpath.evaluate("/article/front/article-meta/article-id[@pub-id-type='doi']", domDoc, XPathConstants.STRING);
		entries.putIf(doiString, BxZoneLabel.MET_BIB_INFO);

		//volume
		String volumeString = (String) xpath.evaluate("/article/front/article-meta/article-id/volume", domDoc, XPathConstants.STRING);
		entries.putIf(volumeString, BxZoneLabel.MET_BIB_INFO);

		//issue
		String issueString = (String) xpath.evaluate("/article/front/article-meta/article-id/issue", domDoc, XPathConstants.STRING);
		
		entries.putIf(issueString, BxZoneLabel.MET_BIB_INFO);

		//first page
		String firstPageString = (String) xpath.evaluate("/article/front/article-meta/article-id/fpage", domDoc, XPathConstants.STRING);
		//last page
		String lastPageString = (String) xpath.evaluate("/article/front/article-meta/article-id/lpage", domDoc, XPathConstants.STRING);
		
		List<String> authorNames = new ArrayList<String>();
		List<String> authorEmails = new ArrayList<String>();
		List<String> authorAffiliations = new ArrayList<String>();
		List<String> editors = new ArrayList<String>();
		
		//editors
		NodeList editorNodes = (NodeList)xpath.evaluate("/article/front/article-meta/contrib-group/contrib[@contrib-type='editor']", domDoc, XPathConstants.NODESET);
		for(int nodeIdx=0; nodeIdx < editorNodes.getLength(); ++nodeIdx) {
			String editorString = extractTextFromNode(editorNodes.item(nodeIdx));
			editors.add(editorString);
		}
		entries.putIf(StringTools.joinStrings(editors), BxZoneLabel.MET_EDITOR);
		
		NodeList authorsResult = (NodeList)xpath.evaluate("/article/front/article-meta/contrib-group/contrib[@contrib-type='author']", domDoc, XPathConstants.NODESET);
		for(int nodeIdx=0; nodeIdx < authorsResult.getLength(); ++nodeIdx) {
			Node curNode = authorsResult.item(nodeIdx);
			//author names
			String name = (String)xpath.evaluate("name/given-names", curNode, XPathConstants.STRING);
			String surname = (String)xpath.evaluate("name/surname", curNode, XPathConstants.STRING);
			//author affiliation
			String aff = extractTextFromNodes((NodeList)xpath.evaluate("/article/front/article-meta/contrib-group/aff", domDoc, XPathConstants.NODESET));
			
			//author correspondence
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
			if(!email.isEmpty()) {
				authorEmails.add(email);
			}
			if(!aff.isEmpty()) {
				authorAffiliations.add(aff);
			}
			authorNames.add(name + " " + surname);
		}
		entries.putIf(StringTools.joinStrings(authorNames), BxZoneLabel.MET_AUTHOR);
		
		//authors' affiliations
		NodeList affNodes = (NodeList)xpath.evaluate("/article/front/article-meta/aff", domDoc, XPathConstants.NODESET);
		authorAffiliations.addAll(extractTextAsList(affNodes));
		entries.putIf(authorAffiliations, BxZoneLabel.MET_AFFILIATION);
		
		//correspondence again
		NodeList correspNodes = (NodeList)xpath.evaluate("/article/front/article-meta/author-notes/corresp", domDoc, XPathConstants.NODESET);
		authorEmails.add(extractTextFromNodes(correspNodes));
		entries.putIf(authorEmails, BxZoneLabel.MET_CORRESPONDENCE);
		
		//author notes
		Node notesNode = (Node) xpath.evaluate("/article/front/article-meta/author-notes/corresp/fn", domDoc, XPathConstants.NODE);
		String notesString = extractTextFromNode(notesNode);
		entries.putIf(notesString, BxZoneLabel.GEN_OTHER);
		notesString = extractTextFromNode((Node)xpath.evaluate("/article/back/notes", domDoc, XPathConstants.NODE));
		entries.putIf(notesString, BxZoneLabel.GEN_OTHER);
		
		//article body
		NodeList paragraphNodes = (NodeList) xpath.evaluate("/article/body//p", domDoc, XPathConstants.NODESET);
		String paragraphStrings = extractTextFromNodes(paragraphNodes);
		entries.putIf(paragraphStrings, BxZoneLabel.GEN_BODY);
		
		NodeList appNodes = (NodeList) xpath.evaluate("/article/back/app-group//p", domDoc, XPathConstants.NODESET);
		String appStrings = extractTextFromNodes(appNodes);
		entries.putIf(appStrings, BxZoneLabel.GEN_BODY);
		
		//section titles
		NodeList sectionTitleNodes = (NodeList) xpath.evaluate("/article/body//title", domDoc, XPathConstants.NODESET);
		List<String> sectionTitles = extractTextAsList(sectionTitleNodes);
		entries.putIf(sectionTitles, BxZoneLabel.BODY_HEADER);
		
		NodeList appTitleNodes = (NodeList) xpath.evaluate("/article/back/app-group//title", domDoc, XPathConstants.NODESET);
		List<String> appTitles = extractTextAsList(appTitleNodes);
		entries.putIf(appTitles, BxZoneLabel.BODY_HEADER);
		
		//figures
		NodeList figureNodes = (NodeList) xpath.evaluate("/article/floats-wrap//fig", domDoc, XPathConstants.NODESET);
		List<String> figureStrings = extractTextAsList(figureNodes);
		
		figureNodes = (NodeList) xpath.evaluate("/article/back//fig", domDoc, XPathConstants.NODESET);
		figureStrings.addAll(extractTextAsList(figureNodes));
		
		figureNodes = (NodeList) xpath.evaluate("/article/body//fig", domDoc, XPathConstants.NODESET);
		figureStrings.addAll(extractTextAsList(figureNodes));
		
		figureNodes = (NodeList) xpath.evaluate("/article/back/app-group//fig", domDoc, XPathConstants.NODESET);
		figureStrings.addAll(extractTextAsList(figureNodes));
		
		entries.putIf(figureStrings, BxZoneLabel.BODY_FIGURE);

		//tables
		NodeList tableNodes = (NodeList) xpath.evaluate("/article/floats-wrap//table-wrap", domDoc, XPathConstants.NODESET);
		List<String> tableStrings = extractTextAsList(tableNodes);
		
		tableNodes = (NodeList) xpath.evaluate("/article/back/app-group//table-wrap", domDoc, XPathConstants.NODESET);
		tableStrings.addAll(extractTextAsList(tableNodes));
		entries.putIf(tableStrings, BxZoneLabel.BODY_TABLE);

		//financial disclosure
		String financialDisclosure = extractTextFromNode((Node) xpath.evaluate("/article//fn[@fn-type='financial-disclosure']", domDoc, XPathConstants.NODE));
		entries.putIf(financialDisclosure, BxZoneLabel.GEN_OTHER);
		
		//conflict
		String conflictString = extractTextFromNode((Node) xpath.evaluate("/article//fn[@fn-type='conflict']", domDoc, XPathConstants.NODE));
		entries.putIf(conflictString, BxZoneLabel.GEN_OTHER);
		
		//acknowledgement
		String acknowledgement = extractTextFromNode((Node)xpath.evaluate("/article/back/ack", domDoc, XPathConstants.NODE));
		entries.putIf(acknowledgement, BxZoneLabel.GEN_OTHER);
		
		//references
		List<String> refStrings = new ArrayList<String>();
		Node refParentNode = (Node) xpath.evaluate("/article/back/ref-list", domDoc, XPathConstants.NODE);
		if(refParentNode != null) {
			for(Integer refIdx = 0; refIdx < refParentNode.getChildNodes().getLength(); ++refIdx) {
				refStrings.add(extractTextFromNode(refParentNode.getChildNodes().item(refIdx)));
			}
		}
//		for(String refString: refStrings) {
//			entries.putIf(refString, BxZoneLabel.GEN_REFERENCES);
//		}
		entries.putIf(StringTools.joinStrings(refStrings), BxZoneLabel.GEN_REFERENCES);
		entries.put("references", BxZoneLabel.GEN_REFERENCES);
		
		System.out.println("journalTitle: " + journalTitleString);
		System.out.println("journalPublisher: " + journalPublisherString);
		System.out.println("journalISSNPublisher: " + journalISSNString);
		
		System.out.println("articleType: " + articleTypeString);
		System.out.println("received: " + receivedDate);
		System.out.println("accepted: " + acceptedDate);
		System.out.println("pubdate: " + pubdateString);
		
		System.out.println("title: " + titleString);
		System.out.println("abstract: " + abstractString);
		
		System.out.println("authorEmails: " + authorEmails);
		System.out.println("authorNames: " + authorNames);
		System.out.println("authorAff: " + authorAffiliations);
		System.out.println("authorNotes: " + notesString);
		System.out.println("editor: " + editors);
		
		System.out.println("keywords: " + keywordsString);
		System.out.println("DOI: " + doiString);
		System.out.println("volume: " + volumeString);
		System.out.println("issue: " + issueString);
		System.out.println("financial dis.: " + financialDisclosure);
		
		
		System.out.println("paragraphs: " + paragraphStrings);
		System.out.println("section titles: " + sectionTitles);
		
		System.out.println("tables: " + tableStrings);
		System.out.println("figures: " + figureStrings);
		System.out.println("acknowledgement: " + acknowledgement);
		
		System.out.println("ref: " + refStrings.size() + " " + refStrings);
	
		LevenshteinDistance lev = new LevenshteinDistance();
		SmithWatermanDistance smith = new SmithWatermanDistance(.1, 0.1);
		CosineDistance cos = new CosineDistance();

		//index: (zone,entry)
		List<List<LabelTrio>> swLabelSim = new ArrayList<List<LabelTrio>>(bxDocLen);
		List<List<LabelTrio>> cosLabProb = new ArrayList<List<LabelTrio>>(bxDocLen);
		for(Integer i=0; i < bxDocLen; ++i) {
			swLabelSim.add(new ArrayList<LabelTrio>());
			cosLabProb.add(new ArrayList<LabelTrio>());
		}

		//iterate over entries
		for(Entry<String, BxZoneLabel> entry: entries.entrySet()) {
			List<String> entryTokens = StringTools.tokenize(entry.getKey());
			System.out.println("--------------------");
			System.out.println(entry.getValue() + " " + entry.getKey() + "\n");
			//iterate over zones
			for(Integer zoneIdx=0; zoneIdx < bxDocLen ; ++zoneIdx) {
				BxZone curZone = bxDoc.asZones().get(zoneIdx);
				List<String> zoneTokens = StringTools.tokenize(curZone.toText());

				Double sim = null;
				Double cosSim = null;
				if(curZone.toText().contains("www.biomedcentral.com")) {
					//ignore
					sim = 0.;
					cosSim = 0.;
				} else {
					sim = smith.compare(entryTokens, zoneTokens);
					cosSim = cos.compare(entryTokens, zoneTokens);
				}
				System.out.println(sim + " " + bxDoc.asZones().get(zoneIdx).toText() + "\n\n");
				swLabelSim.get(zoneIdx).add(new LabelTrio(entry.getValue(), entryTokens, sim));
				cosLabProb.get(zoneIdx).add(new LabelTrio(entry.getValue(), entryTokens, cosSim));
			}
		}

		System.out.println("===========================");
		for(Integer zoneIdx=0; zoneIdx < swLabelSim.size(); ++zoneIdx) {
			BxZone curZone = bxDoc.asZones().get(zoneIdx);
			List<String> zoneTokens = StringTools.tokenize(curZone.toText());
			Boolean valueSet = false;
			
			Collections.sort(swLabelSim.get(zoneIdx), new Comparator<LabelTrio>() { 
				@Override
				public int compare(LabelTrio t1, LabelTrio t2) {
					Double simDif = t1.alignment/t1.entryTokens.size() - t2.alignment/t2.entryTokens.size();
					if(Math.abs(simDif) < 0.0001) {
						Integer lenDif = t1.entryTokens.size() - t2.entryTokens.size();
						return -lenDif;
					}
					if(simDif > 0) {
						return 1;
					} else {
						return -1;
					}
				}
			});
			Collections.reverse(swLabelSim.get(zoneIdx));
			
			List<String> entryTokens = swLabelSim.get(zoneIdx).get(0).entryTokens;
			if(Math.min(zoneTokens.size(), entryTokens.size())/Math.max(zoneTokens.size(), entryTokens.size()) > 0.7 
					&& swLabelSim.get(zoneIdx).get(0).alignment/entryTokens.size() > 0.7) {
				curZone.setLabel(swLabelSim.get(zoneIdx).get(0).label);
				valueSet = true;
				System.out.print("XX ");
			}
			
			///////
			if(!valueSet) {
				Collections.sort(swLabelSim.get(zoneIdx), new Comparator<LabelTrio>() { 
					@Override
					public int compare(LabelTrio t1, LabelTrio t2) {
						Double simDif = t1.alignment - t2.alignment;
						if(Math.abs(simDif) < 0.0001) {
							Integer lenDif = t1.entryTokens.size() - t2.entryTokens.size();
							return -lenDif;
						}
						if(simDif > 0) {
							return 1;
						} else {
							return -1;
						}
					}
				});
				Collections.reverse(swLabelSim.get(zoneIdx));
				if(!valueSet && swLabelSim.get(zoneIdx).get(0).alignment/zoneTokens.size() > 0.2) {
					curZone.setLabel(swLabelSim.get(zoneIdx).get(0).label);
					valueSet = true;
				}
			}
			///////
			if(!valueSet) {
				Map<BxZoneLabel, Double> cumulated = new HashMap<BxZoneLabel, Double>();
				for(LabelTrio trio: swLabelSim.get(zoneIdx)) {
					if(cumulated.containsKey(trio.label)) {
						cumulated.put(trio.label, cumulated.get(trio.label) + trio.alignment/Math.min(zoneTokens.size(), trio.entryTokens.size()));
					} else {
						cumulated.put(trio.label, trio.alignment/Math.min(zoneTokens.size(), trio.entryTokens.size()));
					}
				}
				Double max = Double.NEGATIVE_INFINITY;
				BxZoneLabel bestLabel = null; 
				for(Entry<BxZoneLabel, Double> entry: cumulated.entrySet()) {
					if(entry.getValue() > max) {
						max = entry.getValue();
						bestLabel = entry.getKey();
					}
				}
				if(max > 0.1) {
					curZone.setLabel(bestLabel);
					valueSet = true;
				}
			}
			////
			if(!valueSet) {
					curZone.setLabel(null);
			}
			System.out.println(curZone.getLabel() + " "+ curZone.toText());
		}
	
		FileWriter fstream = new FileWriter(getTrueVizPath(nxmlPath));
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