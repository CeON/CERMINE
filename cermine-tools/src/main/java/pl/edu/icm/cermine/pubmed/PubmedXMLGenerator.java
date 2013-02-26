package pl.edu.icm.cermine.pubmed;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.hadoop.io.BytesWritable;
import org.apache.pig.EvalFunc;
import org.apache.pig.data.DataByteArray;
import org.apache.pig.data.DataType;
import org.apache.pig.data.Tuple;
import org.apache.pig.data.TupleFactory;
import org.apache.pig.impl.logicalLayer.FrontendException;
import org.apache.pig.impl.logicalLayer.schema.Schema;
import org.apache.xpath.NodeSet;
import org.jruby.compiler.ir.operands.Hash;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import edu.umass.cs.mallet.base.extract.Extraction;
import pl.edu.icm.cermine.PdfBxStructureExtractor;
import pl.edu.icm.cermine.evaluation.tools.CosineDistance;
import pl.edu.icm.cermine.evaluation.tools.SmithWatermanDistance;
import pl.edu.icm.cermine.evaluation.tools.StringTools;
import pl.edu.icm.cermine.evaluation.tools.XMLTools;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.exception.TransformationException;
import pl.edu.icm.cermine.metadata.zoneclassification.tools.ZoneLocaliser;
import pl.edu.icm.cermine.structure.model.BxDocument;
import pl.edu.icm.cermine.structure.model.BxPage;
import pl.edu.icm.cermine.structure.model.BxZone;
import pl.edu.icm.cermine.structure.model.BxZoneLabel;
import pl.edu.icm.cermine.structure.transformers.BxDocumentToTrueVizWriter;

public class PubmedXMLGenerator extends EvalFunc<Tuple> {

    private static class LabelTrio {

        private BxZoneLabel label;
        private Double alignment;
        private List<String> entryTokens;
        
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
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            LabelTrio other = (LabelTrio) obj;
            if (label != other.label) {
                return false;
            }
            if (alignment == null) {
                if (other.alignment != null) {
                    return false;
                }
            } else if (!alignment.equals(other.alignment)) {
                return false;
            }
            return true;
        }

