package org.ppi.core.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ppi.preference.Preferences;


public class Model {
	
	State beginState;
	Map<State, Map<State, TransitionType>> transitions;
	
	public Model() {
		transitions = new HashMap<State, Map<State,TransitionType>>();
	}
	
	public void setBeginState(State s) {
		beginState = s;
		add(s);
	}
	
	public void add(State s) {
		if(!transitions.containsKey(s))
			transitions.put(s, new HashMap<State, TransitionType>());
	}
	
	public void setTransitionType(State s1, State s2, TransitionType t) {
		transitions.get(s1).put(s2, t);
	}
	
	public double getTransitionProbability(State s1, State s2) {
		TransitionType t = transitions.get(s1).get(s2);
		if(t!=null) {
			switch (t) {
			case MATCHING_TRANSITION:
				return Preferences.getInstance().getMatchingTransition();
			case FAILING_TRANSITION:
				return Preferences.getInstance().getFailingTransition();
			default:
				throw new RuntimeException("Undefined transition type");
			}
		}
		
		return 0;
	}
	
	public Map<State, TransitionType> getTransitions(State s) {
		return transitions.get(s);
	}
	
	public State getBeginState() {
		return beginState;
	}
	
	public List<State> getStates() {
		List<State> states = new ArrayList<State>();
		for(State s : transitions.keySet()) {
			states.add(s);
		}
		return states;
	}
}
