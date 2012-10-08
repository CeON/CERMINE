package pl.edu.icm.cermine.metadata.extraction.enhancers;

import org.jdom.Element;

/**
 *
 * @author krusek
 */
public class RevisedDateEnhancer extends AbstractDateEnhancer {

    public RevisedDateEnhancer() {
        super(EnhancedField.REVISED_DATE, "revised");
    }

    @Override
    protected void enhanceMetadata(Element metadata, String day, String month, String year) {
        Enhancers.setRevisedDate(metadata, day, month, year);
    }
}
