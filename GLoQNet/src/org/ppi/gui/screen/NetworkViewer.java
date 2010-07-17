package org.ppi.gui.screen;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.ppi.core.graph.Graph;
import org.ppi.gui.model.NetworkModel;


public class NetworkViewer extends JPanel {
	
	private static final long serialVersionUID = 1L;
	
	Graph graph;
	
	JTable netTable;
	NetworkModel netModel;
	
	public NetworkViewer(Graph graph) {
		this.graph = graph;
		setup();
	}
	
	protected void setup() {
		
		this.setBorder(BorderFactory.createTitledBorder("Network: "));
		this.setLayout(new BorderLayout());
		
		
		this.netModel = new NetworkModel(graph);
		
		netTable = new JTable(netModel);
		
		this.add(new JScrollPane(netTable), BorderLayout.CENTER);
		
	}
	
}
