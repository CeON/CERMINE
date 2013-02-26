package pl.edu.icm.cermine.structure.model;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a zone's function on a page.
 * 
 * @author Lukasz Bolikowski (bolo@icm.edu.pl)
 * 
 */
 
public enum BxZoneLabel {
    
    /** Document's metadata. */
	GEN_METADATA        (BxZoneLabelCategory.CAT_GENERAL), //0
	
	/** Document's body. */
	GEN_BODY            (BxZoneLabelCategory.CAT_GENERAL), //1
	
	/** Document's references. */
	GEN_REFERENCES      (BxZoneLabelCategory.CAT_GENERAL), //2
	
	/** Other stuff left in the document. */
	GEN_OTHER           (BxZoneLabelCategory.CAT_GENERAL), //3
	
    /** Document's abstract. */
    MET_ABSTRACT        (BxZoneLabelCategory.CAT_METADATA), //4
    
    /** Authors' Affiliations. */
    MET_AFFILIATION     (BxZoneLabelCategory.CAT_METADATA), //5
    
    MET_ACCESS_DATA (BxZoneLabelCategory.CAT_METADATA), //6

    MET_BIOGRAPHY (BxZoneLabelCategory.CAT_METADATA), //7
    
    /** Authors' names. */
    MET_AUTHOR          (BxZoneLabelCategory.CAT_METADATA), //8
    
    /** A zone containing bibliographic information, such as journal, volume, year, doi, etc. */
    MET_BIB_INFO        (BxZoneLabelCategory.CAT_METADATA), //9
    
    /** Authors' correspondence information. */
    MET_CORRESPONDENCE  (BxZoneLabelCategory.CAT_METADATA), //10

    /** When the document was received/revised/accepted/etc. */
    MET_DATES           (BxZoneLabelCategory.CAT_METADATA), //11
    
    /** Document's editor */
    MET_EDITOR          (BxZoneLabelCategory.CAT_METADATA), //12
    
    /** Keywords */
    MET_KEYWORDS        (BxZoneLabelCategory.CAT_METADATA), //13
    
    /** Document's title. */
    MET_TITLE           (BxZoneLabelCategory.CAT_METADATA), //14
 
    /** Document's type */
    MET_TYPE            (BxZoneLabelCategory.CAT_METADATA), //15
    
    MET_COPYRIGHT		 (BxZoneLabelCategory.CAT_METADATA), //16

    /** Document's body. */
    BODY_CONTENT        (BxZoneLabelCategory.CAT_BODY), //17
    
    BODY_GLOSSARY        (BxZoneLabelCategory.CAT_BODY), //18 
    /** Equation */
    BODY_EQUATION       (BxZoneLabelCategory.CAT_BODY), //19
    
    /** Equation's label */
    BODY_EQUATION_LABEL (BxZoneLabelCategory.CAT_BODY), //20
    
    /** Figure */
    BODY_FIGURE         (BxZoneLabelCategory.CAT_BODY),
    
    /** Figure's caption */
    BODY_FIGURE_CAPTION (BxZoneLabelCategory.CAT_BODY),
    
    /** Content header. */
    BODY_HEADING         (BxZoneLabelCategory.CAT_BODY),
   
    /** Label for tables, figures and equations */
    BODY_JUNK           (BxZoneLabelCategory.CAT_BODY),
    
    /** Table */
    BODY_TABLE          (BxZoneLabelCategory.CAT_BODY),
    
    /** Table's caption */
    BODY_TABLE_CAPTION  (BxZoneLabelCategory.CAT_BODY),
    
    BODY_ACKNOWLEDGMENT (BxZoneLabelCategory.CAT_BODY),
    
    BODY_CONTRIBUTION (BxZoneLabelCategory.CAT_BODY),
    
    BODY_CONFLICT_STMT (BxZoneLabelCategory.CAT_BODY),
    
    BODY_ATTACHMENT 	(BxZoneLabelCategory.CAT_BODY),
    
    /** Page number */
    OTH_PAGE_NUMBER     (BxZoneLabelCategory.CAT_OTHER),
    
    OTH_UNKNOWN  (BxZoneLabelCategory.CAT_OTHER),
    
