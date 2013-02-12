package pl.edu.icm.cermine.metadata.extraction.enhancers;

import java.util.EnumSet;
import java.util.Set;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import org.jdom.Element;
import pl.edu.icm.cermine.structure.model.BxDocument;
import pl.edu.icm.cermine.structure.model.BxZoneLabel;

/**
 *
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class PagesPartialEnhancer extends AbstractPatternEnhancer {

    private static final Pattern PATTERN = Pattern.compile(
            "(\\d{1,5})[\u002D\u00AD\u2010\u2011\u2012\u2013\u2014\u2015\u207B\u208B\u2212-](\\d{1,5})",
            Pattern.CASE_INSENSITIVE);
    private int pages = 10;

    public PagesPartialEnhancer() {
        super(PATTERN);
        setSearchedZoneLabels(BxZoneLabel.MET_BIB_INFO);
    }
    
    @Override
    protected boolean enhanceMetadata(BxDocument document, Element metadata) {
        pages = document.getPages().size();
        return super.enhanceMetadata(document, metadata);
    }

    @Override
    protected boolean enhanceMetadata(MatchResult result, Element metadata) {
        int first = Integer.parseInt(result.group(1));
        int last = Integer.parseInt(result.group(2));
        if (first <= last && last - first < pages *2) {
            Enhancers.setPages(metadata, result.group(1), result.group(2));
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected Set<EnhancedField> getEnhancedFields() {
        return EnumSet.of(EnhancedField.PAGES);
    }

}
