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

package pl.edu.icm.cermine.content.transformers;

import java.util.ArrayList;
import java.util.List;
import pl.edu.icm.cermine.content.model.BxContentStructure;
import pl.edu.icm.cermine.content.model.BxContentStructure.BxDocContentPart;
import pl.edu.icm.cermine.content.model.ContentStructure;
import pl.edu.icm.cermine.content.model.DocumentSection;
import pl.edu.icm.cermine.exception.TransformationException;
import pl.edu.icm.cermine.tools.transformers.ModelToModelConverter;

/**
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class BxContentToDocContentConverter implements ModelToModelConverter<BxContentStructure, ContentStructure> {

    @Override
    public ContentStructure convert(BxContentStructure contentStructure, Object... hints) throws TransformationException {
        ContentStructure dcs = new ContentStructure();
        
        List<BxDocContentPart> sectionContentParts = new ArrayList<BxDocContentPart>();
        if (contentStructure.getParts().isEmpty()) {
            return dcs;
        }
        int topClusterNum = contentStructure.getParts().get(0).getLevelId();
        for (BxDocContentPart contentPart : contentStructure.getParts()) {
            if (contentPart.getLevelId() == topClusterNum && !sectionContentParts.isEmpty()) {
                DocumentSection dcp = convertContentPart(sectionContentParts, 1);
                dcs.addSection(dcp);
                sectionContentParts.clear();
            }
            sectionContentParts.add(contentPart);
        }
        if (!sectionContentParts.isEmpty()) {
            DocumentSection dcp = convertContentPart(sectionContentParts, 1);
            dcs.addSection(dcp);
        }
        
        return dcs;
    }

    private DocumentSection convertContentPart(List<BxDocContentPart> contentParts, int level) {
        DocumentSection section = new DocumentSection();
        if (contentParts.isEmpty()) {
            return section;
        }

        section.setLevel(level);
        section.setTitle(contentParts.get(0).getCleanHeaderText());
        for (String contentText : contentParts.get(0).getCleanContentTexts()) {
            section.addParagraph(contentText);
        }

        contentParts.remove(0);
        if (contentParts.isEmpty()) {
            return section;
        }

        int topClusterNum = contentParts.get(0).getLevelId();
        
        List<BxDocContentPart> sectionContentParts = new ArrayList<BxDocContentPart>();

        for (BxDocContentPart contentPart : contentParts) {
            if (contentPart.getLevelId() == topClusterNum && !sectionContentParts.isEmpty()) {
                DocumentSection dcp = convertContentPart(sectionContentParts, level + 1);
                section.addSection(dcp);
                sectionContentParts.clear();
            }
            sectionContentParts.add(contentPart);
        }
        if (!sectionContentParts.isEmpty()) {
            DocumentSection dcp = convertContentPart(sectionContentParts, level + 1);
            section.addSection(dcp);
        }
        
        return section;
    }

    @Override
    public List<ContentStructure> convertAll(List<BxContentStructure> source, Object... hints) throws TransformationException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
