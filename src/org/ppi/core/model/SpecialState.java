package org.ppi.core.model;

import org.ppi.core.dictionary.Dictionary;
import org.ppi.preference.Preferences;

public class SpecialState extends State {

	String name;
	
	public SpecialState(String name) {
		this.name = name;
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public double outputProbability(Symbol s, Dictionary d) {
		return Preferences.getInstance().getSpecialStatesEmission();
	}
	
}
