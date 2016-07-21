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
package pl.edu.icm.cermine.parsing.features;

import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import pl.edu.icm.cermine.metadata.model.AffiliationLabel;
import pl.edu.icm.cermine.metadata.model.DocumentAffiliation;
import pl.edu.icm.cermine.parsing.model.Token;
import static org.junit.Assert.assertEquals;

/**
 * @author Bartosz Tarnawski
 */
public class IsRareFeatureTest {

    private static final List<String> COMMON_WORDS = Arrays.asList("pies", "kot", "kot1");
    private static final IsRareFeature FEATURE_CASE_SENSITIVE = new IsRareFeature(COMMON_WORDS, true);
    private static final IsRareFeature FEATURE_IGNORE_CASE = new IsRareFeature(COMMON_WORDS, false);

    @Test
    public void testCalculateFeaturePredicate() {
        Token<AffiliationLabel> inSet = new Token<AffiliationLabel>("kot");
        Token<AffiliationLabel> lowerInSet = new Token<AffiliationLabel>("KOT");
        Token<AffiliationLabel> notInSet = new Token<AffiliationLabel>("SZCZUR");
        Token<AffiliationLabel> notWord = new Token<AffiliationLabel>("kot1");
        DocumentAffiliation aff = new DocumentAffiliation("");
        assertEquals(false, FEATURE_CASE_SENSITIVE.calculateFeaturePredicate(inSet, aff));
        assertEquals(true, FEATURE_CASE_SENSITIVE.calculateFeaturePredicate(lowerInSet, aff));
        assertEquals(true, FEATURE_CASE_SENSITIVE.calculateFeaturePredicate(notInSet, aff));
        assertEquals(false, FEATURE_CASE_SENSITIVE.calculateFeaturePredicate(notWord, aff));

        assertEquals(false, FEATURE_IGNORE_CASE.calculateFeaturePredicate(inSet, aff));
        assertEquals(false, FEATURE_IGNORE_CASE.calculateFeaturePredicate(lowerInSet, aff));
        assertEquals(true, FEATURE_IGNORE_CASE.calculateFeaturePredicate(notInSet, aff));
        assertEquals(false, FEATURE_IGNORE_CASE.calculateFeaturePredicate(notWord, aff));
    }

}
