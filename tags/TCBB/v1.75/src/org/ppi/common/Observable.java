package org.ppi.common;

public interface Observable {

	public void addObserver(Observer obs);
	
	public void removeObserver(Observer obs);
	
}
