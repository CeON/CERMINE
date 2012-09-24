package pl.edu.icm.yadda.analysis.metadata.extraction.enhancers;

import com.google.common.collect.Sets;
import java.util.*;
import org.jdom.Element;
import pl.edu.icm.yadda.analysis.textr.model.BxLine;
import pl.edu.icm.yadda.analysis.textr.model.BxPage;
import pl.edu.icm.yadda.analysis.textr.model.BxZone;
import pl.edu.icm.yadda.analysis.textr.model.BxZoneLabel;

/**
 *
 * @author krusek
 */
public class TitleMergedWithTypeEnhancer extends AbstractSimpleEnhancer {

    // All type strings are lowercase to provide case-insensitive matching
    private Set<String> types = Sets.newHashSet(
            "case report", 
            "case study", 
            "clinical study", 
            "debate", 
            "editorial",
            "methodology", 
            "research", 
            "research article", 
            "review article", 
            "study", 
            "study protocol"
            );

    public TitleMergedWithTypeEnhancer() {
        setSearchedZoneLabels(BxZoneLabel.MET_TITLE);
        setSearchedFirstPageOnly(true);
    }

    public void setTypes(Collection<String> types) {
        this.types.clear();
        for (String type : types) {
            this.types.add(type.toLowerCase());
        }
    }

    @Override
    protected Set<EnhancedField> getEnhancedFields() {
        return EnumSet.of(EnhancedField.TITLE);
    }

    @Override
    protected boolean enhanceMetadata(BxZone zone, Element metadata) {
        if (zone.getLines().size() < 2) {
            return false;
        } else {
            Iterator<BxLine> iterator = zone.getLines().iterator();
            String firstLine = iterator.next().toText().toLowerCase();
            if (types.contains(firstLine)) {
                String text = iterator.next().toText();
                while (iterator.hasNext()) {
                    text += " " + iterator.next().toText();
                }
                Enhancers.setTitle(metadata, text);
                return true;
            } else {
                return false;
            }
        }
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
