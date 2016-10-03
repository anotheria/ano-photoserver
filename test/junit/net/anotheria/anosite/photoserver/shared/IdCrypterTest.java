package net.anotheria.anosite.photoserver.shared;

import org.junit.Test;

import java.util.concurrent.CountDownLatch;

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
		for (int i = 0; i<THREADS; i++){
			if (i%2==1)
				new TestWorker(C1, R1, start, finish).start();
			else
				new TestWorker(C2, R2, start, finish).start();
		}
		start.countDown();
		try {
			finish.await();
		}catch(Exception any){
			any.printStackTrace();
		}
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


		TestWorker(String aC, long aR, CountDownLatch start, CountDownLatch finish){
			C = aC;
			R = aR;
			this.start = start;
			this.finish = finish;
		}

		public void run(){
			try{
				start.await();
				for (int i=0; i<1000000; i++){
					try {
						long r = IdCrypter.decodeToLong(C);
						if (r != R) {
							mismatch++;
						}
					}catch(Exception any){
						//any.printStackTrace();
						mismatch++;
					}
				}
				System.out.println("Finished testrun with "+mismatch+" mismatches");
				finish.countDown();
			}catch(InterruptedException e){}
		}

	}
}
