package org.ppi.core.model;

import org.ppi.core.dictionary.Dictionary;

public class MatchedNodeState extends State {
	
	Symbol uniqueEmission;
	
	public MatchedNodeState(Symbol uniqueEmission) {
		this.uniqueEmission = uniqueEmission;
	}
	
	@Override
	public String getName() {
		return "Matched-"+uniqueEmission.getNode().getName();
	}
	
	@Override
	public double outputProbability(Symbol s, Dictionary d) {
		if(s.equals(uniqueEmission))
			return 1;
		return 0;
	}
	
	

}