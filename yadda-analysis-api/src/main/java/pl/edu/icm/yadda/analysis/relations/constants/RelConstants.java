package pl.edu.icm.yadda.analysis.relations.constants;

/**
 * Relationship constants.
 * 
 * @author Lukasz Bolikowski (bolo@icm.edu.pl)
 * @author Piotr Dendek (pdendek@icm.edu.pl)
 *
 */
public class RelConstants {
	private static final String NS_OR_RL_START = "http://";
	private static final String NS_ENDING = ".pl/";
	private static final String RL_ENDING = ".pl";
	
	public static final String NS_AFFILIATION = NS_OR_RL_START + "affiliation" + NS_ENDING ;
	public static final String NS_CONTRIBUTOR = NS_OR_RL_START + "contributor" + NS_ENDING ;
	public static final String NS_DOCUMENT = NS_OR_RL_START + "document" + NS_ENDING ;
	public static final String NS_ZBL_DOCUMENT = NS_OR_RL_START + "document/ZBL" + NS_ENDING ;
	public static final String NS_MR_DOCUMENT = NS_OR_RL_START + "document/MR" + NS_ENDING ;
	public static final String NS_INSTITUTION = NS_OR_RL_START + "institution" + NS_ENDING ;
	public static final String NS_CATEGORY_REF = NS_OR_RL_START + "category-ref" + NS_ENDING ;
//	public static final String NS_OBSERVATION = NS_OR_RL_START + "observation" + NS_ENDING ;
	public static final String NS_PERSON = NS_OR_RL_START + "person" + NS_ENDING ;
	public static final String NS_ZBL_PERSON = NS_OR_RL_START + "person/zbl" + NS_ENDING ;
	public static final String NS_REFERENCE = NS_OR_RL_START + "reference" + NS_ENDING ;
//	public static final String NS_YADDA = NS_OR_RL_START + "yadda" + NS_ENDING ;
	
//	public static final String NS_TRACE = NS_OR_RL_START + "trace" + NS_ENDING ;
	public static final String NS_TAG = NS_OR_RL_START + "tag" + NS_ENDING ;
	public static final String NS_DESCRIPTION = NS_OR_RL_START + "description" + NS_ENDING ;
	
	public static final String NS_ISSN = NS_OR_RL_START + "issn" + NS_ENDING ;
	public static final String NS_ISBN = NS_OR_RL_START + "isbn" + NS_ENDING ;
	
	
	public static final String NS_AFFINITY = NS_OR_RL_START + "affinity" + NS_ENDING ;
	public static final String NS_EXAMINATION = NS_OR_RL_START + "examination" + NS_ENDING ;
	public static final String NS_FEATURE = NS_OR_RL_START + "feature" + NS_ENDING ;
	public static final String NS_CLUSTERING = NS_OR_RL_START + "clustering" + NS_ENDING ;
	public static final String NS_HASH_FUNCTION = NS_OR_RL_START + "hash-function" + NS_ENDING ;
	public static final String NS_TIME = NS_OR_RL_START + "time" + NS_ENDING ;
	public static final String NS_FEATURE_INTERPRETER = NS_OR_RL_START + "feature-interpreter" + NS_ENDING ;
	
	/* URI-to-URI relations */
	public static final String RL_CONTAINS_AFFILIATION = NS_OR_RL_START + "contains-affiliation" + RL_ENDING ;
	public static final String RL_IS_AFFILIATED_WITH_ID = NS_OR_RL_START + "is-affiliated-with-txt" + RL_ENDING ;
	public static final String RL_IS_DOCUMENT = NS_OR_RL_START + "is-document" + RL_ENDING ;
	public static final String RL_IS_INSTITUTION = NS_OR_RL_START + "is-institution" + RL_ENDING ;
	public static final String RL_IS_PERSON = NS_OR_RL_START + "is-person" + RL_ENDING ;
	public static final String RL_IS_PERSON_DB = NS_OR_RL_START + "is-database-person" + RL_ENDING ;
//	public static final String RL_HAS_INSTANCE = NS_OR_RL_START + "has-instance" + RL_ENDING ;
	public static final String RL_HAS_CONTRIBUTOR = NS_OR_RL_START + "has-contributor" + RL_ENDING ;
	public static final String RL_REFERENCES = NS_OR_RL_START + "references" + RL_ENDING ;
//	public static final String RL_IS_TRUE_DOCUMENT = NS_OR_RL_START + "is-true-document" + RL_ENDING ;
//	public static final String RL_HAS_PROOF_EMAIL = NS_OR_RL_START + "has-proof-email" + RL_ENDING ;
	
