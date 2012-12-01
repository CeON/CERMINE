package pl.edu.icm.cermine.content.filtering;

import java.io.*;
import java.util.List;
import pl.edu.icm.cermine.evaluation.EvaluationUtils;
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
		super(ContentFilterTools.vectorBuilder, BxZoneLabel.class);
	}

	public SVMContentFilter(FeatureVectorBuilder<BxZone, BxPage> featureVectorBuilder) throws AnalysisException {
		super(featureVectorBuilder, BxZoneLabel.class);
	}
    
    public SVMContentFilter(BufferedReader modelFile, BufferedReader rangeFile) throws AnalysisException {
		this(modelFile, rangeFile, ContentFilterTools.vectorBuilder);
	}

	public SVMContentFilter(String modelFilePath, String rangeFilePath) throws AnalysisException {
		this(modelFilePath, rangeFilePath, ContentFilterTools.vectorBuilder);
	}
    
    public SVMContentFilter(BufferedReader modelFile, BufferedReader rangeFile, FeatureVectorBuilder<BxZone, BxPage> featureVectorBuilder) throws AnalysisException {
		super(featureVectorBuilder, BxZoneLabel.class);
        try {
            loadModel(modelFile, rangeFile);
        } catch (IOException ex) {
            throw new AnalysisException("Cannot create SVM classifier!", ex);
        }
	}

	public SVMContentFilter(String modelFilePath, String rangeFilePath, FeatureVectorBuilder<BxZone, BxPage> featureVectorBuilder) throws AnalysisException {
		super(featureVectorBuilder, BxZoneLabel.class);
		InputStreamReader modelISR = new InputStreamReader(Thread.currentThread().getClass()
				.getResourceAsStream(modelFilePath));
		BufferedReader modelFile = new BufferedReader(modelISR);
		
		InputStreamReader rangeISR = new InputStreamReader(Thread.currentThread().getClass()
				.getResourceAsStream(rangeFilePath));
		BufferedReader rangeFile = new BufferedReader(rangeISR);
        try {
            loadModel(modelFile, rangeFile);
        } catch (IOException ex) {
            throw new AnalysisException("Cannot create SVM classifier!", ex);
        }
	}
    
    @Override
    public BxDocument filter(BxDocument document) throws AnalysisException {
        for (BxZone zone: document.asZones()) {
			if (zone.getLabel().isOfCategoryOrGeneral(BxZoneLabelCategory.CAT_BODY)) {
                
                zone.setLabel(predictLabel(zone, zone.getParent()));
                System.out.println(zone.getLabel()+" "+zone.toText());
                System.out.println("");
            }
		}
		return document;
    }
    
    public static void main(String[] args) throws AnalysisException, FileNotFoundException {
        BufferedReader r1 = new BufferedReader(new FileReader("/tmp/junkfilter"));
        BufferedReader r2 = new BufferedReader(new FileReader("/tmp/junkfilter.range"));
        SVMContentFilter filter = new SVMContentFilter(r1, r2);
        String trainPath = "/home/domin/newexamples/all/data/ex/";
        List<BxDocument> documents = EvaluationUtils.getDocumentsFromPath(trainPath);
        for (BxDocument d : documents) {
            filter.filter(d);
        }
    }
    
}
