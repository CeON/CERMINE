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

import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import pl.edu.icm.cermine.metadata.model.DocumentMetadata;
import pl.edu.icm.cermine.structure.model.*;

/**
 * @author Krzysztof Rusek
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
        List<BxLine> lines = new ArrayList<BxLine>();
        for (BxPage page : filterPages(document)) {
            for (BxZone zone : filterZones(page)) {
                for (BxLine line : zone) {
                    lines.add(line);
                }
            }
        }
        
        StringBuilder sb = new StringBuilder();
        BxLine prev = null;
        for (BxLine line : lines) {
            String normalized = line.toText().toLowerCase().trim();
            if (normalized.startsWith("abstract")
                || normalized.startsWith("a b s t r a c t")
                || normalized.startsWith("article info")) {
                sb = new StringBuilder();
            }
            if (normalized.startsWith("keywords")
                || normalized.startsWith("key words")
                || normalized.equals("introduction")
                || normalized.startsWith("this work is licensed")
                || (normalized.length() < 20 &&
                   (normalized.startsWith("introduction") || normalized.endsWith("introduction")))) {
                break;
            }
            if (prev != null && !prev.getParent().equals(line.getParent()) &&
                lines.indexOf(line) > 5 && prev.toText().endsWith(".") &&
                (Math.abs(prev.getX()-line.getX()) > 5 || Sets.intersection(prev.getFontNames(), line.getFontNames()).isEmpty())) {
                break;
            }
            sb.append("\n");
            sb.append(line.toText().trim());
            prev = line;
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
