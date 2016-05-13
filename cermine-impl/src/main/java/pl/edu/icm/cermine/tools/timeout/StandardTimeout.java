package pl.edu.icm.cermine.tools.timeout;

import com.google.common.base.Preconditions;

/** 
 * Standard {@link Timeout} implementation.
 * 
 * @author Mateusz Kobos
 */
public class StandardTimeout implements Timeout {
	private long endTimeMillis;
	private final CurrentTime currentTime;

	/**
	 * Create a new instance and start the timeout clock.
	 */
	public StandardTimeout(long timeoutMillis){
		this(timeoutMillis, new CurrentSystemTime());
	}
	
	/**
	 * The same as {@link TimeoutClock(CurrentTime)}, but the current system 
	 * time is provided by the object given as an additional parameter.
	 * 
	 * @param currentTime object that delivers information about current time
	 */
	public StandardTimeout(long timeoutMillis, CurrentTime currentTime){
        Preconditions.checkArgument(timeoutMillis >= 0);
		long startTime = currentTime.get();
		this.endTimeMillis = startTime + timeoutMillis;
		this.currentTime = currentTime;
	}
	
	/**
	 * Throw exception if the clock has started and it is pass the timeout time.
	 * 
	 * @throws TimeoutException
	 */
	public void check() throws TimeoutException{
		long currTimeMillis = currentTime.get();
		if(currTimeMillis > endTimeMillis){
			throw new TimeoutException(currTimeMillis, endTimeMillis);
		}
	}
	
}