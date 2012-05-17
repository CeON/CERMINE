package pl.edu.icm.yadda.analysis.bibref;

import java.util.Iterator;
import java.util.Map;
import pl.edu.icm.yadda.analysis.AnalysisException;

/**
 * Interface for managing relations between bibliographic references and documents.
 *
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public interface BibReferenceRelationManager {

    /**
     * Extracts all relations involving references contained in the given document.
     *
     * @param sourceId id of a document containing references
     * @return a map (reference's position in the document (they start from 1), id of a document referenced by it)
     * @throws AnalysisException
     */
    public Map<Integer, String> getFromReferences(String sourceId) throws AnalysisException;

    /**
     * Extracts all relations involving references that reference given document.
     *
     * @param targetId id of a referenced document
     * @return a map (id of a document containing reference, reference's position in the document (they start from 1))
     * @throws AnalysisException
     */
    public Map<String, Integer> getToReferences(String targetId) throws AnalysisException;

    /**
     * Sets a relation between the document containing the reference and the document referenced by it.
     *
     * @param sourceId id of a document containing a reference
     * @param position position of a reference in the document (positions start from 1)
     * @param targetId id of a referenced document
     * @throws AnalysisException
     */
    public void setReference(String sourceId, int position, String targetId) throws AnalysisException;

    /**
     * Removes a reference.
     *
     * @param sourceId id of a document containing a reference
     * @param position position of a reference in the document (positions start from 1)
     * @throws AnalysisException
     */
    public void removeReference(String sourceId, int position) throws AnalysisException;

    /**
     * Extracts all relations involving references contained in the given document and locks document id so that
     * no modifications of the document's references are permitted until setSourceDoneAndUnlock method is called.
     *
     * This method is typically called by a process that extracts recently changed references of a document from browse
     * and applies changes to documents in storage. Updating a single document's references in the storage contains
     * of three main stages:
     * - getting all current references of given document from browse (that is when this method is called),
     * - applying changes to the document in the storage,
     * - setting document's "dirty" flag in browse to "false" (that is when setSourceDoneAndUnlock is called).
     * We need to make sure that between first and third stage no further modifications involving document's references
     * are set in browse, otherwise those changes will be omitted by the process. To prevent that, during the first
     * stage document's id is locked. Unlocking takes place in the third stage.
     * 
     * @param sourceId id of a document containing references
     * @return a map (reference's position in the document (they start from 1), id of a document referenced by it)
     * @throws AnalysisException
     */
    public Map<Integer, String> getFromReferencesAndLock(String sourceId) throws AnalysisException;

    /**
     * Gets iterator of ids of documents whose references were recently modified and have not been stored permanently
     * yet.
     *
     * @return iterator of ids of documents with "dirty" flag set to "true"
     * @throws AnalysisException
     */
    public Iterator<String> getModifiedSourceIds() throws AnalysisException;

    /**
     * Sets document's "dirty" flag to "false" and releases document's lock.
     *
     * @param sourceId id of a document
     * @throws AnalysisException
     */
    public void setSourceDoneAndUnlock(String sourceId) throws AnalysisException;

}
