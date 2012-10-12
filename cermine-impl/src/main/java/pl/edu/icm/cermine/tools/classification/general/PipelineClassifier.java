package pl.edu.icm.cermine.tools.classification.general;

import java.util.ArrayList;
import java.util.List;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.structure.ZoneClassifier;
import pl.edu.icm.cermine.structure.model.BxDocument;
import pl.edu.icm.cermine.structure.model.BxZone;
import pl.edu.icm.cermine.tools.classification.svm.SVMZoneClassifier;

public class PipelineClassifier implements ZoneClassifier {
	private List<PickyClassifier> classifiers = new ArrayList<PickyClassifier>();

	public void addClassifier(PickyClassifier clas) throws AnalysisException {
		classifiers.add(clas);
	}

	@Override
	public BxDocument classifyZones(BxDocument document) throws AnalysisException {
		assert !classifiers.isEmpty();
		
		BxDocument ret = document;
		for(ZoneClassifier clas: classifiers) {
			ret = clas.classifyZones(ret);
		}
		return ret;
	}

	public static abstract class PickyClassifier implements ZoneClassifier {
		SVMZoneClassifier classifier;
		
		public PickyClassifier(SVMZoneClassifier classifier) {
			super();
			this.classifier = classifier;
		}

		public abstract Boolean shouldBeClassified(BxZone zone);
		
		@Override
		public BxDocument classifyZones(BxDocument document)
				throws AnalysisException {
			for(BxZone zone: document.asZones())
				if(shouldBeClassified(zone))
					zone.setLabel(classifier.predictZoneLabel(zone));
			return document;
		}
	}
}
