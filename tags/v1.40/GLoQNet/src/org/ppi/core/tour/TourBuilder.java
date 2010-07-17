package org.ppi.core.tour;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.ppi.common.execute.Executable;
import org.ppi.core.graph.Graph;
import org.ppi.core.graph.Node;
import org.ppi.core.model.Symbol;
import org.ppi.core.tree.SingleTreeNode;
import org.ppi.core.tree.TreeBuilder;
import org.ppi.core.tree.TreeNode;

public class TourBuilder extends Executable<List<Symbol>> {

	Graph graph;
	Set<Node> startingNodes;
	int depth;
	
	public TourBuilder(Graph graph, int depth) {
		this(graph, depth, null);
	}
	
	public TourBuilder(Graph graph, int depth, Set<Node> startingNodes) {
		this.graph = graph;
		this.startingNodes = startingNodes;
		this.depth = depth;
	}
	
	@Override
	protected void execute() throws Exception {
		
		List<Symbol> tour = new LinkedList<Symbol>();
		
		Set<Node> nodes = startingNodes;
		if(nodes==null)
			nodes = graph.getNodes();
		
		for(Node n : nodes) {
			TreeBuilder builder = new TreeBuilder(graph, n, depth, startingNodes);
			launchSubProcedure(builder);
			if(!builder.hasCompleted())
				throw new Exception("Error");
			SingleTreeNode tn = builder.getResult();
			visit(tn, 0, tour);
		}
		
		checkPoint();
		
		result = tour;
	}
	
	private void visit(SingleTreeNode t, int depth, List<Symbol> list) {
		list.add(new Symbol(t.getUniqueNode(), depth));
		List<TreeNode> children = t.getChildren();
		if (children.size() > 0) {
			for(TreeNode c : children) {
				visit((SingleTreeNode)c, depth + 1, list);
				list.add(new Symbol(t.getUniqueNode(), depth));
				
				checkPoint();
			}
		}
	}
	
}
