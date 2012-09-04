package pl.edu.icm.yadda.analysis.metadata.process.nodes;

import pl.edu.icm.yadda.analysis.bibref.BibReferenceExtractor;
import pl.edu.icm.yadda.analysis.metadata.MetadataExtractor;
import pl.edu.icm.yadda.analysis.textr.model.BxDocument;
import pl.edu.icm.yadda.bwmeta.model.YConstants;
import pl.edu.icm.yadda.bwmeta.model.YElement;
import pl.edu.icm.yadda.bwmeta.model.YRelation;
import pl.edu.icm.yadda.process.ctx.ProcessContext;
import pl.edu.icm.yadda.process.model.EnrichedPayload;
import pl.edu.icm.yadda.process.node.IProcessingNode;

/**
 * Metadata extractor node. The node extracts metadata and
 * bibliographic references from BxDocument and stores them as YElement.
 *
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class MetadataExtractorNode implements IProcessingNode<EnrichedPayload<BxDocument>, EnrichedPayload<YElement>> {

    private MetadataExtractor<YElement> metadataExtractor;

    private BibReferenceExtractor bibReferenceExtractor;

    @Override
    public EnrichedPayload<YElement> process(EnrichedPayload<BxDocument> input, ProcessContext ctx) throws Exception {
        YElement yElement = metadataExtractor.extractMetadata(input.getObject());
        String[] references = bibReferenceExtractor.extractBibReferences(input.getObject());
        for(String reference : references) {
            YRelation yRelation = new YRelation().setType(YConstants.RL_REFERENCE_TO);
            yRelation.addAttribute(YConstants.AT_REFERENCE_TEXT, reference);
            yElement.addRelation(yRelation);
        }
        return new EnrichedPayload(input.getId(), yElement, input.getCollections(), input.getLicenses());
    }

    public void setBibReferenceExtractor(BibReferenceExtractor bibReferenceExtractor) {
        this.bibReferenceExtractor = bibReferenceExtractor;
    }

    public void setMetadataExtractor(MetadataExtractor<YElement> metadataExtractor) {
        this.metadataExtractor = metadataExtractor;
    }

}
