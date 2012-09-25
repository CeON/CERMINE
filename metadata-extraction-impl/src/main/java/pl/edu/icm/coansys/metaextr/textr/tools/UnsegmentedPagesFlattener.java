package pl.edu.icm.coansys.metaextr.textr.tools;

import pl.edu.icm.coansys.metaextr.textr.tools.DocumentPreprocessor;
import pl.edu.icm.coansys.metaextr.textr.model.BxDocument;
import pl.edu.icm.coansys.metaextr.textr.model.BxLine;
import pl.edu.icm.coansys.metaextr.textr.model.BxPage;
import pl.edu.icm.coansys.metaextr.textr.model.BxWord;
import pl.edu.icm.coansys.metaextr.textr.model.BxZone;

/**
 *
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class UnsegmentedPagesFlattener implements DocumentPreprocessor {

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
