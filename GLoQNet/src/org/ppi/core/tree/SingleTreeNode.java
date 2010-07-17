package org.ppi.core.tree;

import java.util.LinkedList;

import org.ppi.core.graph.Node;


public class SingleTreeNode extends TreeNode {

	public SingleTreeNode(Node uniqueNode) {
		super(new LinkedList<Node>());
		this.nodes.add(uniqueNode);
		children = new LinkedList<TreeNode>();
	}
	
	public Node getUniqueNode() {
		return super.getNodes().get(0);
	}
	
}
