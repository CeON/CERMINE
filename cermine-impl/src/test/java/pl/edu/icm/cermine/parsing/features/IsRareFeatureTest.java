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

package pl.edu.icm.cermine.parsing.features;

import java.util.Arrays;
import java.util.List;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import pl.edu.icm.cermine.metadata.model.AffiliationLabel;
import pl.edu.icm.cermine.metadata.model.DocumentAffiliation;
import pl.edu.icm.cermine.parsing.model.Token;

public class IsRareFeatureTest {

	private static final List<String> commonWords = Arrays.asList("pies", "kot", "kot1");
	private static final IsRareFeature featureCaseSensitive = new IsRareFeature(commonWords, true);
	private static final IsRareFeature featureIgnoreCase = new IsRareFeature(commonWords, false);
	
	@Test
	public void testCalculateFeaturePredicate() {
		Token<AffiliationLabel> inSet = new Token<AffiliationLabel>("kot");
		Token<AffiliationLabel> lowerInSet = new Token<AffiliationLabel>("KOT");
		Token<AffiliationLabel> notInSet = new Token<AffiliationLabel>("SZCZUR");
		Token<AffiliationLabel> notWord = new Token<AffiliationLabel>("kot1");
		DocumentAffiliation aff = new DocumentAffiliation("");
		assertEquals(false, featureCaseSensitive.calculateFeaturePredicate(inSet, aff));
		assertEquals(true, featureCaseSensitive.calculateFeaturePredicate(lowerInSet, aff));
		assertEquals(true, featureCaseSensitive.calculateFeaturePredicate(notInSet, aff));
		assertEquals(false, featureCaseSensitive.calculateFeaturePredicate(notWord, aff));
		
		assertEquals(false, featureIgnoreCase.calculateFeaturePredicate(inSet, aff));
		assertEquals(false, featureIgnoreCase.calculateFeaturePredicate(lowerInSet, aff));
		assertEquals(true, featureIgnoreCase.calculateFeaturePredicate(notInSet, aff));
		assertEquals(false, featureIgnoreCase.calculateFeaturePredicate(notWord, aff));
	}

}