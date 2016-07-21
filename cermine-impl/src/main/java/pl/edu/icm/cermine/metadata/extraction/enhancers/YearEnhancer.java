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

import com.google.common.collect.Lists;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import pl.edu.icm.cermine.metadata.model.DocumentDate;
import pl.edu.icm.cermine.metadata.model.DocumentMetadata;
import pl.edu.icm.cermine.structure.model.BxZoneLabel;
import pl.edu.icm.cermine.tools.TextUtils;

/**
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class YearEnhancer extends AbstractMultiPatternEnhancer {

    private static final List<Pattern> PATTERNS = Lists.newArrayList(
            Pattern.compile("(?<=\\s|^)(\\d\\d\\d\\d)(?=\\s|$)"),
            Pattern.compile("(?<=\\s|^)(\\d\\d\\d\\d)\\b"),
            Pattern.compile("\\b(\\d\\d\\d\\d)\\b")
            );
    private static final Set<BxZoneLabel> SEARCHED_ZONE_LABELS = EnumSet.of(BxZoneLabel.MET_BIB_INFO);

    private static final int MIN_YEAR = 1800;
    private static final int MAX_YEAR = 2100;
    
    public YearEnhancer() {
        super(PATTERNS, SEARCHED_ZONE_LABELS);
    }
    
    @Override
    protected Set<EnhancedField> getEnhancedFields() {
        return EnumSet.of(EnhancedField.PUBLISHED_DATE);
    }

    @Override
    protected boolean enhanceMetadata(MatchResult result, DocumentMetadata metadata) {
        for (int i = 1; i <= result.groupCount(); i++) {
            String year = result.group(i);
            if (TextUtils.isNumberBetween(year, MIN_YEAR, MAX_YEAR)) {
                if (metadata.getDate(DocumentDate.DATE_PUBLISHED) == null) {
                    metadata.setDate(DocumentDate.DATE_PUBLISHED, null, null, year);
                }
                return true;
            }
        }
        return false;
    }
}
