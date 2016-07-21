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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import pl.edu.icm.cermine.metadata.model.DocumentMetadata;
import pl.edu.icm.cermine.structure.model.BxZone;
import pl.edu.icm.cermine.structure.model.BxZoneLabel;

/**
 * @author Krzysztof Rusek
 */
public abstract class AbstractDateEnhancer extends AbstractSimpleEnhancer {

    private static final String[] MONTHS = {
        "january|jan\\.",
        "february|feb\\.",
        "march|mar\\.",
        "april|apr\\.",
        "may",
        "june|jun\\.",
        "july|jul\\.",
        "august|aug\\.",
        "september|sep\\.",
        "october|oct\\.",
        "november|nov\\.",
        "december|dec\\."};

    private final EnhancedField field;
    private Pattern simplePattern;
    private Pattern anotherPattern;

    public AbstractDateEnhancer(EnhancedField field, String nameRegex) {
        createPatterns(nameRegex);
        setSearchedZoneLabels(BxZoneLabel.MET_DATES);
        this.field = field;
    }

    @Override
    protected Set<EnhancedField> getEnhancedFields() {
        return EnumSet.of(field);
    }

    @Override
    protected boolean enhanceMetadata(BxZone zone, DocumentMetadata metadata) {
        Matcher simpleMatcher = simplePattern.matcher(zone.toText());
        if (simpleMatcher.find()) {
            MatchResult result = simpleMatcher.toMatchResult();
            String day = result.group(2);
            String month = null;
            for (int i = 0; i < 12; i++) {
                if (result.group(i + 3) != null) {
                    month = String.valueOf(i + 1);
                    break;
                }
            }
            String year = result.group(15);
            enhanceMetadata(metadata, day, month, year);
            return true;
        }
        Matcher anotherMatcher = anotherPattern.matcher(zone.toText());
        if (anotherMatcher.find()) {
            MatchResult result = anotherMatcher.toMatchResult();
            String day = result.group(14);
            String month = null;
            for (int i = 0; i < 12; i++) {
                if (result.group(i + 2) != null) {
                    month = String.valueOf(i + 1);
                    break;
                }
            }
            String year = result.group(15);
            enhanceMetadata(metadata, day, month, year);
            return true;
        }
        return false;
    }
    
    protected abstract void enhanceMetadata(DocumentMetadata metadata, String day, String month, String year);

    private void createPatterns(String nameRegex) {
        StringBuilder simpleRegex = new StringBuilder();
        simpleRegex.append("\\b");
        simpleRegex.append(nameRegex);
        simpleRegex.append("[\\s:-]\\s*((\\d{1,2})\\s+(?:");
        
        StringBuilder anotherRegex = new StringBuilder();
        anotherRegex.append("\\b");
        anotherRegex.append(nameRegex);
        anotherRegex.append("[\\s:-]\\s*((?:");
        
        boolean first = true;
        for (String monthRegex : MONTHS) {
            if (first) {
                first = false;
            } else {
                simpleRegex.append("|");
                anotherRegex.append("|");
            }
            simpleRegex.append("(");
            simpleRegex.append(monthRegex);
            simpleRegex.append(")");
            
            anotherRegex.append("(");
            anotherRegex.append(monthRegex);
            anotherRegex.append(")");
        }
        simpleRegex.append(")\\s+(\\d{4}))");
        anotherRegex.append(")\\s+(\\d{1,2})\\s*,?\\s+(\\d{4}))");

        simplePattern = Pattern.compile(simpleRegex.toString(), Pattern.CASE_INSENSITIVE);
        anotherPattern = Pattern.compile(anotherRegex.toString(), Pattern.CASE_INSENSITIVE);
    }
}
