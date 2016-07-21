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
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import pl.edu.icm.cermine.metadata.model.DocumentMetadata;
import pl.edu.icm.cermine.structure.model.BxDocument;
import pl.edu.icm.cermine.structure.model.BxZoneLabel;
import pl.edu.icm.cermine.tools.CharacterUtils;

/**
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class JournalVolumePagesEnhancer extends AbstractPatternEnhancer {

    private static final Pattern PATTERN = 
            Pattern.compile("([A-Z][^\\d]*),?\\s+(\\d+):?\\s*(\\d{1,5})[" + String.valueOf(CharacterUtils.DASH_CHARS) + "](\\d{1,5})");
    private static final Set<BxZoneLabel> SEARCHED_ZONE_LABELS = EnumSet.of(BxZoneLabel.MET_BIB_INFO);
    private int pages = 10;

    public JournalVolumePagesEnhancer() {
        super(PATTERN, SEARCHED_ZONE_LABELS);
    }
    
    @Override
    protected Set<EnhancedField> getEnhancedFields() {
        return EnumSet.of(EnhancedField.JOURNAL, EnhancedField.VOLUME, EnhancedField.PAGES);
    }

    @Override
    protected boolean enhanceMetadata(BxDocument document, DocumentMetadata metadata) {
        pages = document.childrenCount();
        return super.enhanceMetadata(document, metadata);
    }
    
    @Override
    protected boolean enhanceMetadata(MatchResult result, DocumentMetadata metadata) {
        int first = Integer.parseInt(result.group(3));
        int last = Integer.parseInt(result.group(4));
        if (first <= last && last - first < pages * 2) {
            metadata.setJournal(result.group(1).trim()
                .replaceAll("Published as: ", "").replaceAll(",$", ""));
            metadata.setVolume(result.group(2));
            metadata.setPages(result.group(3), result.group(4));
            return true;
        }
        
        return false;
    }
}
