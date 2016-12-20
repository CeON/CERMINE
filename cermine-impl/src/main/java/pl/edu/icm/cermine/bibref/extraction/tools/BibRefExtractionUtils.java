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

package pl.edu.icm.cermine.bibref.extraction.tools;

import java.util.ArrayList;
import java.util.List;
import pl.edu.icm.cermine.bibref.extraction.model.BibReferenceLineLabel;
import pl.edu.icm.cermine.bibref.extraction.model.BxDocumentBibReferences;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.structure.model.*;

/**
 * Bibliographic reference extraction utility class.
 *
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public final class BibRefExtractionUtils {

    private BibRefExtractionUtils() {}
    
    /**
     * Extracts lines and zones labeled as "references" from BxDocument.
     *
     * @param document A document
     * @return an object holding references' lines and zones
     * @throws AnalysisException AnalysisException
     */
    public static BxDocumentBibReferences extractBibRefLines(BxDocument document) throws AnalysisException {
        BxDocumentBibReferences lines = new BxDocumentBibReferences();

        for (BxPage page : document) {
            for (BxZone zone : page) {
                if (zone.getLabel().isOfCategoryOrGeneral(BxZoneLabelCategory.CAT_REFERENCES)) {
                    lines.addZone(zone);
                }
            }
        }

        return lines;
    }

    /**
     * Extracts lines and zones labeled as "references" from BxDocument.
     * Extracted lines are tagged with labels according to the list of document's references.
     *
     * @param document A document
     * @param references A list of document's references
     * @return an object holding extracted zones and tagged lines
     * @throws AnalysisException AnalysisException
     */
    public static BxDocumentBibReferences extractBibRefLines(BxDocument document, List<String> references) throws AnalysisException {
        BxDocumentBibReferences refLines = extractBibRefLines(document);
        List<BxLine> lines = refLines.getLines();

        int lineIndex = 0;
        int citationIndex = 0;
        String actCitation = null;
        boolean first = true;

        while (lineIndex < lines.size() && citationIndex < references.size()) {
            BxLine line = lines.get(lineIndex);
            if (actCitation == null) {
                actCitation = references.get(citationIndex);
            }
            if (actCitation.equals(line.toText())) {
                if (first) {
                    refLines.setLabel(line, BibReferenceLineLabel.BIBREF_START);
                } else {
                    refLines.setLabel(line, BibReferenceLineLabel.BIBREF_END);
                }
                first = true;
                actCitation = null;
                lineIndex++;
                citationIndex++;
            } else if (actCitation.startsWith(line.toText())) {
                if (first) {
                    refLines.setLabel(line, BibReferenceLineLabel.BIBREF_START);
                } else {
                    refLines.setLabel(line, BibReferenceLineLabel.BIBREF_INNER);
                }
                first = false;
                actCitation = actCitation.substring(line.toText().length() + 1);
                lineIndex++;
            } else {
                refLines.setLabel(line, BibReferenceLineLabel.BLOCK_LABEL);
                lineIndex++;
            }
        }

        while (lineIndex < lines.size()) {
            BxLine line = lines.get(lineIndex);
            refLines.setLabel(line, BibReferenceLineLabel.BLOCK_LABEL);
            lineIndex++;
        }

        return refLines;
    }

    /**
     * Groups references' lines according to the list of line labels obtained from HMM service.
     *
     * @param docRefs Document's untagged reference lines
     * @param labels A list of line labels
     * @return A list of bibliographic references
     */
    public static String[] groupLinesIntoBibRefs(BxDocumentBibReferences docRefs, List<BibReferenceLineLabel> labels) {
        List<String> references = new ArrayList<String>();

        String actCitation = "";
        for (int i = 0; i < docRefs.getLines().size(); i++) {
            String line = docRefs.getLines().get(i).toText();
            BibReferenceLineLabel label = labels.get(i);
            if (label.equals(BibReferenceLineLabel.BLOCK_LABEL)) {
                continue;
            }
            if (label.equals(BibReferenceLineLabel.BIBREF_START)) {
                if (!actCitation.isEmpty()) {
                    references.add(actCitation);
                }
                actCitation = "";
            } else {
                actCitation += " ";
            }
            actCitation += line;
        }
        if (!actCitation.isEmpty()) {
            references.add(actCitation);
        }

        return references.toArray(new String[references.size()]);
    }

}
