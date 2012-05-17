package pl.edu.icm.yadda.analysis.metadata.evaluation;

import java.util.Iterator;
import pl.edu.icm.yadda.analysis.metadata.evaluation.AbstractEvaluator.Results;

/**
 * Abstract evaluator used for implementation of evaluators that requires single
 * input file for each test case (see AbstractSingleInputEvaluator for details),
 * where each file contains items that can be compared separately (like pages).
 *
 * @author krusek
 */
abstract public class AbstractItemSingleInputEvaluator<L, P, I, R extends Results<R>>
        extends AbstractSingleInputEvaluator<L, P, R> {

    protected abstract Iterator<I> iterateItems(P document);

    protected abstract R compareItems(I expected, I actual);

    abstract protected void printItemResults(I expected, I actual, int itemIndex, R results);

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
            if (detail == Detail.FULL) {
                printItemResults(expectedItem, actualItem, i, results);
            }
            summary.add(results);
            i++;
        }
        return summary;
    }
}
