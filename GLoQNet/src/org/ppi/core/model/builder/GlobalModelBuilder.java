package org.ppi.core.model.builder;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.ppi.common.execute.Executable;
import org.ppi.core.dictionary.Dictionary;
import org.ppi.core.graph.Graph;
import org.ppi.core.graph.Node;
import org.ppi.core.model.Model;
import org.ppi.core.tree.SingleTreeNode;
import org.ppi.core.tree.TreeBuilder;
import org.ppi.core.tree.TreeMerger;
import org.ppi.core.tree.TreeNode;


public class GlobalModelBuilder extends Executable<Model> {

	List<Graph> graphs;
	Dictionary dict;
	int depth;
	
	public GlobalModelBuilder(List<Graph> graphs, Dictionary dict, int depth) {
		this.graphs = graphs;
		this.dict = dict;
		this.depth = depth;
	}
	
	@Override
	protected void execute() throws Exception {
		
		signalCurrentOperation("Building a complete model");
		
		Set<? extends TreeNode> mergeTree = null;
		
		for(Graph g : graphs) {
			
			Set<SingleTreeNode> tree = new HashSet<SingleTreeNode>();
			
			for(Node n : g.getNodes()) {
				TreeBuilder tb = new TreeBuilder(g, n, depth);
				launchSubProcedure(tb);
				
				if(!tb.hasCompleted())
					throw new Exception("Error while computing trees");
				
				SingleTreeNode res = tb.getResult();
				
				tree.add(res);
				
				checkPoint();
				
			}
			
			if(mergeTree==null) {
				
				mergeTree = tree;
				
			} else {
				
				TreeMerger merger = new TreeMerger(mergeTree, tree, dict);
				launchSubProcedure(merger);
				
				if(!merger.hasCompleted())
					throw new Exception("Error while merging trees");
				
				mergeTree = merger.getResult();
				
			}
		}
		
		TreeModelBuilder mBuilder = new TreeModelBuilder(mergeTree, dict);
		launchSubProcedure(mBuilder);
		
		if(!mBuilder.hasCompleted())
			throw new Exception("Error while computing the model");
		
		checkPoint();
		
		result = mBuilder.getResult();
		
	}
	
}
