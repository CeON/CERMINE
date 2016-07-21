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

package pl.edu.icm.cermine.content;

import java.util.HashSet;
import java.util.Set;
import org.jdom.Element;
import pl.edu.icm.cermine.content.cleaning.ContentCleaner;
import pl.edu.icm.cermine.content.model.BxContentStructure;
import pl.edu.icm.cermine.content.model.BxContentStructure.BxDocContentPart;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.structure.model.BxDocument;
import pl.edu.icm.cermine.structure.model.BxLine;
import pl.edu.icm.cermine.structure.model.BxZoneLabel;
import pl.edu.icm.cermine.structure.model.BxZoneLabelCategory;

/**
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class RawTextWithLabelsExtractor {
    
    public Element extractRawTextWithLabels(BxDocument document, BxContentStructure structure) 
            throws AnalysisException {
        Set<BxLine> headerLines = new HashSet<BxLine>();
        for (BxDocContentPart contentPart : structure.getParts()) {
            headerLines.addAll(contentPart.getHeaderLines());
        }
        
        Element root = new Element("document");
        
        if (!document.asLines().iterator().hasNext()) {
            return root;
        }
        
        BxZoneLabel actLabel = document.asLines().iterator().next().getParent().getLabel();
        StringBuilder sb = new StringBuilder();
        for (BxLine line : document.asLines()) {
            BxZoneLabel label = line.getParent().getLabel();
            if (headerLines.contains(line)) {
                if (actLabel.equals(BxZoneLabel.BODY_HEADING)) {
                    sb.append(line.toText());
                    sb.append("\n");
                } else {
                    addZone(root, actLabel, sb.toString());
                    sb = new StringBuilder();
                    sb.append(line.toText());
                    sb.append("\n");
                    actLabel = BxZoneLabel.BODY_HEADING;
                }
            } else if (label.isOfCategoryOrGeneral(BxZoneLabelCategory.CAT_BODY)) {
                if (actLabel.equals(BxZoneLabel.BODY_CONTENT)) {
                    sb.append(line.toText());
                    sb.append("\n");
                } else {
                    addZone(root, actLabel, sb.toString());
                    sb = new StringBuilder();
                    sb.append(line.toText());
                    sb.append("\n");
                    actLabel = BxZoneLabel.BODY_CONTENT;
                }
            } else {
                if (actLabel.equals(label)) {
                    sb.append(line.toText());
                    sb.append("\n");
                } else {
                    addZone(root, actLabel, sb.toString());
                    sb = new StringBuilder();
                    sb.append(line.toText());
                    sb.append("\n");
                    actLabel = label;
                }
            }
        }
        Element z = new Element("zone");
        z.setAttribute("label", actLabel.toString());
        z.addContent(ContentCleaner.cleanAll(sb.toString()));
        root.addContent(z);
        
        return root;
    }
        
    private void addZone(Element root, BxZoneLabel label, String rawText) {
        Element z = new Element("zone");
        z.setAttribute("label", label.toString());
        z.addContent(ContentCleaner.cleanAll(rawText));
        root.addContent(z);
    }
    
}
