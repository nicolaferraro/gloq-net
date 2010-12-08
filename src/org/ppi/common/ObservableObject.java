package org.ppi.common;

import java.util.HashSet;
import java.util.Set;

public class ObservableObject implements Observable {

	Set<Observer> observers;
	Object source;
	
	public ObservableObject(Object source) {
		this.observers = new HashSet<Observer>();
		if(source==null) source = this;
		this.source = source;
	}
	
	public ObservableObject() {
		this(null);
	}
	
	@Override
	public void addObserver(Observer obs) {
		observers.add(obs);
	}
	
	@Override
	public void removeObserver(Observer obs) {
		observers.remove(obs);
	}
	
	public void notifyObservers() {
		for(Observer o : observers) {
			o.notifyChanged(source);
		}
	}
	
}