        public LabelTrio(BxZoneLabel label, List<String> tokens, Double similarity) {
            this.alignment = similarity;
            this.label = label;
            this.entryTokens = tokens;
        }
    };

    private Tuple output = TupleFactory.getInstance().newTuple(4);
	private boolean verbose = false;

    
    @Override
    public Schema outputSchema(Schema p_input) {
    	try{
    		return Schema.generateNestedSchema(DataType.TUPLE, DataType.CHARARRAY,
    			DataType.BYTEARRAY, DataType.BYTEARRAY, DataType.CHARARRAY);
    	} catch(FrontendException e) {
    		throw new IllegalStateException(e);
    	}
    }
    
    @Override
    public Tuple exec(Tuple input) throws IOException {
    	System.out.println("doc start");
    	if(input == null || input.size() == 0) {
    		throw new IllegalStateException("Input tuple can't be empty!");
    	}
    	String keyString = (String) input.get(0);
    	DataByteArray nlmByteArray = (DataByteArray) input.get(1);
    	DataByteArray pdfByteArray = (DataByteArray) input.get(2);
    	BxDocument bxDoc = null;
    	try {
    		ByteArrayInputStream nlmIS = new ByteArrayInputStream(nlmByteArray.get());
    		ByteArrayInputStream pdfIS = new ByteArrayInputStream(pdfByteArray.get());
			bxDoc = generateTrueViz(pdfIS, nlmIS);
		} catch (XPathExpressionException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (AnalysisException e) {
			e.printStackTrace();
			System.exit(2);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			System.exit(3);
		} catch (SAXException e) {
			e.printStackTrace();
			System.exit(4);
		} catch (TransformationException e) {
			e.printStackTrace();
			System.exit(5);
		}
    	BxDocumentToTrueVizWriter trueVizWriter = new BxDocumentToTrueVizWriter();
    	String returnDoc = new String();
    	//try {
    	//	returnDoc = null; //returnDoc = trueVizWriter.write(bxDoc.getPages());
	//	} catch (TransformationException e) {
	//		e.printStackTrace();
	//		System.exit(6);
	//	}
    	
    	output.set(0, keyString);
    	output.set(1, nlmByteArray);
    	output.set(2, pdfByteArray);
    	output.set(3, returnDoc);
    	System.out.println("doc end");
    	return output;
    }

	private void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}

	private void printlnVerbose(String string) {
		if(verbose) {
			System.out.println(string);
		}
	}
	
	private void printVerbose(String string) {
		if(verbose) {
			printVerbose(string);
		}
	}
	
    private Map<BxZoneLabel, Integer> summarizeDocument(BxDocument doc) {
    	Map<BxZoneLabel, Integer> ret = new HashMap<BxZoneLabel, Integer>();
    	for(BxZone zone: doc.asZones()) {
    		if(ret.containsKey(zone.getLabel())) {
    			ret.put(zone.getLabel(), ret.get(zone.getLabel())+1);
    		} else {
    			ret.put(zone.getLabel(), 1);
    		}
    	}
    	return ret;
    }
    
    public BxDocument generateTrueViz(InputStream pdfStream, InputStream nlmStream) 
    		throws AnalysisException, ParserConfigurationException, SAXException, IOException, XPathExpressionException, TransformationException {
        XPath xpath = XPathFactory.newInstance().newXPath();
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setValidating(false);
        dbf.setFeature("http://xml.org/sax/features/namespaces", false);
        dbf.setFeature("http://xml.org/sax/features/validation", false);
        dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
        dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);

        DocumentBuilder builder = dbf.newDocumentBuilder();
        Document domDoc = builder.parse(nlmStream);

        PdfBxStructureExtractor structureExtractor = new PdfBxStructureExtractor();
        BxDocument bxDoc = structureExtractor.extractStructure(pdfStream);
        Integer bxDocLen = bxDoc.asZones().size();

        SmartHashMap entries = new SmartHashMap();

        //abstract
        Node abstractNode = (Node) xpath.evaluate("/article/front/article-meta/abstract", domDoc, XPathConstants.NODE);
        String abstractString = XMLTools.extractTextFromNode(abstractNode);
        entries.putIf("Abstract " + abstractString, BxZoneLabel.MET_ABSTRACT);

        //title
        String titleString = (String) xpath.evaluate("/article/front/article-meta/title-group/article-title", domDoc, XPathConstants.STRING);
        entries.putIf(titleString, BxZoneLabel.MET_TITLE);
        String subtitleString = (String) xpath.evaluate("/article/front/article-meta/title-group/article-subtitle", domDoc, XPathConstants.STRING);
        entries.putIf(subtitleString, BxZoneLabel.MET_TITLE);
        //journal title
        String journalTitleString = (String) xpath.evaluate("/article/front/journal-meta/journal-title", domDoc, XPathConstants.STRING);
        if (journalTitleString == null || journalTitleString.isEmpty()) {
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

        //copyright/permissions
        String permissionsString = XMLTools.extractTextFromNode((Node) xpath.evaluate("/article/front/article-meta/permissions", domDoc, XPathConstants.NODE));
        entries.putIf(permissionsString, BxZoneLabel.MET_COPYRIGHT);

        //license
        Node licenseNode = (Node) xpath.evaluate("/article/front/article-meta/license", domDoc, XPathConstants.NODE);
        String licenseString = (String) XMLTools.extractTextFromNode(licenseNode);
        entries.putIf(licenseString, BxZoneLabel.MET_COPYRIGHT);

        //article type
        NodeList articleTypeNodes = (NodeList) xpath.evaluate("/article/@article-type", domDoc, XPathConstants.NODESET);
        List<String> articleTypeStrings = XMLTools.extractTextAsList(articleTypeNodes);
        Node articleTypeNode = (Node) xpath.evaluate("/article/front/article-meta/article-categories/subj-group", domDoc, XPathConstants.NODE);
        articleTypeStrings.add(XMLTools.extractTextFromNode(articleTypeNode));

        entries.putIf(articleTypeStrings, BxZoneLabel.MET_TYPE);

        //received date
        List<String> receivedDate = XMLTools.extractChildrenAsTextList((Node) xpath.evaluate("/article/front/article-meta/history/date[@date-type='received']", domDoc, XPathConstants.NODE));
        if (!receivedDate.isEmpty()) {
            for (String date : StringTools.produceDates(receivedDate)) {
                entries.putIf(date, BxZoneLabel.MET_DATES);
            }
        }

        //accepted date
        List<String> acceptedDate = XMLTools.extractChildrenAsTextList((Node) xpath.evaluate("/article/front/article-meta/history/date[@date-type='accepted']", domDoc, XPathConstants.NODE));
        if (!acceptedDate.isEmpty()) {
            for (String date : StringTools.produceDates(acceptedDate)) {
                entries.putIf(date, BxZoneLabel.MET_DATES);
            }
        }

        //publication date
        List<String> pubdateString = null;
        if (((NodeList) xpath.evaluate("/article/front/article-meta/pub-date", domDoc, XPathConstants.NODESET)).getLength() > 1) {
            Node pubdateNode = (Node) xpath.evaluate("/article/front/article-meta/pub-date[@pub-type='epub']", domDoc, XPathConstants.NODE);
            pubdateString = XMLTools.extractChildrenAsTextList(pubdateNode);
        } else {
            Node pubdateNode = (Node) xpath.evaluate("/article/front/article-meta/pub-date[@pub-type='collection']", domDoc, XPathConstants.NODE);
            pubdateString = XMLTools.extractChildrenAsTextList(pubdateNode);
        }
        if (pubdateString != null && !pubdateString.isEmpty()) {
            for (String date : StringTools.produceDates(pubdateString)) {
                entries.putIf(date, BxZoneLabel.MET_DATES);
            }
        }

        String extLink =(String) xpath.evaluate("/article/front/article-meta/ext-link[@ext-link-type='uri']/xlink:href", domDoc, XPathConstants.STRING);
        printlnVerbose(extLink);
        entries.putIf(extLink, BxZoneLabel.MET_ACCESS_DATA);
        //keywords
        Node keywordsNode = (Node) xpath.evaluate("/article/front/article-meta/kwd-group", domDoc, XPathConstants.NODE);
        String keywordsString = XMLTools.extractTextFromNode(keywordsNode);
        entries.putIf(keywordsString, BxZoneLabel.MET_KEYWORDS);

        //DOI
        String doiString = (String) xpath.evaluate("/article/front/article-meta/article-id[@pub-id-type='doi']", domDoc, XPathConstants.STRING);
        entries.putIf("DOI " + doiString, BxZoneLabel.MET_BIB_INFO);

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
        NodeList editorNodes = (NodeList) xpath.evaluate("/article/front/article-meta/contrib-group/contrib[@contrib-type='editor']", domDoc, XPathConstants.NODESET);
        for (int nodeIdx = 0; nodeIdx < editorNodes.getLength(); ++nodeIdx) {
            String editorString = XMLTools.extractTextFromNode(editorNodes.item(nodeIdx));
            editors.add(editorString);
        }
        entries.putIf(StringTools.joinStrings(editors), BxZoneLabel.MET_EDITOR);

        NodeList authorsResult = (NodeList) xpath.evaluate("/article/front/article-meta/contrib-group/contrib[@contrib-type='author']", domDoc, XPathConstants.NODESET);
        for (int nodeIdx = 0; nodeIdx < authorsResult.getLength(); ++nodeIdx) {
            Node curNode = authorsResult.item(nodeIdx);
            //author names
            String name = (String) xpath.evaluate("name/given-names", curNode, XPathConstants.STRING);
            String surname = (String) xpath.evaluate("name/surname", curNode, XPathConstants.STRING);
            //author affiliation
            List<String> aff = XMLTools.extractTextAsList((NodeList) xpath.evaluate("/article/front/article-meta/contrib-group/aff", domDoc, XPathConstants.NODESET));

            //author correspondence
            String email;
            try {
                email = (String) xpath.evaluate("address/email", curNode, XPathConstants.STRING);
            } catch (XPathExpressionException e) {
                email = "";
            }
            if (email.isEmpty()) {
                try {
                    email = (String) xpath.evaluate("email", curNode, XPathConstants.STRING);
                } catch (XPathExpressionException e) {
                    //yaaay, probably there is no e-mail at all! => do nothing
                }
            }
            if (!email.isEmpty()) {
                authorEmails.add(email);
            }
            if (!aff.isEmpty()) {
                authorAffiliations.addAll(aff);
            }
            authorNames.add(name + " " + surname);
        }
        entries.putIf(StringTools.joinStrings(authorNames), BxZoneLabel.MET_AUTHOR);

        //authors' affiliations
        NodeList affNodes = (NodeList) xpath.evaluate("/article/front/article-meta/aff", domDoc, XPathConstants.NODESET);
        authorAffiliations.addAll(XMLTools.extractTextAsList(affNodes));
        entries.putIf(authorAffiliations, BxZoneLabel.MET_AFFILIATION);

        //correspondence again
        NodeList correspNodes = (NodeList) xpath.evaluate("/article/front/article-meta/author-notes/corresp", domDoc, XPathConstants.NODESET);
        authorEmails.add(XMLTools.extractTextFromNodes(correspNodes));
        entries.putIf(authorEmails, BxZoneLabel.MET_CORRESPONDENCE);

        //author notes
        Node notesNode = (Node) xpath.evaluate("/article/front/article-meta/author-notes/corresp/fn", domDoc, XPathConstants.NODE);
        String notesString = XMLTools.extractTextFromNode(notesNode);
        entries.putIf(notesString, BxZoneLabel.MET_CORRESPONDENCE);
        notesString = XMLTools.extractTextFromNode((Node) xpath.evaluate("/article/back/notes", domDoc, XPathConstants.NODE));
        entries.putIf(notesString, BxZoneLabel.OTH_UNKNOWN);

        //article body
        NodeList paragraphNodes = (NodeList) xpath.evaluate("/article/body//p", domDoc, XPathConstants.NODESET);
        List<String> paragraphStrings = XMLTools.extractTextAsList(paragraphNodes);
        entries.putIf(paragraphStrings, BxZoneLabel.BODY_CONTENT);

        NodeList appNodes = (NodeList) xpath.evaluate("/article/back/app-group//p", domDoc, XPathConstants.NODESET);
        String appStrings = XMLTools.extractTextFromNodes(appNodes);
        entries.putIf(appStrings, BxZoneLabel.BODY_CONTENT);

        //section titles
        NodeList sectionTitleNodes = (NodeList) xpath.evaluate("/article/body//title", domDoc, XPathConstants.NODESET);
        List<String> sectionTitles = XMLTools.extractTextAsList(sectionTitleNodes);
        entries.putIf(sectionTitles, BxZoneLabel.BODY_HEADING);

        NodeList appTitleNodes = (NodeList) xpath.evaluate("/article/back/app-group//title", domDoc, XPathConstants.NODESET);
        List<String> appTitles = XMLTools.extractTextAsList(appTitleNodes);
        entries.putIf(appTitles, BxZoneLabel.BODY_HEADING);

        //figures
        NodeList figureNodes = (NodeList) xpath.evaluate("/article/floats-wrap//fig", domDoc, XPathConstants.NODESET);
        List<String> figureStrings = XMLTools.extractTextAsList(figureNodes);

        figureNodes = (NodeList) xpath.evaluate("/article/floats-group//fig", domDoc, XPathConstants.NODESET);
        figureStrings.addAll(XMLTools.extractTextAsList(figureNodes));

        figureNodes = (NodeList) xpath.evaluate("/article/back//fig", domDoc, XPathConstants.NODESET);
        figureStrings.addAll(XMLTools.extractTextAsList(figureNodes));

        figureNodes = (NodeList) xpath.evaluate("/article/body//fig", domDoc, XPathConstants.NODESET);
        figureStrings.addAll(XMLTools.extractTextAsList(figureNodes));

        figureNodes = (NodeList) xpath.evaluate("/article/back/app-group//fig", domDoc, XPathConstants.NODESET);
        figureStrings.addAll(XMLTools.extractTextAsList(figureNodes));

        entries.putIf(figureStrings, BxZoneLabel.BODY_FIGURE_CAPTION);

        //tables
        List<String> tableCaptions = new ArrayList<String>();
        List<String> tableBodies = new ArrayList<String>();
        List<String> tableFootnotes = new ArrayList<String>();
        //tableNodes.
        NodeList tableNodes = (NodeList) xpath.evaluate("/article//table-wrap", domDoc, XPathConstants.NODESET);

        for (Integer nodeIdx = 0; nodeIdx < tableNodes.getLength(); ++nodeIdx) {
            Node tableNode = tableNodes.item(nodeIdx);

            String caption = (String) xpath.evaluate("caption", tableNode, XPathConstants.STRING);
            tableCaptions.add(caption);

            String body = XMLTools.extractTextFromNode((Node) xpath.evaluate("table", tableNode, XPathConstants.NODE));
            tableBodies.add(body);

            List<String> footnotes = XMLTools.extractTextAsList((NodeList) xpath.evaluate("table-wrap-foot/fn", tableNode, XPathConstants.NODESET));
            tableFootnotes.addAll(footnotes);

            entries.putIf(caption, BxZoneLabel.BODY_TABLE_CAPTION);
            entries.putIf(body, BxZoneLabel.BODY_TABLE);
            entries.putIf(footnotes, BxZoneLabel.BODY_TABLE_CAPTION);
        }

        //financial disclosure
        String financialDisclosure = XMLTools.extractTextFromNode((Node) xpath.evaluate("/article//fn[@fn-type='financial-disclosure']", domDoc, XPathConstants.NODE));
        entries.putIf(financialDisclosure, BxZoneLabel.BODY_ACKNOWLEDGMENT);

        //conflict
        String conflictString = XMLTools.extractTextFromNode((Node) xpath.evaluate("/article//fn[@fn-type='conflict']", domDoc, XPathConstants.NODE));
        entries.putIf(conflictString, BxZoneLabel.BODY_CONFLICT_STMT);

        //copyright
        String copyrightString = XMLTools.extractTextFromNode((Node) xpath.evaluate("/article/front/article-meta/permissions/copyright-statement", domDoc, XPathConstants.NODE));
        entries.putIf(copyrightString, BxZoneLabel.MET_COPYRIGHT);
        
        //acknowledgment
        String acknowledgement = XMLTools.extractTextFromNode((Node) xpath.evaluate("/article/back/ack", domDoc, XPathConstants.NODE));
        entries.putIf(acknowledgement, BxZoneLabel.BODY_ACKNOWLEDGMENT);

        acknowledgement = XMLTools.extractTextFromNode((Node) xpath.evaluate("/article/back/fn-group/fn", domDoc, XPathConstants.NODE));
        entries.putIf(acknowledgement, BxZoneLabel.BODY_CONFLICT_STMT);
        
        //glossary
        String glossary = XMLTools.extractTextFromNode((Node) xpath.evaluate("/article/back/glossary", domDoc, XPathConstants.NODE));
        entries.putIf(glossary, BxZoneLabel.BODY_GLOSSARY);

        //formula
        NodeList formulaNodes = (NodeList) xpath.evaluate("/article/body//disp-formula", domDoc, XPathConstants.NODESET);
        for(int nodeIdx = 0; nodeIdx < formulaNodes.getLength(); ++nodeIdx) {
        	Node curFormulaNode = formulaNodes.item(nodeIdx);
        	String label = (String) xpath.evaluate("label", curFormulaNode);
        	entries.putIf(label, BxZoneLabel.BODY_EQUATION_LABEL);

        	NodeList curNodeChildren = curFormulaNode.getChildNodes();
        	List<String> formulaParts = new ArrayList<String>();
        	for(int childIdx = 0; childIdx < curNodeChildren.getLength(); ++childIdx) {
        		Node curChild = curNodeChildren.item(childIdx);
        		if(curChild.getNodeName().equals("label")) {
        			continue;
        		}
        		formulaParts.add(XMLTools.extractTextFromNode(curChild));
        	}
        	entries.putIf(StringTools.joinStrings(formulaParts), BxZoneLabel.BODY_EQUATION);
        }

        //references
        List<String> refStrings = new ArrayList<String>();
        Node refParentNode = (Node) xpath.evaluate("/article/back/ref-list", domDoc, XPathConstants.NODE);
        if (refParentNode != null) {
            for (Integer refIdx = 0; refIdx < refParentNode.getChildNodes().getLength(); ++refIdx) {
                refStrings.add(XMLTools.extractTextFromNode(refParentNode.getChildNodes().item(refIdx)));
            }
        }
//			entries.putIf(refString, BxZoneLabel.GEN_REFERENCES);
//		}
        entries.putIf(StringTools.joinStrings(refStrings), BxZoneLabel.REFERENCES);
        entries.put("references", BxZoneLabel.REFERENCES);

        printlnVerbose("journalTitle: " + journalTitleString);
        printlnVerbose("journalPublisher: " + journalPublisherString);
        printlnVerbose("journalISSNPublisher: " + journalISSNString);

        printlnVerbose("articleType: " + articleTypeStrings);
        printlnVerbose("received: " + receivedDate);
        printlnVerbose("accepted: " + acceptedDate);
        printlnVerbose("pubdate: " + pubdateString);
        printlnVerbose("permissions: " + permissionsString);
        printlnVerbose("license: " + licenseString);

        printlnVerbose("title: " + titleString);
        printlnVerbose("abstract: " + abstractString);

        printlnVerbose("authorEmails: " + authorEmails);
        printlnVerbose("authorNames: " + authorNames);
        printlnVerbose("authorAff: " + authorAffiliations);
        printlnVerbose("authorNotes: " + notesString);
        printlnVerbose("editor: " + editors);

        printlnVerbose("keywords: " + keywordsString);
        printlnVerbose("DOI: " + doiString);
        printlnVerbose("volume: " + volumeString);
        printlnVerbose("issue: " + issueString);
        printlnVerbose("financial dis.: " + financialDisclosure);

        printlnVerbose("paragraphs: " + paragraphStrings);
        printlnVerbose("section titles: " + sectionTitles);

        printlnVerbose("tableBodies: " + tableBodies);
        printlnVerbose("tableCaptions: " + tableCaptions);
        printlnVerbose("tableFootnotes: " + tableFootnotes);

        printlnVerbose("figures: " + figureStrings);
        printlnVerbose("acknowledgement: " + acknowledgement);

        printlnVerbose("ref: " + refStrings.size() + " " + refStrings);

        LevenshteinDistance lev = new LevenshteinDistance();
        SmithWatermanDistance smith = new SmithWatermanDistance(.1, 0.1);
        CosineDistance cos = new CosineDistance();

        //index: (zone,entry)
        List<List<LabelTrio>> swLabelSim = new ArrayList<List<LabelTrio>>(bxDocLen);
        List<List<LabelTrio>> cosLabProb = new ArrayList<List<LabelTrio>>(bxDocLen);
        for (Integer i = 0; i < bxDocLen; ++i) {
            swLabelSim.add(new ArrayList<LabelTrio>());
            cosLabProb.add(new ArrayList<LabelTrio>());
        }

        //iterate over entries
        for (Entry<String, BxZoneLabel> entry : entries.entrySet()) {
            List<String> entryTokens = StringTools.tokenize(entry.getKey());
            printlnVerbose("--------------------");
            printlnVerbose(entry.getValue() + " " + entry.getKey() + "\n");
            //iterate over zones
            for (Integer zoneIdx = 0; zoneIdx < bxDocLen; ++zoneIdx) {
                BxZone curZone = bxDoc.asZones().get(zoneIdx);
                List<String> zoneTokens = StringTools.tokenize(StringTools.removeOrphantSpaces(StringTools.cleanLigatures(curZone.toText())));

                Double smithSim = null;
                Double cosSim = null;
                if (curZone.toText().contains("www.biomedcentral.com")) {
                    //ignore
                    smithSim = 0.;
                    cosSim = 0.;
                } else {
                    smithSim = smith.compare(entryTokens, zoneTokens);
                    cosSim = cos.compare(entryTokens, zoneTokens);
                }
                printlnVerbose(smithSim + " " + bxDoc.asZones().get(zoneIdx).toText() + "\n\n");
                swLabelSim.get(zoneIdx).add(new LabelTrio(entry.getValue(), entryTokens, smithSim));
                cosLabProb.get(zoneIdx).add(new LabelTrio(entry.getValue(), entryTokens, cosSim));
            }
        }

        printlnVerbose("===========================");
        for (BxPage page: bxDoc.getPages()) {
        	for(BxZone zone: page.getZones()) {
        		Integer zoneIdx = bxDoc.asZones().indexOf(zone);
        		BxZone curZone = bxDoc.asZones().get(zoneIdx);
        		String zoneText = StringTools.removeOrphantSpaces(curZone.toText());
        		List<String> zoneTokens = StringTools.tokenize(zoneText);
        		Boolean valueSet = false;

        		Collections.sort(swLabelSim.get(zoneIdx), new Comparator<LabelTrio>() {

        			@Override 
        			public int compare(LabelTrio t1, LabelTrio t2) {
        				Double simDif = t1.alignment / t1.entryTokens.size() - t2.alignment / t2.entryTokens.size();
        				if (Math.abs(simDif) < 0.0001) {
        					Integer lenDif = t1.entryTokens.size() - t2.entryTokens.size();
        					return -lenDif;
        				}
        				if (simDif > 0) {
        					return 1;
        				} else {
        					return -1;
        				}
        			}
        		});
        		Collections.reverse(swLabelSim.get(zoneIdx));

        		List<String> entryTokens = swLabelSim.get(zoneIdx).get(0).entryTokens;
        		if (Math.min(zoneTokens.size(), entryTokens.size()) / Math.max(zoneTokens.size(), entryTokens.size()) > 0.7
        				&& swLabelSim.get(zoneIdx).get(0).alignment / entryTokens.size() > 0.7) {
        			curZone.setLabel(swLabelSim.get(zoneIdx).get(0).label);
        			valueSet = true;
        			printVerbose("0 ");
        		}
        		///////
        		if (!valueSet) {
        			Collections.sort(swLabelSim.get(zoneIdx), new Comparator<LabelTrio>() {
        				
        				@Override
        				public int compare(LabelTrio t1, LabelTrio t2) {
        					Double simDif = t1.alignment - t2.alignment;
        					if (Math.abs(simDif) < 0.0001) {
        						Integer lenDif = t1.entryTokens.size() - t2.entryTokens.size();
        						return -lenDif;
        					}
        					if (simDif > 0) {
        						return 1;
        					} else {
        						return -1;
        					}
        				}
        			});
        			Collections.reverse(swLabelSim.get(zoneIdx));
        			printlnVerbose("-->" + swLabelSim.get(zoneIdx).get(0).alignment / zoneTokens.size());
        			if (swLabelSim.get(zoneIdx).get(0).alignment / zoneTokens.size() > 0.5) {
        				curZone.setLabel(swLabelSim.get(zoneIdx).get(0).label);
        				valueSet = true;
        				printVerbose("1 ");
        			}
        		}
        		///////
        		if (!valueSet) {
        			Map<BxZoneLabel, Double> cumulated = new HashMap<BxZoneLabel, Double>();
        			for (LabelTrio trio : swLabelSim.get(zoneIdx)) {
        				if (cumulated.containsKey(trio.label)) {
        					cumulated.put(trio.label, cumulated.get(trio.label) + trio.alignment / Math.max(zoneTokens.size(), trio.entryTokens.size()));
        				} else {
        					cumulated.put(trio.label, trio.alignment / Math.max(zoneTokens.size(), trio.entryTokens.size()));
        				}
        			}
        			Double max = Double.NEGATIVE_INFINITY;
        			BxZoneLabel bestLabel = null;
        			for (Entry<BxZoneLabel, Double> entry : cumulated.entrySet()) {
        				if (entry.getValue() > max) {
        					max = entry.getValue();
        					bestLabel = entry.getKey();
        				}
        			}
        			if (max > 0.5){
        				curZone.setLabel(bestLabel);
        				printVerbose("2 ");
        				valueSet = true;
        			}
        		}
        		////
        		if(!valueSet) {
        			curZone.setLabel(null);
        		}
        		printlnVerbose(zone.getLabel() + " " + zone.toText() + "\n");
        	}
        	Map<BxZone, ZoneLocaliser> zoneLocMap = new HashMap<BxZone, ZoneLocaliser>();
        	Set<BxZone> unlabeledZones = new HashSet<BxZone>();
        	for(BxZone zone: page.getZones()) {
        		if(zone.getLabel() == null) {
        			unlabeledZones.add(zone);
        			zoneLocMap.put(zone, new ZoneLocaliser(zone));
        		}
        	}
        	Integer lastNumberOfUnlabeledZones;
        	do {
        		lastNumberOfUnlabeledZones = unlabeledZones.size();
        		infereLabels(unlabeledZones, zoneLocMap);
        	} while(lastNumberOfUnlabeledZones != unlabeledZones.size());
        }
        printlnVerbose("=>=>=>=>=>=>=>=>=>=>=>=>=>=");

        return bxDoc;
    }

	private void infereLabels(Set<BxZone> unlabeledZones, Map<BxZone, ZoneLocaliser> zoneLocMap) {
    	Set<BxZone> toBeRemoved = new HashSet<BxZone>();
        for(BxZone zone: unlabeledZones) {
            if (zone.getLabel() == null) {
            	ZoneLocaliser loc = zoneLocMap.get(zone); 
            	if((loc.getLeftZone() != null && loc.getRightZone() != null)
            			&& (loc.getLeftZone().getLabel() == loc.getRightZone().getLabel())) {
            		zone.setLabel(loc.getLeftZone().getLabel());
                    printVerbose("3 ");
                    toBeRemoved.add(zone);
            	} else if((loc.getLowerZone() != null && loc.getUpperZone() != null)
            			&& (loc.getLowerZone().getLabel() == loc.getUpperZone().getLabel())) {
            		zone.setLabel(loc.getLowerZone().getLabel());
                    printVerbose("3 ");
                    toBeRemoved.add(zone);
            	}
            }
        }
        for(BxZone zone: toBeRemoved) {
        	zoneLocMap.remove(zone);
        }
        unlabeledZones.removeAll(toBeRemoved);
    }
    
    private static Double max(List<Double> values) {
        Double max = Double.NEGATIVE_INFINITY;
        for (Double val : values) {
            if (!val.isNaN()) {
                max = Math.min(max, val);
            } else {
                continue;
            }
        }
        return max;
    }

    private static Map<BxZoneLabel, Double> aggregate(List<LabelTrio> sims) {
        HashMap<BxZoneLabel, Double> ret = new HashMap<BxZoneLabel, Double>();
        for (LabelTrio trio : sims) {
            if (ret.containsKey(trio.label)) {
                ret.put(trio.label, ret.get(trio.label) + trio.alignment / trio.entryTokens.size());
            } else {
                ret.put(trio.label, trio.alignment / trio.entryTokens.size());
            }
        }
        return ret;
    }

    
    
    public static void main(String[] args) throws AnalysisException, ParserConfigurationException, SAXException, IOException, XPathExpressionException, TransformationException {
        if (args.length != 1) {
            System.err.println("Usage: <pubmed .pdf path>");
            System.exit(1);
        }
        String pdfPath = args[0];
        String nxmlPath = StringTools.getNLMPath(pdfPath);


        InputStream pdfStream = new FileInputStream(pdfPath);
        InputStream nxmlStream = new FileInputStream(nxmlPath);

        PubmedXMLGenerator datasetGenerator = new PubmedXMLGenerator();
        datasetGenerator.setVerbose(true);
        BxDocument bxDoc = datasetGenerator.generateTrueViz(pdfStream, nxmlStream);

        FileWriter fstream = new FileWriter(StringTools.getTrueVizPath(nxmlPath));
        BufferedWriter out = new BufferedWriter(fstream);
        BxDocumentToTrueVizWriter writer = new BxDocumentToTrueVizWriter();
        out.write(writer.write(bxDoc.getPages()));
        out.close();

    }

}
