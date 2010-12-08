package org.ppi.core.model;

import org.ppi.core.dictionary.Dictionary;
import org.ppi.preference.Preferences;

public class EscapeState extends State {

	int minLevel;
	
	public EscapeState(int minLevel) {
		this.minLevel = minLevel;
	}
	
	@Override
	public String getName() {
		return "Escape-"+minLevel;
	}
	
	@Override
	public double outputProbability(Symbol s, Dictionary d) {
		return Preferences.getInstance().getEscapeStatesEmission();
	}
	
}
