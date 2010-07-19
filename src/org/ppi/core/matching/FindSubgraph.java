package org.ppi.core.matching;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.ppi.common.execute.Executable;
import org.ppi.common.result.Matching;
import org.ppi.core.dictionary.Dictionary;
import org.ppi.core.graph.Graph;

public class FindSubgraph extends Executable<Set<Matching>> {
	
	Dictionary primaryDict;
	Dictionary secondaryDict;
	List<Graph> graphs;
	int depth;
	
	// Secondary result
	Matching primaryMatching;
	
	public FindSubgraph(Dictionary dict, List<Graph> graphs, int depth) {
		this(dict, dict, graphs, depth);
	}
	
	public FindSubgraph(Dictionary primaryDict, Dictionary secondaryDict, List<Graph> graphs, int depth) {
		this.primaryDict=primaryDict;
		this.secondaryDict=secondaryDict;
		this.graphs=graphs;
		this.depth=depth;
	}
	
	@Override
	protected void execute() throws Exception {
		
		signalCurrentOperation("Finding a matching subgraph");
		
		Set<Matching> matchings = new LinkedHashSet<Matching>();
		
		BestMatching bm = new BestMatching(primaryDict, graphs, depth);
		launchSubProcedure(bm);
		
		if(!bm.hasCompleted())
			throw new Exception("Error");
		
		primaryMatching = bm.getResult();
		
		if(primaryMatching!=null) {
			
			matchings.add(primaryMatching);
			
			Matching sm;
			
			do {
				BestPartialMatching pm = new BestPartialMatching(secondaryDict, graphs, depth, matchings);
				launchSubProcedure(pm);
				
				if(!pm.hasCompleted())
					throw new Exception("Error");
				
				sm = pm.getResult();
				
				if(sm!=null) {
					matchings.add(sm);
				}
				
				checkPoint();
				
			} while(sm!=null);
			
		}
		
		result = matchings; 
		
	}
	
	public Matching getPrimaryMatching() {
		return primaryMatching;
	}
	
}
