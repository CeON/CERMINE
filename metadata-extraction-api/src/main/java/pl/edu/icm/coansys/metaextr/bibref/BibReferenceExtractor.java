package pl.edu.icm.coansys.metaextr.bibref;

import pl.edu.icm.coansys.metaextr.AnalysisException;
import pl.edu.icm.coansys.metaextr.structure.model.BxDocument;

/**
 * Extracting individual references strings from zones labelled as REFERENCES. 
 *
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public interface BibReferenceExtractor {

    public String[] extractBibReferences(BxDocument document) throws AnalysisException;
}
