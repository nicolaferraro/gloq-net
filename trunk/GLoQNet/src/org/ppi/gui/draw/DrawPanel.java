package org.ppi.gui.draw;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.ppi.core.graph.Graph;
import org.ppi.gui.graph.VisualGraph;


public class DrawPanel extends JPanel {
	
	private static final long serialVersionUID = 8838266210945440962L;
	
	DrawArea drawArea;
	DrawAreaController controller;
	
	
	public DrawPanel(DrawArea drawArea, DrawAreaController controller) {
		this.drawArea = drawArea;
		this.controller = controller;
		
		setup();
	}
	
	protected void setup() {
		
		this.setLayout(new BorderLayout());
		
		this.add(new JScrollPane(drawArea));
		this.add(controller, BorderLayout.SOUTH);
		
	}
	
	public Graph getDrawnGraph() {
		return drawArea.getDrawnGraph();
	}
	
	public VisualGraph getDrawnVisualGraph() {
		return drawArea.getDrawnVisualGraph();
	}
	
	public DrawArea getDrawArea() {
		return drawArea;
	}
	
	
}