	public static final String RL_HAS_DESCRIPTION = NS_OR_RL_START + "has-description" + RL_ENDING ;
	
	/* URI-to-literal relations */
	public static final String RL_HAS_POSITION_IN_DOCUMENT = NS_OR_RL_START + "has-position-in-document" + RL_ENDING ;
	public static final String RL_BOOK = NS_OR_RL_START + "in-book" + RL_ENDING ;
	public static final String RL_CONTACT_EMAIL = NS_OR_RL_START + "has-contact-email" + RL_ENDING ;
	public static final String RL_CONTACT_FAX = NS_OR_RL_START + "has-contact-fax" + RL_ENDING ;
	public static final String RL_CONTACT_PHONE = NS_OR_RL_START + "has-contact-phone" + RL_ENDING ;
	public static final String RL_CONTACT_URL = NS_OR_RL_START + "has-contact-url" + RL_ENDING ;
	public static final String RL_CITY = NS_OR_RL_START + "has-city" + RL_ENDING ;
	public static final String RL_FORENAMES = NS_OR_RL_START + "has-forenames" + RL_ENDING ;
	public static final String RL_ISSUE = NS_OR_RL_START + "in-issue" + RL_ENDING ;
	public static final String RL_JOURNAL = NS_OR_RL_START + "in-journal" + RL_ENDING ;
	public static final String RL_KEYWORDS = NS_OR_RL_START + "has-key-words" + RL_ENDING ;
	public static final String RL_LANGUAGE = NS_OR_RL_START + "in-language" + RL_ENDING ;
	public static final String RL_TYPE = NS_OR_RL_START + "has-type" + RL_ENDING ;
	public static final String RL_TAG = NS_OR_RL_START + "has-tag" + RL_ENDING ;
	public static final String RL_NAME = NS_OR_RL_START + "has-name" + RL_ENDING ;
	public static final String RL_PAGES = NS_OR_RL_START + "is-between-pages" + RL_ENDING ;
	public static final String RL_PART = NS_OR_RL_START + "in-part" + RL_ENDING ;
	public static final String RL_PUBLISHER = NS_OR_RL_START + "has-publisher" + RL_ENDING ;
	public static final String RL_SECTION = NS_OR_RL_START + "in-section" + RL_ENDING ;
	public static final String RL_SERIES = NS_OR_RL_START + "in-series" + RL_ENDING ;
	public static final String RL_SURNAME = NS_OR_RL_START + "has-surname" + RL_ENDING ;
//	public static final String RL_SURNAME_HASH = NS_OR_RL_START + "has-surname-hash" + RL_ENDING ;
//	public static final String RL_INITIALZ_SURNAME = NS_OR_RL_START + "has-initialz-with-surname" + RL_ENDING ;
//	public static final String RL_INITIALZ_NO_SURNAME = NS_OR_RL_START + "has-initialz-no-surname" + RL_ENDING ;
	public static final String RL_TEXT = NS_OR_RL_START + "has-text" + RL_ENDING ;
	public static final String RL_TITLE = NS_OR_RL_START + "has-title" + RL_ENDING ;
	public static final String RL_VOLUME = NS_OR_RL_START + "in-volume" + RL_ENDING ;
	public static final String RL_MONTH = NS_OR_RL_START + "published-in-month" + RL_ENDING ;
	public static final String RL_YEAR = NS_OR_RL_START + "published-in-year" + RL_ENDING ;
	public static final String RL_CATEGORY_CLC = NS_OR_RL_START + "is-in-category/CLC" + RL_ENDING ; 
 	public static final String RL_CATEGORY_JEL = NS_OR_RL_START + "is-in-category/JEL" + RL_ENDING ; 
 	public static final String RL_CATEGORY_MSC = NS_OR_RL_START + "is-in-category/MSC" + RL_ENDING ; 
 	public static final String RL_CATEGORY_PACS = NS_OR_RL_START + "is-in-category/PACS" + RL_ENDING ; 
 	public static final String RL_CATEGORY_QICS = NS_OR_RL_START + "is-in-category/QICS" + RL_ENDING ; 
 	public static final String RL_CATEGORY_ZDM = NS_OR_RL_START + "is-in-category/ZDM" + RL_ENDING ; 
 	public static final String RL_CATEGORY_CEJSH = NS_OR_RL_START + "is-in-category/CEJSH" + RL_ENDING ; 
	
