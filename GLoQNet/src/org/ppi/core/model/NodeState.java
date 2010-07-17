package org.ppi.core.model;

import java.util.List;

import org.ppi.core.dictionary.Dictionary;
import org.ppi.core.graph.Node;
import org.ppi.preference.Preferences;


public class NodeState extends State {
	
	List<Node> nodes;
	int level;
	
	public NodeState(List<Node> nodes, int level) {
		this.nodes=nodes;
		this.level=level;
	}
	
	public List<Node> getNodes() {
		return nodes;
	}
	
	public int getLevel() {
		return level;
	}
	
	@Override
	public String getName() {
		String name = "(";
		int i=0;
		for(Node n : nodes) {
			name+=n.getName();
			i++;
			if(i<nodes.size())
				name+=", ";
		}
		return name+"["+level+"])";
	}
	
	@Override
	public double outputProbability(Symbol s, Dictionary dict) {
		
		if(s.getLevel()!=this.getLevel())
			return 0d;
		
		int defined = 0;
		double sum = 0d;
		double max = Double.NEGATIVE_INFINITY;
		
		for(Node n : nodes) {
			double val = dict.getSimilarity(n.getName(), s.getNode().getName());
			if(val>0) {
				sum+=val;
				defined++;
				max = Math.max(max, val);
			}
		}
		
		switch(Preferences.getInstance().getSimilarityStrategy()) {
		case MAX:
			return Math.max(0d, max);
		case AVG:
			return (defined>0) ? sum/defined : 0d;
		default:
			throw new RuntimeException("Undefined Strategy");
		}
		
	}

}