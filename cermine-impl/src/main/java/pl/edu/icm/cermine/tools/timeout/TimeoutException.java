package pl.edu.icm.cermine.tools.timeout;

/**
 * 
 * @author Mateusz Kobos
 *
 */
public class TimeoutException extends RuntimeException{
	private static final long serialVersionUID = 1L;

	public TimeoutException(long currentTimeMillis, long endTimeMillis){
		super(String.format("Timeout occured: %d ms past the deadline time", 
				currentTimeMillis - endTimeMillis));
	}
}
