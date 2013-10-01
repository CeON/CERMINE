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

package pl.edu.icm.cermine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.jdom.Element;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.metadata.EnhancerMetadataExtractor;
import pl.edu.icm.cermine.metadata.MetadataExtractor;
import pl.edu.icm.cermine.structure.SVMMetadataZoneClassifier;
import pl.edu.icm.cermine.structure.ZoneClassifier;
import pl.edu.icm.cermine.structure.model.BxDocument;


/**
 * NLM-based metadata extractor from PDF files.
 *
 * @author Dominika Tkaczyk
 */
public class PdfNLMMetadataExtractor implements DocumentMetadataExtractor<Element> {

    /** geometric structure extractor */
    private DocumentStructureExtractor strExtractor;
   
    /** metadata zone classifier */
    private ZoneClassifier metadataClassifier;

    /** metadata extractor from labelled zones */
    private MetadataExtractor<Element> extractor;

    public PdfNLMMetadataExtractor() throws AnalysisException {
        try {
            strExtractor = new PdfBxStructureExtractor();
            metadataClassifier = SVMMetadataZoneClassifier.getDefaultInstance();
            extractor = new EnhancerMetadataExtractor();
        } catch (IOException ex) {
            throw new AnalysisException(ex);
        }
    }
    
    public PdfNLMMetadataExtractor(InputStream metadataModel, InputStream metadataRange) throws AnalysisException {
        try {
            strExtractor = new PdfBxStructureExtractor();

            BufferedReader metaModelFileReader = new BufferedReader(new InputStreamReader(metadataModel));
            BufferedReader metaRangeFileReader = new BufferedReader(new InputStreamReader(metadataRange));
            metadataClassifier = new SVMMetadataZoneClassifier(metaModelFileReader, metaRangeFileReader);
            metaModelFileReader.close();
            metaRangeFileReader.close();

            extractor = new EnhancerMetadataExtractor();
        } catch (IOException ex) {
            throw new AnalysisException(ex);
        }
    }

    public PdfNLMMetadataExtractor(DocumentStructureExtractor strExtractor, 
    		ZoneClassifier metadataClassifier, MetadataExtractor<Element> extractor) {
        this.strExtractor = strExtractor;
        this.metadataClassifier = metadataClassifier;
        this.extractor = extractor;
    }
         
    /**
     * Extracts metadata from PDF file and stores it in NLM format.
     * 
     * @param stream
     * @return extracted metadata in NLM format
     * @throws AnalysisException 
     */
    @Override
    public Element extractMetadata(InputStream stream) throws AnalysisException {
        BxDocument doc = strExtractor.extractStructure(stream);
        return extractMetadata(doc);
    }
    
    /**
     * Extracts metadata from PDF file and stores it in NLM format.
     * 
     * @param document
     * @return extracted metadata in NLM format
     * @throws AnalysisException 
     */
    @Override
    public Element extractMetadata(BxDocument document) throws AnalysisException {
        BxDocument doc = metadataClassifier.classifyZones(document);
        return extractor.extractMetadata(doc);
    }

    public void setExtractor(MetadataExtractor<Element> extractor) {
        this.extractor = extractor;
    }

    public void setMetadataClassifier(ZoneClassifier metadataClassifier) {
        this.metadataClassifier = metadataClassifier;
    }

    public void setStrExtractor(DocumentStructureExtractor strExtractor) {
        this.strExtractor = strExtractor;
    }
    
    private static String getXPathValue(Element nlm, String path) throws XPathExpressionException {
    	XPath xPath = XPathFactory.newInstance().newXPath();
        String res = (String) xPath.evaluate(path, nlm,  XPathConstants.STRING);
        if (res != null) {
            res = res.trim();
        }
        return res;
    }
    
}