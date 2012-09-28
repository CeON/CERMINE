package pl.edu.icm.coansys.metaextr.metadata.extraction.enhancers;

import java.util.EnumSet;
import java.util.Set;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import org.jdom.Element;
import pl.edu.icm.coansys.metaextr.structure.model.BxDocument;
import pl.edu.icm.coansys.metaextr.structure.model.BxZoneLabel;

/**
 *
 * @author krusek
 */
public class PagesEnhancer extends AbstractPatternEnhancer {

    private static final Pattern PATTERN = Pattern.compile(
            "\\bpp[\\s:-]\\s*(\\d+)[-\u002D\u00AD\u2010\u2011\u2012\u2013\u2014\u2015\u207B\u208B\u2212](\\d+)",
            Pattern.CASE_INSENSITIVE);
    private int pages = 100;

    public PagesEnhancer() {
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
        if (first <= last && last - first < pages + 10) {
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
