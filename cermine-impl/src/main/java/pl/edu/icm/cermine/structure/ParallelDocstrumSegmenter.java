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
        computeDocumentOrientation(document);
        
        BxDocument output = new BxDocument();

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
