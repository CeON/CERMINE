package pl.edu.icm.coansys.metaextr.content;

import pl.edu.icm.coansys.metaextr.structure.model.BxZone;
import pl.edu.icm.coansys.metaextr.structure.model.BxLine;
import pl.edu.icm.coansys.metaextr.structure.model.BxPage;
import pl.edu.icm.coansys.metaextr.structure.model.BxZoneLabel;
import pl.edu.icm.coansys.metaextr.structure.model.BxDocument;
import pl.edu.icm.coansys.metaextr.exception.AnalysisException;
import pl.edu.icm.coansys.metaextr.content.model.BxDocContentStructure;
import pl.edu.icm.coansys.metaextr.content.model.DocumentContentStructure;
import pl.edu.icm.coansys.metaextr.tools.classification.features.FeatureVectorBuilder;
import pl.edu.icm.coansys.metaextr.tools.classification.knn.KnnModel;

/**
 *
 * @author Dominika Tkaczyk
 */
public class LogicalStructureExtractor {

    public DocumentContentStructure extractStructure(KnnModel<BxZoneLabel> junkFilterModel, 
            KnnModel<BxZoneLabel> headerModel,
            FeatureVectorBuilder<BxZone, BxPage> junkVectorBuilder, 
            FeatureVectorBuilder<BxLine, BxPage> classVectorBuilder, 
            FeatureVectorBuilder<BxLine, BxPage> clustVectorBuilder, 
            BxDocument document) throws AnalysisException {
        ContentJunkFilter junkFilter = new ContentJunkFilter();
        document = junkFilter.filterJunk(junkFilterModel, junkVectorBuilder, document);
        
        ContentHeaderExtractor headerExtractor = new ContentHeaderExtractor(); 
        BxDocContentStructure tmpContentStructure = headerExtractor.extractHeaders(headerModel, classVectorBuilder, 
                clustVectorBuilder, document);

        ContentCleaner contentCleaner = new ContentCleaner();
        contentCleaner.cleanupContent(tmpContentStructure);
                
        DocumentContentStructure contentStructure = new DocumentContentStructure();
        contentStructure.build(tmpContentStructure);
        
        return contentStructure;
    }
    
}
