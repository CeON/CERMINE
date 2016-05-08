package pl.edu.icm.cermine.tools.timeout;

/**
 * A dummy class that does nothing. You use it if you don't want any timeout
 * to be set.
 * 
 * @author Mateusz Kobos
 *
 */
public class DummyTimeout implements Timeout {
	@Override
	public void check() throws TimeoutException {
	}

}
