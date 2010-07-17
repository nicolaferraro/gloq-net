package org.ppi.common.result;

import java.util.List;

import org.ppi.core.graph.Node;


public class Matching {

	List<Node> nodeList;
	
	public Matching(List<Node> nodeList) {
		this.nodeList=nodeList;
	}
	
	public List<Node> getNodeList() {
		return nodeList;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Matching) {
			Matching m = (Matching) obj;
			
			return this.nodeList.equals(m.nodeList);
		}
		return false;
	}
	
	@Override
	public String toString() {
		return nodeList.toString();
	}
	
}
