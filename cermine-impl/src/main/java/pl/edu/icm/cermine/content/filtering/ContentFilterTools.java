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

package pl.edu.icm.cermine.content.filtering;

import java.util.Arrays;
import pl.edu.icm.cermine.metadata.zoneclassification.features.*;
import pl.edu.icm.cermine.structure.model.BxPage;
import pl.edu.icm.cermine.structure.model.BxZone;
import pl.edu.icm.cermine.tools.classification.general.FeatureCalculator;
import pl.edu.icm.cermine.tools.classification.general.FeatureVectorBuilder;

/**
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public final class ContentFilterTools {

    public static final FeatureVectorBuilder<BxZone, BxPage> VECTOR_BUILDER = new FeatureVectorBuilder<BxZone, BxPage>();
    static {
        VECTOR_BUILDER.setFeatureCalculators(Arrays.<FeatureCalculator<BxZone, BxPage>>asList(
            new FigureFeature(),
            new IsLastPageFeature(),
            new AcknowledgementFeature(),
            new AuthorFeature(),
            new IsWidestOnThePageFeature(),
            new LicenseFeature(),
            new BracketedLineRelativeCountFeature(),
            new ContainsPageNumberFeature(),
            new IsLeftFeature(),
            new IsAnywhereElseFeature(),
            new FigureTableFeature(),
            new AffiliationFeature(),
            new IsLowestOnThePageFeature(),
            new IsOnSurroundingPagesFeature(),
            new BibinfoFeature(),
            new IsFirstPageFeature(),
            new IsLongestOnThePageFeature(),
            new IsFontBiggerThanNeighboursFeature(),
            new YearFeature(),
            new ContainsCuePhrasesFeature(),
            new CuePhrasesRelativeCountFeature(),
            new DigitCountFeature(),
            new IsSingleWordFeature(),
            new ReferencesFeature(),
            new BracketCountFeature(),
            new BracketRelativeCountFeature(),
            new PageNumberFeature(),
            new DotCountFeature(),
            new StartsWithDigitFeature(),
            new AuthorNameRelativeFeature(),
            new LineCountFeature(),
            new LineHeightMaxMeanFeature(),
            new LineXPositionDiffFeature(),
            new LineXPositionMeanFeature(),
            new CommaCountFeature(),
            new UppercaseWordCountFeature(),
            new UppercaseCountFeature(),
            new XVarianceFeature(),
            new CommaRelativeCountFeature(),
            new UppercaseWordRelativeCountFeature(),
            new DotRelativeCountFeature(),
            new FullWordsRelativeFeature(),
            new DigitRelativeCountFeature(),
            new LowercaseCountFeature(),
            new WordLengthMeanFeature(),
            new LineXWidthPositionDiffFeature(),
            new UppercaseRelativeCountFeature(),
            new PunctuationRelativeCountFeature(),
            new LastButOneZoneFeature(),
            new LineRelativeCountFeature(),
            new PreviousZoneFeature(),
            new LineHeightMeanFeature(),
            new XPositionFeature(),
            new HorizontalRelativeProminenceFeature(),
            new DistanceFromNearestNeighbourFeature(),
            new EmptySpaceFeature(),
            new EmptySpaceRelativeFeature(),
            new VerticalProminenceFeature(),
            new YPositionFeature(),
            new YPositionRelativeFeature(),
            new LineWidthMeanFeature(),
            new ProportionsFeature(),
            new RelativeMeanLengthFeature()
            ));
    }

    private ContentFilterTools() {
    }
    
}
