package pl.edu.icm.yadda.analysis.metadata.zoneclassification.tools;

import java.util.Map;
import pl.edu.icm.yadda.analysis.textr.model.BxDocument;
import pl.edu.icm.yadda.analysis.textr.model.BxPage;
import pl.edu.icm.yadda.analysis.textr.model.BxZone;
import pl.edu.icm.yadda.analysis.textr.model.BxZoneLabel;
import pl.edu.icm.yadda.analysis.textr.tools.BxBoundsBuilder;

/**
 * Zone classification utility class.
 *
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class ZoneClassificationUtils {

    public static void mapZoneLabels(BxDocument document, Map<BxZoneLabel, BxZoneLabel> labelMap) {
        for (BxPage page : document.getPages()) {
            for (BxZone zone : page.getZones()) {
                if (labelMap.get(zone.getLabel()) != null) {
                    zone.setLabel(labelMap.get(zone.getLabel()));
                }
            }
        }
    }

    public static void correctPagesBounds(BxDocument document) {
        BxBoundsBuilder builder = new BxBoundsBuilder();
        for (BxPage page : document.getPages()) {
            builder.expand(page.getBounds());
        }
        for (BxPage page : document.getPages()) {
            page.setBounds(builder.getBounds());
        }
    }
}
