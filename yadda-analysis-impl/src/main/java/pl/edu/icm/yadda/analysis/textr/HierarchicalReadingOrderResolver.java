package pl.edu.icm.yadda.analysis.textr;

import pl.edu.icm.yadda.analysis.AnalysisException;
import pl.edu.icm.yadda.analysis.textr.model.BxDocument;
import pl.edu.icm.yadda.analysis.textr.readingorder.ReadingOrderAnalyzer;

public class HierarchicalReadingOrderResolver implements ReadingOrderResolver {

	@Override
	public BxDocument resolve(BxDocument document) throws AnalysisException {
		ReadingOrderAnalyzer readingOrder = new ReadingOrderAnalyzer();
		BxDocument sortedDoc = readingOrder.setReadingOrder(document);
		return sortedDoc;		
	}

}
