package cs681;

import java.util.LinkedList;

// Generates prime factors of a given number (dividend)
// Prime factors are generated in the range of 2 and Math.sqrt(dividend)

public class ConcurrentPrimeFactorizer {
	private LinkedList<Long> factors = new LinkedList<Long>();
	private LinkedList<RunnablePrimeFactorizer> runnables = new LinkedList<RunnablePrimeFactorizer>();
	private LinkedList<Thread> threads = new LinkedList<Thread>();

	public ConcurrentPrimeFactorizer(long dividend, int nThreads) {
		System.out.println("The number to be factorized: " + dividend);
		long upperLimit; 	// Upper limit for the range of divisors
		long dividedRange;	// Range of divisors that each thread works on
		
		// Decrease nThreads if it is too many to factorize dividend. 
		while (true) {
			upperLimit = (long) Math.sqrt(dividend) + 1;
			dividedRange = upperLimit / nThreads;
			if (dividedRange > 2) {
				break;
			} else {
				nThreads--;
			}
		}
		System.out.println("The number of threads: " + nThreads);
		
		RunnablePrimeFactorizer runnable;
		Thread thread;
		long fromLocal = 2;
		long toLocal = dividedRange;
		for (int i = 0; i < nThreads; i++) {
			runnable = new RunnablePrimeFactorizer(dividend, fromLocal, toLocal);
			runnables.add(runnable);
			fromLocal = toLocal + 1;
			toLocal = fromLocal + dividedRange;
			if(i == nThreads-2) {toLocal = upperLimit;}

			thread = new Thread(runnable);
			threads.add(thread);
			System.out.println("Thread ID: " + thread.getId() + 
								", Range of divisors: " + runnable.getFrom() + "->" + runnable.getTo());
		}
	}

	public ConcurrentPrimeFactorizer(long dividend) {
		this(dividend, Runtime.getRuntime().availableProcessors());
	}

	public LinkedList<Long> getPrimeFactors() {
		threads.forEach((thread) -> thread.start());
		threads.forEach((thread) -> {
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		});
		runnables.forEach((runnable) -> factors.addAll(runnable.getPrimeFactors()));
		return factors;
	}

	private class RunnablePrimeFactorizer extends PrimeFactorizer implements Runnable {

		public RunnablePrimeFactorizer(long dividend, long from, long to) {
			super(dividend);
			if (from >= 2 && to >= from) {
				this.from = from;
				this.to = to;
			} else {
				throw new RuntimeException(
						"from must be >= 2, and to must be >= from." + "from==" + from + " to==" + to);
			}
		}
		
		public long getFrom() {
			return from;
		}
		
		public long getTo() {
			return to; 
		}
		
		protected boolean isEven(long n){
			if(n%2 == 0){ return true; }
			else{ return false; }
		}

		public void generatePrimeFactors() {
			long divisor = from;
		    while( dividend != 1 && divisor <= to ){
		    	if( divisor > 2 && isEven(divisor)) {
		    		divisor++;
		    		continue;
		    	}
			    if(dividend % divisor == 0) {
			        factors.add(divisor);
			        dividend /= divisor;
			    }else {
			    	if(divisor==2){ divisor++; }
			    	else{ divisor += 2; }
			    	
			    }
			}
		}

		public void run() {
			generatePrimeFactors();
			System.out.println("Thread #" + Thread.currentThread().getId() + " generated " + factors);
		}
	}

	public static void main(String[] args) {
		ConcurrentPrimeFactorizer fac = new ConcurrentPrimeFactorizer(36, 2);
		System.out.println("Final result: " + fac.getPrimeFactors());

		fac = new ConcurrentPrimeFactorizer(84, 4);
		System.out.println("Final result: " + fac.getPrimeFactors());

//		System.out.println(Runtime.getRuntime().availableProcessors());
		fac = new ConcurrentPrimeFactorizer(125);
		System.out.println("Final result: " + fac.getPrimeFactors());

		fac = new ConcurrentPrimeFactorizer(2489);
		System.out.println("Final result: " + fac.getPrimeFactors());
		
		fac = new ConcurrentPrimeFactorizer(8633);
		System.out.println("Final result: " + fac.getPrimeFactors());
	}

}
