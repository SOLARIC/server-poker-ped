package poker.server.model.game.timer;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TestChrono {

	@Test
	public void testChrono() {

		Chrono chrono = new Chrono(30);
		Thread th = new Thread(chrono);
		th.start();
		wait(6);
		assertEquals(true, chrono.isRunning());
	}

	private void wait(int seconds) {

		long start = System.currentTimeMillis();
		long end = start + seconds * 1000; // seconds * 1000 ms/sec
		while (System.currentTimeMillis() < end)
			;
	}
}
