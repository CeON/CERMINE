package pl.edu.icm.coansys.metaextr.metadata.extraction.enhancers;

import org.jdom.Element;

/**
 *
 * @author krusek
 */
public class ReceivedDateEnhancer extends AbstractDateEnhancer {

    public ReceivedDateEnhancer() {
        super(EnhancedField.RECEIVED_DATE, "received");
    }

    @Override
    protected void enhanceMetadata(Element metadata, String day, String month, String year) {
        Enhancers.setReceivedDate(metadata, day, month, year);
    }
}
