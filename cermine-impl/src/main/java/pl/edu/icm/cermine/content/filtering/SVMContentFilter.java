package pl.edu.icm.cermine.content.filtering;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.structure.model.*;
import pl.edu.icm.cermine.tools.classification.features.FeatureVectorBuilder;
import pl.edu.icm.cermine.tools.classification.svm.SVMClassifier;

/**
 *
 * @author Dominika Tkaczyk
 */
public class SVMContentFilter extends SVMClassifier<BxZone, BxPage, BxZoneLabel> implements ContentFilter {
    
    public SVMContentFilter() throws AnalysisException {
		super(ContentFilterTools.VECTOR_BUILDER, BxZoneLabel.class);
	}

	public SVMContentFilter(FeatureVectorBuilder<BxZone, BxPage> featureVectorBuilder) throws AnalysisException {
		super(featureVectorBuilder, BxZoneLabel.class);
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

	public SVMContentFilter(String modelFilePath, String rangeFilePath, FeatureVectorBuilder<BxZone, BxPage> featureVectorBuilder) throws AnalysisException {
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
