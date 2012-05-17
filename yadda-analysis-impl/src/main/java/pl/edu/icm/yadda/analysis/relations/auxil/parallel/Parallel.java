package pl.edu.icm.yadda.analysis.relations.auxil.parallel;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import pl.edu.icm.yadda.process.iterator.ISourceIterator;

/**
 * Code taken from http://stackoverflow.com/questions/4010185/parallel-for-for-java
 * Person who wrote answer: @author mlaw
 * 
 * @author pdendek
 *
 */
public class Parallel {
    private static final int NUM_CORES = 1;//Runtime.getRuntime().availableProcessors();
    
    
    
    public static <T> void For(final Iterable<T> pElements, final Operation<T> pOperation) {
        ExecutorService executor = Executors.newFixedThreadPool(NUM_CORES);
        List<Future<?>> futures = new LinkedList<Future<?>>();
        for (final T element : pElements) {
            Future<?> future = executor.submit(new Runnable() {
                @Override
                public void run() {
                    pOperation.perform(element);
                }
            });

            futures.add(future);
        }

        for (Future<?> f : futures) {
            try {
                f.get();
            } catch (InterruptedException e) {
            } catch (ExecutionException e) {
            }
        }
        executor.shutdown();
    }
    
    
    
    
    public static <T> void For(final ISourceIterator<T> itElement, final Operation<T> pOperation) throws Exception {
        For(itElement, pOperation, 1000);
    }




    public static <T> void For(final ISourceIterator<T> itElement, 
    		final Operation<T> pOperation,
    		final int step) throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(NUM_CORES);
        List<Future<?>> futures = new LinkedList<Future<?>>();
        
        int chunk = step;
        for(int outer = 0;itElement.hasNext();outer++){
        	System.out.println("\rWykonano "+outer*chunk+"/"+itElement.getEstimatedSize()+"  ("+outer*chunk*100/(double)itElement.getEstimatedSize()+"%) "+new Date());
	        for(int i=0;itElement.hasNext() && i<chunk;i++) {
	        	final T element = itElement.next();
	            Future<?> future = executor.submit(new Runnable() {
	                @Override
	                public void run() {
	                    pOperation.perform(element);
	                }
	            });
	
	            futures.add(future);
	        }
	
	        for (Future<?> f : futures) {
	            try {
	                f.get();
	            } catch (InterruptedException e) {
	            } catch (ExecutionException e) {
	            }
	        }
	        futures.clear();
//	        break;
        }
        executor.shutdown();
    }
    
    public static <T> void CloneOperationFor(final ISourceIterator<T> itElement, 
    		final Operation<T> pOperation,
    		final int step) {
        ExecutorService executor = Executors.newFixedThreadPool(NUM_CORES);
        List<Future<?>> futures = new LinkedList<Future<?>>();
        
        int chunk = step;
        for(int outer = 0;itElement.hasNext();outer++){
        	System.out.println("\rWykonano "+outer*chunk+"/"+itElement.getEstimatedSize()+"  ("+outer*chunk*100/(double)itElement.getEstimatedSize()+"%) "+new Date());
	        for(int i=0;itElement.hasNext() && i<chunk;i++) {
	        	final T element = itElement.next();
	            Future<?> future = executor.submit(new Runnable() {
	                @Override
	                public void run() {
	                    pOperation.replicate().perform(element);
	                }
	            });
	
	            futures.add(future);
	        }
	
	        for (Future<?> f : futures) {
	            try {
	                f.get();
	            } catch (InterruptedException e) {
	            } catch (ExecutionException e) {
	            }
	        }
	        futures.clear();
        }
        executor.shutdown();
    }

}
