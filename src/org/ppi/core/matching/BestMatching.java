package org.ppi.core.matching;

import java.util.ArrayList;

import org.ppi.common.execute.Executable;
import org.ppi.common.result.Matching;
import org.ppi.core.dictionary.Dictionary;
import org.ppi.core.graph.Graph;
import org.ppi.core.model.Model;
import org.ppi.core.model.Symbol;
import org.ppi.core.model.builder.GlobalModelBuilder;
import org.ppi.core.model.solver.OptimizedViterbiSolver;
import org.ppi.core.tour.TourBuilder;

import java.util.List;

public class BestMatching extends Executable<Matching> {
	
	Dictionary dict;
	List<Graph> masterGraphs;
	Graph slaveGraph;
	int depth;
	
	public BestMatching(Dictionary dict, List<Graph> graphs, int depth) {
		
		this.dict = dict;
		
		masterGraphs = new ArrayList<Graph>();
		for(int i=0; i<graphs.size()-1; i++) {
			masterGraphs.add(i, graphs.get(i));
		}
		
		slaveGraph = graphs.get(graphs.size()-1);
		this.depth = depth;
		
	}
	
	@Override
	protected void execute() throws Exception {
		
		signalCurrentOperation("Finding best matching");
		
		GlobalModelBuilder builder = new GlobalModelBuilder(masterGraphs, dict, depth);
		launchSubProcedure(builder);
			
		if(!builder.hasCompleted()) {
			throw new Exception("Error");
		}
			
		Model model = builder.getResult();
		
		TourBuilder tourBuilder = new TourBuilder(slaveGraph, depth);
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
