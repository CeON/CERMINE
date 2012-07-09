package pl.edu.icm.yadda.analysis.textr.model;

/**
 * Represents a zone's function on a page.
 * 
 * @author Pawel Szostek (p.szostek@icm.edu.pl)
 * 
 */
public enum BxZoneGeneralLabel {
    /** Document's metadata. */
	METADATA,
	/** Document's body. */
	BODY,
	/** Document's references. */
	REFERENCES,
	/** Other stuff left in the document. */
	OTHER
}
