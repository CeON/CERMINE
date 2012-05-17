package pl.edu.icm.yadda.analysis.metadata.evaluation;

import java.io.Reader;
import java.io.Writer;
import java.util.Iterator;
import java.util.regex.Pattern;
import pl.edu.icm.yadda.analysis.metadata.evaluation.AbstractEvaluator.Results;
import pl.edu.icm.yadda.analysis.textr.model.BxDocument;
import pl.edu.icm.yadda.analysis.textr.model.BxPage;
import pl.edu.icm.yadda.analysis.textr.tools.BxModelUtils;
import pl.edu.icm.yadda.analysis.textr.transformers.BxDocumentToTrueVizWriter;
import pl.edu.icm.yadda.analysis.textr.transformers.TrueVizToBxDocumentReader;

/**
 *
 * @author krusek
 */
public abstract class AbstractBxModelEvaluator<R extends Results<R>>
        extends AbstractItemSingleInputEvaluator<BxDocument, BxDocument, BxPage, R> {

    private static final Pattern FILENAME_PATTERN = Pattern.compile("(.+)\\.xml");

    @Override
    protected Pattern getFilenamePattern() {
        return FILENAME_PATTERN;
    }

    protected abstract void flattenDocument(BxDocument document);

    protected abstract BxDocument processDocument(BxDocument document) throws Exception;

    @Override
    protected BxDocument prepareActualDocument(BxDocument document) throws Exception {
        document = BxModelUtils.deepClone(document);
        flattenDocument(document);
        return processDocument(document);
    }
    
    @Override
    protected BxDocument prepareExpectedDocument(BxDocument document) {
        return document;
    }
    
    private TrueVizToBxDocumentReader reader = new TrueVizToBxDocumentReader();
    
    @Override
    protected BxDocument readDocument(Reader input) throws Exception {
        return new BxDocument().setPages(reader.read(input));
    }

    private BxDocumentToTrueVizWriter writer = new BxDocumentToTrueVizWriter();

    @Override
    protected void writeDocument(BxDocument document, Writer output) throws Exception {
        writer.write(output, document.getPages());
    }

    @Override
    protected Iterator<BxPage> iterateItems(BxDocument document) {
        return document.getPages().iterator();
    }

    abstract protected void printItemResults(int itemIndex, R results);

    @Override
    protected void printItemResults(BxPage expected, BxPage actual, int itemIndex, R results) {
        printItemResults(itemIndex, results);
    }
}
