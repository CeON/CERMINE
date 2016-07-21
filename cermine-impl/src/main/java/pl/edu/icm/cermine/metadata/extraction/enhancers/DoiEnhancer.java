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
import pl.edu.icm.cermine.metadata.model.DocumentMetadata;
import pl.edu.icm.cermine.structure.model.BxZoneLabel;
import pl.edu.icm.cermine.tools.PatternUtils;

/**
 * @author Krzysztof Rusek
 */
public class DoiEnhancer extends AbstractMultiPatternEnhancer {

    private static final List<Pattern> PATTERNS = Lists.newArrayList(
            Pattern.compile("\\bdoi:?\\s*(" + PatternUtils.DOI_PATTERN + ")", Pattern.CASE_INSENSITIVE),
            Pattern.compile("\\bdx\\.doi\\.org/(" + PatternUtils.DOI_PATTERN + ")", Pattern.CASE_INSENSITIVE)
            );
    private static final Set<BxZoneLabel> SEARCHED_ZONE_LABELS = EnumSet.of(BxZoneLabel.MET_BIB_INFO);

    public DoiEnhancer() {
        super(PATTERNS, SEARCHED_ZONE_LABELS);
    }
    
    @Override
    protected Set<EnhancedField> getEnhancedFields() {
        return EnumSet.of(EnhancedField.DOI);
    }

    @Override
    protected boolean enhanceMetadata(MatchResult result, DocumentMetadata metadata) {
        metadata.addId(DocumentMetadata.ID_DOI, result.group(1).replaceAll(",$", ""));
        return true;
    }
}
