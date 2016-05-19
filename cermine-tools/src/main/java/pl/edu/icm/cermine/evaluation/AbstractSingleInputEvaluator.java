/**
 * This file is part of CERMINE project.
 * Copyright (c) 2011-2013 ICM-UW
 *
 * CERMINE is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * CERMINE is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with CERMINE. If not, see <http://www.gnu.org/licenses/>.
 */

package pl.edu.icm.cermine.evaluation;

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
 * @param <L>
 * @param <P>
 * @param <I>
 * @param <R>
 */
public abstract class AbstractSingleInputEvaluator<L, P, I, R extends Results<R>> extends AbstractEvaluator<P, R> {

    protected abstract L readDocument(Reader input) throws TransformationException;

    protected abstract Pattern getFilenamePattern();
    
    protected L readDocument(String path) throws TransformationException, IOException {
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
    protected Documents<P> getDocuments(String directory, String filename) throws TransformationException, IOException, AnalysisException {
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

    protected abstract void printItemResults(I expected, I actual, int itemIndex, R results);

}
