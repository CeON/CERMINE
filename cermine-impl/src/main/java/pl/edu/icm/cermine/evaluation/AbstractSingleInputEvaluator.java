package pl.edu.icm.cermine.evaluation;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;
import java.util.regex.Pattern;
import pl.edu.icm.cermine.evaluation.AbstractEvaluator.Results;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.exception.TransformationException;

/**
 * Abstract evaluator used for implementation of evaluators that requires single
 * input file for each test case. This file is used both to generate input for
 * evaluated processor and to create expected result of the processing.
 *
 * @author krusek
 */
abstract public class AbstractSingleInputEvaluator<L, P, I, R extends Results<R>> extends AbstractEvaluator<P, R> {


    abstract protected L readDocument(Reader input) throws TransformationException;

    protected abstract Pattern getFilenamePattern();
    protected L readDocument(String path) throws FileNotFoundException, TransformationException, IOException {
        Reader input = new FileReader(path);
        try {
            return readDocument(input);
        } finally {
            input.close();
        }
    }

    protected abstract P processDocument(L document) throws AnalysisException;

    protected abstract void preprocessDocument(L document);

    protected abstract P prepareExpectedDocument(L document) throws AnalysisException;

    protected abstract P prepareActualDocument(L document) throws AnalysisException;

    @Override
    protected Documents<P> getDocuments(String directory, String filename) throws FileNotFoundException, TransformationException, IOException, AnalysisException {
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

	protected abstract Iterator<I> iterateItems(P document);

	protected abstract R compareItems(I expected, I actual);

	@Override
	protected R compareDocuments(P expected, P actual) {
	    R summary = newResults();
	
	    Iterator<I> expectedItems = iterateItems(expected);
	    Iterator<I> actualItems = iterateItems(actual);
	    int i = 0;
	    while(expectedItems.hasNext() && actualItems.hasNext()) {
	        I expectedItem = expectedItems.next();
	        I actualItem = actualItems.next();
	        R results = compareItems(expectedItem, actualItem);
	        if (getDetail() == Detail.FULL) {
	            printItemResults(expectedItem, actualItem, i, results);
	        }
	        summary.add(results);
	        i++;
	    }
	    return summary;
	}    
	abstract protected void printItemResults(I expected, I actual, int itemIndex, R results);

}
