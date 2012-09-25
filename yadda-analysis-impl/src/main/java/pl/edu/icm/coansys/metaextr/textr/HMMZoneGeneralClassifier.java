package pl.edu.icm.coansys.metaextr.textr;

import pl.edu.icm.coansys.metaextr.textr.ZoneClassifier;
import java.io.IOException;

import pl.edu.icm.coansys.metaextr.AnalysisException;
import pl.edu.icm.coansys.metaextr.classification.features.FeatureVectorBuilder;
import pl.edu.icm.coansys.metaextr.classification.hmm.HMMServiceImpl;
import pl.edu.icm.coansys.metaextr.classification.hmm.probability.HMMProbabilityInfo;
import pl.edu.icm.coansys.metaextr.textr.model.BxDocument;
import pl.edu.icm.coansys.metaextr.textr.model.BxPage;
import pl.edu.icm.coansys.metaextr.textr.model.BxZone;
import pl.edu.icm.coansys.metaextr.textr.model.BxZoneGeneralLabel;

public class HMMZoneGeneralClassifier implements ZoneClassifier {

	public HMMZoneGeneralClassifier(HMMServiceImpl hmmServiceImpl,
			HMMProbabilityInfo<BxZoneGeneralLabel> hmmProbabilities,
			FeatureVectorBuilder<BxZone, BxPage> vectorBuilder) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public BxDocument classifyZones(BxDocument document)
			throws AnalysisException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void loadModel(String modelPath) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void saveModel(String modelPath) throws IOException {
		// TODO Auto-generated method stub
		
	}

}
