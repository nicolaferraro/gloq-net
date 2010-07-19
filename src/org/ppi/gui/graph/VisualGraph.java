package org.ppi.gui.graph;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.ppi.core.graph.Graph;
import org.ppi.core.graph.Node;

public class VisualGraph {

	Graph graph;
	Map<Node, Point> positions;
	List<Node> zOrder;
	Map<Node, Node> additionalLinks;
	
	public VisualGraph() {
		graph = new Graph();
		positions = new HashMap<Node, Point>();
		zOrder = new ArrayList<Node>();
		additionalLinks = new HashMap<Node, Node>();
	}

	public Graph getGraph() {
		return graph;
	}

	public void setGraph(Graph graph) {
		this.graph = graph;
	}

	public Map<Node, Point> getPositions() {
		return positions;
	}

	public void setPositions(Map<Node, Point> positions) {
		this.positions = positions;
	}

	public List<Node> getzOrder() {
		return zOrder;
	}

	public void setzOrder(List<Node> order) {
		zOrder = order;
	}
	
	public void setAdditionalLinks(Map<Node, Node> additionalLinks) {
		this.additionalLinks = additionalLinks;
	}
	
	public Map<Node, Node> getAdditionalLinks() {
		return additionalLinks;
	}
	
	public void changeNodeName(Node n, String toName) {
		Node nn = new Node(toName);
		
		int pos = 0;
		for(Node nod : zOrder) {
			if(nod.equals(n)) {
				break;
			}
			pos++;
		}
		
		if(pos>=zOrder.size())
			throw new RuntimeException("Not present");
		
		zOrder.set(pos, nn);
		
		Point point = positions.remove(n);
		positions.put(nn, point);
		
		for(Entry<Node, Node> e : additionalLinks.entrySet()) {
			if(e.getValue().equals(n))
				e.setValue(nn);
		}
		
		Node dst = additionalLinks.remove(n);
		if(dst!=null)
			additionalLinks.put(nn, dst);
		
		Set<Node> adj = new HashSet<Node>(graph.getAdjacent(n));
		graph.deleteNode(n);
		graph.addNode(nn);
		for(Node a : adj) {
			graph.addEdge(nn, a);
		}
		
	} 
	
}
