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

package pl.edu.icm.cermine.content.transformers;

import java.util.ArrayList;
import java.util.List;
import pl.edu.icm.cermine.content.model.BxDocContentStructure;
import pl.edu.icm.cermine.content.model.BxDocContentStructure.BxDocContentPart;
import pl.edu.icm.cermine.content.model.DocumentContentStructure;
import pl.edu.icm.cermine.content.model.DocumentHeader;
import pl.edu.icm.cermine.content.model.DocumentParagraph;
import pl.edu.icm.cermine.exception.TransformationException;
import pl.edu.icm.cermine.tools.transformers.ModelToModelConverter;

/**
 *
 * @author Dominika Tkaczyk
 */
public class BxContentStructToDocContentStructConverter implements ModelToModelConverter<BxDocContentStructure, DocumentContentStructure> {

    @Override
    public DocumentContentStructure convert(BxDocContentStructure contentStructure, Object... hints) throws TransformationException {
        DocumentContentStructure dcs = convertContentParts(contentStructure.getParts(), 0);
        dcs.setParents();
        return dcs;
    }

    private DocumentContentStructure convertContentParts(List<BxDocContentPart> contentParts, int level) {
        DocumentContentStructure dcs = new DocumentContentStructure();
        if (contentParts.isEmpty()) {
            return dcs;
        }

        if (level > 0) {
            dcs.setHeader(new DocumentHeader(level, contentParts.get(0).getCleanHeaderText(), dcs));
            for (String contentText : contentParts.get(0).getCleanContentTexts()) {
                dcs.addParagraph(new DocumentParagraph(contentText, dcs));
            }

            contentParts.remove(0);
            if (contentParts.isEmpty()) {
                return dcs;
            }
        }

        int topClusterNum = contentParts.get(0).getLevelId();
        
        List<BxDocContentPart> sectionContentParts = new ArrayList<BxDocContentPart>();

        for (BxDocContentPart contentPart : contentParts) {
            if (contentPart.getLevelId() == topClusterNum && !sectionContentParts.isEmpty()) {
                DocumentContentStructure dcp = convertContentParts(sectionContentParts, level + 1);
                dcs.addPart(dcp);
                sectionContentParts.clear();
            }
            sectionContentParts.add(contentPart);
        }
        if (!sectionContentParts.isEmpty()) {
            DocumentContentStructure dcp = convertContentParts(sectionContentParts, level + 1);
            dcs.addPart(dcp);
        }
        
        return dcs;
    }

    @Override
    public List<DocumentContentStructure> convertAll(List<BxDocContentStructure> source, Object... hints) throws TransformationException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
