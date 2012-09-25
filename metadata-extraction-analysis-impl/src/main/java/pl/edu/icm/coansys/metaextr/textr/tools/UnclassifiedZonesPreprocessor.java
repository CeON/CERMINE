package pl.edu.icm.coansys.metaextr.textr.tools;

import pl.edu.icm.coansys.metaextr.textr.tools.DocumentPreprocessor;
import pl.edu.icm.coansys.metaextr.textr.model.BxDocument;
import pl.edu.icm.coansys.metaextr.textr.model.BxPage;
import pl.edu.icm.coansys.metaextr.textr.model.BxZone;
import pl.edu.icm.coansys.metaextr.textr.model.BxZoneLabel;

/**
 *
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class UnclassifiedZonesPreprocessor implements DocumentPreprocessor {

    @Override
    public void process(BxDocument document) {
        for (BxPage page: document.getPages()) {
            for (BxZone zone: page.getZones()) {
                zone.setLabel(BxZoneLabel.OTH_UNKNOWN);
            }
        }
    }

}
