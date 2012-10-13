package pl.edu.icm.cermine.evaluation;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import pl.edu.icm.cermine.evaluation.AbstractEvaluator.Results;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.exception.TransformationException;

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

    abstract protected P getExpectedDocument(Reader input) throws IOException;

    protected P getExpectedDocument(String path) throws FileNotFoundException, IOException {
        Reader input = new FileReader(path);
        try {
            return getExpectedDocument(input);
        } finally {
            input.close();
        }
    }

    abstract protected P getActualDocument(Reader input) throws AnalysisException, TransformationException;

    protected P getActualDocument(String path) throws FileNotFoundException, AnalysisException, TransformationException, IOException {
        Reader input = new FileReader(path);
        try {
            return getActualDocument(input);
        } finally {
            input.close();
        }
    }

    @Override
    protected Documents<P> getDocuments(String directory, String filename) throws FileNotFoundException, IOException, AnalysisException, TransformationException {
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
