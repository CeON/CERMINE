package pl.edu.icm.cermine.bibref.extraction.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import pl.edu.icm.cermine.structure.model.BxLine;
import pl.edu.icm.cermine.structure.model.BxZone;

/**
 * Stores BxDocument's bibliographic references lines with their labels.
 *
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class BxDocumentBibReferences {

    private static final int MAX_TITLE_LENGTH = 30;
    
    /** A list of references' lines */
    private List<BxLine> lines = new ArrayList<BxLine>();

    /** A map associating references' line with their zones */
    private Map<BxLine, BxZone> lineZones = new HashMap<BxLine, BxZone>();

    /** A map associating references' lines with their labels */
    private Map<BxLine, BibReferenceLineLabel> lineLabels = new HashMap<BxLine, BibReferenceLineLabel>();

    public void addZone(BxZone zone) {
        for (BxLine line : zone.getLines()) {
            String normalized = line.toText().toLowerCase().replaceAll("[^a-z]", "");
            if (line.toText().length() < MAX_TITLE_LENGTH && zone.getLines().indexOf(line) == 0 && 
                    (normalized.startsWith("refer") || normalized.startsWith("biblio"))) {
                continue;
            }
                   
            lines.add(line);
            lineZones.put(line, zone);
        }
    }

    public List<BxLine> getLines() {
        return lines;
    }

    public BxZone getZone(BxLine line) {
        return lineZones.get(line);
    }

    public BibReferenceLineLabel getLabel(BxLine line) {
        return lineLabels.get(line);
    }

    public void setLabel(BxLine line, BibReferenceLineLabel label) {
        lineLabels.put(line, label);
    }

}
