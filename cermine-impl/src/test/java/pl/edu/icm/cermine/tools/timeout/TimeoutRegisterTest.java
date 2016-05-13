package pl.edu.icm.cermine.tools.timeout;

import org.junit.Test;

public class TimeoutRegisterTest {
	@Test(expected=TimeoutException.class)
	public void testSimpleWithSleep() throws InterruptedException{
		try {
			TimeoutRegister.set(new StandardTimeout(300));
			doStuffWithSleep();
		} finally {
			TimeoutRegister.remove();
		}
	}
	
	private static void doStuffWithSleep() throws InterruptedException{
		for(int i = 0; i < 10; i++){
			Thread.sleep(100);
			TimeoutRegister.get().check();
		}
	}
	
	@Test(expected=TimeoutException.class)
	public void testSimple() throws InterruptedException{
		try {
			TimeoutRegister.set(new StandardTimeout(3, new MockCurrentTime()));
			doStuff();
		} finally {
			TimeoutRegister.remove();
		}
	}
	
	private static void doStuff() throws InterruptedException{
		for(int i = 0; i < 10; i++){
			TimeoutRegister.get().check();
		}
	}
	
	@Test
	public void testRemoveTimeout() throws InterruptedException{
		try {
			TimeoutRegister.set(new StandardTimeout(3, new MockCurrentTime()));
			TimeoutRegister.get().check();
			TimeoutRegister.get().check();
			TimeoutRegister.remove();
			TimeoutRegister.get().check();
			TimeoutRegister.get().check();
			TimeoutRegister.get().check();
			TimeoutRegister.get().check();
			TimeoutRegister.get().check();
		} finally {
			TimeoutRegister.remove();
		}		
	}
	
	@Test(expected=TimeoutException.class)
	public void testSetAndRemoveAndSetTimeoutAgain() throws InterruptedException{
		try {
			TimeoutRegister.set(new StandardTimeout(5, new MockCurrentTime()));
			TimeoutRegister.get().check();
			TimeoutRegister.get().check();
			TimeoutRegister.remove();
			TimeoutRegister.set(new StandardTimeout(2, new MockCurrentTime()));
			TimeoutRegister.get().check();
			TimeoutRegister.get().check();
			TimeoutRegister.get().check();
		} finally {
			TimeoutRegister.remove();
		}	
	}
}

/** Mock version of {@link CurrentTime}. Consecutive numbers starting from 0 
 * are returned each time the {@link #get()} method is called.
 * 
 * @author Mateusz Kobos
 *
 */
class MockCurrentTime implements CurrentTime {
	private long currTime = -1;

	@Override
	public long get() {
		currTime++;
		return currTime;
	}
}
