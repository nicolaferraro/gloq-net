package org.ppi.gui.screen;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import org.ppi.common.manager.DictionaryManager;
import org.ppi.core.algorithm.GlobalMatching;
import org.ppi.core.graph.Graph;
import org.ppi.gui.config.ConfigPanel;
import org.ppi.gui.execute.ObserverDialog;
import org.ppi.gui.result.ResultPanel;
import org.ppi.preference.Preferences;


public class GlobalAlignmentPanel extends JPanel {
	
	private static final long serialVersionUID = 1L;
	
	ConfigPanel confPanel;
	ResultPanel resPanel;
	
	JButton startButton;
	
	public GlobalAlignmentPanel() {
		setup();
		setControls();
	}
	
	protected void setup() {

		this.setLayout(new BorderLayout());
		
		JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		split.setDividerLocation(250);
		
		this.add(split);
		
		confPanel = new ConfigPanel();
		
		split.add(confPanel);
		
		resPanel = new ResultPanel();
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
				List<Graph> graphs = confPanel.getOrderedNetworks();
				
				resPanel.clearResult();
				resPanel.setCurrentGraphs(graphs);
				
				GlobalMatching algo = new GlobalMatching(graphs, DictionaryManager.getInstance().getDictionary(), Preferences.getInstance().getDepth());
				algo.addPartialResultObserver(resPanel);
				
				ObserverDialog dialog = new ObserverDialog(algo);
				
				algo.launch();
				
				dialog.showProgress();
			}
		});
	}
	
}
