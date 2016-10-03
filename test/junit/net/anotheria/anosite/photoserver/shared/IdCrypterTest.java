package net.anotheria.anosite.photoserver.shared;

import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.Assert.assertEquals;

/**
 * TODO comment this class
 *
 * @author lrosenberg
 * @since 03/10/2016 12:59
 */
public class IdCrypterTest {

	private static final String C1 = "521734CCC663EF4505FEF3FD30490C3B";
	private static final String C2 = "AD7FABDA069C29C86750CC70877D8DB5";
	private static final long R1 = 464;
	private static final long R2 = 1742;

	//executes a single test for correctness.
	@Test
	public void testSingle(){
		assertEquals(R1, IdCrypter.decodeToLong(C1));
		assertEquals(R2, IdCrypter.decodeToLong(C2));
	}

	@Test
	public void testConcurrently(){
		int THREADS = 10;
		CountDownLatch start = new CountDownLatch(1);
		CountDownLatch finish = new CountDownLatch(THREADS);
		AtomicLong errorCounter = new AtomicLong(0);
		for (int i = 0; i<THREADS; i++){
			if (i%2==1)
				new TestWorker(C1, R1, start, finish, errorCounter).start();
			else
				new TestWorker(C2, R2, start, finish, errorCounter).start();
		}
		start.countDown();
		try {
			finish.await();
		}catch(Exception any){
			any.printStackTrace();
		}
		assertEquals(0, errorCounter.get());
	}

	class TestWorker extends Thread{
		/**
		 * Chiffre
		 */
		private String C;
		/**
		 * Expected result
		 */
		private long R;

		private CountDownLatch start, finish;

		int mismatch = 0;

		private AtomicLong errorCounter;


		TestWorker(String aC, long aR, CountDownLatch start, CountDownLatch finish, AtomicLong errorCounter){
			C = aC;
			R = aR;
			this.start = start;
			this.finish = finish;
			this.errorCounter = errorCounter;
		}

		public void run(){
			try{
				start.await();
				for (int i=0; i<100000; i++){
					try {
						long r = IdCrypter.decodeToLong(C);
						if (r != R) {
							mismatch++;
							errorCounter.incrementAndGet();
						}
					}catch(Exception any){
						//any.printStackTrace();
						mismatch++;
						errorCounter.incrementAndGet();
					}
				}
				System.out.println("Finished testrun with "+mismatch+" mismatches");
				finish.countDown();
			}catch(InterruptedException e){}
		}

	}
}
