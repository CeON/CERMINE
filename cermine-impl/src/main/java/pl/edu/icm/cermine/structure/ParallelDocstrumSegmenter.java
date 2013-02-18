package pl.edu.icm.cermine.structure;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.structure.model.BxDocument;
import pl.edu.icm.cermine.structure.model.BxPage;

/**
 * Page segmenter using Docstrum algorithm.
 * 
 * @author krusek
 */
public class ParallelDocstrumSegmenter extends DocstrumSegmenter {
    
    class NumBxPage {
        BxPage page;
        int number;

        public NumBxPage(BxPage page, int number) {
            this.page = page;
            this.number = number;
        }
        
    }
    
    class SingleSegmenter implements Callable<NumBxPage> {
        NumBxPage page;

        public SingleSegmenter(BxPage page, int number) {
            this.page = new NumBxPage(page, number);
        }
       
        @Override
        public NumBxPage call() throws AnalysisException{
            return new NumBxPage(segmentPage(page.page), page.number);
        }
    }

    @Override
    public BxDocument segmentDocument(BxDocument document) throws AnalysisException {
        BxDocument output = new BxDocument();

        System.out.println("PARRRL");
        BxPage[] pages = new BxPage[document.getPages().size()];
        
        ExecutorService exec = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        
        ArrayList<Callable<NumBxPage>> tasks = new ArrayList<Callable<NumBxPage>>();
        int i = 0;
        for (BxPage page : document.getPages()) {
           tasks.add(new SingleSegmenter(page, i++));
        }
        
        List<Future<NumBxPage>> results;
        try {
            results = exec.invokeAll(tasks);
            exec.shutdown();
            
            for (Future<NumBxPage> result : results) {
                NumBxPage p = result.get();
                pages[p.number] = p.page;
            }
            for (BxPage p : pages) {
                if (p.getBounds() != null) {
                    output.addPage(p);
                }
            }
            return output;
        } catch (ExecutionException ex) {
            throw new AnalysisException("Cannot segment pages!", ex);
        } catch (InterruptedException ex) {
            throw new AnalysisException("Cannot segment pages!", ex);
        }
    }

}
