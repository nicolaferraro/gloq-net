package org.ppi.core.graph;

import java.util.HashMap;
import java.util.Map;

public class NodeFactory {

	private static final NodeFactory instance = new NodeFactory();
	
	private Map<String, Node> nodeCache;
	
	private NodeFactory() {
		this.nodeCache = new HashMap<String, Node>();
	}
	
	public static NodeFactory getInstance() {
		return instance;
	}
	
	public synchronized Node createNode(String name) {
		if(!nodeCache.containsKey(name))
			nodeCache.put(name, new Node(name));
		return nodeCache.get(name);
	}
	
}
