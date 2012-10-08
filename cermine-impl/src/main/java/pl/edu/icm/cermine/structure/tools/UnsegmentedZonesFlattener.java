package pl.edu.icm.cermine.structure.tools;

import pl.edu.icm.cermine.structure.model.*;

/**
 *
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class UnsegmentedZonesFlattener implements DocumentPreprocessor {

    @Override
    public void process(BxDocument document) {
        for (BxPage page: document.getPages()) {
            for (BxZone zone: page.getZones()) {
                for (BxLine line: zone.getLines()) {
                    for (BxWord word: line.getWords()) {
                        zone.getChunks().addAll(word.getChunks());
                    }
                }
                zone.getLines().clear();
            }
        }
    }

}
