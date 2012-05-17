package pl.edu.icm.yadda.analysis.textr.model;

/**
 * Represents a zone's function on a page.
 * 
 * @author Lukasz Bolikowski (bolo@icm.edu.pl)
 * 
 */
public enum BxZoneLabel {
    /** Document's abstract. */
    ABSTRACT,
    /** Authors' Affiliations. */
    AFFILIATION,
    /** Authors' names. */
    AUTHOR,
    /** A zone zontaining bibliographic information, such as journal, volume, year, doi, etc. */
    BIB_INFO,
    /** Document's body. */
    BODY,
    /** Document's copyright or license */
    COPYRIGHT,
    /** Author's correspondence information */
    CORRESPONDENCE,
    /** When the dodument was received/revised/accepted/etc. */
    DATES,
    /** Document's editor */
    EDITOR,
    /** Equation */
    EQUATION,
    /** Equation's label */
    EQUATION_LABEL,
    /** Figure */
    FIGURE,
    /** Figure's caption */
    FIGURE_CAPTION,
    /** Page footer. */
    FOOTER,
    /** Page header. */
    HEADER,
    /** Keywords */
    KEYWORDS,
    /** Page number */
    PAGE_NUMBER,
    /** Bibliographic references. */
    REFERENCES,
    /** Table */
    TABLE,
    /** Table's caption */
    TABLE_CAPTION,
    /** Document's title. */
    TITLE,
    /** Document's type */
    TYPE,
    /** Undetermined zone. */
    UNKNOWN,
}
