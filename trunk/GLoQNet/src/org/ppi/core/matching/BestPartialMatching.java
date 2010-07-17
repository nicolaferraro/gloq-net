package org.ppi.core.matching;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.ppi.common.execute.Executable;
import org.ppi.common.result.Matching;
import org.ppi.core.dictionary.Dictionary;
import org.ppi.core.graph.Graph;
import org.ppi.core.graph.Node;
import org.ppi.core.model.Model;
import org.ppi.core.model.Symbol;
import org.ppi.core.model.builder.PartialModelBuilder;
import org.ppi.core.model.solver.OptimizedViterbiSolver;
import org.ppi.core.tour.TourBuilder;

import java.util.List;

public class BestPartialMatching extends Executable<Matching> {
	
	Dictionary dict;
	List<Graph> masterGraphs;
	Graph slaveGraph;
	int depth;
	Map<List<Node>, Node> currentAlignment;
	
	public BestPartialMatching(Dictionary dict, List<Graph> graphs, int depth, Set<Matching> currentMatchings) {
		
		this.dict = dict;
		
		masterGraphs = new ArrayList<Graph>();
		for(int i=0; i<graphs.size()-1; i++) {
			masterGraphs.add(graphs.get(i));
		}
		
		slaveGraph = graphs.get(graphs.size()-1);
		this.depth = depth;
		
		this.currentAlignment = new HashMap<List<Node>, Node>();
		
		for(Matching m : currentMatchings) {
			List<Node> nl = m.getNodeList();
			List<Node> keyList = new ArrayList<Node>(); 
			for(int i=0; i<nl.size()-1; i++) {
				keyList.add(i, nl.get(i));
			}
			this.currentAlignment.put(keyList, nl.get(nl.size()-1));
		}
		
	}
	
	@Override
	protected void execute() throws Exception {
		
		signalCurrentOperation("Computing best partial matching");
		
		PartialModelBuilder builder = new PartialModelBuilder(masterGraphs, dict, currentAlignment, depth);
		launchSubProcedure(builder);
		
		if(!builder.hasCompleted()) {
			throw new Exception("Error");
		}
			
		Model model = builder.getResult();
		
		TourBuilder tourBuilder = new TourBuilder(slaveGraph, depth, new HashSet<Node>(currentAlignment.values()));
		launchSubProcedure(tourBuilder);
		
		if(!tourBuilder.hasCompleted()) {
			throw new Exception("Error");
		}
		
		List<Symbol> tour = tourBuilder.getResult();
		
		OptimizedViterbiSolver solver = new OptimizedViterbiSolver(model, tour, dict);
		launchSubProcedure(solver);
		
		if(!solver.hasCompleted()) {
			throw new Exception("Error");
		}
		
		checkPoint();
		
		result = solver.getResult();
		
	}
	
}
