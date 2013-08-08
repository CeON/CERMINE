/**
 * This file is part of CERMINE project.
 * Copyright (c) 2011-2013 ICM-UW
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

import java.util.EnumSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jdom.Element;
import pl.edu.icm.cermine.structure.model.BxZone;
import pl.edu.icm.cermine.structure.model.BxZoneLabel;

/**
 *
 * @author krusek
 */
public class EmailEnhancer extends AbstractSimpleEnhancer {

    private static final Pattern PATTERN = Pattern.compile("\\S+@\\S+");
    
    public EmailEnhancer() {
        this.setSearchedZoneLabels(BxZoneLabel.MET_CORRESPONDENCE, BxZoneLabel.MET_AFFILIATION);
    }

    @Override
    protected Set<EnhancedField> getEnhancedFields() {
        return EnumSet.of(EnhancedField.EMAIL);
    }

    @Override
    protected boolean enhanceMetadata(BxZone zone, Element metadata) {
        Matcher matcher = PATTERN.matcher(zone.toText());
        while (matcher.find()) {
            String email = matcher.group().replaceFirst("[;\\.,]$", "");
            Enhancers.addEmail(metadata, email);
        }
        return false;
    }
       
}
