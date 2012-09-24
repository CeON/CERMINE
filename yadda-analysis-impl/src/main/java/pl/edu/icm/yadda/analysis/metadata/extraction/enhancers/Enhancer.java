package pl.edu.icm.yadda.analysis.metadata.extraction.enhancers;

import java.util.Set;
import org.jdom.Element;
import pl.edu.icm.yadda.analysis.textr.model.BxDocument;

/**
 *
 * @author krusek
 */
public interface Enhancer {

    /**
     * Extracts metadata from the document and puts it into metadata object.
     *
     * @param document segmented and classified document
     * @param metadata metadata object containing metadata already extracted
     * by other enhancers; newly extracted metadata are added to this object
     * @param enhancedFields set of fields enhanced already enhanced by other
     * enhancers; newly enhanced fields are added be added to this set
     */
    void enhanceMetadata(BxDocument document, Element metadata, Set<EnhancedField> enhancedFields);
}
