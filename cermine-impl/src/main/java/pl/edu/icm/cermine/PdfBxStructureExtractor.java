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

import java.io.*;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.exception.TransformationException;
import pl.edu.icm.cermine.structure.*;
import pl.edu.icm.cermine.structure.model.BxDocument;
import pl.edu.icm.cermine.structure.tools.BxModelUtils;
import pl.edu.icm.cermine.structure.transformers.BxDocumentToTrueVizWriter;


/**
 * Document geometric structure extractor. Extracts the geometric hierarchical structure
 * (pages, zones, lines, words and characters) from a PDF file and stores it as a BxDocument object.
 *
 * @author Dominika Tkaczyk
 */
public class PdfBxStructureExtractor implements DocumentStructureExtractor {

    /** individual character extractor */
    private CharacterExtractor characterExtractor;
    
    /** document object segmenter */
    private DocumentSegmenter documentSegmenter;
    
    /** reading order resolver */
    private ReadingOrderResolver roResolver;
    
    /** initial zone classifier */
    private ZoneClassifier initialClassifier;


    public PdfBxStructureExtractor() throws AnalysisException {
        try {
            characterExtractor = new ITextCharacterExtractor();
            documentSegmenter = new ParallelDocstrumSegmenter();
            roResolver = new HierarchicalReadingOrderResolver();
            initialClassifier = new SVMInitialZoneClassifier();
        } catch (IOException ex) {
            throw new AnalysisException("Cannot create PdfBxStructureExtractor!", ex);
        }
    }
    
    public PdfBxStructureExtractor(InputStream model, InputStream range) throws AnalysisException {
        try {
            characterExtractor = new ITextCharacterExtractor();
            documentSegmenter = new ParallelDocstrumSegmenter();
            roResolver = new HierarchicalReadingOrderResolver();
            
            InputStreamReader modelISRI = new InputStreamReader(model);
            BufferedReader modelFileI = new BufferedReader(modelISRI);
            InputStreamReader rangeISRI = new InputStreamReader(range);
            BufferedReader rangeFileI = new BufferedReader(rangeISRI);
            initialClassifier = new SVMInitialZoneClassifier(modelFileI, rangeFileI);
        } catch (IOException ex) {
            throw new AnalysisException("Cannot create PdfBxStructureExtractor!", ex);
        }
    }

    public PdfBxStructureExtractor(CharacterExtractor glyphExtractor, DocumentSegmenter pageSegmenter, 
            ReadingOrderResolver roResolver, ZoneClassifier initialClassifier) {
        this.characterExtractor = glyphExtractor;
        this.documentSegmenter = pageSegmenter;
        this.roResolver = roResolver;
        this.initialClassifier = initialClassifier;
    }
    
    /**
     * Extracts the geometric structure from a PDF file and stores it as BxDocument.
     * 
     * @param stream
     * @return BxDocument object storing the geometric structure
     * @throws AnalysisException 
     */
    @Override
    public BxDocument extractStructure(InputStream stream) throws AnalysisException {
        BxDocument doc = characterExtractor.extractCharacters(stream);
        doc = documentSegmenter.segmentDocument(doc);
        BxModelUtils.setParents(doc);
        doc = roResolver.resolve(doc);
        return initialClassifier.classifyZones(doc);
    }

    public void setGlyphExtractor(CharacterExtractor glyphExtractor) {
        this.characterExtractor = glyphExtractor;
    }

    public void setInitialClassifier(ZoneClassifier initialClassifier) {
        this.initialClassifier = initialClassifier;
    }

    public void setPageSegmenter(DocumentSegmenter pageSegmenter) {
        this.documentSegmenter = pageSegmenter;
    }

    public void setRoResolver(ReadingOrderResolver roResolver) {
        this.roResolver = roResolver;
    }
    
    public static void main(String[] args) throws AnalysisException, IOException, TransformationException {
    	if(args.length != 1){
    		System.err.println("USAGE: program DIR_PATH");
    		System.exit(1);
    	}
    	PdfBxStructureExtractor extractor = new PdfBxStructureExtractor();
    	File dir = new File(args[0]);
    	for(File pdf: dir.listFiles()) {
    		BxDocument result = extractor.extractStructure(new FileInputStream(pdf));
    		FileWriter fstream = new FileWriter(pdf.getName() + ".xml");
            BufferedWriter out = new BufferedWriter(fstream);
            try {
                BxDocumentToTrueVizWriter writer = new BxDocumentToTrueVizWriter();
                out.write(writer.write(result.getPages()));
                writer.write(result.getPages());
            } finally {
                out.close();
            }
    	}
    }
   
}