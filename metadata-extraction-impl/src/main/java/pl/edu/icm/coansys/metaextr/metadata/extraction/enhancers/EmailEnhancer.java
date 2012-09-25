package pl.edu.icm.coansys.metaextr.metadata.extraction.enhancers;

import java.util.EnumSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jdom.Element;
import pl.edu.icm.coansys.metaextr.textr.model.BxZone;
import pl.edu.icm.coansys.metaextr.textr.model.BxZoneLabel;

/**
 *
 * @author krusek
 */
public class EmailEnhancer extends AbstractSimpleEnhancer {

    private static final Pattern PATTERN = Pattern.compile("\\S+@\\S+");
    
    public EmailEnhancer() {
        this.setSearchedZoneLabels(BxZoneLabel.MET_CORRESPONDENCE, BxZoneLabel.MET_AFFILIATION);
    }

    @Override
    protected Set<EnhancedField> getEnhancedFields() {
        return EnumSet.of(EnhancedField.EMAIL);
    }

    @Override
    protected boolean enhanceMetadata(BxZone zone, Element metadata) {
        Matcher matcher = PATTERN.matcher(zone.toText());
        while (matcher.find()) {
            String email = matcher.group().replaceFirst("[;\\.,]$", "");
            Enhancers.addEmail(metadata, email);
        }
        return false;
    }
       
}
