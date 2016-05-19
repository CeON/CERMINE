package pl.edu.icm.cermine.tools.timeout;

/**
 * 
 * @author Mateusz Kobos
 *
 */
public class TimeoutException extends RuntimeException{
	private static final long serialVersionUID = 1L;

	public TimeoutException(long currentTimeMillis, long deadlineMillis){
		super(String.format("Timeout occured: when checked, it was "
					+"%d milliseconds past the deadline time", 
					currentTimeMillis - deadlineMillis));
	}
	
	/**
	 * Constructor to be used when you want to re-throw a timeout-related 
	 * exception. 
	 * 
	 * @param ex original exception
	 */
	public TimeoutException(Exception ex){
	    super(ex);
	}
}
