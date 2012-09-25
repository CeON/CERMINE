package pl.edu.icm.coansys.metaextr.bibref.extraction.model;

/**
 * Bibliographic reference line label.
 *
 * @author Dominika Tkaczyk (dtkaczyk@icm.edu.pl)
 */
public enum BibReferenceLineLabel {

    /** References block title line, eg. "References" */
    BLOCK_LABEL,
    /** The first line of the reference (even if the only line) **/
    BIBREF_START,
    /** The inner line of the reference (if the reference has at least three lines) */
    BIBREF_INNER,
    /* The last line of the reference (if the reference has at leat two lines) */
    BIBREF_END,

}
