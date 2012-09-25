package pl.edu.icm.yadda.analysis.metadata.extraction.enhancers;

import java.util.*;
import pl.edu.icm.yadda.analysis.textr.model.BxPage;
import pl.edu.icm.yadda.analysis.textr.model.BxZone;
import pl.edu.icm.yadda.analysis.textr.model.BxZoneLabel;
import pl.edu.icm.yadda.bwmeta.model.YElement;
import pl.edu.icm.yadda.bwmeta.model.YName;

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
    protected boolean enhanceMetadata(BxZone zone, YElement metadata) {
        metadata.addName(new YName(zone.toText().replaceAll("\n", " ")));
        return true;
    }

    @Override
    protected boolean enhanceMetadata(BxPage page, YElement metadata) {
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
