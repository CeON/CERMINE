package pl.edu.icm.yadda.analysis.metadata.evaluation;

import java.io.FileReader;
import java.io.Reader;
import java.util.regex.Pattern;
import pl.edu.icm.yadda.analysis.metadata.evaluation.AbstractEvaluator.Documents;
import pl.edu.icm.yadda.analysis.metadata.evaluation.AbstractEvaluator.Results;
import pl.edu.icm.yadda.analysis.textr.model.BxDocument;

/**
 * Abstract evaluator used for implementation of evaluators that requires single
 * input file for each test case. This file is used both to generate input for
 * evaluated processor and to create expected result of the processing.
 *
 * @author krusek
 */
abstract public class AbstractSingleInputEvaluator<L, P, R extends Results<R>> extends AbstractEvaluator<P, R> {

    abstract protected Pattern getFilenamePattern();

    abstract protected L readDocument(Reader input) throws Exception;

    protected L readDocument(String path) throws Exception {
        Reader input = new FileReader(path);
        try {
            return readDocument(input);
        } finally {
            input.close();
        }
    }
    
    protected abstract P prepareExpectedDocument(L document) throws Exception;

    protected abstract P prepareActualDocument(L document) throws Exception;

	protected Documents<P> getDocuments(P loadedDocument) throws Exception {
        P expectedDocument = null;
        P actualDocument = null;
        expectedDocument = prepareExpectedDocument((L)loadedDocument);
        actualDocument = prepareActualDocument((L)loadedDocument);
        return new Documents<P>(expectedDocument, actualDocument);
	}

    @Override
    protected Documents<P> getDocuments(String directory, String filename) throws Exception {
        if (getFilenamePattern().matcher(filename).matches()) {
            L loadedDocument = readDocument(directory + filename);
            P expectedDocument = prepareExpectedDocument(loadedDocument);
            P actualDocument = prepareActualDocument(loadedDocument);
            return new Documents<P>(expectedDocument, actualDocument);
        }
        else {
            return null;
        }
    }
}
