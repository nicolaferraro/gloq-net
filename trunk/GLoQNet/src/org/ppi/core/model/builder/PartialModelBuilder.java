package org.ppi.core.model.builder;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
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


public class PartialModelBuilder extends Executable<Model> {

	List<Graph> graphs;
	Dictionary dict;
	int depth;
	Map<List<Node>, Node> currentAlignment;
	
	public PartialModelBuilder(List<Graph> graphs, Dictionary dict, Map<List<Node>, Node> currentAlignment, int depth) {
		this.graphs = graphs;
		this.dict = dict;
		this.depth = depth;
		this.currentAlignment = currentAlignment;
	}
	
	@Override
	protected void execute() throws Exception {
		
		signalCurrentOperation("Building a partial model");
		
		Set<? extends TreeNode> mergeTree = null;
		
		for(int i=0; i<graphs.size(); i++) {
			
			Graph g = graphs.get(i);
			
			Set<Node> startingNodes = new HashSet<Node>();
			
			for(List<Node> ls : currentAlignment.keySet()) {
				startingNodes.add(ls.get(i));
			}
			
			Set<SingleTreeNode> tree = new HashSet<SingleTreeNode>();
			
			for(Node n : startingNodes) {
				TreeBuilder tb = new TreeBuilder(g, n, depth, startingNodes);
				launchSubProcedure(tb);
				
				if(!tb.hasCompleted())
					throw new Exception("Error while computing local trees");
				
				SingleTreeNode res = tb.getResult();
				
				tree.add(res);
				
				checkPoint();
				
			}
			
			if(mergeTree==null) {
				
				mergeTree = tree;
				
			} else {
				
				TreeMerger merger = new TreeMerger(mergeTree, tree, dict, currentAlignment);
				launchSubProcedure(merger);
				
				if(!merger.hasCompleted())
					throw new Exception("Error while merging trees");
				
				mergeTree = merger.getResult();
				
			}
		}
		
		TreeModelBuilder mBuilder = new TreeModelBuilder(mergeTree, dict, currentAlignment);
		launchSubProcedure(mBuilder);
		
		if(!mBuilder.hasCompleted())
			throw new Exception("Error while computing the model");
		
		checkPoint();
		
		result = mBuilder.getResult();
		
	}
	
}
