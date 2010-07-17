package org.ppi.core.algorithm;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.ppi.common.execute.IncrementalExecutable;
import org.ppi.common.result.Matching;
import org.ppi.common.util.LoggingUtil;
import org.ppi.core.GraphCleaner;
import org.ppi.core.dictionary.Dictionary;
import org.ppi.core.graph.Graph;
import org.ppi.core.graph.Node;
import org.ppi.core.matching.FindSubgraph;


public class GlobalMatching extends IncrementalExecutable<Set<Matching>, Set<Matching>> {

	List<Graph> graphs;
	Dictionary dict;
	int depth;
	
	public GlobalMatching(List<Graph> graphs, Dictionary dict, int depth) {
		this.graphs = graphs;
		this.dict = new Dictionary(dict); // copy
		this.depth = depth;
	}
	
	@Override
	protected void execute() throws Exception {
		
		signalCurrentOperation("Computing global matching");
		
		Set<Matching> alignment = new HashSet<Matching>();
		
		Set<Matching> partial;
		
		do {
			
			GraphCleaner cleaner = new GraphCleaner(graphs, dict);
			launchSubProcedure(cleaner);
			
			if(!cleaner.hasCompleted())
				throw new Exception("Error");
			
			graphs = cleaner.getResult();
			
			FindSubgraph finder = new FindSubgraph(dict, graphs, depth);
			launchSubProcedure(finder);
			
			if(!finder.hasCompleted())
				throw new Exception("Error");
			
			partial = finder.getResult();
			
			if(partial.size()>0) {
				
				LoggingUtil.logResult(partial, logger);
				
				alignment.addAll(partial);
				signalPartialResult(partial);
				
				for(Matching m : partial) {
					for(int i=0; i<graphs.size(); i++) {
						Node n = m.getNodeList().get(i);

						dict.removeNode(n.getName());
						
						Set<Node> neighs = graphs.get(i).getAdjacent(n);
						for(Node nei : neighs) {
							dict.removeNode(nei.getName());
						}
					}
				}
				
			}
			
			checkPoint();
		} while(partial.size()>0);
		
		
	}
	
}
