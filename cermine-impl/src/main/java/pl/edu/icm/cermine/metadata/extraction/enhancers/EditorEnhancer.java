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

import java.util.EnumSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import pl.edu.icm.cermine.metadata.model.DocumentMetadata;
import pl.edu.icm.cermine.structure.model.BxZone;
import pl.edu.icm.cermine.structure.model.BxZoneLabel;

/**
 * @author Krzysztof Rusek
 */
public class EditorEnhancer extends AbstractSimpleEnhancer {

    private static final Pattern PREFIX_PATTERN = Pattern.compile(
            "^\\s*(?:academic\\s*)?editor:",
            Pattern.CASE_INSENSITIVE);
    
    public EditorEnhancer() {
        setSearchedZoneLabels(BxZoneLabel.MET_EDITOR);
    }

    @Override
    protected Set<EnhancedField> getEnhancedFields() {
        return EnumSet.of(EnhancedField.EDITOR);
    }
    
    @Override
    protected boolean enhanceMetadata(BxZone zone, DocumentMetadata metadata) {
        String text = zone.toText();
        Matcher matcher = PREFIX_PATTERN.matcher(text);
        if (matcher.find()) {
            text = text.substring(matcher.end()).trim();
        }
        metadata.addEditor(text);
        return true;
    }
}
