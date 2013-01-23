package pl.edu.icm.cermine.tools.classification.svm;

import libsvm.svm;
import libsvm.svm_node;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.structure.ZoneClassifier;
import pl.edu.icm.cermine.structure.model.BxDocument;
import pl.edu.icm.cermine.structure.model.BxPage;
import pl.edu.icm.cermine.structure.model.BxZone;
import pl.edu.icm.cermine.structure.model.BxZoneLabel;
import pl.edu.icm.cermine.tools.classification.features.FeatureVectorBuilder;

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
	
}
