package pl.edu.icm.yadda.analysis.metadata.extraction;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import pl.edu.icm.yadda.analysis.AnalysisException;
import pl.edu.icm.yadda.analysis.metadata.extraction.enhancers.EnhancedField;
import pl.edu.icm.yadda.analysis.metadata.extraction.enhancers.Enhancer;
import pl.edu.icm.yadda.analysis.textr.MetadataExtractor;
import pl.edu.icm.yadda.analysis.textr.model.BxDocument;
import pl.edu.icm.yadda.analysis.textr.tools.BxModelUtils;
import pl.edu.icm.yadda.bwmeta.model.YElement;

/**
 *
 * @author krusek
 */
public class EnhancerMetadataExtractor implements MetadataExtractor<YElement> {

    private final List<Enhancer> enhancers = new ArrayList<Enhancer>();

    public List<Enhancer> getEnhancers() {
        return enhancers;
    }

    public EnhancerMetadataExtractor addEnhancer(Enhancer enhancer) {
        enhancers.add(enhancer);
        return this;
    }

    public EnhancerMetadataExtractor setEnhancers(List<Enhancer> value) {
        enhancers.clear();
        enhancers.addAll(value);
        return this;
    }

    @Override
    public YElement extractMetadata(BxDocument document) throws AnalysisException {
        Set<EnhancedField> enhancedFields = EnumSet.noneOf(EnhancedField.class);
        BxModelUtils.sortZonesRecursively(document);
        YElement metadata = new YElement().setId("elementId");
        for (Enhancer enhancer : enhancers) {
            enhancer.enhanceMetadata(document, metadata, enhancedFields);
        }
        return metadata;
    }

}
