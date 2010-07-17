package org.ppi.gui.draw;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

import org.ppi.resource.ResourceManager;


public class DrawAreaController extends JPanel {

	private static final long serialVersionUID = 1L;

	protected DrawArea drawArea;
	protected ResourceManager resourceManager;
	
	protected static final Color BACKGROUND = Color.WHITE;
	
	JToggleButton moveBtn;
	JToggleButton drawBtn;
	JToggleButton edgeBtn;
	
	boolean readOnly;
	
	public DrawAreaController(boolean readOnly) {
		this.readOnly = readOnly;
	}
	
	public boolean isReadOnly() {
		return readOnly;
	}
	
	public void setDrawPanel(DrawArea drawArea) {
		this.drawArea = drawArea;
	}
	
	public void setResourceManager(ResourceManager resourceManager) {
		this.resourceManager = resourceManager;
	}
	
	public void init() {
		
		this.setBackground(BACKGROUND);
		
		drawBtn = new JToggleButton(resourceManager.getResourceAsIcon(ResourceManager.NODE_BTN));
		drawBtn.setToolTipText("Draw new nodes");
		drawBtn.setEnabled(!readOnly);
		this.add(drawBtn);
		
		moveBtn = new JToggleButton(resourceManager.getResourceAsIcon(ResourceManager.MOVE_BTN));
		moveBtn.setToolTipText("Move nodes");
		this.add(moveBtn);
		
		edgeBtn = new JToggleButton(resourceManager.getResourceAsIcon(ResourceManager.EDGE_BTN));
		edgeBtn.setToolTipText("Draw edges");
		edgeBtn.setEnabled(!readOnly);
		this.add(edgeBtn);
		
		ButtonGroup grp = new ButtonGroup();
		grp.add(drawBtn);
		grp.add(moveBtn);
		grp.add(edgeBtn);
		
		switch(DrawArea.DEFAULT_MODE) {
		case DrawArea.MODE_DRAW:
			drawBtn.setSelected(true);
			break;
		case DrawArea.MODE_EDGE:
			edgeBtn.setSelected(true);
			break;
		case DrawArea.MODE_MOVE:
			moveBtn.setSelected(true);
			break;
		}
		
		setControls();
	}
	
	protected void setControls() {
		
		drawBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				drawArea.setMode(DrawArea.MODE_DRAW);
			}
		});
		
		moveBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				drawArea.setMode(DrawArea.MODE_MOVE);
			}
		});
		
		edgeBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				drawArea.setMode(DrawArea.MODE_EDGE);
			}
		});
		
	}
	
}
