package org.ppi.core;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.ppi.common.execute.Executable;
import org.ppi.core.dictionary.Dictionary;
import org.ppi.core.graph.Graph;
import org.ppi.core.graph.Node;

public class GraphCleaner extends Executable<List<Graph>> {

	List<Graph> graphs;
	Dictionary dict;

	public GraphCleaner(List<Graph> graphs, Dictionary dict) {
		this.graphs = graphs;
		this.dict = dict;
	}

	@Override
	protected void execute() throws Exception {

		signalCurrentOperation("Cleaning the networks");
		
		List<Graph> nLst = new ArrayList<Graph>();
		
		for (int i=0; i<graphs.size(); i++) {
			
			Graph graph = graphs.get(i);
			
			Set<Node> toRemove = new HashSet<Node>();
			for (Node n : graph.getNodes()) {
				if (!dict.containsNode(n.getName())) {
					toRemove.add(n);
				}
				
				checkPoint();
			}
			
			Graph nGraph = graph;
			
			if (toRemove.size() > 0) {
				Graph res = new Graph(graph);

				for (Node n : toRemove) {
					res.deleteNode(n);
				}

				nGraph = res;
			}
			
			nLst.add(i, nGraph);
			
			checkPoint();
		}
		
		checkPoint();
		
		result = nLst;
	}

}
