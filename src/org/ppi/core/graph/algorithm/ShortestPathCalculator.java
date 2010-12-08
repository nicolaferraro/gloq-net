package org.ppi.core.graph.algorithm;

import java.util.Set;

import org.ppi.core.graph.Graph;
import org.ppi.core.graph.Node;

public interface ShortestPathCalculator {

	public boolean isDistantLower(Set<Node> source, Node destination, int max, Graph g);
	
}
