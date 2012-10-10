package pl.edu.icm.cermine.bibref;

import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.structure.model.BxDocument;

/**
 * Extracting individual references strings from zones labelled as REFERENCES. 
 *
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public interface BibReferenceExtractor {

    String[] extractBibReferences(BxDocument document) throws AnalysisException;
}