    /** Undetermined zone. */
    REFERENCES (BxZoneLabelCategory.CAT_REFERENCES);
         
    private final BxZoneLabelCategory category;
    
    private static final Map<BxZoneLabelCategory, BxZoneLabel> CATEGORY_TO_GENERAL = 
            new EnumMap<BxZoneLabelCategory, BxZoneLabel>(BxZoneLabelCategory.class);
    static {
        CATEGORY_TO_GENERAL.put(BxZoneLabelCategory.CAT_BODY,         GEN_BODY);
        CATEGORY_TO_GENERAL.put(BxZoneLabelCategory.CAT_METADATA,     GEN_METADATA);
        CATEGORY_TO_GENERAL.put(BxZoneLabelCategory.CAT_OTHER,        GEN_OTHER);
        CATEGORY_TO_GENERAL.put(BxZoneLabelCategory.CAT_REFERENCES,   GEN_REFERENCES);
    }
    
    private static final Map<BxZoneLabel, BxZoneLabelCategory> GENERAL_TO_CATEGORY = 
            new EnumMap<BxZoneLabel, BxZoneLabelCategory>(BxZoneLabel.class);
    static {
        GENERAL_TO_CATEGORY.put(GEN_BODY,         BxZoneLabelCategory.CAT_BODY);
        GENERAL_TO_CATEGORY.put(GEN_METADATA,     BxZoneLabelCategory.CAT_METADATA);
        GENERAL_TO_CATEGORY.put(GEN_OTHER,        BxZoneLabelCategory.CAT_OTHER);
        GENERAL_TO_CATEGORY.put(GEN_REFERENCES,   BxZoneLabelCategory.CAT_REFERENCES);
    }
    
    private static final Map<BxZoneLabel, BxZoneLabel> LABEL_TO_GENERAL = 
            new EnumMap<BxZoneLabel, BxZoneLabel>(BxZoneLabel.class);
    static {
        for (BxZoneLabel label : BxZoneLabel.values()) {
            if (CATEGORY_TO_GENERAL.get(label.category) != null) {
                LABEL_TO_GENERAL.put(label, CATEGORY_TO_GENERAL.get(label.category));
            } else {
                LABEL_TO_GENERAL.put(label, label);
            }
        }
    }
    
    private static final Map<BxZoneLabel, BxZoneLabelCategory> LABEL_TO_CATEGORY = 
            new EnumMap<BxZoneLabel, BxZoneLabelCategory>(BxZoneLabel.class);
    static {
        for (BxZoneLabel label : BxZoneLabel.values()) {
            LABEL_TO_CATEGORY.put(label, label.category);
        }
    }
    
    BxZoneLabel(BxZoneLabelCategory category) {
        this.category = category;        
    }

    public BxZoneLabelCategory getCategory() {
        return category;
    }
    
    public BxZoneLabel getGeneralLabel() {
        return LABEL_TO_GENERAL.get(this);
    }
    
    public boolean isOfCategory(BxZoneLabelCategory category) {
        return category.equals(this.getCategory());
    }
    
    public boolean isOfCategoryOrGeneral(BxZoneLabelCategory category) {
        return category.equals(this.getCategory()) || this.equals(CATEGORY_TO_GENERAL.get(category));
    }
    
    public static List<BxZoneLabel> valuesOfCategory(BxZoneLabelCategory category) {
        List<BxZoneLabel> values = new ArrayList<BxZoneLabel>();
        for (BxZoneLabel label : BxZoneLabel.values()) {
            if (category.equals(label.getCategory())) {
                values.add(label);
            }
        }
        return values;
    }
    
    public static Map<BxZoneLabel, BxZoneLabel> getLabelToGeneralMap() {
        return LABEL_TO_GENERAL;
    }
    
    public static Map<BxZoneLabel, BxZoneLabel> getIdentityMap() {
    	Map<BxZoneLabel, BxZoneLabel> ret = new EnumMap<BxZoneLabel, BxZoneLabel>(BxZoneLabel.class);
    	for (BxZoneLabel label : BxZoneLabel.values()) {
    		ret.put(label, label);
        }
    	return ret;
    }
    
    public static void main(String args[]) {
    	System.out.println(LABEL_TO_GENERAL);
    }
}
