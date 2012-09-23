package pl.edu.icm.yadda.analysis.textr.model;

/**
 * 
 * @author Dominika Tkaczyk
 */
public enum BxZoneLabelCategory {
	/** Category including all categories - for filtering purposes */
	CAT_ALL,
    /** General labels. */
    CAT_GENERAL,
    /** Document's metadata. */
	CAT_METADATA,
	/** Document's body. */
	CAT_BODY,
	/** Document's references. */
	CAT_REFERENCES,
	/** Other stuff left in the document. */
	CAT_OTHER,
}
