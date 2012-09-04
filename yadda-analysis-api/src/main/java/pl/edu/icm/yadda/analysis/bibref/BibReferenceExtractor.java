package pl.edu.icm.yadda.analysis.bibref;

import pl.edu.icm.yadda.analysis.AnalysisException;
import pl.edu.icm.yadda.analysis.textr.model.BxDocument;

/**
 * Extracting individual references strings from zones labelled as REFERENCES. 
 *
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public interface BibReferenceExtractor {

    public String[] extractBibReferences(BxDocument document) throws AnalysisException;
}
