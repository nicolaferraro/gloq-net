package org.ppi.core.tree;

import java.util.LinkedList;
import java.util.List;

import org.ppi.core.graph.Node;

public class TreeNode {
	
	List<Node> nodes;
	TreeNode parent;
	List<TreeNode> children;
	
	public TreeNode(List<Node> nodes) {
		this.nodes=nodes;
		children = new LinkedList<TreeNode>();
	}
	
	public void setParent(TreeNode parent) {
		this.parent = parent;
	}
	
	public void addChild(TreeNode pt) {
		children.add(pt);
		pt.setParent(this);
	}
	
	public TreeNode getParent() {
		return parent;
	}
	
	public List<TreeNode> getChildren() {
		return children;
	}
	
	public List<Node> getNodes() {
		return nodes;
	}
	
	@Override
	public String toString() {
		String res = "(";
		
		int i=0;
		for(Node n : nodes) {
			res+=n.getName();
			i++;
			if(i<nodes.size())
				res+=", ";
		}
		
		res+=" [";
		for(TreeNode c : children) {
			res+=c.toString();
		}
		res+="])";
		return res;
	}
}
