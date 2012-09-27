package pl.edu.icm.coansys.metaextr.classification.tools;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import pl.edu.icm.coansys.metaextr.AnalysisException;
import pl.edu.icm.coansys.metaextr.classification.svm.SVMZoneClassifier;
import pl.edu.icm.coansys.metaextr.textr.ZoneClassifier;
import pl.edu.icm.coansys.metaextr.textr.model.BxDocument;
import pl.edu.icm.coansys.metaextr.textr.model.BxZone;

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

	@Override
	public void loadModel(String modelPath) throws IOException {
		// TODO Auto-generated method stub
	}

	@Override
	public void saveModel(String modelPath) throws IOException {
		// TODO Auto-generated method stub
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

		@Override
		public void loadModel(String modelPath) throws IOException { }

		@Override
		public void saveModel(String modelPath) throws IOException { }
		
	}
}
