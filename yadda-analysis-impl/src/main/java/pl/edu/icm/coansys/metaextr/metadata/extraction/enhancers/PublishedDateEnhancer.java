package pl.edu.icm.coansys.metaextr.metadata.extraction.enhancers;

import org.jdom.Element;

/**
 *
 * @author krusek
 */
public class PublishedDateEnhancer extends AbstractDateEnhancer {

    public PublishedDateEnhancer() {
        super(EnhancedField.PUBLISHED_DATE, "published");
    }

    @Override
    protected void enhanceMetadata(Element metadata, String day, String month, String year) {
        Enhancers.setPublishedDate(metadata, day, month, year);
    }
}
