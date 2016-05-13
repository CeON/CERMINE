package pl.edu.icm.cermine.tools.timeout;

/**
 * Class that throws an exception when given amount of time passes when 
 * its {@link check()} method is called.
 * 
 * @author Mateusz Kobos
 *
 */
public interface Timeout {
	/**
	 * Throw exception if the timeout time has passed.
	 * 
	 * @throws TimeoutException
	 */
	void check() throws TimeoutException;
}
