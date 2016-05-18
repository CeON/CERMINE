package pl.edu.icm.cermine.tools.timeout;

import com.google.common.base.Preconditions;

/** 
 * Class that throws an exception when given amount of time has already passed
 * when its {@link #check()} method is called.
 * 
 * @author Mateusz Kobos
 */
public class Timeout {
	private long endTimeMillis;

	/**
	 * Create a new instance and start a virtual timeout clock.
	 */
	public Timeout(long timeoutMillis){
        Preconditions.checkArgument(timeoutMillis >= 0);
		long startTime = getCurrentTime();
		this.endTimeMillis = startTime + timeoutMillis;
	}
	
    /**
     * Create a new instance and start a virtual timeout clock that will never 
     * stop, i.e., it will never indicate that the time has already passed.
     */	
	public Timeout(){
	    this.endTimeMillis = Long.MAX_VALUE;
	}
	
	/**
	 * Throw exception if it already is the deadline time or past it.
	 * 
	 * @throws TimeoutException
	 */
	public void check() throws TimeoutException{
		long currTimeMillis = getCurrentTime();
		if(currTimeMillis >= endTimeMillis){
			throw new TimeoutException(currTimeMillis, endTimeMillis);
		}
	}
	
	private static long getCurrentTime(){
        return System.currentTimeMillis();
	}	
	
	/**
	 * Return the timeout with more immediate deadline time.
	 */
	public static Timeout min(Timeout t0, Timeout t1){
	    if (t0.endTimeMillis < t1.endTimeMillis){
	        return t0;
	    } else {
	        return t1;
	    }
	}
}