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

package pl.edu.icm.cermine.metadata.zoneclassification.features;

import java.util.Arrays;
import pl.edu.icm.cermine.structure.model.BxPage;
import pl.edu.icm.cermine.structure.model.BxZone;
import pl.edu.icm.cermine.tools.classification.general.FeatureCalculator;
import pl.edu.icm.cermine.tools.classification.general.FeatureVectorBuilder;

/**
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public final class FeatureList {

    public static final FeatureVectorBuilder<BxZone, BxPage> VECTOR_BUILDER;
    
    static {
        VECTOR_BUILDER = new FeatureVectorBuilder<BxZone, BxPage>();
        VECTOR_BUILDER.setFeatureCalculators(Arrays.<FeatureCalculator<BxZone, BxPage>>asList(
            new AbstractFeature(),
            new AcknowledgementFeature(),
            new AffiliationFeature(),
            new AreaFeature(),
            new AtCountFeature(),
            new AtRelativeCountFeature(),
            new AuthorFeature(),
            new AuthorNameRelativeFeature(),
            new BibinfoFeature(),
            new BracketCountFeature(),
            new BracketedLineRelativeCountFeature(),
            new BracketRelativeCountFeature(),
            new CharCountFeature(),
            new CharCountRelativeFeature(),
            new CommaCountFeature(),
            new CommaRelativeCountFeature(),
            new ContainsCuePhrasesFeature(),
            new ContainsPageNumberFeature(),
            new ContributionFeature(),
            new CorrespondenceFeature(),
            new CuePhrasesRelativeCountFeature(),
            new DateFeature(),
            new DigitCountFeature(),
            new DigitRelativeCountFeature(),
            new DistanceFromNearestNeighbourFeature(),
            new DotCountFeature(),
            new DotRelativeCountFeature(),
            new EditorFeature(),
            new EmailFeature(),
            new EmptySpaceFeature(),
            new EmptySpaceRelativeFeature(),
            new FigureFeature(),
            new FigureTableFeature(),
            new FontHeightMeanFeature(),
            new FreeSpaceWithinZoneFeature(),
            new FullWordsRelativeFeature(),
            new GreekLettersFeature(),
            new HeightFeature(),
            new HeightRelativeFeature(),
            new HorizontalRelativeProminenceFeature(),
            new IsAfterMetTitleFeature(),
            new IsAnywhereElseFeature(),
            new IsFirstPageFeature(),
            new IsFontBiggerThanNeighboursFeature(),
            new IsGreatestFontOnPageFeature(),
            new IsHighestOnThePageFeature(),
            new IsItemizeFeature(),
            new IsLastButOnePageFeature(),
            new IsLastPageFeature(),
            new IsLeftFeature(),
            new IsLongestOnThePageFeature(),
            new IsLowestOnThePageFeature(),
            new IsOnSurroundingPagesFeature(),
            new IsPageNumberFeature(),
            new IsRightFeature(),
            new IsSingleWordFeature(),
            new IsWidestOnThePageFeature(),
            new KeywordsFeature(),
            new LastButOneZoneFeature(),
            new LetterCountFeature(),
            new LetterRelativeCountFeature(),
            new LicenseFeature(),
            new LineCountFeature(),
            new LineHeightMaxMeanFeature(),
            new LineHeightMeanFeature(),
            new LineRelativeCountFeature(),
            new LineWidthMeanFeature(),
            new LineXPositionDiffFeature(),
            new LineXPositionMeanFeature(),
            new LineXWidthPositionDiffFeature(),
            new LowercaseCountFeature(),
            new LowercaseRelativeCountFeature(),
            new MathSymbolsFeature(),
            new PageNumberFeature(),
            new PreviousZoneFeature(),
            new ProportionsFeature(),
            new PunctuationRelativeCountFeature(),
            new ReferencesFeature(),
            new ReferencesTitleFeature(),
            new RelativeMeanLengthFeature(),
            new StartsWithDigitFeature(),
            new StartsWithHeaderFeature(),
            new TypeFeature(),
            new UppercaseCountFeature(),
            new UppercaseRelativeCountFeature(),
            new UppercaseWordCountFeature(),
            new UppercaseWordRelativeCountFeature(),
            new VerticalProminenceFeature(),
            new WhitespaceCountFeature(),
            new WhitespaceRelativeCountLogFeature(),
            new WidthFeature(),
            new WidthRelativeFeature(),
            new WordCountFeature(),
            new WordCountRelativeFeature(),
            new WordLengthMeanFeature(),
            new WordLengthMedianFeature(),
            new WordWidthMeanFeature(),
            new XPositionFeature(),
            new XPositionRelativeFeature(),
            new XVarianceFeature(),
            new YearFeature(),
            new YPositionFeature(),
            new YPositionRelativeFeature()
    ));
    }

    private FeatureList() {}
    
}
