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
import org.apache.commons.lang.StringUtils;
import pl.edu.icm.cermine.metadata.model.DocumentAuthor;
import pl.edu.icm.cermine.metadata.model.DocumentMetadata;
import pl.edu.icm.cermine.structure.model.BxZone;
import pl.edu.icm.cermine.structure.model.BxZoneLabel;

/**
 * @author Krzysztof Rusek
 */
public class EmailEnhancer extends AbstractSimpleEnhancer {

    private static final String NAME_REGEXP = "[\\w!\\+\\-\\.]+";
    private static final String NAME_MULTI_REGEXP = "[\\w!\\+\\-\\.\\|, ]+";
    private static final String DOMAIN_REGEXP = "[\\w\\-\\.]+";
    private static final Pattern PATTERN = Pattern.compile(NAME_REGEXP + "@" + DOMAIN_REGEXP);
    private static final Pattern MULTI_PATTERN = Pattern.compile("[\\{\\[\\(](" + NAME_MULTI_REGEXP + ")[\\}\\]\\)]@(" + DOMAIN_REGEXP + ")");
    
    public EmailEnhancer() {
        this.setSearchedZoneLabels(BxZoneLabel.MET_CORRESPONDENCE, BxZoneLabel.MET_AFFILIATION);
    }

    @Override
    protected Set<EnhancedField> getEnhancedFields() {
        return EnumSet.of(EnhancedField.EMAIL);
    }

    @Override
    protected boolean enhanceMetadata(BxZone zone, DocumentMetadata metadata) {
        Matcher matcher = MULTI_PATTERN.matcher(zone.toText());
        while (matcher.find()) {
            String emails = matcher.group(1);
            String domain = matcher.group(2);
            String[] names = emails.split("[\\|, ]+");
            for (String name : names) {
                if (!name.isEmpty()) {
                    addEmail(metadata, name+"@"+domain);
                }
            }
        }
        matcher = PATTERN.matcher(zone.toText());
        while (matcher.find()) {
            String email = matcher.group().replaceFirst("[;\\.,]$", "");
            if (!email.contains("}")) {
                addEmail(metadata, email.replaceFirst("^\\(", "").replaceFirst("\\)$", ""));
            }
        }
        return false;
    }
  
    private void addEmail(DocumentMetadata metadata, String email) {
        DocumentAuthor author = null;
        boolean one = true;
        
        for (DocumentAuthor a : metadata.getAuthors()) {
            String[] names = a.getName().split(" ");
            String fname = StringUtils.join(names, "");
            if (fname.toLowerCase().contains(email.toLowerCase().replaceFirst("@.*", ""))) {
                if (author == null) {
                    author = a;
                    break;
                } else {
                    one = false;
                }
            } else {
                for (String namePart : names) {
                    if (namePart.length() > 2 && email.toLowerCase().contains(namePart.toLowerCase())) {
                        if (author == null) {
                            author = a;
                            break;
                        } else {
                            one = false;
                        }
                    }
                }
            }
        }
        
        if (author != null && one) {
            author.addEmail(email);
        }
    }
    
}
