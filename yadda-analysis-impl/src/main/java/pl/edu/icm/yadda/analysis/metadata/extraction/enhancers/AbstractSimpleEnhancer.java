package pl.edu.icm.yadda.analysis.metadata.extraction.enhancers;

import java.util.Collection;
import java.util.Set;
import org.apache.commons.collections.CollectionUtils;
import org.jdom.Element;
import pl.edu.icm.yadda.analysis.textr.model.BxDocument;
import pl.edu.icm.yadda.analysis.textr.model.BxPage;
import pl.edu.icm.yadda.analysis.textr.model.BxZone;
import pl.edu.icm.yadda.analysis.textr.model.BxZoneLabel;

/**
 * Abstract base class for enhancers that can only succeed or fail - if
 * an enhancer extracts, for example, both volume and issue (from text
 * "Vol. 1/2"), it can only extract both pieces of information or none of them.
 *
 * @author krusek
 */
abstract public class AbstractSimpleEnhancer extends AbstractFilterEnhancer {

    protected AbstractSimpleEnhancer() {}
    
    protected AbstractSimpleEnhancer(Collection<BxZoneLabel> zoneLabels) {
        setSearchedZoneLabels(zoneLabels);
    }

    protected boolean enhanceMetadata(BxZone zone, Element metadata) {
        return false;
    }

    protected boolean enhanceMetadata(BxPage page, Element metadata) {
        for (BxZone zone : filterZones(page)) {
            if (enhanceMetadata(zone, metadata)) {
                return true;
            }
        }
        return false;
    }

    protected boolean enhanceMetadata(BxDocument document, Element metadata) {
        for (BxPage page : filterPages(document)) {
            if (enhanceMetadata(page, metadata)) {
                return true;
            }
        }
        return false;
    }

    abstract protected Set<EnhancedField> getEnhancedFields();

    @Override
    public void enhanceMetadata(BxDocument document, Element metadata, Set<EnhancedField> enhancedFields) {
        Set<EnhancedField> fieldsToEnhance = getEnhancedFields();
        if (! CollectionUtils.containsAny(enhancedFields, fieldsToEnhance)) {
            if (enhanceMetadata(document, metadata)) {
                enhancedFields.addAll(fieldsToEnhance);
            }
        }
    }
}
