package pl.edu.icm.cermine.tools.timeout;

/**
 * The default implementation that uses system time to provide the current time.
 * 
 * @author Mateusz Kobos
 */
class CurrentSystemTime implements CurrentTime {
	@Override
	public long get() {
		return System.currentTimeMillis();
	}
}
