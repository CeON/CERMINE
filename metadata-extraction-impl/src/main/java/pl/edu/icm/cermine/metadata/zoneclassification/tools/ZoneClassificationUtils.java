package pl.edu.icm.cermine.metadata.zoneclassification.tools;

import java.util.Map;
import pl.edu.icm.cermine.structure.model.BxDocument;
import pl.edu.icm.cermine.structure.model.BxPage;
import pl.edu.icm.cermine.structure.model.BxZone;
import pl.edu.icm.cermine.structure.model.BxZoneLabel;
import pl.edu.icm.cermine.structure.tools.BxBoundsBuilder;

/**
 * Zone classification utility class.
 *
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class ZoneClassificationUtils {
	private static String[] conjunctions = {"and", "or", "for", "or", "nor"};

	public static Boolean isConjunction(String word) {
		for(String conjunction: conjunctions) {
			if(conjunction.equalsIgnoreCase(word))
				return true;
		}
		return false;
	}

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
