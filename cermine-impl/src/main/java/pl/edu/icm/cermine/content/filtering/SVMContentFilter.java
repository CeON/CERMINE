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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.structure.model.*;
import pl.edu.icm.cermine.tools.classification.general.FeatureVectorBuilder;
import pl.edu.icm.cermine.tools.classification.svm.SVMClassifier;

/**
 *
 * @author Dominika Tkaczyk
 */
public class SVMContentFilter extends SVMClassifier<BxZone, BxPage, BxZoneLabel> implements ContentFilter {

    private static final String MODEL_FILE_PATH = "/pl/edu/icm/cermine/content/filtering.model";
    private static final String RANGE_FILE_PATH = "/pl/edu/icm/cermine/content/filtering.range";
    
    public SVMContentFilter() throws AnalysisException {
        this(MODEL_FILE_PATH, RANGE_FILE_PATH);
	}

	public SVMContentFilter(FeatureVectorBuilder<BxZone, BxPage> featureVectorBuilder) throws AnalysisException {
		this(MODEL_FILE_PATH, RANGE_FILE_PATH, featureVectorBuilder);
	}
    
    public SVMContentFilter(BufferedReader modelFile, BufferedReader rangeFile) throws AnalysisException {
		this(modelFile, rangeFile, ContentFilterTools.VECTOR_BUILDER);
	}

	public SVMContentFilter(String modelFilePath, String rangeFilePath) throws AnalysisException {
		this(modelFilePath, rangeFilePath, ContentFilterTools.VECTOR_BUILDER);
	}
    
    public SVMContentFilter(BufferedReader modelFile, BufferedReader rangeFile, FeatureVectorBuilder<BxZone, BxPage> featureVectorBuilder) throws AnalysisException {
		super(featureVectorBuilder, BxZoneLabel.class);
        try {
            loadModelFromFile(modelFile, rangeFile);
        } catch (IOException ex) {
            throw new AnalysisException("Cannot create SVM classifier!", ex);
        }
	}

	public SVMContentFilter(String modelFilePath, String rangeFilePath, FeatureVectorBuilder<BxZone, BxPage> featureVectorBuilder) 
            throws AnalysisException {
		super(featureVectorBuilder, BxZoneLabel.class);
		InputStreamReader modelISR = new InputStreamReader(SVMContentFilter.class
				.getResourceAsStream(modelFilePath));
		BufferedReader modelFile = new BufferedReader(modelISR);
		
		InputStreamReader rangeISR = new InputStreamReader(SVMContentFilter.class
				.getResourceAsStream(rangeFilePath));
		BufferedReader rangeFile = new BufferedReader(rangeISR);
        try {
            loadModelFromFile(modelFile, rangeFile);
        } catch (IOException ex) {
            throw new AnalysisException("Cannot create SVM classifier!", ex);
        }
	}
    
    @Override
    public BxDocument filter(BxDocument document) throws AnalysisException {
        for (BxZone zone: document.asZones()) {
			if (zone.getLabel().isOfCategoryOrGeneral(BxZoneLabelCategory.CAT_BODY)) {
                zone.setLabel(predictLabel(zone, zone.getParent()));
            }
		}
		return document;
    }
    
}
