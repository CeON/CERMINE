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
import pl.edu.icm.cermine.structure.model.BxZoneLabelCategory;
import pl.edu.icm.cermine.tools.classification.general.FeatureCalculator;
import pl.edu.icm.cermine.tools.classification.general.FeatureVectorBuilder;
import pl.edu.icm.cermine.tools.classification.svm.SVMZoneClassifier;

/**
 * SVM-based metadata zone classifier.
 *
 * @author Pawel Szostek
 */
public class SVMMetadataZoneClassifier extends SVMZoneClassifier {

    public SVMMetadataZoneClassifier(BufferedReader modelFile, BufferedReader rangeFile) throws AnalysisException {
        super(getFeatureVectorBuilder());
        try {
            loadModelFromFile(modelFile, rangeFile);
        } catch (IOException ex) {
            throw new AnalysisException("Cannot create SVM classifier!", ex);
        }
    }

    public SVMMetadataZoneClassifier(String modelFilePath, String rangeFilePath) throws AnalysisException {
        this(modelFilePath, rangeFilePath, false);
    }

    public SVMMetadataZoneClassifier(String modelFilePath, String rangeFilePath, boolean fromResources) throws AnalysisException {
        super(getFeatureVectorBuilder());
        try {
            if (fromResources) {
                loadModelFromResources(modelFilePath, rangeFilePath);
            } else {
                loadModelFromFile(modelFilePath, rangeFilePath);
            }
        } catch (IOException ex) {
            throw new AnalysisException("Cannot create SVM classifier!", ex);
        }
    }

    public static FeatureVectorBuilder<BxZone, BxPage> getFeatureVectorBuilder() {
        FeatureVectorBuilder<BxZone, BxPage> vectorBuilder = new FeatureVectorBuilder<BxZone, BxPage>();
        vectorBuilder.setFeatureCalculators(Arrays
                .<FeatureCalculator<BxZone, BxPage>>asList(
                        new LineHeightMaxMeanFeature(),
                        new KeywordsFeature(),
                        new IsRightFeature(),
                        new BibinfoFeature(),
                        new IsGreatestFontOnPageFeature(),
                        new AuthorFeature(),
                        new CorrespondenceFeature(),
                        new IsLowestOnThePageFeature(),
                        new AbstractFeature(),
                        new AtCountFeature(),
                        new IsAfterMetTitleFeature(),
                        new LicenseFeature(),
                        new DotCountFeature(),
                        new AtRelativeCountFeature(),
                        new WordLengthMedianFeature(),
                        new AffiliationFeature(),
                        new DigitCountFeature(),
                        new YearFeature(),
                        new IsHighestOnThePageFeature(),
                        new AuthorNameRelativeFeature(),
                        new CommaCountFeature(),
                        new LineXPositionMeanFeature(),
                        new IsOnSurroundingPagesFeature(),
                        new UppercaseCountFeature(),
                        new UppercaseWordCountFeature(),
                        new IsAnywhereElseFeature(),
                        new IsFirstPageFeature(),
                        new PageNumberFeature(),
                        new LastButOneZoneFeature(),
                        new LineRelativeCountFeature(),
                        new DotRelativeCountFeature(),
                        new PreviousZoneFeature(),
                        new FullWordsRelativeFeature(),
                        new CommaRelativeCountFeature(),
                        new HorizontalRelativeProminenceFeature(),
                        new UppercaseWordRelativeCountFeature(),
                        new LowercaseCountFeature(),
                        new DigitRelativeCountFeature(),
                        new PunctuationRelativeCountFeature(),
                        new LineXWidthPositionDiffFeature(),
                        new FontHeightMeanFeature(),
                        new UppercaseRelativeCountFeature(),
                        new LowercaseRelativeCountFeature(),
                        new DistanceFromNearestNeighbourFeature(),
                        new HeightRelativeFeature(),
                        new EmptySpaceFeature(),
                        new EmptySpaceRelativeFeature(),
                        new VerticalProminenceFeature(),
                        new YPositionFeature(),
                        new YPositionRelativeFeature(),
                        new LineWidthMeanFeature(),
                        new ProportionsFeature(),
                        new RelativeMeanLengthFeature()
                )
        );
        return vectorBuilder;
    }

    @Override
    public BxDocument classifyZones(BxDocument document) throws AnalysisException {
        for (BxPage page : document) {
            for (BxZone zone : page) {
                zone.setParent(page);
            }
        }
        for (BxZone zone : document.asZones()) {
            if (zone.getLabel().isOfCategoryOrGeneral(BxZoneLabelCategory.CAT_METADATA)) {
                zone.setLabel(predictLabel(zone, zone.getParent()));
            }
        }
        return document;
    }

}
