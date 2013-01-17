package pl.edu.icm.cermine.metadata.extraction.enhancers;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import org.jdom.Element;
import pl.edu.icm.cermine.structure.model.BxDocument;
import pl.edu.icm.cermine.structure.model.BxPage;
import pl.edu.icm.cermine.structure.model.BxZone;
import pl.edu.icm.cermine.structure.model.BxZoneLabel;

/**
 * Author enhancer.
 *
 * This enhancer should be invoked after affiliation enhancer.
 *
 * @author krusek
 */
public class AuthorEnhancer extends AbstractSimpleEnhancer {

    public AuthorEnhancer() {
        setSearchedZoneLabels(EnumSet.of(BxZoneLabel.MET_AUTHOR));
    }

    @Override
    protected boolean enhanceMetadata(BxDocument document, Element metadata) {
        boolean enhanced = false;
        for (BxPage page : filterPages(document)) {
            for (BxZone zone : filterZones(page)) {
                String text = zone.toText().replaceFirst("^[Aa]uthors:", "").trim();
                String author = null;
                List<String> refs = new ArrayList<String>();
                Pattern ref = Pattern.compile("\\d+|\\*|†");
                // We have to support following cases:
                //     Name, Name and Name
                //     Name1, Name,1, Name,1,2
                //     Name1*, Name1,*, Name,1*†
                for (String part : text.split("(?=\\*|†)|,|\\band\\b|&|(?<=[^\\d])(?=[\\d])|(?<=\\d)\\s")) {
                    part = part.trim();
                    if (!part.isEmpty()) {
                        if (ref.matcher(part).matches()) {
                            if (author != null) {
                                refs.add(part);
                            }
                        } else {
                            if (author != null) {
                                Enhancers.addAuthor(metadata, author, refs);
                            }
                            author = part;
                            refs.clear();
                        }
                    }
                }
                if (author != null) {
                    Enhancers.addAuthor(metadata, author, refs);
                }
                enhanced = true;
            }
            if (enhanced) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected Set<EnhancedField> getEnhancedFields() {
        return EnumSet.of(EnhancedField.AUTHORS);
    }

}
