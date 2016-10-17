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
import java.util.Set;
import pl.edu.icm.cermine.metadata.model.DocumentMetadata;
import pl.edu.icm.cermine.structure.model.BxDocument;
import pl.edu.icm.cermine.structure.model.BxPage;
import pl.edu.icm.cermine.structure.model.BxZone;
import pl.edu.icm.cermine.structure.model.BxZoneLabel;

/**
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class AuthorAffiliationGeometricEnhancer extends AbstractSimpleEnhancer {

    public AuthorAffiliationGeometricEnhancer() {
        setSearchedZoneLabels(BxZoneLabel.MET_AFFILIATION);
    }

    @Override
    protected Set<EnhancedField> getEnhancedFields() {
        return EnumSet.of(EnhancedField.AUTHORS, EnhancedField.AFFILIATION);
    }

    @Override
    protected boolean enhanceMetadata(BxDocument document, DocumentMetadata metadata) {
        BxPage page = document.getFirstChild();
        for (BxZone zone : page) {
            if (zone.getY() < page.getHeight()/2 && zone.getLabel().equals(BxZoneLabel.MET_AUTHOR)) {
                return false;
            }
        }
        
        boolean inLine = false;
        for (BxZone zone1 : filterZones(page)) {
            for (BxZone zone2 : filterZones(page)) {
                if (!zone1.equals(zone2) && Math.abs(zone1.getY()-zone2.getY()) < 10) {
                    inLine = true;
                }
            }
        }
        if (!inLine) {
            return false;
        }

        boolean added = false;
        
        int ind = 0;
        for (BxZone zone : filterZones(page)) {
            if (zone.getY() > page.getHeight() / 2 && zone.hasPrev() && zone.getPrev().toText().equals("Keywords")) {
                continue;
            }
            String authorText = zone.getFirstChild().toText();
            String emailText = null;
            StringBuilder sb = new StringBuilder();
            int j = zone.childrenCount();
            for (int i = 1; i < zone.childrenCount(); i++) {
                String lineText = zone.getChild(i).toText();
                if (lineText.matches(".*@.*")) {
                    if (i == 1) {
                        emailText = lineText;
                        continue;
                    } else {
                        j = i;
                        break;
                    }
                }
                if (lineText.endsWith("-")) {
                    sb.append(lineText.replaceAll("-$", ""));
                } else if (lineText.endsWith(",")) {
                    sb.append(lineText);
                    sb.append(" ");
                } else {
                    sb.append(lineText);
                    sb.append(", ");
                }
            }
            String affText = sb.toString().trim().replaceAll(",$", "");

            if (emailText == null) {
                sb = new StringBuilder();
            
                for (int i = j; i < zone.childrenCount(); i++) {
                    String lineText = zone.getChild(i).toText();
                    if (lineText.endsWith("-")) {
                        sb.append(lineText.replaceAll("-$", ""));
                    } else {
                        sb.append(lineText);
                        sb.append(" ");
                    }
                }
                emailText = sb.toString().trim();
            }
            
            if (authorText.isEmpty() || affText.isEmpty()) {
                continue;
            }
            
            emailText = emailText
                    .replaceFirst("^[Ee]mails?: *", "")
                    .replaceFirst("^[Ee]- *[Mm]ails?: *", "");
            
            metadata.addAuthor(authorText, Lists.newArrayList(String.valueOf(ind)), emailText);
            metadata.setAffiliationByIndex(String.valueOf(ind), affText);
            added = true;
            ind++;
        }

        return added;
    }

}
