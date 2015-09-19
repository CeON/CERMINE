/**
 * This file is part of CERMINE project.
 * Copyright (c) 2011-2013 ICM-UW
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

package pl.edu.icm.cermine.bibref.sentiment.model;

import com.google.common.collect.Lists;
import java.util.List;

/**
 *
 * @author Dominika Tkaczyk
 */
public enum CiTOProperty {
    
    CITES                               ("http://purl.org/spar/cito/cites"),
    IS_CITED_BY                         ("http://purl.org/spar/cito/isCitedBy"),
    AGREES_WITH                         ("http://purl.org/spar/cito/agreesWith", CITES),
    CITES_AS_AUTHORITY                  ("http://purl.org/spar/cito/citesAsAuthority", CITES),
    CITES_AS_DATA_SOURCE                ("http://purl.org/spar/cito/citesAsDataSource", CITES),
    CITES_AS_EVIDENCE                   ("http://purl.org/spar/cito/citesAsEvidence", CITES),
    CITES_AS_METADATA_DOCUMENT          ("http://purl.org/spar/cito/citesAsMetadataDocument", CITES),
    CITES_AS_RELATED                    ("http://purl.org/spar/cito/citesAsRelated", CITES),
    CITES_AS_SOURCE_DOCUMENT            ("http://purl.org/spar/cito/citesAsSourceDocument", CITES),
    CITES_FOR_INFORMATION               ("http://purl.org/spar/cito/citesForInformation", CITES),
    CONFIRMS                            ("http://purl.org/spar/cito/confirms", CITES),
    CONTAINS_ASSERTION_FROM             ("http://purl.org/spar/cito/containsAssertionFrom", CITES),
    CORRECTS                            ("http://purl.org/spar/cito/corrects", CITES),
    CREDITS                             ("http://purl.org/spar/cito/credits", CITES),
    CRITIQUES                           ("http://purl.org/spar/cito/critiques", CITES),
    DISAGREES_WITH                      ("http://purl.org/spar/cito/disagreesWith", CITES),
    DISCUSSES                           ("http://purl.org/spar/cito/discusses", CITES),
    DISPUTES                            ("http://purl.org/spar/cito/disputes", CITES),
    DOCUMENTS                           ("http://purl.org/spar/cito/documents", CITES),
    EXTENDS                             ("http://purl.org/spar/cito/extends", CITES),
    GIVES_BACKGROUND_TO                 ("http://purl.org/spar/cito/givesBackgroundTo", IS_CITED_BY),
    GIVES_SUPPORT_TO                    ("http://purl.org/spar/cito/givesSupportTo", IS_CITED_BY),
    INCLUDES_EXCERPT_FROM               ("http://purl.org/spar/cito/includesExcerptFrom", CITES),
    INCLUDES_QUOTATION_FROM             ("http://purl.org/spar/cito/includesQuotationFrom", CITES),
    IS_AGREED_WITH_BY                   ("http://purl.org/spar/cito/isAgreedWithBy", IS_CITED_BY),
    IS_CITED_AS_AUTHORITY_BY            ("http://purl.org/spar/cito/isCitedAsAuthorityBy", IS_CITED_BY),
    IS_CITED_AS_DATA_SOURCE_BY          ("http://purl.org/spar/cito/isCitedAsDataSourceBy", IS_CITED_BY),
    IS_CITED_AS_EVIDENCE_BY             ("http://purl.org/spar/cito/isCitedAsEvidenceBy", IS_CITED_BY),
    IS_CITED_AS_METADATA_DOCUMENT_BY    ("http://purl.org/spar/cito/isCitedAsMetadataDocumentBy", IS_CITED_BY),
    IS_CITED_AS_RELATED_BY              ("http://purl.org/spar/cito/isCitedAsRelatedBy", IS_CITED_BY),
    IS_CITED_AS_SOURCE_DOCUMENT_BY      ("http://purl.org/spar/cito/isCitedAsSourceDocumentBy", IS_CITED_BY),
    IS_CITED_FOR_INFORMATION_BY         ("http://purl.org/spar/cito/isCitedForInformationBy", IS_CITED_BY),
    IS_CONFIRMED_BY                     ("http://purl.org/spar/cito/isConfirmedBy", IS_CITED_BY),
    IS_CORRECTED_BY                     ("http://purl.org/spar/cito/isCorrectedBy", IS_CITED_BY),
    IS_CREDITED_BY                      ("http://purl.org/spar/cito/isCreditedBy", IS_CITED_BY),
    IS_CRITIQUED_BY                     ("http://purl.org/spar/cito/isCritiquedBy", IS_CITED_BY),
    IS_DISAGREED_WITH_BY                ("http://purl.org/spar/cito/isDisagreedWithBy", IS_CITED_BY),
    IS_DISCUSSED_BY                     ("http://purl.org/spar/cito/isDiscussedBy", IS_CITED_BY),
    IS_DISPUTED_BY                      ("http://purl.org/spar/cito/isDisputedBy", IS_CITED_BY),
    IS_DOCUMENTED_BY                    ("http://purl.org/spar/cito/isDocumentedBy", IS_CITED_BY),
    IS_EXTENDED_BY                      ("http://purl.org/spar/cito/isExtendedBy", IS_CITED_BY),
    IS_PARODIED_BY                      ("http://purl.org/spar/cito/isParodiedBy", IS_CITED_BY),
    IS_PLAGIARIZED_BY                   ("http://purl.org/spar/cito/isPlagiarizedBy", IS_CITED_BY),
    IS_QUALIFIED_BY                     ("http://purl.org/spar/cito/isQualifiedBy", IS_CITED_BY),
    IS_REFUTED_BY                       ("http://purl.org/spar/cito/isRefutedBy", IS_CITED_BY),
    IS_REVIEWED_BY                      ("http://purl.org/spar/cito/isReviewedBy", IS_CITED_BY),
    IS_RIDICULED_BY                     ("http://purl.org/spar/cito/isRidiculedBy", IS_CITED_BY),
    IS_SUPPORTED_BY                     ("http://purl.org/spar/cito/isSupportedBy", IS_CITED_BY),
    IS_UPDATED_BY                       ("http://purl.org/spar/cito/isUpdatedBy", IS_CITED_BY),
    OBTAINS_BACKGROUND_FROM             ("http://purl.org/spar/cito/obtainsBackgroundFrom", CITES),
    OBTAINS_SUPPORT_FROM                ("http://purl.org/spar/cito/obtainsSupportFrom", CITES),
    PARODIES                            ("http://purl.org/spar/cito/parodies", CITES),
    PLAGIARIZES                         ("http://purl.org/spar/cito/plagiarizes", CITES),
    PROVIDES_ASSERTION_FOR              ("http://purl.org/spar/cito/providesAssertionFor", IS_CITED_BY),
    PROVIDES_DATA_FOR                   ("http://purl.org/spar/cito/providesDataFor", IS_CITED_BY),
    PROVIDES_EXCERPT_FOR                ("http://purl.org/spar/cito/providesExcerptFor", IS_CITED_BY),
    PROVIDES_METHOD_FOR                 ("http://purl.org/spar/cito/providesMethodFor", IS_CITED_BY),
    PROVIDES_QUOTATION_FOR              ("http://purl.org/spar/cito/providesQuotationFor", IS_CITED_BY),
    QUALIFIES                           ("http://purl.org/spar/cito/qualifies", CITES),
    REFUTES                             ("http://purl.org/spar/cito/refutes", CITES),
    REVIEWS                             ("http://purl.org/spar/cito/reviews", CITES),
    RIDICULES                           ("http://purl.org/spar/cito/ridicules", CITES),
    SHARES_AUTHORS_WITH                 ("http://purl.org/spar/cito/sharesAuthorsWith"),
    SUPPORTS                            ("http://purl.org/spar/cito/supports", CITES),
    UPDATES                             ("http://purl.org/spar/cito/updates", CITES),
    USES_DATA_FROM                      ("http://purl.org/spar/cito/usesDataFrom", CITES),
    USES_METHOD_IN                      ("http://purl.org/spar/cito/usesMethodIn", CITES);

    private final String uri;
    
    //parent property within CiTO, null if does not exist
    private final CiTOProperty parent;

    private CiTOProperty(String uri) {
        this(uri, null);
    }
    
    private CiTOProperty(String uri, CiTOProperty parent) {
        this.uri = uri;
        this.parent = parent;
    }

    public String getUri() {
        return uri;
    }

    public CiTOProperty getParent() {
        return parent;
    }

    public static List<CiTOProperty> getValuesOfParent(CiTOProperty parent) {
        List<CiTOProperty> values = Lists.newArrayList(CiTOProperty.values());
        for (CiTOProperty property : CiTOProperty.values()) {
            if (parent != property.parent) {
                values.remove(property);
            }
        }
        return values;
    }
}
