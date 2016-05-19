package pl.edu.icm.cermine.tools.timeout;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.Callable;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.Test;

/**
 * 
 * @author Mateusz Kobos
 *
 */
public class TimeoutRegisterTest {

    @Test(expected = TimeoutException.class)
    public void testBasic() throws InterruptedException {
        try {
            TimeoutRegister.set(new Timeout(10));
            doStuff();
        } finally {
            TimeoutRegister.remove();
        }
    }

    private static void doStuff() throws InterruptedException {
        for (int i = 0; i < 10; i++) {
            Thread.sleep(5);
            TimeoutRegister.get().check();
        }
    }

    @Test(expected = TimeoutException.class)
    public void testZeroTimeout() {
        Timeout t = new Timeout(0);
        t.check();
    }

    @Test
    public void testRemoveTimeout() throws InterruptedException {
        try {
            TimeoutRegister.set(new Timeout(30000));
            TimeoutRegister.get().check();
            Thread.sleep(10);
            TimeoutRegister.get().check();
            TimeoutRegister.remove();
            TimeoutRegister.get().check();
            Thread.sleep(10);
            TimeoutRegister.get().check();
        } finally {
            TimeoutRegister.remove();
        }
    }

    @Test(expected = TimeoutException.class)
    public void testSetingNewTimeoutAfterRemovingPreviousOne() 
            throws InterruptedException {
        try {
            TimeoutRegister.set(new Timeout(30000));
            TimeoutRegister.get().check();
            TimeoutRegister.remove();
            TimeoutRegister.set(new Timeout(5));
            Thread.sleep(10);
            TimeoutRegister.get().check();
        } finally {
            TimeoutRegister.remove();
        }
    }

    @Test
    public void testTimeoutInChildThreadDoesntInfluenceParent() 
            throws InterruptedException {
        Thread child = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    TimeoutRegister.set(new Timeout(0));
                    TimeoutRegister.get().check();
                    fail("Didn't throw the exception as expected");
                } catch (TimeoutException ex){
                    /** empty */
                } finally {
                    /**
                     * We don't call TimeoutRegister.remove() here, which we
                     * would normally do, because we want to check if the
                     * timeout is carried over to the parent thread.
                     */
                }
            }
        });
        TimeoutRegister.get().check();
        child.start();
        child.join();
        TimeoutRegister.get().check();
    }

    @Test
    public void testTimeoutInParentThreadDoesntInfluenceChild() 
            throws InterruptedException {
        try {
            TimeoutRegister.set(new Timeout(0));
            Thread child = new Thread(new Runnable() {
                @Override
                public void run() {
                    TimeoutRegister.get().check();
                }
            });
            child.start();
            child.join();
        } finally {
            TimeoutRegister.remove();
        }
    }

    @Test
    public void testTimeoutInThreadDoesntInfluenceItsSibling() 
            throws InterruptedException, BrokenBarrierException {

        ArrayList<Callable<Void>> tasks = createTasksWithBarriersToCall();
        ExecutorService exec = Executors.newFixedThreadPool(2);
        List<Future<Void>> results = exec.invokeAll(tasks);
        exec.shutdown();

        try {
            results.get(0).get();
            fail("Didn't throw the exception as expected");
        } catch (ExecutionException ex) {
            assertTrue(ex.getCause() instanceof TimeoutException);
        }
        results.get(1);
    }
    
    private static ArrayList<Callable<Void>> createTasksWithBarriersToCall(){
        final CyclicBarrier barrier0 = new CyclicBarrier(2);
        final CyclicBarrier barrier1 = new CyclicBarrier(2);
        ArrayList<Callable<Void>> tasks = new ArrayList<Callable<Void>>();
        tasks.add(new Callable<Void>() {
            @Override
            public Void call() 
                    throws InterruptedException, BrokenBarrierException {
                try {
                    TimeoutRegister.set(new Timeout(0));
                    barrier0.await();
                    barrier1.await();
                    TimeoutRegister.get().check();
                    return null;
                } finally {
                    TimeoutRegister.remove();
                }
            }
        });
        tasks.add(new Callable<Void>() {
            @Override
            public Void call() 
                    throws InterruptedException, BrokenBarrierException {
                barrier0.await();
                TimeoutRegister.get().check();
                barrier1.await();
                return null;
            }
        });
        return tasks;
    }
}
