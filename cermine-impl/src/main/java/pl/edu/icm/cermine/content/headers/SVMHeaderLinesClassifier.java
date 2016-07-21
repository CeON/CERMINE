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
package pl.edu.icm.cermine.content.headers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.structure.model.BxLine;
import pl.edu.icm.cermine.structure.model.BxPage;
import pl.edu.icm.cermine.structure.model.BxZoneLabel;
import pl.edu.icm.cermine.tools.classification.general.FeatureVectorBuilder;
import pl.edu.icm.cermine.tools.classification.svm.SVMClassifier;

/**
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class SVMHeaderLinesClassifier extends SVMClassifier<BxLine, BxPage, BxZoneLabel> {

    public SVMHeaderLinesClassifier() throws AnalysisException {
        super(HeaderExtractingTools.EXTRACT_VB, BxZoneLabel.class);
    }

    public SVMHeaderLinesClassifier(FeatureVectorBuilder<BxLine, BxPage> featureVectorBuilder) throws AnalysisException {
        super(featureVectorBuilder, BxZoneLabel.class);
    }

    public SVMHeaderLinesClassifier(BufferedReader modelFile, BufferedReader rangeFile) throws AnalysisException {
        this(modelFile, rangeFile, HeaderExtractingTools.EXTRACT_VB);
    }

    public SVMHeaderLinesClassifier(String modelFilePath, String rangeFilePath) throws AnalysisException {
        this(modelFilePath, rangeFilePath, HeaderExtractingTools.EXTRACT_VB);
    }

    public SVMHeaderLinesClassifier(BufferedReader modelFile, BufferedReader rangeFile, FeatureVectorBuilder<BxLine, BxPage> featureVectorBuilder) throws AnalysisException {
        super(featureVectorBuilder, BxZoneLabel.class);
        try {
            loadModelFromFile(modelFile, rangeFile);
        } catch (IOException ex) {
            throw new AnalysisException("Cannot create SVM classifier!", ex);
        }
    }

    public SVMHeaderLinesClassifier(String modelFilePath, String rangeFilePath, FeatureVectorBuilder<BxLine, BxPage> featureVectorBuilder) throws AnalysisException {
        super(featureVectorBuilder, BxZoneLabel.class);
        InputStreamReader modelISR = new InputStreamReader(SVMHeaderLinesClassifier.class
                .getResourceAsStream(modelFilePath));
        BufferedReader modelFile = new BufferedReader(modelISR);

        InputStreamReader rangeISR = new InputStreamReader(SVMHeaderLinesClassifier.class
                .getResourceAsStream(rangeFilePath));
        BufferedReader rangeFile = new BufferedReader(rangeISR);
        try {
            loadModelFromFile(modelFile, rangeFile);
        } catch (IOException ex) {
            throw new AnalysisException("Cannot create SVM classifier!", ex);
        }
    }

}
