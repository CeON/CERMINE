package pl.edu.icm.cermine.tools.classification.svm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import libsvm.svm;
import libsvm.svm_node;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.structure.ZoneClassifier;
import pl.edu.icm.cermine.structure.model.BxDocument;
import pl.edu.icm.cermine.structure.model.BxPage;
import pl.edu.icm.cermine.structure.model.BxZone;
import pl.edu.icm.cermine.structure.model.BxZoneLabel;
import pl.edu.icm.cermine.tools.classification.features.FeatureVector;
import pl.edu.icm.cermine.tools.classification.features.FeatureVectorBuilder;
import pl.edu.icm.cermine.tools.classification.general.TrainingSample;

public class SVMZoneClassifier extends SVMClassifier<BxZone, BxPage, BxZoneLabel>  implements ZoneClassifier{
	public SVMZoneClassifier(FeatureVectorBuilder<BxZone, BxPage> featureVectorBuilder) {
		super(featureVectorBuilder, BxZoneLabel.class);
	}
	
	@Override
	public BxDocument classifyZones(BxDocument document) throws AnalysisException 
	{       
    	for (BxZone zone: document.asZones()) {
			svm_node[] instance = buildDatasetForClassification(zone, zone.getParent());
			double predictedVal = svm.svm_predict(model, instance);
//			System.out.println("predictedVal " + predictedVal + " " + BxZoneLabel.values()[(int)predictedVal] + " (is " + zone.getLabel() + ")");
			zone.setLabel(BxZoneLabel.values()[(int)predictedVal]);
		}
		return document;
	}
	
	public BxZoneLabel classify(FeatureVector fv) {
		svm_node[] instance = buildDatasetForClassification(fv);
		double predictedVal = svm.svm_predict(model, instance);
		return BxZoneLabel.values()[(int)predictedVal];
	}
	
	public static List<TrainingSample<BxZoneLabel>>loadProblem(String path) throws IOException {
		File file = new File(path);
		return loadProblem(file);
	}

	public static List<TrainingSample<BxZoneLabel>> loadProblem(File file) throws IOException {
		List<TrainingSample<BxZoneLabel>> ret = new ArrayList<TrainingSample<BxZoneLabel>>();
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
		String line = null;
		while((line = br.readLine()) != null) {
			String[] parts = line.split(" ");
			BxZoneLabel label = BxZoneLabel.values()[Integer.parseInt(parts[0])];
			FeatureVector fv = new FeatureVector();
			List<Double> values = new ArrayList<Double>();
			for(Integer partIdx=1; partIdx<parts.length; ++partIdx) {
				String[] partParts = parts[partIdx].split(":");
				values.add(Double.parseDouble(partParts[1]));
			}
			fv.setValues(values.toArray(new Double[1]));
			TrainingSample<BxZoneLabel> sample = new TrainingSample<BxZoneLabel>(fv, label);
			ret.add(sample);
		}
		return ret;
	}
}
