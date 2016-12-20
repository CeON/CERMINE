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
package pl.edu.icm.cermine.metadata.affiliation.features;

import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.metadata.affiliation.tools.AffiliationTokenizer;
import pl.edu.icm.cermine.metadata.model.AffiliationLabel;
import pl.edu.icm.cermine.parsing.features.KeywordFeatureCalculator;
import pl.edu.icm.cermine.parsing.model.Token;

/**
 * Keyword feature calculator suitable for processing affiliations.
 *
 * @author Bartosz Tarnawski
 */
public class AffiliationDictionaryFeature extends KeywordFeatureCalculator<Token<AffiliationLabel>> {

    /**
     * @param FeatureString the string which will be added to the matching
     * tokens' features lists
     * @param dictionaryFileName the name of the dictionary to be used (must be
     * a package resource)
     * @param caseSensitive whether dictionary lookups should be case sensitive
     * @throws AnalysisException AnalysisException
     */
    public AffiliationDictionaryFeature(String FeatureString, String dictionaryFileName,
            boolean caseSensitive) throws AnalysisException {
        super(FeatureString, dictionaryFileName, caseSensitive, new AffiliationTokenizer());
    }

}
