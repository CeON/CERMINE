package pl.edu.icm.yadda.analysis.jrlsimilarity.nodes.processing;

import pl.edu.icm.yadda.analysis.jrlsimilarity.common.DoubleJournalSimilarity;
import pl.edu.icm.yadda.analysis.jrlsimilarity.metadata.JournalPairMetaData;
import pl.edu.icm.yadda.analysis.jrlsimilarity.process.BasicSimilarityCaculateMethod;
import pl.edu.icm.yadda.process.ctx.ProcessContext;
import pl.edu.icm.yadda.process.node.IProcessingNode;

public class CalculateSimilarityProcessingNode implements IProcessingNode<JournalPairMetaData, DoubleJournalSimilarity> {

	@Override
	public DoubleJournalSimilarity process(JournalPairMetaData input,
			ProcessContext ctx) throws Exception {
		// TODO Auto-generated method stub
		BasicSimilarityCaculateMethod method = new BasicSimilarityCaculateMethod();
		method.setJournalPair(input);
		
		return method.calculate();
	}

}
