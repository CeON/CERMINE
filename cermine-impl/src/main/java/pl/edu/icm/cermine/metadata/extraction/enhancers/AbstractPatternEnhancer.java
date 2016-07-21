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

import java.util.Collection;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import pl.edu.icm.cermine.metadata.model.DocumentMetadata;
import pl.edu.icm.cermine.structure.model.BxZone;
import pl.edu.icm.cermine.structure.model.BxZoneLabel;

/**
 * @author Krzysztof Rusek
 */
public abstract class AbstractPatternEnhancer extends AbstractSimpleEnhancer {

    private Pattern pattern;

    protected AbstractPatternEnhancer(Pattern pattern, Collection<BxZoneLabel> zoneLabels) {
        super(zoneLabels);
        this.pattern = pattern;
    }

    protected AbstractPatternEnhancer(Pattern pattern) {
        this.pattern = pattern;
    }

    protected void setPattern(Pattern pattern) {
        this.pattern = pattern;
    }

    protected abstract boolean enhanceMetadata(MatchResult result, DocumentMetadata metadata);

    @Override
    protected boolean enhanceMetadata(BxZone zone, DocumentMetadata metadata) {
        Matcher matcher = pattern.matcher(zone.toText());
        while (matcher.find()) {
            if(enhanceMetadata(matcher.toMatchResult(), metadata)) {
                return true;
            }
        }
        return false;
    }
}
