package pl.edu.icm.cermine.tools.timeout;

/**
 * Delivers information about the current time.
 * 
 * @author Mateusz Kobos
 */
public interface CurrentTime {
	/**
	 * @return current time in milliseconds
	 */
	long get();
}