	public static final String RL_PERSON_SURNAME_HASH = NS_OR_RL_START + "has-person-surname-hash" + RL_ENDING ;
	public static final String RL_PERSON_SURNAME = NS_OR_RL_START + "has-person-surname" + RL_ENDING ;
	public static final String RL_HAS_POSITION_IN_REFERENCE_DOCUMENT = NS_OR_RL_START + "has-position-in-reference-document" + RL_ENDING ;
	public static final String RL_HAS_ROLE = NS_OR_RL_START + "has-role" + RL_ENDING ;
//	public static final String RL_HAS_TRACE = NS_OR_RL_START + "has-trace" + RL_ENDING ;
	public static final String RL_CANONICAL_NAME = NS_OR_RL_START + "has-canonical-name" + RL_ENDING ;
	public static final String RL_HAS_ISSN = NS_OR_RL_START + "has-issn" + RL_ENDING ;
	public static final String RL_HAS_ISBN = NS_OR_RL_START + "has-isbn" + RL_ENDING ;
	public static final String RL_CHAPTER = NS_OR_RL_START + "in-chapter" + RL_ENDING ;
	
//	public static final String RL_OBSERVATION_ID = NS_OR_RL_START + "has-observation-id" + RL_ENDING ;
//	public static final String RL_OBSERVATION_CONTAINS_SAME_PERSON = NS_OR_RL_START + "observation-contains-same-person" + RL_ENDING ;
//	public static final String RL_OBSERVATION_HAS_WEIGHT = NS_OR_RL_START + "observation-has-weight" + RL_ENDING ;
//	public static final String RL_OBSERVATION_FEATURE = NS_OR_RL_START + "has-feature" + RL_ENDING ;
//	public static final String RL_OBSERVATION_CONTRIBUTOR = NS_OR_RL_START + "has-observation-contrib" + RL_ENDING ;
	public static final String RL_ID = NS_OR_RL_START + "has-id" + RL_ENDING ;
	public static final String RL_INITIALS = NS_OR_RL_START + "has-initials" + RL_ENDING ;
	
	public static final String RL_IS_AFFINE = NS_OR_RL_START + "is-affinite" + RL_ENDING ;
	public static final String RL_HAS_AFFINITY_VALUE = NS_OR_RL_START + "has-affinity-value" + RL_ENDING ;
	public static final String RL_WAS_EXAMINATED = NS_OR_RL_START + "was-examinated" + RL_ENDING ;
	public static final String RL_HAS_FEATURE = NS_OR_RL_START + "has-feature" + RL_ENDING ;
	public static final String RL_HAS_FEATURE_ID = NS_OR_RL_START + "has-feature-id" + RL_ENDING ;
	public static final String RL_HAS_FEATURE_WEIGHT = NS_OR_RL_START + "has-feature-weight" + RL_ENDING ;
	public static final String RL_HAS_EXAMINATION_TIME = NS_OR_RL_START + "has-examination-time" + RL_ENDING ;
	public static final String RL_HAS_FEATURE_INTERPRETER = NS_OR_RL_START + "has-feature-interpreter" + RL_ENDING ;
	public static final String RL_HAS_CLUSTERING_METHOD = NS_OR_RL_START + "has-clustering-method" + RL_ENDING ;
}
