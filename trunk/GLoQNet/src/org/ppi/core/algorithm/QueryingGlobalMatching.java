package org.ppi.core.algorithm;

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
import org.ppi.core.matching.FindSubgraph;

public class QueryingGlobalMatching extends IncrementalExecutable<Set<Set<Matching>>, Set<Matching>> {

	List<Graph> baseGraphs;
	Graph query;
	Dictionary dictionary;
	int depth;

	public QueryingGlobalMatching(List<Graph> graphs, Graph query, Dictionary dict, int depth) {
		this.baseGraphs = graphs;
		this.query = query;
		this.dictionary = dict; // do not copy it now
		this.depth = depth;
	}

	@Override
	protected void execute() throws Exception {

		Set<Set<Matching>> wholeAlignment = new LinkedHashSet<Set<Matching>>();
		Dictionary dict = new Dictionary(this.dictionary);
		Set<Matching> lastAlignment = null;
		do {
			List<Graph> graphs = new LinkedList<Graph>(baseGraphs);
			graphs.add(new Graph(query));
			lastAlignment = computeGlobalAlignment(graphs, dict);

			if (lastAlignment.size() > 0) {
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

	protected Set<Matching> computeGlobalAlignment(List<Graph> graphs, Dictionary dictionaryToUse) throws Exception {

		signalCurrentOperation("Computing global matching");

		Set<Matching> alignment = new LinkedHashSet<Matching>();

		Set<Matching> partial;
		
		Dictionary dict = new Dictionary(dictionaryToUse);

		do {

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

				for (Matching m : partial) {
					for (int i = 0; i < graphs.size(); i++) {
						Node n = m.getNodeList().get(i);

						dict.removeNode(n.getName());
					}
				}

			}

			checkPoint();
		} while (partial.size() > 0);

		return alignment;
	}

}
