package pl.edu.icm.cermine.metadata.extraction.enhancers;

import java.util.*;
import org.jdom.Element;
import pl.edu.icm.cermine.structure.model.BxPage;
import pl.edu.icm.cermine.structure.model.BxZone;
import pl.edu.icm.cermine.structure.model.BxZoneLabel;

/**
 * Merged title enhancer.
 * 
 * @author krusek
 */
public class TitleEnhancer extends AbstractSimpleEnhancer {

    public TitleEnhancer() {
        setSearchedZoneLabels(BxZoneLabel.MET_TITLE);
        setSearchedFirstPageOnly(true);
    }

    @Override
    protected Set<EnhancedField> getEnhancedFields() {
        return EnumSet.of(EnhancedField.TITLE);
    }

    @Override
    protected boolean enhanceMetadata(BxPage page, Element metadata) {
        List<BxZone> titleZones = new ArrayList<BxZone>();
        for (BxZone zone : filterZones(page)) {
            titleZones.add(zone);
        }
        Collections.sort(titleZones, new Comparator<BxZone>() {

            @Override
            public int compare(BxZone t1, BxZone t2) {
                return Double.compare(t2.getLines().get(0).getBounds().getHeight(),
                        t1.getLines().get(0).getBounds().getHeight());
            }

        });

        if (!titleZones.isEmpty()) {
            BxZone titleZone = titleZones.get(0);
            double height = titleZone.getLines().get(0).getHeight();
            while (titleZone.hasPrev() 
                    && BxZoneLabel.MET_TITLE.equals(titleZone.getPrev().getLabel())
                    && Math.abs(height-titleZone.getPrev().getLines().get(0).getHeight()) < 0.5) {
                titleZone = titleZone.getPrev();
            }
            
            String title = titleZone.toText();
            while (titleZone.hasNext() 
                    && BxZoneLabel.MET_TITLE.equals(titleZone.getNext().getLabel())
                    && Math.abs(height-titleZone.getNext().getLines().get(0).getHeight()) < 0.5) {
                titleZone = titleZone.getNext();
                title += " ";
                title += titleZone.toText();
            }

            if (!title.isEmpty()) {
                Enhancers.setTitle(metadata, title.trim().replaceAll("\n", " "));
                return true;
            }
        }
        return false;
    }
}
