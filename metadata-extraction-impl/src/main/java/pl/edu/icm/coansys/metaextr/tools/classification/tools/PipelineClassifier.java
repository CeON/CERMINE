package pl.edu.icm.coansys.metaextr.tools.classification.tools;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import pl.edu.icm.coansys.metaextr.AnalysisException;
import pl.edu.icm.coansys.metaextr.tools.classification.svm.SVMZoneClassifier;
import pl.edu.icm.coansys.metaextr.structure.ZoneClassifier;
import pl.edu.icm.coansys.metaextr.structure.model.BxDocument;
import pl.edu.icm.coansys.metaextr.structure.model.BxZone;

public class PipelineClassifier implements ZoneClassifier {
	private List<PickyClassifier> classifiers = new ArrayList<PickyClassifier>();

	public void addClassifier(PickyClassifier clas) throws AnalysisException {
		classifiers.add(clas);
	}

	@Override
	public BxDocument classifyZones(BxDocument document) throws AnalysisException {
		assert classifiers.size() != 0;
		
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
