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
import pl.edu.icm.cermine.metadata.model.DocumentMetadata;
import pl.edu.icm.cermine.structure.model.BxDocument;
import pl.edu.icm.cermine.structure.model.BxPage;
import pl.edu.icm.cermine.structure.model.BxZone;
import pl.edu.icm.cermine.structure.model.BxZoneLabel;

/**
 *
 * @author krusek
 */
public class DescriptionEnhancer extends AbstractSimpleEnhancer {

    private static final Pattern PREFIX = Pattern.compile("^Abstract[-:\\.]?", Pattern.CASE_INSENSITIVE);

    public DescriptionEnhancer() {
        setSearchedZoneLabels(EnumSet.of(BxZoneLabel.MET_ABSTRACT));
        setSearchedFirstPageOnly(true);
    }

    @Override
    protected Set<EnhancedField> getEnhancedFields() {
        return EnumSet.of(EnhancedField.DESCRIPTION);
    }

    @Override
    protected boolean enhanceMetadata(BxDocument document, DocumentMetadata metadata) {
        StringBuilder sb = new StringBuilder();
        for (BxPage page : filterPages(document)) {
            for (BxZone zone : filterZones(page)) {
                String[] lines = zone.toText().split("\n");
                for (String line : lines) {
                    String normalized = line.toLowerCase().trim();
                    if (normalized.startsWith("abstract")
                        || normalized.startsWith("a b s t r a c t")
                        || normalized.startsWith("article info")) {
                        sb = new StringBuilder();
                    }
                    if (normalized.startsWith("keywords")
                        || normalized.startsWith("key words")
                        || normalized.startsWith("introduction")
                        || normalized.endsWith("introduction")
                        || normalized.startsWith("this work is licensed")) {
                        break;
                    }
                    sb.append("\n");
                    sb.append(line.trim());
                }
            }
         }

        String text = sb.toString().trim();
        if (!text.isEmpty()) {
            Matcher matcher = PREFIX.matcher(text);
            if (matcher.find()) {
                text = text.substring(matcher.end()).trim();
            }
            metadata.setAbstrakt(text);
            return true;
        }
        return false;
    }

}
