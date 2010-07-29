package org.ppi.core.algorithm;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.ppi.common.execute.IncrementalExecutable;
import org.ppi.common.result.Matching;
import org.ppi.common.util.LoggingUtil;
import org.ppi.core.GraphCleaner;
import org.ppi.core.dictionary.Dictionary;
import org.ppi.core.graph.Graph;
import org.ppi.core.graph.Node;
import org.ppi.core.graph.algorithm.QuickShortestPathCalculator;
import org.ppi.core.graph.algorithm.ShortestPathCalculator;
import org.ppi.core.matching.FindSubgraph;
import org.ppi.preference.Preferences;

public class QueryingLimitedGlobalMatching extends IncrementalExecutable<Set<Set<Matching>>, Set<Matching>> {

	List<Graph> baseGraphs;
	Graph query;
	Dictionary dictionary;
	int depth;

	public QueryingLimitedGlobalMatching(List<Graph> graphs, Graph query, Dictionary dict, int depth) {
		this.baseGraphs = graphs;
		this.query = query;
		this.dictionary = dict; // it's not a copy, it should not be modified
		this.depth = depth;
	}

	@Override
	protected void execute() throws Exception {

		List<Graph> graphs = new LinkedList<Graph>(baseGraphs);
		graphs.add(new Graph(query));
		
		Set<Set<Matching>> wholeAlignment = new LinkedHashSet<Set<Matching>>();
		Dictionary dict = new Dictionary(this.dictionary);
		Set<Matching> lastAlignment = null;
		do {
			lastAlignment = computeGlobalAlignment(graphs, dict);
			if(lastAlignment.size()>0) {
				signalPartialResult(lastAlignment);
				
				for(Matching m : lastAlignment) {
					for (int i = 0; i < graphs.size() - 1; i++) {
						Node n = m.getNodeList().get(i);

						dict.removeNode(n.getName());
					}
				}
			}
			
			wholeAlignment.add(lastAlignment);
		} while (lastAlignment.size() > 0);

		result = wholeAlignment;

	}
	

	protected Set<Matching> computeGlobalAlignment(List<Graph> initialGraphs, Dictionary dictionaryToUse) throws Exception {

		signalCurrentOperation("Computing global matching");

		Set<Matching> alignment = new LinkedHashSet<Matching>();

		Set<Matching> partial;
		
		do {
			
			List<Graph> graphs = initialGraphs;
			
			Dictionary dict = new Dictionary(dictionaryToUse);
			
			for (Matching m : alignment) {
				for (int i = 0; i < graphs.size(); i++) {
					Node n = m.getNodeList().get(i);

					dict.removeNode(n.getName());
				}
			}
			
			cleanDistantNodesFromDictionary(dict, alignment, graphs);
			
			GraphCleaner cleaner = new GraphCleaner(graphs, dict);
			launchSubProcedure(cleaner);

			if (!cleaner.hasCompleted())
				throw new Exception("Error");

			graphs = cleaner.getResult();

			FindSubgraph finder = new FindSubgraph(dict, graphs, depth);
			launchSubProcedure(finder);

			if (!finder.hasCompleted())
				throw new Exception("Error");

			partial = finder.getResult();

			if (partial.size() > 0) {

				LoggingUtil.logResult(partial, logger);

				alignment.addAll(partial);

			}

			checkPoint();
		} while (partial.size() > 0);

		return alignment;
	}

	protected void cleanDistantNodesFromDictionary(Dictionary dict, Set<Matching> currentAlignment, List<Graph> graphs) {
		// This method skips the query network while deleting nodes from the dictionary
		if(currentAlignment.size()>0) {
			// after the first step we delete the nodes that are far from the current solution
			List<Set<Node>> core = new LinkedList<Set<Node>>();
			for(int i=0; i<graphs.size() -1 ; i++)
				core.add(new HashSet<Node>());
			
			for(Matching m : currentAlignment) {
				for(int i=0; i<m.getNodeList().size() - 1; i++) {
					Node n = m.getNodeList().get(i);
					core.get(i).add(n);
				}
			}
			
			int maxDistance = Preferences.getInstance().getQueryingSubgraphsMaxDistance();
			ShortestPathCalculator distanceCalculator = new QuickShortestPathCalculator();
			for(int i=0; i<graphs.size() - 1; i++) {
				Set<Node> graphCore = core.get(i);
				Graph g = graphs.get(i);
				for(Node n : g.getNodes()) {
					if(!distanceCalculator.isDistantLower(graphCore, n, maxDistance, g))
						dict.removeNode(n.getName());
				}
			}
		}
	}
	
}
