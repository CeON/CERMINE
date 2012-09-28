package pl.edu.icm.coansys.metaextr.structure;

import pl.edu.icm.coansys.metaextr.structure.ReadingOrderResolver;
import pl.edu.icm.coansys.metaextr.AnalysisException;
import pl.edu.icm.coansys.metaextr.structure.model.BxDocument;
import pl.edu.icm.coansys.metaextr.structure.readingorder.ReadingOrderAnalyzer;

public class HierarchicalReadingOrderResolver implements ReadingOrderResolver {

	@Override
	public BxDocument resolve(BxDocument document) throws AnalysisException {
		ReadingOrderAnalyzer readingOrder = new ReadingOrderAnalyzer();
		BxDocument sortedDoc = readingOrder.setReadingOrder(document);
		return sortedDoc;		
	}

}
