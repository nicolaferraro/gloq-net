package org.ppi.core.graph.algorithm;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.ppi.core.graph.Graph;
import org.ppi.core.graph.Node;
import org.ppi.core.graph.NodeFactory;

public class QuickShortestPathCalculatorTest {

	@Test
	public void testIsDistant() {
		
		Graph g = new Graph();
		
		Node n1 = NodeFactory.getInstance().createNode("1");
		Node n2 = NodeFactory.getInstance().createNode("2");
		Node n3 = NodeFactory.getInstance().createNode("3");
		Node n4 = NodeFactory.getInstance().createNode("4");
		Node n5 = NodeFactory.getInstance().createNode("5");
		Node n6 = NodeFactory.getInstance().createNode("6");
		Node n7 = NodeFactory.getInstance().createNode("7");
		Node n8 = NodeFactory.getInstance().createNode("8");
		
		g.addNode(n1);
		g.addNode(n2);
		g.addNode(n3);
		g.addNode(n4);
		g.addNode(n5);
		g.addNode(n6);
		g.addNode(n7);
		g.addNode(n8);
		
		g.addEdge(n1, n2);
		g.addEdge(n2, n3);
		g.addEdge(n4, n3);
		g.addEdge(n4, n5);
		g.addEdge(n5, n6);
		g.addEdge(n6, n2);
		g.addEdge(n5, n7);
		g.addEdge(n6, n7);
		
		
		ShortestPathCalculator calc = new QuickShortestPathCalculator();
		
		Set<Node> source1 = new HashSet<Node>();
		source1.add(n1);
		source1.add(n3);
		
		assertTrue(calc.isDistantLower(source1, n5, 2, g));
		assertFalse(calc.isDistantLower(source1, n7, 2, g));
		
		Set<Node> source2 = new HashSet<Node>();
		source2.add(n7);
		source2.add(n3);
		
		assertFalse(calc.isDistantLower(source2, n8, 1000, g));
		assertFalse(calc.isDistantLower(source2, n2, 0, g));
		assertTrue(calc.isDistantLower(source2, n2, 1, g));
	}

	@Test
	public void test2() {
		Graph g = new Graph();
		
		Node n1 = NodeFactory.getInstance().createNode("1");
		Node n2 = NodeFactory.getInstance().createNode("2");
		Node n3 = NodeFactory.getInstance().createNode("3");
		Node n4 = NodeFactory.getInstance().createNode("4");
		Node n5 = NodeFactory.getInstance().createNode("5");
		Node n6 = NodeFactory.getInstance().createNode("6");
		Node n7 = NodeFactory.getInstance().createNode("7");
		Node n8 = NodeFactory.getInstance().createNode("8");
		
		g.addNode(n1);
		g.addNode(n2);
		g.addNode(n3);
		g.addNode(n4);
		g.addNode(n5);
		g.addNode(n6);
		g.addNode(n7);
		g.addNode(n8);
		
		g.addEdge(n1, n2);
		g.addEdge(n2, n3);
		g.addEdge(n3, n4);
		g.addEdge(n1, n4);
		g.addEdge(n4, n5);
		g.addEdge(n5, n6);
		
		
		ShortestPathCalculator calc = new QuickShortestPathCalculator();
		
		Set<Node> source1 = new HashSet<Node>();
		source1.add(n1);
		source1.add(n8);
		
		assertTrue(calc.isDistantLower(source1, n5, 2, g));
		assertFalse(calc.isDistantLower(source1, n5, 1, g));
		
		Set<Node> source2 = new HashSet<Node>();
		source2.add(n2);
		
		assertFalse(calc.isDistantLower(source2, n6, 3, g));

		Set<Node> source3 = new HashSet<Node>();
		source3.add(n2);
		source3.add(n3);
		
		assertTrue(calc.isDistantLower(source3, n6, 3, g));

	}
	
}
