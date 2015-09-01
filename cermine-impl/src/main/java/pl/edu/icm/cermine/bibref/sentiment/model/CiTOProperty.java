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
    
    AGREES_WITH                         ("http://purl.org/spar/cito/agreesWith"),
    CITES                               ("http://purl.org/spar/cito/cites"),
    CITES_AS_AUTHORITY                  ("http://purl.org/spar/cito/citesAsAuthority"),
    CITES_AS_DATA_SOURCE                ("http://purl.org/spar/cito/citesAsDataSource"),
    CITES_AS_EVIDENCE                   ("http://purl.org/spar/cito/citesAsEvidence"),
    CITES_AS_METADATA_DOCUMENT          ("http://purl.org/spar/cito/citesAsMetadataDocument"),
    CITES_AS_RELATED                    ("http://purl.org/spar/cito/citesAsRelated"),
    CITES_AS_SOURCE_DOCUMENT            ("http://purl.org/spar/cito/citesAsSourceDocument"),
    CITES_FOR_INFORMATION               ("http://purl.org/spar/cito/citesForInformation"),
    CONFIRMS                            ("http://purl.org/spar/cito/confirms"),
    CONTAINS_ASSERTION_FROM             ("http://purl.org/spar/cito/containsAssertionFrom"),
    CORRECTS                            ("http://purl.org/spar/cito/corrects"),
    CREDITS                             ("http://purl.org/spar/cito/credits"),
    CRITIQUES                           ("http://purl.org/spar/cito/critiques"),
    DISAGREES_WITH                      ("http://purl.org/spar/cito/disagreesWith"),
    DISCUSSES                           ("http://purl.org/spar/cito/discusses"),
    DISPUTES                            ("http://purl.org/spar/cito/disputes"),
    DOCUMENTS                           ("http://purl.org/spar/cito/documents"),
    EXTENDS                             ("http://purl.org/spar/cito/extends"),
    GIVES_BACKGROUND_TO                 ("http://purl.org/spar/cito/givesBackgroundTo"),
    GIVES_SUPPORT_TO                    ("http://purl.org/spar/cito/givesSupportTo"),
    INCLUDES_EXCERPT_FROM               ("http://purl.org/spar/cito/includesExcerptFrom"),
    INCLUDES_QUOTATION_FROM             ("http://purl.org/spar/cito/includesQuotationFrom"),
    IS_AGREED_WITH_BY                   ("http://purl.org/spar/cito/isAgreedWithBy", false),
    IS_CITED_AS_AUTHORITY_BY            ("http://purl.org/spar/cito/isCitedAsAuthorityBy", false),
    IS_CITED_AS_DATA_SOURCE_BY          ("http://purl.org/spar/cito/isCitedAsDataSourceBy", false),
    IS_CITED_AS_EVIDENCE_BY             ("http://purl.org/spar/cito/isCitedAsEvidenceBy", false),
    IS_CITED_AS_METADATA_DOCUMENT_BY    ("http://purl.org/spar/cito/isCitedAsMetadataDocumentBy", false),
    IS_CITED_AS_RELATED_BY              ("http://purl.org/spar/cito/isCitedAsRelatedBy", false),
    IS_CITED_AS_SOURCE_DOCUMENT_BY      ("http://purl.org/spar/cito/isCitedAsSourceDocumentBy", false),
    IS_CITED_BY                         ("http://purl.org/spar/cito/isCitedBy", false),
    IS_CITED_FOR_INFORMATION_BY         ("http://purl.org/spar/cito/isCitedForInformationBy", false),
    IS_CONFIRMED_BY                     ("http://purl.org/spar/cito/isConfirmedBy", false),
    IS_CORRECTED_BY                     ("http://purl.org/spar/cito/isCorrectedBy", false),
    IS_CREDITED_BY                      ("http://purl.org/spar/cito/isCreditedBy", false),
    IS_CRITIQUED_BY                     ("http://purl.org/spar/cito/isCritiquedBy", false),
    IS_DISAGREED_WITH_BY                ("http://purl.org/spar/cito/isDisagreedWithBy", false),
    IS_DISCUSSED_BY                     ("http://purl.org/spar/cito/isDiscussedBy", false),
    IS_DISPUTED_BY                      ("http://purl.org/spar/cito/isDisputedBy", false),
    IS_DOCUMENTED_BY                    ("http://purl.org/spar/cito/isDocumentedBy", false),
    IS_EXTENDED_BY                      ("http://purl.org/spar/cito/isExtendedBy", false),
    IS_PARODIED_BY                      ("http://purl.org/spar/cito/isParodiedBy", false),
    IS_PLAGIARIZED_BY                   ("http://purl.org/spar/cito/isPlagiarizedBy", false),
    IS_QUALIFIED_BY                     ("http://purl.org/spar/cito/isQualifiedBy", false),
    IS_REFUTED_BY                       ("http://purl.org/spar/cito/isRefutedBy", false),
    IS_REVIEWED_BY                      ("http://purl.org/spar/cito/isReviewedBy", false),
    IS_RIDICULED_BY                     ("http://purl.org/spar/cito/isRidiculedBy", false),
    IS_SUPPORTED_BY                     ("http://purl.org/spar/cito/isSupportedBy", false),
    IS_UPDATED_BY                       ("http://purl.org/spar/cito/isUpdatedBy", false),
    OBTAINS_BACKGROUND_FROM             ("http://purl.org/spar/cito/obtainsBackgroundFrom"),
    OBTAINS_SUPPORT_FROM                ("http://purl.org/spar/cito/obtainsSupportFrom"),
    PARODIES                            ("http://purl.org/spar/cito/parodies"),
    PLAGIARIZES                         ("http://purl.org/spar/cito/plagiarizes"),
    PROVIDES_ASSERTION_FOR              ("http://purl.org/spar/cito/providesAssertionFor"),
    PROVIDES_DATA_FOR                   ("http://purl.org/spar/cito/providesDataFor"),
    PROVIDES_EXCERPT_FOR                ("http://purl.org/spar/cito/providesExcerptFor"),
    PROVIDES_METHOD_FOR                 ("http://purl.org/spar/cito/providesMethodFor"),
    PROVIDES_QUOTATION_FOR              ("http://purl.org/spar/cito/providesQuotationFor"),
    QUALIFIES                           ("http://purl.org/spar/cito/qualifies"),
    REFUTES                             ("http://purl.org/spar/cito/refutes"),
    REVIEWS                             ("http://purl.org/spar/cito/reviews"),
    RIDICULES                           ("http://purl.org/spar/cito/ridicules"),
    SHARES_AUTHORS_WITH                 ("http://purl.org/spar/cito/sharesAuthorsWith"),
    SUPPORTS                            ("http://purl.org/spar/cito/supports"),
    UPDATES                             ("http://purl.org/spar/cito/updates"),
    USES_DATA_FROM                      ("http://purl.org/spar/cito/usesDataFrom"),
    USES_METHOD_IN                      ("http://purl.org/spar/cito/usesMethodIn");

    private final String uri;
    private final boolean isActive;

    private CiTOProperty(String uri) {
        this(uri, true);
    }
    
    private CiTOProperty(String uri, boolean isActive) {
        this.uri = uri;
        this.isActive = isActive;
    }

    public String getUri() {
        return uri;
    }

    public boolean isIsActive() {
        return isActive;
    }

    public static List<CiTOProperty> getActiveValues() {
        return getValues(true);
    }
    
    public static List<CiTOProperty> getPassiveValues() {
        return getValues(false);
    }
    
    public static List<CiTOProperty> getValues(boolean isActive) {
        List<CiTOProperty> values = Lists.newArrayList(CiTOProperty.values());
        for (CiTOProperty property : CiTOProperty.values()) {
            if (isActive != property.isIsActive()) {
                values.remove(property);
            }
        }
        return values;
    }
}
