package pl.edu.icm.coansys.metaextr.metadata.extraction.enhancers;

import java.util.*;
import org.jdom.Element;
import pl.edu.icm.coansys.metaextr.textr.model.BxPage;
import pl.edu.icm.coansys.metaextr.textr.model.BxZone;
import pl.edu.icm.coansys.metaextr.textr.model.BxZoneLabel;

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
    protected boolean enhanceMetadata(BxZone zone, Element metadata) {
        Enhancers.setTitle(metadata, zone.toText().replaceAll("\n", " "));
        return true;
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

        for (BxZone zone : titleZones) {
            if (enhanceMetadata(zone, metadata)) {
                return true;
            }
        }
        return false;
    }
}
