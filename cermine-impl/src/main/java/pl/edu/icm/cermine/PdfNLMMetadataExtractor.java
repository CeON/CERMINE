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
import org.jdom.Element;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.exception.TransformationException;
import pl.edu.icm.cermine.metadata.EnhancerMetadataExtractor;
import pl.edu.icm.cermine.metadata.MetadataExtractor;
import pl.edu.icm.cermine.metadata.affiliations.parsing.AffiliationParser;
import pl.edu.icm.cermine.metadata.model.DocumentMetadata;
import pl.edu.icm.cermine.metadata.transformers.DocumentMetadataToNLMElementConverter;
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
    private MetadataExtractor<DocumentMetadata> extractor;
    
    /** affiliation parser **/
    private AffiliationParser parser;
    
    DocumentMetadataToNLMElementConverter converter;

    public PdfNLMMetadataExtractor() throws AnalysisException {
        try {
            strExtractor = new PdfBxStructureExtractor();
            metadataClassifier = SVMMetadataZoneClassifier.getDefaultInstance();
            extractor = new EnhancerMetadataExtractor();
            converter = new DocumentMetadataToNLMElementConverter();
            parser = new AffiliationParser();
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
            converter = new DocumentMetadataToNLMElementConverter();
        } catch (IOException ex) {
            throw new AnalysisException(ex);
        }
    }

    public PdfNLMMetadataExtractor(DocumentStructureExtractor strExtractor, 
    		ZoneClassifier metadataClassifier, MetadataExtractor<DocumentMetadata> extractor) {
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
        try {
            BxDocument doc = metadataClassifier.classifyZones(document);
            DocumentMetadata metadata = extractor.extractMetadata(doc);
            parser.parseAffiliation(metadata.getAffiliations());
            return converter.convert(metadata);
        } catch (TransformationException ex) {
            throw new AnalysisException(ex);
        }
    }

    public void setExtractor(MetadataExtractor<DocumentMetadata> extractor) {
        this.extractor = extractor;
    }

    public void setMetadataClassifier(ZoneClassifier metadataClassifier) {
        this.metadataClassifier = metadataClassifier;
    }

    public void setStrExtractor(DocumentStructureExtractor strExtractor) {
        this.strExtractor = strExtractor;
    }
    
}