package pl.edu.icm.yadda.analysis.metadata.extraction.enhancers;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import pl.edu.icm.yadda.analysis.textr.model.BxDocument;
import pl.edu.icm.yadda.analysis.textr.model.BxPage;
import pl.edu.icm.yadda.analysis.textr.model.BxZone;
import pl.edu.icm.yadda.analysis.textr.model.BxZoneLabel;
import pl.edu.icm.yadda.bwmeta.model.YConstants;
import pl.edu.icm.yadda.bwmeta.model.YContributor;
import pl.edu.icm.yadda.bwmeta.model.YElement;
import pl.edu.icm.yadda.bwmeta.model.YName;

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

    private static void putAuthor(YElement element, String author, List<String> refs) {
        author = author.replaceAll(" +\\.", ".");
        YName name = new YName().setType(YConstants.NM_CANONICAL).setText(author);
        YContributor contributor = new YContributor().setRole(YConstants.CR_AUTHOR).addName(name);
        for (String ref : refs) {
            if (ref.equals("*") || ref.equals("†")) {
                // Currently nothing is done
            } else {
                String id = Enhancers.affiliationIdFromIndex(ref);
                if (element.getAffiliation(id) != null) {
                    contributor.addAffiliationRef(id);
                }
            }
        }
        element.addContributor(contributor);
    }

    @Override
    protected boolean enhanceMetadata(BxDocument document, YElement metadata) {
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
                for (String part : text.split("(?=\\*|†)|,|\\band\\b|(?<=[^\\d])(?=[\\d])|(?<=\\d)\\s")) {
                    part = part.trim();
                    if (!part.isEmpty()) {
                        if (ref.matcher(part).matches()) {
                            if (author != null) {
                                refs.add(part);
                            }
                        } else {
                            if (author != null) {
                                putAuthor(metadata, author, refs);
                            }
                            author = part;
                            refs.clear();
                        }
                    }
                }
                if (author != null) {
                    putAuthor(metadata, author, refs);
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
