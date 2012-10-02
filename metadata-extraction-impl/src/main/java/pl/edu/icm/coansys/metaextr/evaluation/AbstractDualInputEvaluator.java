package pl.edu.icm.coansys.metaextr.evaluation;

import java.io.FileReader;
import java.io.Reader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import pl.edu.icm.coansys.metaextr.evaluation.AbstractEvaluator.Results;

/**
 * Abstract evaluator used for implementation of evaluators that requires two
 * input files for each test case - one containing the document that needs to
 * be processed (actual document) and one containing expected result of the
 * processing (expected document).
 *
 * @author krusek
 */
abstract public class AbstractDualInputEvaluator<P, R extends Results<R>> extends AbstractEvaluator<P, R> {

    abstract protected Pattern getActualFilenamePattern();

    abstract protected String getExpectedFilenameReplacement();

    abstract protected P getExpectedDocument(Reader input) throws Exception;

    protected P getExpectedDocument(String path) throws Exception {
        Reader input = new FileReader(path);
        try {
            return getExpectedDocument(input);
        } finally {
            input.close();
        }
    }

    abstract protected P getActualDocument(Reader input) throws Exception;

    protected P getActualDocument(String path) throws Exception {
        Reader input = new FileReader(path);
        try {
            return getActualDocument(input);
        } finally {
            input.close();
        }
    }

    @Override
    protected Documents<P> getDocuments(String directory, String filename) throws Exception {
        Matcher matcher = getActualFilenamePattern().matcher(filename);
        if (matcher.matches()) {
            StringBuffer buffer = new StringBuffer();
            matcher.appendReplacement(buffer, getExpectedFilenameReplacement());
            matcher.appendTail(buffer);
            P expectedDocument = getExpectedDocument(directory + buffer.toString());
            P actualDocument = getActualDocument(directory + filename);
            return new Documents<P>(expectedDocument, actualDocument);
        }
        else {
            return null;
        }
    }
}
