/**
 * This file is part of CERMINE project.
 * Copyright (c) 2011-2016 ICM-UW
 *
 * CERMINE is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * CERMINE is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with CERMINE. If not, see <http://www.gnu.org/licenses/>.
 */
package pl.edu.icm.cermine.structure.model;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a zone's function on a page.
 *
 * @author Lukasz Bolikowski (bolo@icm.edu.pl)
 */
public enum BxZoneLabel {

    /**
     * General zones
     */
    /**
     * Document's metadata - 0
     */
    GEN_METADATA(BxZoneLabelCategory.CAT_GENERAL),
    /**
     * Document's body - 1
     */
    GEN_BODY(BxZoneLabelCategory.CAT_GENERAL),
    /**
     * Document's references - 2
     */
    GEN_REFERENCES(BxZoneLabelCategory.CAT_GENERAL),
    /**
     * Other stuff left in the document - 3
     */
    GEN_OTHER(BxZoneLabelCategory.CAT_GENERAL),
    /**
     * Metadata zones
     */
    /**
     * Document's abstract - 4
     */
    MET_ABSTRACT(BxZoneLabelCategory.CAT_METADATA),
    /**
     * Authors' Affiliations - 5
     */
    MET_AFFILIATION(BxZoneLabelCategory.CAT_METADATA),
    /**
     * Document's access info - 6
     */
    MET_ACCESS_DATA(BxZoneLabelCategory.CAT_METADATA),
    /**
     * Authors' biographies - 7
     */
    MET_BIOGRAPHY(BxZoneLabelCategory.CAT_METADATA),
    /**
     * Authors' names. - 8
     */
    MET_AUTHOR(BxZoneLabelCategory.CAT_METADATA),
    /**
     * Bibliographic information, such as journal, volume, year, doi, etc. - 9
     */
    MET_BIB_INFO(BxZoneLabelCategory.CAT_METADATA),
    /**
     * Authors' correspondence information - 10
     */
    MET_CORRESPONDENCE(BxZoneLabelCategory.CAT_METADATA),
    /**
     * When the document was received/revised/accepted/etc. - 11
     */
    MET_DATES(BxZoneLabelCategory.CAT_METADATA),
    /**
     * Document's editor - 12
     */
    MET_EDITOR(BxZoneLabelCategory.CAT_METADATA),
    /**
     * Keywords - 13
     */
    MET_KEYWORDS(BxZoneLabelCategory.CAT_METADATA),
    /**
     * Document's title - 14
     */
    MET_TITLE(BxZoneLabelCategory.CAT_METADATA),
    /**
     * Document's type - 15
     */
    MET_TYPE(BxZoneLabelCategory.CAT_METADATA),
    /**
     * Document's copyright - 16
     */
    MET_COPYRIGHT(BxZoneLabelCategory.CAT_METADATA),
    /**
     * Body zones
     */
    /**
     * Document's body - 17
     */
    BODY_CONTENT(BxZoneLabelCategory.CAT_BODY),
    /**
     * Glossary - 18
     */
    BODY_GLOSSARY(BxZoneLabelCategory.CAT_BODY),
    /**
     * Equation - 19
     */
    BODY_EQUATION(BxZoneLabelCategory.CAT_BODY),
    /**
     * Equation's label - 20
     */
    BODY_EQUATION_LABEL(BxZoneLabelCategory.CAT_BODY),
    /**
     * Figure - 21
     */
    BODY_FIGURE(BxZoneLabelCategory.CAT_BODY),
    /**
     * Figure's caption - 22
     */
    BODY_FIGURE_CAPTION(BxZoneLabelCategory.CAT_BODY),
    /**
     * Content header - 23
     */
    BODY_HEADING(BxZoneLabelCategory.CAT_BODY),
    /**
     * General label for tables, figures and equations - 24
     */
    BODY_JUNK(BxZoneLabelCategory.CAT_BODY),
    /**
     * Table - 25
     */
    BODY_TABLE(BxZoneLabelCategory.CAT_BODY),
    /**
     * Table's caption - 26
     */
    BODY_TABLE_CAPTION(BxZoneLabelCategory.CAT_BODY),
    /**
     * Acknowledgments - 27
     */
    BODY_ACKNOWLEDGMENT(BxZoneLabelCategory.CAT_BODY),
    /**
     * Author's contributions - 28
     */
    BODY_CONTRIBUTION(BxZoneLabelCategory.CAT_BODY),
    /**
     * Conflict statements - 29
     */
    BODY_CONFLICT_STMT(BxZoneLabelCategory.CAT_BODY),
    /**
     * Attachments - 30
     */
    BODY_ATTACHMENT(BxZoneLabelCategory.CAT_BODY),
    /**
     * Other zones
     */
    /**
     * Page number - 31
     */
    OTH_PAGE_NUMBER(BxZoneLabelCategory.CAT_OTHER),
    /**
     * Undetermined zone - 32
     */
    OTH_UNKNOWN(BxZoneLabelCategory.CAT_OTHER),
    /**
     * References zones
     */
    /**
     * References - 33
     */
    REFERENCES(BxZoneLabelCategory.CAT_REFERENCES),
    /**
     * Document's title with authors - 34
     */
    MET_TITLE_AUTHOR(BxZoneLabelCategory.CAT_METADATA),
    /**
     * 35
     */
    MET_CATEGORY(BxZoneLabelCategory.CAT_METADATA),
    /**
     * 36
     */
    MET_TERMS(BxZoneLabelCategory.CAT_METADATA);

