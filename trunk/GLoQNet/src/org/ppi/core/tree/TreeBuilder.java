package org.ppi.core.tree;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.ppi.common.execute.Executable;
import org.ppi.core.graph.Graph;
import org.ppi.core.graph.Node;

public class TreeBuilder extends Executable<SingleTreeNode> {

	int depth;
	Graph g;
	Node n;
	Set<Node> avoidSet;

	public TreeBuilder(Graph g, Node n, int depth) {
		this(g, n, depth, null);
	}

	public TreeBuilder(Graph g, Node n, int depth, Set<Node> avoidSet) {
		this.g = g;
		this.n = n;
		this.depth = depth;
		this.avoidSet = avoidSet;
	}

	@Override
	protected void execute() {

		List<SingleTreeNode> currentNodes = new LinkedList<SingleTreeNode>();
		SingleTreeNode nt0 = new SingleTreeNode(n);
		currentNodes.add(nt0);

		for (int lev = 1; lev < depth; lev++) {

			List<SingleTreeNode> nextNodes = new LinkedList<SingleTreeNode>();

			for (SingleTreeNode nt : currentNodes) {
				Set<Node> neigh = g.getAdjacent(nt.getUniqueNode());
				for (Node nei : neigh) {
					if (avoidSet!=null && avoidSet.contains(nei))
						continue;

					SingleTreeNode neit = new SingleTreeNode(nei);
					nt.addChild(neit);
					nextNodes.add(neit);
					
					checkPoint();
				}
				
				checkPoint();
			}
			
			currentNodes = nextNodes;
			
			checkPoint();
		}
		
		checkPoint();
		
		result = nt0;
	}

}
