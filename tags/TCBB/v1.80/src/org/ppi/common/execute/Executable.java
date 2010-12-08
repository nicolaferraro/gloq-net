package org.ppi.common.execute;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;

public abstract class Executable<R> implements Runnable {

	public static final int READY = 0, RUNNING = 1, COMPLETED = 2, ERROR = 3;
	
	protected Logger logger;
	
	boolean interruptedByUser;

	protected R result;

	List<ExecutionObserver> observers;

	Thread runningThread;

	int status;

	Lock lock;
	Condition completion;

	public Executable() {
		this.status = READY;

		lock = new ReentrantLock(true);
		completion = lock.newCondition();

		observers = new LinkedList<ExecutionObserver>();
		interruptedByUser = false;
		logger = Logger.getLogger(this.getClass());
	}

	public void addExecutionObserver(ExecutionObserver observer) {
		this.observers.add(observer);
	}
	
	public void launch() {
		try {
			lock.lock();

			if (status != READY)
				throw new IllegalStateException("Already started");

			status = RUNNING;

		} finally {
			lock.unlock();
		}

		runSynch(new ObserverCall() {
			@Override
			public void run(ExecutionObserver observer) {
				observer.executionStarted();
			}
		});

		runningThread = new Thread(this);

		runningThread.start();

	}
	
	@Override
	public void run() {
		try {
			
			logger.debug("Starting execution");
			
			execute();

			logger.debug("Execution completed");
			
			try {
				lock.lock();
				status = COMPLETED;

			} finally {
				completion.signalAll();
				lock.unlock();
			}

			runSynch(new ObserverCall() {
				@Override
				public void run(ExecutionObserver observer) {
					observer.executionCompleted();
				}
			});

		} catch (Exception ex) {
			
			logger.error("Execution error", ex);
			
			try {

				lock.lock();

				status = ERROR;
			} finally {
				completion.signalAll();
				lock.unlock();
			}

			if (Thread.currentThread().isInterrupted()) {
				interruptedByUser = true;
				runSynch(new ObserverCall() {
					@Override
					public void run(ExecutionObserver observer) {
						observer.executionInterrupted();
					}
				});
			} else {
				runSynch(new ObserverCall() {
					@Override
					public void run(ExecutionObserver observer) {
						observer.executionFailed();
					}
				});
			}

		}
	}
	
	public boolean wasInterruptedByUser() {
		return interruptedByUser;
	}
	
	protected void signalPercentageUnavailable() {
		
		runSynch(new ObserverCall() {
			@Override
			public void run(ExecutionObserver observer) {
				observer.percentageUnavailable();
			}
		});
	}
	
	protected void signalPercentExecuted(int perc) {
		
		perc = Math.max(perc, 0);
		perc = Math.min(perc, 100);
		
		final int finalPerc = perc;

		runSynch(new ObserverCall() {
			@Override
			public void run(ExecutionObserver observer) {
				observer.percentExecuted(finalPerc);
			}
		});
	}
	
	protected void signalCurrentOperation(String op) {
		
		logger.info("Current operation: " + op);
		
		final String finalOp = op;

		runSynch(new ObserverCall() {
			@Override
			public void run(ExecutionObserver observer) {
				observer.currentOperation(finalOp);
			}
		});
	}

	protected void checkPoint() {
		if (Thread.currentThread().isInterrupted()) {
			throw new RuntimeException("Interrupted by user");
		}
	}
	
	protected void launchSubProcedure(Executable<? extends Object> sub) throws Exception {
		observe(sub);
		sub.run();
		signalPercentageUnavailable();
	}
	
	private void observe(Executable<? extends Object> subEx) {
		subEx.addExecutionObserver(new ExecutionObserver() {
			
			@Override
			public void percentExecuted(int percent) {
				signalPercentExecuted(percent);
			}
			
			@Override
			public void percentageUnavailable() {
				signalPercentageUnavailable();
			}
			
			@Override
			public void executionStarted() {
				
			}
			
			@Override
			public void executionInterrupted() {
				
			}
			
			@Override
			public void executionFailed() {
				
			}
			
			@Override
			public void executionCompleted() {
				
			}
			
			@Override
			public void currentOperation(String operation) {
				signalCurrentOperation(operation);
			}
		});
	}

	public void waitForCompletion() {
		try {
			lock.lock();

			while (status != COMPLETED && status != ERROR) {
				completion.await();
			}

		} catch (InterruptedException ex) {
			Thread.currentThread().interrupt();
			ex.printStackTrace();
		} finally {
			lock.unlock();
		}
	}

	public void interrupt() {
		runningThread.interrupt();
	}

	public boolean needWaiting() {
		try {
			lock.lock();

			return status != COMPLETED && status != ERROR;

		} finally {
			lock.unlock();
		}
	}

	public boolean hasCompleted() {
		try {
			lock.lock();

			return status == COMPLETED;

		} finally {
			lock.unlock();
		}

	}

	public R getResult() {
		if (status != COMPLETED)
			throw new IllegalStateException("Result not computed");

		return result;
	}

	protected abstract void execute() throws Exception;

	private abstract static class ObserverCall {

		public abstract void run(ExecutionObserver observer);

	}

	private void runSynch(ObserverCall call) {
		for (ExecutionObserver obs : observers) {
			call.run(obs);
		}
	}

}
