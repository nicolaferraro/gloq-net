package org.ppi.core.dictionary;

import java.util.List;

import org.ppi.core.graph.Node;
import org.ppi.preference.Preferences;


public class DictionaryHelper {

	protected static DictionaryHelper instance;
	
	protected DictionaryHelper() {
	}
	
	public static DictionaryHelper getInstance() {
		if(instance==null)
			instance = new DictionaryHelper();
		return instance;
	}
	
	public double getSimilarity(List<Node> masterNodes, Node slaveNode, Dictionary dict) {
		
		int defined = 0;
		double sum = 0d;
		double max = Double.NEGATIVE_INFINITY;
		
		for(Node n : masterNodes) {
			double val = dict.getSimilarity(n.getName(), slaveNode.getName());
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
