package org.ppi.gui.graph;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ppi.common.execute.Executable;
import org.ppi.common.result.Matching;
import org.ppi.core.graph.Graph;
import org.ppi.core.graph.Node;
import org.ppi.core.graph.NodeFactory;

public class GraphMerger extends Executable<VisualGraph> {
	
	protected static final int MEAN_DISTANCE = 150;
	protected static final int MAX_WIDTH = 400;
	protected static final int MARGIN = 100;
	protected static final int VERTICAL_SPACING = 100;
	protected static final String SEPARATOR = ":";
	protected static final int ITERATIONS = 200;
	
	List<Graph> graphs;
	Set<Matching> alignment;

	public GraphMerger(List<Graph> graphs, Set<Matching> alignment) {
		this.graphs = graphs;
		this.alignment = alignment;
	}

	@Override
	protected void execute() throws Exception {
		
		signalCurrentOperation("Creating the result");
		
		VisualGraph visualGraph = new VisualGraph();
		
		List<Set<Node>> nodes = new ArrayList<Set<Node>>();
		
		for(int i=0; i<graphs.size(); i++) {
			nodes.add(new HashSet<Node>());
		}
		
		for(Matching m : alignment) {
			for(int i=0; i<m.getNodeList().size(); i++) {
				nodes.get(i).add(m.getNodeList().get(i));
			}
		}
		
		
		List<Map<Node, Node>> nodeMap = new ArrayList<Map<Node, Node>>();
		
		for(int i=0; i<graphs.size(); i++) {
			nodeMap.add(new HashMap<Node, Node>());
		}
		
		for(int i=0; i<graphs.size(); i++) {
			for(Node n : nodes.get(i)) {
				Node nn = NodeFactory.getInstance().createNode((i+1) + SEPARATOR + n.getName());
				visualGraph.getGraph().addNode(nn);
				nodeMap.get(i).put(n, nn);
			}
			
			for(Node n : nodes.get(i)) {
				for(Node n2 : graphs.get(i).getAdjacent(n)) {
					if(nodes.get(i).contains(n2)) {
						visualGraph.getGraph().addEdge(nodeMap.get(i).get(n), nodeMap.get(i).get(n2));
					}
				}
			}
		}
		
		// Computing zOrders - random
		for(Node n : visualGraph.getGraph().getNodes()) {
			visualGraph.getzOrder().add(n);
		}
		
		// Computing additional links
		for(Matching m : alignment) {
			for(int i=0; i<m.getNodeList().size()-1; i++) {
					Node n1 = nodeMap.get(i).get(m.getNodeList().get(i));
					Node n2 = nodeMap.get(i+1).get(m.getNodeList().get(i+1));
					visualGraph.getAdditionalLinks().put(n1, n2);
			}
		}
		
		
		// Dividing first network in components
		Set<Node> no = nodes.get(0);
		Collection<Set<Node>> compno = findConnectedModules(no, graphs.get(0));
		
		// Trasformazione in visual
		List<Set<Node>> visualCompno = new ArrayList<Set<Node>>();
		
		for(Set<Node> s : compno) {
			Set<Node> vs = new HashSet<Node>();
			for(Node n : s) {
				vs.add(nodeMap.get(0).get(n));
			}
			visualCompno.add(vs);
		}
		
		// Ordinamento per dimensione crescente dei moduli
		Collections.sort(visualCompno, new SizeComparator());
			
		Map<Node, Point> bestPos = computeModulesPositions(visualCompno, visualGraph, graphs);
		
		visualGraph.setPositions(bestPos);
		
		result = visualGraph;
	}
	
