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

import pl.edu.icm.cermine.evaluation.tools.MetadataSingle;
import pl.edu.icm.cermine.evaluation.tools.MetadataList;
import pl.edu.icm.cermine.evaluation.tools.EvaluationUtils;
import pl.edu.icm.cermine.evaluation.tools.PrecisionRecall;
import pl.edu.icm.cermine.evaluation.tools.NlmPair;
import pl.edu.icm.cermine.evaluation.tools.ComparisonResult;
import pl.edu.icm.cermine.evaluation.tools.NlmIterator;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import org.apache.commons.io.FileUtils;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.exception.TransformationException;
import pl.edu.icm.cermine.tools.XMLTools;

/**
 * @author Pawel Szostek (p.szostek@icm.edu.pl)
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public final class CrossrefMetadataExtractionEvaluation {

    public void evaluate(int mode, NlmIterator iter) throws AnalysisException, IOException, TransformationException, ParserConfigurationException, SAXException, JDOMException, XPathExpressionException, TransformerException {

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

        List<ComparisonResult> titles = new ArrayList<ComparisonResult>();
        List<ComparisonResult> affiliations = new ArrayList<ComparisonResult>();
        List<ComparisonResult> references = new ArrayList<ComparisonResult>();

        int i = 0;
        
        for (NlmPair pair : iter) {
            i++;
            
            if (mode == 0) {
                System.out.println("");
                System.out.println(">>>>>>>>> "+i);
                System.out.println(pair.getExtractedNlm().getPath());
            }
            
            org.w3c.dom.Document extractedNlm;
            List<String> lines;
            try {
                extractedNlm = documentBuilder.parse(new FileInputStream(pair.getExtractedNlm()));
                lines = FileUtils.readLines(pair.getOriginalNlm());
            } catch (SAXException ex) {
                i--;
                continue;
            }
            if (lines.isEmpty()) {
                i--;
                continue;
            }
            
         
            // Document's title
            MetadataSingle title = new MetadataSingle(lines.get(0),
                    XMLTools.extractTextFromNode(extractedNlm, "/article/front/article-meta//article-title").trim());
            title.setComp(EvaluationUtils.swComparator);
            titles.add(title);
            title.print(mode, "title");
            

            // Affiliations
            int secondSpace = 2;
            while (!lines.get(secondSpace).isEmpty()) {
                secondSpace++;
            }
            Set<String> expectedAffiliationsSet = Sets.newHashSet(lines.subList(2, secondSpace));
            Set<String> extractedAffiliationsSet = Sets.newHashSet(XMLTools.extractTextAsList(extractedNlm, "/article/front/article-meta//aff"));
            List<String> expectedAffiliations = Lists.newArrayList(expectedAffiliationsSet);
            List<String> extractedAffiliations = Lists.newArrayList(extractedAffiliationsSet);
            MetadataList affiliation = new MetadataList(expectedAffiliations, extractedAffiliations);
            affiliation.setComp(EvaluationUtils.cosineComparator());
            affiliations.add(affiliation);
            affiliation.print(mode, "affiliation");
            

            //references
            List<Node> extractedRefNodes = XMLTools.extractNodes(extractedNlm, "//ref-list/ref");
            List<String> extractedRefs = new ArrayList<String>();
            for (Node extractedRefNode : extractedRefNodes) {
                extractedRefs.add(XMLTools.extractTextFromNode(extractedRefNode).trim());
            }

            List<String> originalRefs = lines.subList(secondSpace+1, lines.size());
            
            MetadataList refs = new MetadataList(originalRefs, extractedRefs);
            refs.setComp(EvaluationUtils.cosineComparator(0.6));
            
            references.add(refs);
            refs.print(mode, "references");

        }

        if (mode != 1) {
            System.out.println("==== Summary (" + iter.size() + " docs)====");
        
            PrecisionRecall titlePR = new PrecisionRecall().build(titles);
            titlePR.print("Title");

            PrecisionRecall affiliationsPR = new PrecisionRecall().build(affiliations);
            affiliationsPR.print("Affiliations");
        
            PrecisionRecall refsPR = new PrecisionRecall().build(references);
            refsPR.print("References");
        }
    }

    public static void main(String[] args) throws AnalysisException, IOException, TransformationException, ParserConfigurationException, SAXException, JDOMException, XPathExpressionException, TransformerException {
        if (args.length != 3 && args.length != 4) {
            System.out.println("Usage: FinalMetadataExtractionEvaluation <input dir> <orig extension> <extract extension>");
            return;
        }
        String directory = args[0];
        String origExt = args[1];
        String extrExt = args[2];

        int mode = 0;
        if (args.length == 4 && args[3].equals("csv")) {
            mode = 1;
        }
        if (args.length == 4 && args[3].equals("q")) {
            mode = 2;
        }

        CrossrefMetadataExtractionEvaluation e = new CrossrefMetadataExtractionEvaluation();
        NlmIterator iter = new NlmIterator(directory, origExt, extrExt);
        e.evaluate(mode, iter);

    }
    
}
