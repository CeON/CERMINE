/**
 * This file is part of CERMINE project.
 * Copyright (c) 2011-2016 ICM-UW
 *
 * CERMINE is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * CERMINE is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with CERMINE. If not, see <http://www.gnu.org/licenses/>.
 */

package pl.edu.icm.cermine.metadata.extraction.enhancers;

import com.google.common.collect.Sets;
import java.util.*;
import pl.edu.icm.cermine.metadata.model.DocumentMetadata;
import pl.edu.icm.cermine.structure.model.BxLine;
import pl.edu.icm.cermine.structure.model.BxPage;
import pl.edu.icm.cermine.structure.model.BxZone;
import pl.edu.icm.cermine.structure.model.BxZoneLabel;

/**
 * @author Krzysztof Rusek
 */
public class TitleMergedWithTypeEnhancer extends AbstractSimpleEnhancer {

    // All type strings are lowercase to provide case-insensitive matching
    private final Set<String> types = Sets.newHashSet(
            "case report", 
            "case study", 
            "clinical study", 
            "debate", 
            "editorial",
            "forum",
            "full research paper",
            "methodology", 
            "original article",
            "original research",
            "primary research",
            "research", 
            "research article",
            "research paper",
            "review",
            "review article",
            "short article",
            "short paper",
            "study", 
            "study protocol",
            "technical note"
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
    protected boolean enhanceMetadata(BxZone zone, DocumentMetadata metadata) {
        if (zone.childrenCount() < 2) {
            return false;
        } else {
            Iterator<BxLine> iterator = zone.iterator();
            String firstLine = iterator.next().toText().toLowerCase();
            if (types.contains(firstLine)) {
                StringBuilder text = new StringBuilder();
                text.append(iterator.next().toText());
                while (iterator.hasNext()) {
                    text.append(" ");
                    text.append(iterator.next().toText());
                }
                metadata.setTitle(text.toString());
                return true;
            } else {
                return false;
            }
        }
    }

    @Override
    protected boolean enhanceMetadata(BxPage page, DocumentMetadata metadata) {
        List<BxZone> titleZones = new ArrayList<BxZone>();
        for (BxZone zone : filterZones(page)) {
            titleZones.add(zone);
        }
        Collections.sort(titleZones, new Comparator<BxZone>() {

            @Override
            public int compare(BxZone t1, BxZone t2) {
                return Double.compare(t2.getChild(0).getHeight(),
                        t1.getChild(0).getHeight());
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
