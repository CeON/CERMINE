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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import pl.edu.icm.cermine.PdfNLMContentExtractor;
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
        int index = -1;
        BxPage page;
        List<Component> components;

        public NumBxPage(BxPage page, int index) {
            this.page = page;
            this.index = index;
        }

        public NumBxPage(BxPage page) {
            this.page = page;
        }
    }
    
    class SingleSegmenter implements Callable<NumBxPage> {
        NumBxPage page;

        public SingleSegmenter(BxPage page, int index) {
            this.page = new NumBxPage(page, index);
        }
       
        @Override
        public NumBxPage call() throws AnalysisException{
            return new NumBxPage(segmentPage(page.page), page.index);
        }
    }
    
    class ComponentCounter implements Callable<NumBxPage> {
        NumBxPage page;

        public ComponentCounter(BxPage page) {
            this.page = new NumBxPage(page);
        }
       
        @Override
        public NumBxPage call() throws AnalysisException {
            page.components = createComponents(page.page);
            return page;
        }
    }
    

    @Override
    public BxDocument segmentDocument(BxDocument document) throws AnalysisException {
        Map<BxPage, List<Component>> componentMap = new HashMap<BxPage, List<Component>>();

        ExecutorService exec = Executors.newFixedThreadPool(PdfNLMContentExtractor.THREADS_NUMBER);
        ArrayList<Callable<NumBxPage>> tasks = new ArrayList<Callable<NumBxPage>>();
        for (BxPage page : document.getPages()) {
           tasks.add(new ComponentCounter(page));
        }
        
        List<Future<NumBxPage>> results;
        try {
            results = exec.invokeAll(tasks);
            exec.shutdown();
            
            for (Future<NumBxPage> result : results) {
                NumBxPage p = result.get();
                componentMap.put(p.page, p.components);
            }
        } catch (ExecutionException ex) {
            throw new AnalysisException("Cannot segment pages!", ex);
        } catch (InterruptedException ex) {
            throw new AnalysisException("Cannot segment pages!", ex);
        }
                
        this.computeDocumentOrientation(componentMap);
    
        BxDocument output = new BxDocument();
        BxPage[] pages = new BxPage[document.getPages().size()];
        
        exec = Executors.newFixedThreadPool(PdfNLMContentExtractor.THREADS_NUMBER);
        tasks = new ArrayList<Callable<NumBxPage>>();
        int i = 0;
        for (BxPage page : document.getPages()) {
           tasks.add(new SingleSegmenter(page, i++));
        }
        
        try {
            results = exec.invokeAll(tasks);
            exec.shutdown();
            
            for (Future<NumBxPage> result : results) {
                NumBxPage p = result.get();
                pages[p.index] = p.page;
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
