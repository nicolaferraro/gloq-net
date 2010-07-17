package org.ppi.common.execute;

public interface ExecutionObserver {
	
	public void executionStarted();
	
	public void executionCompleted();
	
	public void executionInterrupted();
	
	public void executionFailed();
	
	public void percentExecuted(int percent);
	
	public void percentageUnavailable();
	
	public void currentOperation(String operation);
	
}
