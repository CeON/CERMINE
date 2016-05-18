package pl.edu.icm.cermine.tools.timeout;

/**
 * 
 * @author Mateusz Kobos
 *
 */
public class TimeoutException extends RuntimeException{
	private static final long serialVersionUID = 1L;

	public TimeoutException(long currentTimeMillis, long endTimeMillis){
		super(String.format("Timeout occured: when checked, it was "
					+"%d milliseconds past the deadline time", 
					currentTimeMillis - endTimeMillis));
	}
	
	/**
	 * Constructor to be used when you want to re-throw a timeout-related 
	 * exception. 
	 * @param ex exception
	 */
	public TimeoutException(Exception ex){
	    super(ex);
	}
}
