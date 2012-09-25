package pl.edu.icm.coansys.metaextr.bibref.extraction.tools;

import pl.edu.icm.coansys.metaextr.textr.model.BxZone;
import pl.edu.icm.coansys.metaextr.textr.model.BxLine;
import pl.edu.icm.coansys.metaextr.textr.model.BxPage;
import pl.edu.icm.coansys.metaextr.textr.model.BxZoneLabelCategory;
import pl.edu.icm.coansys.metaextr.textr.model.BxDocument;
import java.util.ArrayList;
import java.util.List;
import pl.edu.icm.coansys.metaextr.AnalysisException;
import pl.edu.icm.coansys.metaextr.bibref.extraction.model.BibReferenceLineLabel;
import pl.edu.icm.coansys.metaextr.bibref.extraction.model.BxDocumentBibReferences;
import pl.edu.icm.coansys.metaextr.textr.HierarchicalReadingOrderResolver;
import pl.edu.icm.coansys.metaextr.textr.ReadingOrderResolver;

/**
 * Bibliographic reference extraction utility class.
 *
 * @author Dominika Tkaczyk (dtkaczyk@icm.edu.pl)
 */
public class BibRefExtractionUtils {

    /**
     * Extracts lines and zones labeled as "references" from BxDocument.
     *
     * @param document A document
     * @return an object holding references' lines and zones
     */
    public static BxDocumentBibReferences extractBibRefLines(BxDocument document) throws AnalysisException {
        BxDocumentBibReferences lines = new BxDocumentBibReferences();

        ReadingOrderResolver ror = new HierarchicalReadingOrderResolver();
        document = ror.resolve(document);
        
        for (BxPage page : document.getPages()) {
            for (BxZone zone : page.getZones()) {
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

        return references.toArray(new String[]{});
    }

}
