package pl.edu.icm.cermine.metadata.extraction.enhancers;

import org.jdom.Element;

/**
 *
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class RevisedFormDateEnhancer extends AbstractDateEnhancer {

    public RevisedFormDateEnhancer() {
        super(EnhancedField.REVISED_DATE, "revised form");
    }

    @Override
    protected void enhanceMetadata(Element metadata, String day, String month, String year) {
        Enhancers.setRevisedDate(metadata, day, month, year);
    }
}
