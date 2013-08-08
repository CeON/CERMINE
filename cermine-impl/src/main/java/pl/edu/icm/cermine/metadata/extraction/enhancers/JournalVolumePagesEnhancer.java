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
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import org.jdom.Element;
import pl.edu.icm.cermine.structure.model.BxDocument;
import pl.edu.icm.cermine.structure.model.BxZoneLabel;

/**
 *
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class JournalVolumePagesEnhancer extends AbstractPatternEnhancer {

    private static final Pattern PATTERN = 
            Pattern.compile("([A-Z].*)\\s*[\\( ](\\d{4})[\\),;]\\s*(\\d+):\\s*(\\d{1,5})[\u002D\u00AD\u2010\u2011\u2012\u2013\u2014\u2015\u207B\u208B\u2212-](\\d{1,5})");
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
    protected boolean enhanceMetadata(BxDocument document, Element metadata) {
        pages = document.getPages().size();
        return super.enhanceMetadata(document, metadata);
    }
    
    @Override
    protected boolean enhanceMetadata(MatchResult result, Element metadata) {
        Enhancers.setJournal(metadata, result.group(1).trim());
        Enhancers.setYear(metadata, result.group(2));
        Enhancers.setVolume(metadata, result.group(3));
        Enhancers.setIssue(metadata, result.group(3));
        int first = Integer.parseInt(result.group(4));
        int last = Integer.parseInt(result.group(5));
        if (first <= last && last - first < pages * 2) {
            Enhancers.setPages(metadata, result.group(4), result.group(5));
        }
        
        return true;
    }
}