    private final BxZoneLabelCategory category;

    private static final Map<BxZoneLabelCategory, BxZoneLabel> CATEGORY_TO_GENERAL
            = new EnumMap<BxZoneLabelCategory, BxZoneLabel>(BxZoneLabelCategory.class);

    static {
        CATEGORY_TO_GENERAL.put(BxZoneLabelCategory.CAT_BODY, GEN_BODY);
        CATEGORY_TO_GENERAL.put(BxZoneLabelCategory.CAT_METADATA, GEN_METADATA);
        CATEGORY_TO_GENERAL.put(BxZoneLabelCategory.CAT_OTHER, GEN_OTHER);
        CATEGORY_TO_GENERAL.put(BxZoneLabelCategory.CAT_REFERENCES, GEN_REFERENCES);
    }

    private static final Map<BxZoneLabel, BxZoneLabelCategory> GENERAL_TO_CATEGORY
            = new EnumMap<BxZoneLabel, BxZoneLabelCategory>(BxZoneLabel.class);

    static {
        GENERAL_TO_CATEGORY.put(GEN_BODY, BxZoneLabelCategory.CAT_BODY);
        GENERAL_TO_CATEGORY.put(GEN_METADATA, BxZoneLabelCategory.CAT_METADATA);
        GENERAL_TO_CATEGORY.put(GEN_OTHER, BxZoneLabelCategory.CAT_OTHER);
        GENERAL_TO_CATEGORY.put(GEN_REFERENCES, BxZoneLabelCategory.CAT_REFERENCES);
    }

    private static final Map<BxZoneLabel, BxZoneLabel> LABEL_TO_GENERAL
            = new EnumMap<BxZoneLabel, BxZoneLabel>(BxZoneLabel.class);

    static {
        for (BxZoneLabel label : BxZoneLabel.values()) {
            if (CATEGORY_TO_GENERAL.get(label.category) != null) {
                LABEL_TO_GENERAL.put(label, CATEGORY_TO_GENERAL.get(label.category));
            } else {
                LABEL_TO_GENERAL.put(label, label);
            }
        }
    }

    private static final Map<BxZoneLabel, BxZoneLabelCategory> LABEL_TO_CATEGORY
            = new EnumMap<BxZoneLabel, BxZoneLabelCategory>(BxZoneLabel.class);

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

}
