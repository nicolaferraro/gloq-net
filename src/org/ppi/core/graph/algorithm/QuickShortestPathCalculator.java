package org.ppi.core.graph.algorithm;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.ppi.core.graph.Graph;
import org.ppi.core.graph.Node;

public class QuickShortestPathCalculator implements ShortestPathCalculator {

	@Override
	public boolean isDistantLower(Set<Node> source, Node destination, int max, Graph g) {
		if(max<0)
			throw new IllegalArgumentException("max distance should be greater than 0");
		
		if(source.contains(destination))
			return true;
		
		if(max==0)
			return false;
		
		Map<Node, Integer> visited = new HashMap<Node, Integer>();
		Queue<Node> queue = new LinkedList<Node>();
		
		for(Node n : source) {
			visited.put(n, 0);
			queue.add(n);
		}
		
		while(queue.size()>0) {
			Node n = queue.poll();
			int dist = visited.get(n);
			
			if(dist+1>max)
				return false;
			
			Set<Node> adj = g.getAdjacent(n);
			for(Node a : adj) {
				if(a.equals(destination))
					return true;
				
				if(visited.containsKey(a))
					continue;
				
				if(dist+1<max) {
					visited.put(a, dist + 1);
					queue.add(a);
				}
			}
		}
		
		return false;
	}
	
	
}
