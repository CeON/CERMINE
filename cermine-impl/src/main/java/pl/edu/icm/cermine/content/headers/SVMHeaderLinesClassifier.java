package pl.edu.icm.cermine.content.headers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.structure.model.BxLine;
import pl.edu.icm.cermine.structure.model.BxPage;
import pl.edu.icm.cermine.structure.model.BxZoneLabel;
import pl.edu.icm.cermine.tools.classification.features.FeatureVectorBuilder;
import pl.edu.icm.cermine.tools.classification.svm.SVMClassifier;

/**
 *
 * @author Dominika Tkaczyk
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
		InputStreamReader modelISR = new InputStreamReader(Thread.currentThread().getClass()
				.getResourceAsStream(modelFilePath));
		BufferedReader modelFile = new BufferedReader(modelISR);
		
		InputStreamReader rangeISR = new InputStreamReader(Thread.currentThread().getClass()
				.getResourceAsStream(rangeFilePath));
		BufferedReader rangeFile = new BufferedReader(rangeISR);
        try {
            loadModelFromFile(modelFile, rangeFile);
        } catch (IOException ex) {
            throw new AnalysisException("Cannot create SVM classifier!", ex);
        }
	}
    
}
