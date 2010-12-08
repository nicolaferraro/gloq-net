package org.ppi.common.execute;

public interface PartialResultObserver<R> {
	
	public void partialResultComputed(R partialResult);
	
}
