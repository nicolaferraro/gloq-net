package org.ppi.gui.screen;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import org.ppi.common.execute.IncrementalExecutable;
import org.ppi.common.manager.DictionaryManager;
import org.ppi.common.result.Matching;
import org.ppi.core.algorithm.QueryingGlobalMatching;
import org.ppi.core.algorithm.QueryingLimitedGlobalMatching;
import org.ppi.core.algorithm.QueryingLocalMatching;
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

	JButton globalStartButton;
	JButton globalLimitedStartButton;
	JButton localStartButton;

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

		globalStartButton = new JButton("Start global...");
		globalLimitedStartButton = new JButton("Start global (limited distance)...");
		localStartButton = new JButton("Start local...");

		JPanel btns = new JPanel();

		btns.add(globalStartButton);
		btns.add(globalLimitedStartButton);
		btns.add(localStartButton);

		this.add(btns, BorderLayout.SOUTH);

	}

	protected void setControls() {

		class StartListener implements ActionListener {

			private AlgorithmType type;

			public StartListener(AlgorithmType type) {
				this.type = type;
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				List<Graph> graphs = new ArrayList<Graph>(confPanel.getOrderedNetworks());

				Graph queryNet = drawPanel.getDrawnGraph();

				List<Graph> resultGraphs = new LinkedList<Graph>(graphs);
				resultGraphs.add(queryNet);

				resPanel.clearResult();
				resPanel.setCurrentGraphs(resultGraphs);

				IncrementalExecutable<Set<Set<Matching>>, Set<Matching>> algo;
				if (type==AlgorithmType.LOCAL) {
					algo = new QueryingLocalMatching(graphs, queryNet, DictionaryManager.getInstance().getDictionary(), Preferences.getInstance().getDepth());
				} else if(type==AlgorithmType.GLOBAL) {
					algo = new QueryingGlobalMatching(graphs, queryNet, DictionaryManager.getInstance().getDictionary(), Preferences.getInstance().getDepth());
				} else if(type==AlgorithmType.GLOBAL_WITH_LIMITS) {
					algo = new QueryingLimitedGlobalMatching(graphs, queryNet, DictionaryManager.getInstance().getDictionary(), Preferences.getInstance().getDepth());
				} else {
					throw new IllegalStateException("Unknown algorithm");
				}

				algo.addPartialResultObserver(resPanel);

				ObserverDialog dialog = new ObserverDialog(algo);

				algo.launch();

				dialog.showProgress();
			}
		}

		localStartButton.addActionListener(new StartListener(AlgorithmType.LOCAL));
		globalStartButton.addActionListener(new StartListener(AlgorithmType.GLOBAL));
		globalLimitedStartButton.addActionListener(new StartListener(AlgorithmType.GLOBAL_WITH_LIMITS));
	}
	
	private static enum AlgorithmType {
		LOCAL, GLOBAL, GLOBAL_WITH_LIMITS;
	}

}
