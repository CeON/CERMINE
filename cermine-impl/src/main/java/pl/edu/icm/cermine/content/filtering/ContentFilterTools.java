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

package pl.edu.icm.cermine.content.filtering;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import pl.edu.icm.cermine.evaluation.tools.EvaluationUtils;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.exception.TransformationException;
import pl.edu.icm.cermine.metadata.zoneclassification.features.*;
import pl.edu.icm.cermine.structure.model.*;
import pl.edu.icm.cermine.tools.classification.features.FeatureCalculator;
import pl.edu.icm.cermine.tools.classification.features.FeatureVectorBuilder;
import pl.edu.icm.cermine.tools.classification.general.BxDocsToTrainingSamplesConverter;
import pl.edu.icm.cermine.tools.classification.general.ClassificationUtils;
import pl.edu.icm.cermine.tools.classification.general.TrainingSample;
import pl.edu.icm.cermine.tools.classification.sampleselection.OversamplingSelector;
import pl.edu.icm.cermine.tools.classification.sampleselection.SampleSelector;

/**
 *
 * @author Dominika Tkaczyk
 */
public final class ContentFilterTools {

    public static final FeatureVectorBuilder<BxZone, BxPage> VECTOR_BUILDER = new FeatureVectorBuilder<BxZone, BxPage>();
    static {
        VECTOR_BUILDER.setFeatureCalculators(Arrays.<FeatureCalculator<BxZone, BxPage>>asList(
            new AcknowledgementFeature(),
            new FigureFeature(),
            new ContributionFeature(),
            new CorrespondenceFeature(),
            new AffiliationFeature(),
            new AuthorFeature(),
            new ContainsPageNumberFeature(),
            new IsWidestOnThePageFeature(),
            new LicenseFeature(),
            new IsAnywhereElseFeature(),
            new FigureTableFeature(),
            new IsLeftFeature(),
            new BracketedLineRelativeCountFeature(),
            new IsOnSurroundingPagesFeature(),
            new IsLowestOnThePageFeature(),
            new IsFirstPageFeature(),
            new BibinfoFeature(),
            new IsFontBiggerThanNeighboursFeature(),
            new IsLongestOnThePageFeature(),
            new YearFeature(),
            new ReferencesFeature(),
            new IsSingleWordFeature(),
            new DigitCountFeature(),
            new ContainsCuePhrasesFeature(),
            new CuePhrasesRelativeCountFeature(),
            new BracketCountFeature(),
            new PageNumberFeature(),
            new BracketRelativeCountFeature(),
            new StartsWithDigitFeature(),
            new DotCountFeature(),
            new AuthorNameRelativeFeature(),
            new LineHeightMaxMeanFeature(),
            new LineCountFeature(),
            new LineXPositionDiffFeature(),
            new UppercaseWordCountFeature(),
            new LineXPositionMeanFeature(),
            new UppercaseCountFeature(),
            new CommaCountFeature(),
            new XVarianceFeature(),
            new CommaRelativeCountFeature(),
            new UppercaseWordRelativeCountFeature(),
            new DotRelativeCountFeature(),
            new FullWordsRelativeFeature(),
            new LowercaseCountFeature(),
            new DigitRelativeCountFeature(),
            new WordLengthMeanFeature(),
            new LastButOneZoneFeature(),
            new PunctuationRelativeCountFeature(),
            new UppercaseRelativeCountFeature(),
            new LineXWidthPositionDiffFeature(),
            new LineRelativeCountFeature(),
            new PreviousZoneFeature(),
            new XPositionFeature(),
            new LineHeightMeanFeature(),
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
    
    public static List<TrainingSample<BxZoneLabel>> toTrainingSamples(String trainPath) throws AnalysisException, TransformationException {
        List<BxDocument> documents = EvaluationUtils.getDocumentsFromPath(trainPath);
        return toTrainingSamples(documents);
    }
    
    public static List<TrainingSample<BxZoneLabel>> toTrainingSamples(List<BxDocument> documents) throws AnalysisException {
        List<TrainingSample<BxZoneLabel>> trainingSamples;

        SampleSelector<BxZoneLabel> selector = new OversamplingSelector<BxZoneLabel>(1.0);
        
        Map<BxZoneLabel, BxZoneLabel> map = new EnumMap<BxZoneLabel, BxZoneLabel>(BxZoneLabel.class);
        map.put(BxZoneLabel.BODY_HEADING, BxZoneLabel.BODY_CONTENT);
       
        trainingSamples = BxDocsToTrainingSamplesConverter.getZoneTrainingSamples(documents, VECTOR_BUILDER, map);
        trainingSamples = ClassificationUtils.filterElements(trainingSamples, BxZoneLabelCategory.CAT_BODY);
        trainingSamples = selector.pickElements(trainingSamples);
        
        return trainingSamples;
    }

    private ContentFilterTools() {
    }
    
}
