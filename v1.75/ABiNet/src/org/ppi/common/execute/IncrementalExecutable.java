package org.ppi.common.execute;

import java.util.LinkedList;
import java.util.List;

public abstract class IncrementalExecutable<R,P> extends Executable<R> {
	
	List<PartialResultObserver<P>> resultObserver;
	
	public IncrementalExecutable() {
		super();
		
		this.resultObserver = new LinkedList<PartialResultObserver<P>>();
	}
	
	public void addPartialResultObserver(PartialResultObserver<P> observer) {
		this.resultObserver.add(observer);
	}
	
	protected void signalPartialResult(P pRes) {
		for(PartialResultObserver<P> obs : resultObserver) {
			obs.partialResultComputed(pRes);
		}
	}
	
}
