package org.ppi.gui.user;

import java.awt.Container;

import org.ppi.core.graph.Graph;
import org.ppi.core.graph.Node;
import org.ppi.gui.graph.VisualGraph;

public interface UserInterface {

	public boolean canDeleteNode(Node n, Graph g, Container c);
	
	public boolean canDeleteEdge(Node n1, Node n2, Graph g, Container c);

	public void editNodeProperties(Node n, VisualGraph vg, Container c);
	
}
