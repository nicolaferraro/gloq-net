package org.ppi.core.model;

import org.ppi.core.dictionary.Dictionary;

public abstract class State {

	public abstract double outputProbability(Symbol s, Dictionary d);
	
	public abstract String getName();
	
	@Override
	public String toString() {
		return getName();
	}
}
