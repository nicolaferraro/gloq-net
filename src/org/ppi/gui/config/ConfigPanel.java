package org.ppi.gui.config;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.JPanel;

import org.ppi.core.graph.Graph;

public class ConfigPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	
	NetworkOrderPanel orderPanel;
	
	public ConfigPanel() {
		setup();
		setControls();
	}

	private void setup() {
		this.setLayout(new BorderLayout());
		
		orderPanel = new NetworkOrderPanel();
		this.add(orderPanel);
		
	}

	private void setControls() {
		
	}
	
	public List<Graph> getOrderedNetworks() {
		return orderPanel.getOrderedNetworks();
	}
	

}
