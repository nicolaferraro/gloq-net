package org.ppi.core.graph;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class Graph {
	
	String name;
	Map<Node, HashSet<Node>> graph;
	
	public Graph() {
		this("Graph-"+UUID.randomUUID());
	}
	
	public Graph(String name) {
		this.name = name;
		this.graph = new HashMap<Node, HashSet<Node>>();
	}
	
	public Graph(Graph copy) {
		this.name = copy.name;
		this.graph = new HashMap<Node, HashSet<Node>>();
		for(Map.Entry<Node, HashSet<Node>> e : copy.graph.entrySet()) {
			this.graph.put(e.getKey(), new HashSet<Node>(e.getValue()));
		}
	}
	
	public void addNode(Node n) {
		if(graph.containsKey(n))
			throw new IllegalArgumentException("Already present");
		graph.put(n, new HashSet<Node>());
	}
	
	public void addEdge(Node n1, Node n2) {
		if(!graph.containsKey(n1)) {
			addNode(n1);
		}
		
		if(!graph.containsKey(n2)) {
			addNode(n2);
		}
		
		if(n1==n2)
			return;
		graph.get(n1).add(n2);
		graph.get(n2).add(n1);
	}
	
	public void deleteNode(Node n) {
		if(!graph.containsKey(n))
			throw new IllegalArgumentException("Unknown node: " + n);
		HashSet<Node> adjs = graph.remove(n);
		for(Node adj : adjs) {
			graph.get(adj).remove(n);
		}
	}
	
	public void deleteEdge(Node n1, Node n2) {
		if(!graph.containsKey(n1) || !graph.containsKey(n2))
			throw new IllegalArgumentException("Unknown nodes: "+ n1 +", "+n2);
		graph.get(n1).remove(n2);
		graph.get(n2).remove(n1);
	}
	
	public Set<Node> getNodes() {
		return Collections.unmodifiableSet(graph.keySet());
	}
	
	public Set<Node> getAdjacent(Node n) {
		if(!graph.containsKey(n))
			throw new IllegalArgumentException("Unknown node: " + n);
		return Collections.unmodifiableSet(graph.get(n));
	}
	
	public boolean areAdjacent(Node n1, Node n2) {
		if(!graph.containsKey(n1) || !graph.containsKey(n2))
			throw new IllegalArgumentException("Unknown nodes: " + n1 + ", "+n2);
		return graph.get(n1).contains(n2);
	}
	
	@Override
	public String toString() {
		return name;
	}

	public String getName() {
		return name;
	}
	
}
