package org.ppi.common.result;

import java.util.List;

import org.ppi.core.graph.Node;

public class Matching {

	protected List<Node> nodeList;
	protected Double logScore;
	protected Double matchingTransitionsLogScore;

	public Matching(List<Node> nodeList) {
		this.nodeList = nodeList;
	}

	public List<Node> getNodeList() {
		return nodeList;
	}

	public Double getLogScore() {
		return logScore;
	}

	public void setLogScore(Double logScore) {
		this.logScore = logScore;
	}

	public Double getMatchingTransitionsLogScore() {
		return matchingTransitionsLogScore;
	}

	public void setMatchingTransitionsLogScore(
			Double matchingTransitionsLogScore) {
		this.matchingTransitionsLogScore = matchingTransitionsLogScore;
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
		return nodeList.toString() + " -> " + logScore;
	}

}
