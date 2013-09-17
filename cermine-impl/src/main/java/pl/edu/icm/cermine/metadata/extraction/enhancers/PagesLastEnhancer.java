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
 * @author krusek
 */
public class PagesLastEnhancer extends AbstractPatternEnhancer {

    private static final Pattern PATTERN = Pattern.compile(
            "(\\d{1,5})[\u002D\u00AD\u2010\u2011\u2012\u2013\u2014\u2015\u207B\u208B\u2212-](\\d{1,5})",
            Pattern.CASE_INSENSITIVE);

    public PagesLastEnhancer() {
        super(PATTERN);
        setSearchedZoneLabels(BxZoneLabel.MET_BIB_INFO);
    }

    @Override
    protected boolean enhanceMetadata(BxDocument document, Element metadata) {
        return super.enhanceMetadata(document, metadata);
    }
   
    @Override
    protected boolean enhanceMetadata(MatchResult result, Element metadata) {
        int first = Integer.parseInt(result.group(1));
        int last = Integer.parseInt(result.group(2));
        if (first <= last) {
            Enhancers.setPages(metadata, result.group(1), result.group(2));
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected Set<EnhancedField> getEnhancedFields() {
        return EnumSet.of(EnhancedField.PAGES);
    }

}
