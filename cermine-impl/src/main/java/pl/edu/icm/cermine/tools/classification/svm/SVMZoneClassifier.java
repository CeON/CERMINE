package pl.edu.icm.cermine.tools.classification.svm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

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
    		
			BxZoneLabel predicted = predictLabel(zone, zone.getParent());
//			System.out.println("predictedVal " + predicted + "( is " + zone.getLabel() + ")");
			zone.setLabel(predicted);
		}
		return document;
	}
	
	public static List<TrainingSample<BxZoneLabel>>loadProblem(String path, FeatureVectorBuilder<BxZone, BxPage> fvb) throws IOException {
		File file = new File(path);
		return loadProblem(file, fvb);
	}

	public static List<TrainingSample<BxZoneLabel>> loadProblem(File file, FeatureVectorBuilder<BxZone, BxPage> fvb) throws IOException {
		List<TrainingSample<BxZoneLabel>> ret = new ArrayList<TrainingSample<BxZoneLabel>>();
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
		String line = null;
		final Pattern partsPattern = Pattern.compile(" ");
		final Pattern twopartPattern = Pattern.compile(":");
		while((line = br.readLine()) != null) {
			String[] parts = partsPattern.split(line);
			BxZoneLabel label = BxZoneLabel.values()[Integer.parseInt(parts[0])];
			FeatureVector fv = new FeatureVector();
			List<Double> values = new ArrayList<Double>();
			for(Integer partIdx=1; partIdx<parts.length; ++partIdx) {
				String[] subparts = twopartPattern.split(parts[partIdx]);
				values.add(Double.parseDouble(subparts[1]));
			}
			fv.setValues(values.toArray(new Double[values.size()]));
			fv.setNames(fvb.getFeatureNames());
			TrainingSample<BxZoneLabel> sample = new TrainingSample<BxZoneLabel>(fv, label);
			ret.add(sample);
		}
		br.close();
		return ret;
	}
}
