package pl.edu.icm.coansys.metaextr.textr.model;

/**
 * Represents a zone's function on a page.
 * 
 * @author Pawel Szostek (p.szostek@icm.edu.pl)
 * 
 */
@Deprecated
public enum BxZoneGeneralLabel {
    /** Document's metadata. */
	METADATA,
	/** Document's body. */
	BODY,
	/** Document's references. */
	REFERENCES,
	/** Other stuff left in the document. */
	OTHER,
	ABSTRACT
}
