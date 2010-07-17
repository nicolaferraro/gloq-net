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

public class LocalMatching extends IncrementalExecutable<Set<Set<Matching>>, Set<Matching>> {

	List<Graph> graphs;
	Dictionary dict;
	int depth;
	
	public LocalMatching(List<Graph> graphs, Dictionary dict, int depth) {
		this.graphs = graphs;
		this.dict = new Dictionary(dict); // copy
		this.depth = depth;
	}
	
	@Override
	protected void execute() throws Exception {
		
		signalCurrentOperation("Computing local matching");
		
		Set<Set<Matching>> alignment = new HashSet<Set<Matching>>();
		
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
				
				boolean isNew = alignment.add(partial);
				
				if(isNew) {
					signalPartialResult(partial);
					
					Matching primaryMatching = finder.getPrimaryMatching();
					
					for(int i=0; i<graphs.size(); i++) {
						for(int j=i+1; j<graphs.size(); j++) {
							Node n1 = primaryMatching.getNodeList().get(i);
							Node n2 = primaryMatching.getNodeList().get(j);
							
							dict.removeEntry(n1.getName(), n2.getName());
						}
					}
				}
				
			}
			
			checkPoint();
			
		} while(partial.size()>0);
		
		
	}
	
}
