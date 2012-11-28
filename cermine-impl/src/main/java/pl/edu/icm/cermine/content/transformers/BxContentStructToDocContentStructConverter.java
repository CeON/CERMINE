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
    
}
