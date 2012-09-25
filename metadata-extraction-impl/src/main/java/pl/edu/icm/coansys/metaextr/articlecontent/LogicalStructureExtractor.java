package pl.edu.icm.coansys.metaextr.articlecontent;

import pl.edu.icm.coansys.metaextr.textr.model.BxZone;
import pl.edu.icm.coansys.metaextr.textr.model.BxLine;
import pl.edu.icm.coansys.metaextr.textr.model.BxPage;
import pl.edu.icm.coansys.metaextr.textr.model.BxZoneLabel;
import pl.edu.icm.coansys.metaextr.textr.model.BxDocument;
import pl.edu.icm.coansys.metaextr.AnalysisException;
import pl.edu.icm.coansys.metaextr.articlecontent.model.BxDocContentStructure;
import pl.edu.icm.coansys.metaextr.articlecontent.model.DocumentContentStructure;
import pl.edu.icm.coansys.metaextr.classification.features.FeatureVectorBuilder;
import pl.edu.icm.coansys.metaextr.classification.knn.model.KnnModel;

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
