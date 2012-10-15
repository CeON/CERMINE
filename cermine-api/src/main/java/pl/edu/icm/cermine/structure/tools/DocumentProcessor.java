package pl.edu.icm.cermine.structure.tools;

import pl.edu.icm.cermine.structure.model.BxDocument;

/**
 * Document processor interface.
 *
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public interface DocumentProcessor{

    /**
     * Performs an operation on a document.
     * 
     * @param document 
     */
    void process(BxDocument document);
}
