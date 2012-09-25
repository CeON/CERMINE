package pl.edu.icm.coansys.metaextr.metadata.extraction.enhancers;

import org.jdom.Element;

/**
 *
 * @author krusek
 */
public class AcceptedDateEnhancer extends AbstractDateEnhancer {

    public AcceptedDateEnhancer() {
        super(EnhancedField.ACCEPTED_DATE, "accepted");
    }

    @Override
    protected void enhanceMetadata(Element metadata, String day, String month, String year) {
        Enhancers.setAcceptedDate(metadata, day, month, year);
    }
}
