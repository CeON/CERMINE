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

/**
 * Classifying zones as: METADATA, BODY, REFERENCES, OTHER. 
 * 
 * @author Pawel Szostek (p.szostek@icm.edu.pl)
 */
public class SVMInitialZoneClassifier extends SVMZoneClassifier {
    
	private static final String MODEL_FILE_PATH = "/pl/edu/icm/cermine/structure/model-initial-default";
	private static final String RANGE_FILE_PATH = "/pl/edu/icm/cermine/structure/model-initial-default.range";

    private static SVMInitialZoneClassifier defaultInstance;
    
	public SVMInitialZoneClassifier() throws AnalysisException, IOException {
		super(getFeatureVectorBuilder());
		loadModelFromResources(MODEL_FILE_PATH, RANGE_FILE_PATH);
	}
	
	public SVMInitialZoneClassifier(BufferedReader modelFile, BufferedReader rangeFile) throws AnalysisException, IOException {
		super(getFeatureVectorBuilder());
		loadModelFromFile(modelFile, rangeFile);
	}

	public SVMInitialZoneClassifier(String modelFilePath, String rangeFilePath) throws AnalysisException, IOException {
		super(getFeatureVectorBuilder());
		loadModelFromFile(modelFilePath, rangeFilePath);
	}

	public static FeatureVectorBuilder<BxZone, BxPage> getFeatureVectorBuilder()
	{
		FeatureVectorBuilder<BxZone, BxPage> vectorBuilder = new FeatureVectorBuilder<BxZone, BxPage>();
        vectorBuilder.setFeatureCalculators(Arrays.<FeatureCalculator<BxZone, BxPage>>asList(
                new AbstractFeature(),
                new AffiliationFeature(),
                new AuthorFeature(),
                new AuthorNameRelativeFeature(),
                new BibinfoFeature(),
        		new BracketRelativeCount(),
                new BracketedLineRelativeCount(),
                new CharCountFeature(),
                new CharCountRelativeFeature(),
                new CommaCountFeature(),
                new CommaRelativeCountFeature(),
                new ContributionFeature(),
                new ContainsPageNumberFeature(),
                new CuePhrasesRelativeCountFeature(),
                new DateFeature(),
                new DigitCountFeature(),
                new DigitRelativeCountFeature(),
                new DistanceFromNearestNeighbourFeature(),
                new DotCountFeature(),
                new DotRelativeCountFeature(),
                new EmailFeature(),
                new EmptySpaceRelativeFeature(),
                new FontHeightMeanFeature(),
                new FreeSpaceWithinZoneFeature(),
                new FullWordsRelativeFeature(),
                new HeightFeature(),
                new HeightRelativeFeature(),
                new HorizontalRelativeProminenceFeature(),
                new IsAnywhereElseFeature(),
                new IsFirstPageFeature(),
                new IsFontBiggerThanNeighboursFeature(),
                new IsGreatestFontOnPageFeature(),
                new IsHighestOnThePageFeature(),
                new IsItemizeFeature(),
        		new IsWidestOnThePageFeature(),
                new IsLastButOnePageFeature(),
                new IsLastPageFeature(),
                new IsLeftFeature(),
                new IsLongestOnThePageFeature(),
                new IsLowestOnThePageFeature(),
                new IsOnSurroundingPagesFeature(),
                new IsPageNumberFeature(),
                new IsRightFeature(),
                new IsSingleWordFeature(),
                new KeywordsFeature(),
                new LastButOneZoneFeature(),
                new LineCountFeature(),
                new LineRelativeCountFeature(),
                new LineHeightMeanFeature(),
                new LineWidthMeanFeature(),
                new LineXPositionMeanFeature(),
                new LineXPositionDiffFeature(),
                new LineXWidthPositionDiffFeature(),
                new LetterCountFeature(),
                new LetterRelativeCountFeature(),
                new LowercaseCountFeature(),
                new LowercaseRelativeCountFeature(),
                new PageNumberFeature(),
                new PreviousZoneFeature(),
                new ProportionsFeature(),
                new PunctuationRelativeCountFeature(),
                new ReferencesFeature(),
                new StartsWithDigitFeature(),
                new StartsWithHeaderFeature(),
                new UppercaseCountFeature(),
                new UppercaseRelativeCountFeature(),
                new UppercaseWordCountFeature(),
                new UppercaseWordRelativeCountFeature(),
                new VerticalProminenceFeature(),
                new WidthFeature(),
                new WordCountFeature(),
                new WordCountRelativeFeature(),
        		new WordWidthMeanFeature(),
                new WordLengthMeanFeature(),
                new WordLengthMedianFeature(),
        		new WhitespaceCountFeature(),
        		new WhitespaceRelativeCountLogFeature(),
                new WidthRelativeFeature(),
                new XPositionFeature(),
                new XPositionRelativeFeature(),
                new YPositionFeature(),
                new YPositionRelativeFeature(),
                new YearFeature()
                ));
        return vectorBuilder;
	}
	
    public static SVMInitialZoneClassifier getDefaultInstance() throws AnalysisException, IOException {
        if (defaultInstance == null) {
            defaultInstance = new SVMInitialZoneClassifier();
        }
        return defaultInstance;
    }
    
    @Override
	public BxDocument classifyZones(BxDocument document) throws AnalysisException {
        for (BxZone zone : document.asZones()) {
            if (zone.getLabel() == null) {
                BxZoneLabel predicted = predictLabel(zone, zone.getParent());
                zone.setLabel(predicted);
            }
        }
        return document;
	}
    
}