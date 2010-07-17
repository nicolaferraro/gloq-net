package org.ppi.gui.screen;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import org.ppi.common.manager.DictionaryManager;
import org.ppi.core.algorithm.LocalMatching;
import org.ppi.core.graph.Graph;
import org.ppi.gui.config.ConfigPanel;
import org.ppi.gui.draw.DrawPanel;
import org.ppi.gui.draw.DrawPanelFactory;
import org.ppi.gui.execute.ObserverDialog;
import org.ppi.gui.result.LocalResultPanel;
import org.ppi.preference.Preferences;


public class QueryPanel extends JPanel {
	
	private static final long serialVersionUID = 1L;
	
	ConfigPanel confPanel;
	DrawPanel drawPanel;
	LocalResultPanel resPanel;
	
	JButton startButton;
	
	public QueryPanel() {
		setup();
		setControls();
	}
	
	protected void setup() {
		
		this.setLayout(new BorderLayout());
		
		JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		split.setDividerLocation(250);
		
		this.add(split);
		
		JSplitPane splitUp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		
		confPanel = new ConfigPanel();
		
		splitUp.add(confPanel);
		
		drawPanel = DrawPanelFactory.getInstance().newDrawPanel();
		splitUp.add(drawPanel);
		
		split.add(splitUp);
		
		
		resPanel = new LocalResultPanel();
		split.add(resPanel);
		
		startButton = new JButton("Start...");
		
		JPanel btns = new JPanel();
		
		btns.add(startButton);
		
		this.add(btns, BorderLayout.SOUTH);
		
	}
	
	protected void setControls() {
		
		startButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				List<Graph> graphs = new ArrayList<Graph>(confPanel.getOrderedNetworks());
				
				Graph queryNet = drawPanel.getDrawnGraph();
				
				graphs.add(queryNet);
				
				resPanel.clearResult();
				resPanel.setCurrentGraphs(graphs);
				
				LocalMatching algo = new LocalMatching(graphs, DictionaryManager.getInstance().getDictionary(), Preferences.getInstance().getDepth());
				algo.addPartialResultObserver(resPanel);
				
				ObserverDialog dialog = new ObserverDialog(algo);
				
				algo.launch();
				
				dialog.showProgress();
			}
		});
	}
	
}
