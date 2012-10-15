package pl.edu.icm.cermine.structure.tools;

import pl.edu.icm.cermine.structure.model.*;

/**
 *
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class UnsegmentedPagesFlattener implements DocumentProcessor {

    @Override
    public void process(BxDocument document) {
        for (BxPage page: document.getPages()) {
            for (BxZone zone: page.getZones()) {
                page.getChunks().addAll(zone.getChunks());
                for (BxLine line: zone.getLines()) {
                    for (BxWord word: line.getWords()) {
                        page.getChunks().addAll(word.getChunks());
                    }
                }
            }
            page.getZones().clear();
        }
    }

}
