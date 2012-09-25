package pl.edu.icm.yadda.analysis.metadata.extraction.enhancers;

import java.util.EnumSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import pl.edu.icm.yadda.analysis.textr.model.BxZone;
import pl.edu.icm.yadda.analysis.textr.model.BxZoneLabel;
import pl.edu.icm.yadda.bwmeta.model.YElement;

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
    protected boolean enhanceMetadata(BxZone zone, YElement metadata) {
        Matcher matcher = PATTERN.matcher(zone.toText());
        boolean ret = false;
        while (matcher.find()) {
            String email = matcher.group().replaceFirst("[;\\.,]$", "");
            metadata.addAttribute("email", email);
            ret = true;
        }
        return ret;
    }
       
}
