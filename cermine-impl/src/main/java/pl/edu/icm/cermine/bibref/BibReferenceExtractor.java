package pl.edu.icm.cermine.bibref;

import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.structure.model.BxDocument;

/**
 * Interface for extracting individual references strings from zones labelled as REFERENCES. 
 *
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public interface BibReferenceExtractor {

    /**
     * Extracts individual reference strings from the document.
     * 
     * @param document
     * @return extracted references
     * @throws AnalysisException 
     */
    String[] extractBibReferences(BxDocument document) throws AnalysisException;
}
