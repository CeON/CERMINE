package pl.edu.icm.yadda.analysis.textr.tools;

import pl.edu.icm.yadda.analysis.textr.model.BxDocument;
import pl.edu.icm.yadda.analysis.textr.model.BxLine;
import pl.edu.icm.yadda.analysis.textr.model.BxPage;
import pl.edu.icm.yadda.analysis.textr.model.BxWord;
import pl.edu.icm.yadda.analysis.textr.model.BxZone;

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
