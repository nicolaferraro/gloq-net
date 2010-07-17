package org.ppi.common.util;

import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.ppi.common.result.Matching;
import org.ppi.core.graph.Node;


public class LoggingUtil {

	public static void logResult(Set<Matching> res, Logger logger) {
		
		logger.info("Found new matching subgraph");
		for(Matching m : res) {
			
			StringBuffer tuple = new StringBuffer("<");
			
			List<Node> nodes = m.getNodeList();
			int i=0;
			for(Node n : nodes) {
				tuple.append(n.getName());
				
				i++;
				if(i<nodes.size())
					tuple.append(", ");
			}
			
			tuple.append(">");
			
			logger.info(tuple);
		}
	}
}
