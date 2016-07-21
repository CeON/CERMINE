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

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import pl.edu.icm.cermine.metadata.model.DocumentAuthor;
import pl.edu.icm.cermine.metadata.model.DocumentMetadata;
import pl.edu.icm.cermine.structure.model.BxZoneLabel;

/**
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class JournalVolumeIssueWithAuthorEnhancer extends AbstractPatternEnhancer {

    private static final Pattern PATTERN = Pattern.compile("(.*)\\d{4}, (\\d+):(\\d+)");
    private static final Set<BxZoneLabel> SEARCHED_ZONE_LABELS = EnumSet.of(BxZoneLabel.MET_BIB_INFO);

    public JournalVolumeIssueWithAuthorEnhancer() {
        super(PATTERN, SEARCHED_ZONE_LABELS);
    }
    
    @Override
    protected Set<EnhancedField> getEnhancedFields() {
        return EnumSet.of(EnhancedField.JOURNAL, EnhancedField.VOLUME, EnhancedField.ISSUE);
    }

    @Override
    protected boolean enhanceMetadata(MatchResult result, DocumentMetadata metadata) {
        String journal = result.group(1);

        List<String> authors = getAuthorNames(metadata);

        if (authors.size() == 1) {
            journal = removeFirst(journal, authors.get(0));
        }
        if (authors.size() == 2) {
            journal = removeFirst(journal, authors.get(0));
            journal = removeFirst(journal, "and");
            journal = removeFirst(journal, authors.get(1));
        }
        if (authors.size() > 2) {
            journal = journal.replaceFirst("^.*et al\\.", "").trim();
        }

        metadata.setJournal(journal);
        metadata.setVolume(result.group(2));
        metadata.setIssue(result.group(3));
        
        return true;
    }

    private String removeFirst(String journal, String prefix) {
        if (journal.toLowerCase().startsWith(prefix.toLowerCase())) {
            return journal.substring(prefix.length()).trim();
        }
        String[] strs = prefix.split(" ");
        for (String str : strs) {
            if (journal.toLowerCase().startsWith(str.toLowerCase())) {
                return journal.substring(str.length()).trim();
            }
        }
        return journal;
    }
    
    private List<String> getAuthorNames(DocumentMetadata metadata) {
        List<String> authors = new ArrayList<String>();
        for (DocumentAuthor a : metadata.getAuthors()) {
            authors.add(a.getName());
        }
        return authors;
    }
}
