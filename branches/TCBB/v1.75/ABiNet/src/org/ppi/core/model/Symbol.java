package org.ppi.core.model;

import org.ppi.core.graph.Node;

public class Symbol {
	
	Node node;
	int level;
	
	public Symbol(Node node, int level) {
		this.node = node;
		this.level = level;
	}
	
	public Node getNode() {
		return node;
	}
	
	public int getLevel() {
		return level;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Symbol) {
			Symbol s = (Symbol) obj;
			return this.node.equals(s.node) && this.level==s.level;
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return node.hashCode()*(Math.max(level, 0) + 1);
	}
	
	@Override
	public String toString() {
		return "<" + node + ":" + level + ">";
	}
	
}