	protected Collection<Set<Node>> findConnectedModules(Set<Node> nodes, Graph g) {
		
		Set<Set<Node>> conn = new HashSet<Set<Node>>();
		
		Set<Node> usedNodes = new HashSet<Node>();
		
		do {
			Set<Node> s = new HashSet<Node>();
			
			List<Node> toCheck = new ArrayList<Node>(nodes);
			toCheck.removeAll(usedNodes);
			
			s.add(toCheck.get(0));
			
			boolean ins;
			do {
				ins = false;
				for(Node n : toCheck) {
					if(!s.contains(n)) {
						for(Node ns : s) {
							if(g.areAdjacent(ns, n)) {
								s.add(n);
								ins = true;
								break;
							}
						}
					}
				}
				
			} while(ins);
			
			
			
			conn.add(s);
			usedNodes.addAll(s); 
			
		} while(usedNodes.size()<nodes.size());
		
		return conn;
		
	}
	
	protected Map<Node, Point> computeModulesPositions(Collection<Set<Node>> firstDivision, VisualGraph visualGraph, List<Graph> graphs) {
		Map<Node, Point> res = new HashMap<Node, Point>();
		
		int vOffset = 0; 
		
		for(Set<Node> s : firstDivision) {
			List<Set<Node>> nodes = new ArrayList<Set<Node>>();
			nodes.add(s);
			while(nodes.size()<graphs.size()) {
				Set<Node> lastNodes = nodes.get(nodes.size()-1);
				Set<Node> nextNodes = new HashSet<Node>();
				for(Node n : lastNodes) {
					nextNodes.add(visualGraph.getAdditionalLinks().get(n));
				}
				nodes.add(nextNodes);
			}
			
			Map<Node, Point> pos = computePositions(nodes, visualGraph);
			
			int maxY = Integer.MIN_VALUE;
			for(Point p : pos.values()) {
				
				p.y += vOffset;
				
				if(p.y>maxY) {
					maxY = p.y;
				}
			}
			
			vOffset = maxY + VERTICAL_SPACING;
			
			res.putAll(pos);
		}
		
		return res;
	}
	
	protected Map<Node, Point> computePositions(List<Set<Node>> nodes, VisualGraph visualGraph) {

		// Computing positions
		Map<Node, Point> bestPos = null;
		double max = Double.NEGATIVE_INFINITY;
		for(int i=0; i<ITERATIONS; i++) {
			
			// Compute positions
			Map<Node, Point> positions = new HashMap<Node, Point>();
			
			for(Node n : nodes.get(0)) {
				int x = (int)(MARGIN + Math.random()*MAX_WIDTH);
				int y = (int)(MARGIN + Math.random()*(MEAN_DISTANCE * nodes.get(0).size()));
				positions.put(n, new Point(x, y));
			}
			
			double val = evaluate(positions, visualGraph);
			
			// add other nodes in the same position with offsets
			for(int j=0; j<nodes.size()-1; j++) {
				for(Node n : nodes.get(j)) {
					Node nn = visualGraph.getAdditionalLinks().get(n);
					Point nPos = positions.get(n);
					Point nnPos = new Point(nPos.x+MAX_WIDTH+MARGIN, nPos.y);
					positions.put(nn, nnPos);
				}
			}
			// End of computation
		
			if(bestPos==null || val>max) {
				bestPos = positions;
				max = val;
			}
		}
		logger.debug("Module visualization evaluation: " + max);
		return bestPos;
	}

	protected double evaluate(Map<Node, Point> positions, VisualGraph visualGraph) {
		double sum = 0;
		int count = 0;
		for(Point p1 : positions.values()) {
			for(Point p2 : positions.values()) {
				if(p1==p2)
					continue;
				
				sum+=distance(p1, p2);
				count++;
			}
		}
		return sum/count;
	}
	
	protected double distance(Point p1, Point p2) {
		
		if(p1.x==p2.x)
			return Math.abs(p1.y-p2.y);
		
		if(p1.y==p2.y)
			return Math.abs(p1.x-p2.x);
		
		return Math.sqrt(Math.pow(p1.x-p2.x, 2) + Math.pow(p1.y-p2.y, 2));
	}
	
}

class SizeComparator implements Comparator<Collection<?>> {
	
	@Override
	public int compare(Collection<?> o1, Collection<?> o2) {
		return o2.size()-o1.size();
	}
}
