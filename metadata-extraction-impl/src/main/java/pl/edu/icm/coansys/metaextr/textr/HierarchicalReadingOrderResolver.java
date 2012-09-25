package pl.edu.icm.coansys.metaextr.textr;

import pl.edu.icm.coansys.metaextr.textr.ReadingOrderResolver;
import pl.edu.icm.coansys.metaextr.AnalysisException;
import pl.edu.icm.coansys.metaextr.textr.model.BxDocument;
import pl.edu.icm.coansys.metaextr.textr.readingorder.ReadingOrderAnalyzer;

public class HierarchicalReadingOrderResolver implements ReadingOrderResolver {

	@Override
	public BxDocument resolve(BxDocument document) throws AnalysisException {
		ReadingOrderAnalyzer readingOrder = new ReadingOrderAnalyzer();
		BxDocument sortedDoc = readingOrder.setReadingOrder(document);
		return sortedDoc;		
	}

}
