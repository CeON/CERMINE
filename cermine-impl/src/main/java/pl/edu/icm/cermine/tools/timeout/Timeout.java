package pl.edu.icm.cermine.tools.timeout;

import com.google.common.base.Preconditions;

/** 
 * Class that throws an exception when given amount of time has already passed
 * when its {@link #check()} method is called.
 * 
 * @author Mateusz Kobos
 */
public class Timeout {
	private long deadlineMillis;

	/**
	 * Create a new instance with the deadline set corresponding to given 
	 * timeout value. 
	 * If the timeout is set to 0, the first call of {@link #check()} method
	 * will result in throwing the {@link TimeoutException}.
	 * @param timeoutMillis timeout in milliseconds
	 */
	public Timeout(long timeoutMillis){
        Preconditions.checkArgument(timeoutMillis >= 0);
		long startTime = getCurrentTime();
		this.deadlineMillis = startTime + timeoutMillis;
	}
	
    /**
     * Create a new instance and with the deadline set in an unattainable 
     * future.
     */	
	public Timeout(){
	    this.deadlineMillis = Long.MAX_VALUE;
	}
	
	/**
	 * Throw exception if it already is the deadline time or past it.
	 * 
	 * @throws TimeoutException
	 */
	public void check() throws TimeoutException{
		long currTimeMillis = getCurrentTime();
		if(currTimeMillis >= deadlineMillis){
			throw new TimeoutException(currTimeMillis, deadlineMillis);
		}
	}
	
	private static long getCurrentTime(){
        return System.currentTimeMillis();
	}	
	
	/**
	 * Return the timeout corresponding to the more immediate deadline.
	 */
	public static Timeout min(Timeout t0, Timeout t1){
	    if (t0.deadlineMillis < t1.deadlineMillis){
	        return t0;
	    } else {
	        return t1;
	    }
	}
}