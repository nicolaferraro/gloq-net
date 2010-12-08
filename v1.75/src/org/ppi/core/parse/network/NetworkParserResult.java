package org.ppi.core.parse.network;

import org.ppi.core.graph.Graph;

public class NetworkParserResult {
	
	Graph graph;
	int errorCount;
	
	public NetworkParserResult() {
	}
	
	public void setGraph(Graph dictionary) {
		this.graph = dictionary;
	}
	
	public void setErrorCount(int errorCount) {
		this.errorCount = errorCount;
	}
	
	public Graph getGraph() {
		return graph;
	}
	
	public int getErrorCount() {
		return errorCount;
	}
}
