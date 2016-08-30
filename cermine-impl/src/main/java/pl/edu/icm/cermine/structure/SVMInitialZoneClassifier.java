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
package pl.edu.icm.cermine.structure;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.metadata.zoneclassification.features.*;
import pl.edu.icm.cermine.structure.model.BxDocument;
import pl.edu.icm.cermine.structure.model.BxPage;
import pl.edu.icm.cermine.structure.model.BxZone;
import pl.edu.icm.cermine.structure.model.BxZoneLabel;
import pl.edu.icm.cermine.tools.classification.general.FeatureCalculator;
import pl.edu.icm.cermine.tools.classification.general.FeatureVectorBuilder;
import pl.edu.icm.cermine.tools.classification.svm.SVMZoneClassifier;
import pl.edu.icm.cermine.tools.timeout.TimeoutRegister;

/**
 * Classifying zones as: METADATA, BODY, REFERENCES, OTHER.
 *
 * @author Pawel Szostek
 */
public class SVMInitialZoneClassifier extends SVMZoneClassifier {

    public SVMInitialZoneClassifier(BufferedReader modelFile, BufferedReader rangeFile) throws AnalysisException, IOException {
        super(getFeatureVectorBuilder());
        loadModelFromFile(modelFile, rangeFile);
    }

    public SVMInitialZoneClassifier(String modelFilePath, String rangeFilePath) throws AnalysisException, IOException {
        super(getFeatureVectorBuilder());
        loadModelFromFile(modelFilePath, rangeFilePath);
    }

    public static FeatureVectorBuilder<BxZone, BxPage> getFeatureVectorBuilder() {
        FeatureVectorBuilder<BxZone, BxPage> vectorBuilder = new FeatureVectorBuilder<BxZone, BxPage>();
        vectorBuilder.setFeatureCalculators(Arrays.<FeatureCalculator<BxZone, BxPage>>asList(
                new AbstractFeature(),
                new IsSingleWordFeature(),
                new AtRelativeCountFeature(),
                new IsGreatestFontOnPageFeature(),
                new ReferencesTitleFeature(),
                new ReferencesFeature(),
                new DateFeature(),
                new StartsWithDigitFeature(),
                new BracketRelativeCountFeature(),
                new IsLastPageFeature(),
                new BibinfoFeature(),
                new IsPageNumberFeature(),
                new IsLowestOnThePageFeature(),
                new CommaCountFeature(),
                new LineCountFeature(),
                new AuthorNameRelativeFeature(),
                new DotCountFeature(),
                new DigitCountFeature(),
                new IsOnSurroundingPagesFeature(),
                new IsFirstPageFeature(),
                new UppercaseWordCountFeature(),
                new PageNumberFeature(),
                new IsAnywhereElseFeature(),
                new IsHighestOnThePageFeature(),
                new YearFeature(),
                new LineXPositionMeanFeature(),
                new CommaRelativeCountFeature(),
                new LowercaseCountFeature(),
                new LastButOneZoneFeature(),
                new XVarianceFeature(),
                new FullWordsRelativeFeature(),
                new DotRelativeCountFeature(),
                new UppercaseWordRelativeCountFeature(),
                new LineRelativeCountFeature(),
                new PreviousZoneFeature(),
                new PunctuationRelativeCountFeature(),
                new LineXWidthPositionDiffFeature(),
                new UppercaseRelativeCountFeature(),
                new WordLengthMeanFeature(),
                new DigitRelativeCountFeature(),
                new LineHeightMeanFeature(),
                new LowercaseRelativeCountFeature(),
                new XPositionFeature(),
                new HorizontalRelativeProminenceFeature(),
                new EmptySpaceFeature(),
                new DistanceFromNearestNeighbourFeature(),
                new VerticalProminenceFeature(),
                new AreaFeature(),
                new YPositionFeature(),
                new YPositionRelativeFeature(),
                new WordWidthMeanFeature(),
                new LineWidthMeanFeature(),
                new ProportionsFeature(),
                new RelativeMeanLengthFeature()
        ));
        return vectorBuilder;
    }

    @Override
    public BxDocument classifyZones(BxDocument document) throws AnalysisException {
        for (BxZone zone : document.asZones()) {
            if (zone.getLabel() == null) {
                BxZoneLabel predicted = predictLabel(zone, zone.getParent());
                zone.setLabel(predicted);
                TimeoutRegister.get().check();
            }
        }
        return document;
    }
    
}
