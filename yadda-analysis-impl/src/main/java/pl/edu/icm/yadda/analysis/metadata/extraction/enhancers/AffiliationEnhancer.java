package pl.edu.icm.yadda.analysis.metadata.extraction.enhancers;

import java.util.EnumSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jdom.Element;
import pl.edu.icm.yadda.analysis.textr.model.BxLine;
import pl.edu.icm.yadda.analysis.textr.model.BxZone;
import pl.edu.icm.yadda.analysis.textr.model.BxZoneLabel;

/**
 *
 * @author krusek
 */
public class AffiliationEnhancer extends AbstractSimpleEnhancer {

    public AffiliationEnhancer() {
        setSearchedZoneLabels(BxZoneLabel.MET_AFFILIATION);
    }

    @Override
    protected Set<EnhancedField> getEnhancedFields() {
        return EnumSet.of(EnhancedField.AFFILIATION);
    }
/*
    private static void putAffiliation(YElement element, String text, String ref) {
        text = text.replaceFirst(" and$", "").replaceFirst("\\S+@.*$", "").replaceFirst("[Ee]mails?:.*$", "");
        text = text.replaceFirst("[Ee]-[Mm]ails?:.*$", "").trim().replaceFirst("[\\.,;]$", "");
        YAffiliation affiliation = new YAffiliation(Enhancers.affiliationIdFromIndex(ref), text);
        element.addAffiliation(affiliation);
    }
*/
    @Override
    protected boolean enhanceMetadata(BxZone zone, Element metadata) {
        Pattern junkPattern = Pattern.compile("^\\s*(\\*|†)");
        Pattern refPattern = Pattern.compile("^\\s*(\\d+)\\s*");
        String ref = null;
        String fullText = null;
        int expectedIndex = 1;
        boolean isJunk = false;
        for (BxLine line : zone.getLines()) {
            String text = line.toText();
            Matcher matcher = refPattern.matcher(text);
            if (junkPattern.matcher(text).find()) {
                if (!isJunk && fullText != null) {
//                    putAffiliation(metadata, fullText, ref);
                }
                isJunk = true;
            } else if (matcher.find() && Integer.parseInt(matcher.group(1)) == expectedIndex) {
                if (!isJunk && fullText != null) {
//                    putAffiliation(metadata, fullText, ref);
                }
                ref = matcher.group(1);
                fullText = text.substring(matcher.end());
                expectedIndex++;
                isJunk = false;
            } else if(!isJunk) {
                if (fullText != null) {
                    fullText += " " + text;
                } else {
                    fullText = text;
                }
            }
        }
        if (fullText != null) {
//            putAffiliation(metadata, fullText, ref);
        }
        return true;
    }
}
