package pl.edu.icm.cermine.tools.timeout;

import com.google.common.base.Preconditions;

/** Thread-local singleton for setting and removing timeout for the thread.
 * <br/>
 * The standard way of using this class in order to set timeout is to:
 * <ul>
 *   <li>Register the timeout using {@link #set(Timeout)} method.</li>
 *   <li>Call {@link #get()} to retrieve the {@link Timeout} object. User can
 *   subsequently use the {@link Timeout} object to check if the timeout time 
 *   has passed or not.</li>
 *   <li>Remove the timeout from the register {@link #remove()}, i.e., remove
 *   the timeout. After it is called, if {@link #get()} is called to get the 
 *   {@link Timeout} object, a dummy implementation will be received with no 
 *   timeout set.</li>
 * </ul>
 * 
 * Example of usage:
 * <pre>
 * <code>
 * try {
 *   TimeoutRegister.set(new StandardTimeout(300));
 *   doStuff();
 * } finally {
 *   TimeoutRegister.remove();
 * }
 * </code>
 * </pre>
 * 
 * @author Mateusz Kobos
 */
public class TimeoutRegister {
	private static final Timeout noTimeout = new DummyTimeout();
	
	private static final ThreadLocal<Timeout> instance = new ThreadLocal<Timeout>(){
		@Override
		protected Timeout initialValue(){
			return noTimeout;
		}
	};
	
	public static Timeout get(){
		return instance.get();
	}
	
	public static void set(Timeout timeout){
        Preconditions.checkNotNull(timeout);
		instance.set(timeout);
	}
	
	public static void remove(){
		instance.remove();
	}
}
