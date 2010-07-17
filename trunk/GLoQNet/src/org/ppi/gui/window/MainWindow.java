package org.ppi.gui.window;

import java.awt.Component;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.ppi.gui.screen.DictionaryPanel;
import org.ppi.gui.screen.GlobalAlignmentPanel;
import org.ppi.gui.screen.LocalAlignmentPanel;
import org.ppi.gui.screen.NetworkPanel;
import org.ppi.gui.screen.PreferencePanel;
import org.ppi.gui.screen.QueryPanel;
import org.ppi.gui.screen.ToolsPanel;
import org.ppi.preference.Constants;

public class MainWindow extends JFrame {

	private static final long serialVersionUID = 1L;
	
	DictionaryPanel dictionaryPanel;
	NetworkPanel networksPanel;
	PreferencePanel preferencePanel;
	GlobalAlignmentPanel globalPanel;
	LocalAlignmentPanel localPanel;
	QueryPanel queryPanel;
	ToolsPanel toolsPanel;
	
	JTabbedPane tabPane;

	public MainWindow() {
		
	}
	
	public void build() {
		setup();
		setControls();
	}
	
	protected void setup() {
		
		setTitle(Constants.WINDOW_TITLE);
		setSize(Constants.INITIAL_WIDTH, Constants.INITIAL_HEIGHT);
		setLocation(100, 50);
		setExtendedState(Constants.INITIAL_MAXIMIZED_STATE);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
	
		tabPane = new JTabbedPane();
		
		this.setContentPane(tabPane);
		
		networksPanel = new NetworkPanel();
		tabPane.addTab(Constants.TAB_NETWORKS_TITLE, networksPanel);
		
		preferencePanel = new PreferencePanel();
		tabPane.addTab(Constants.TAB_PREFERENCES_TITLE, preferencePanel);
		
		dictionaryPanel = new DictionaryPanel();
		tabPane.addTab(Constants.TAB_DICTIONARY_TITLE, dictionaryPanel);
		
		globalPanel = new GlobalAlignmentPanel();
		tabPane.addTab(Constants.TAB_GLOBAL_TITLE, globalPanel);
		
		localPanel = new LocalAlignmentPanel();
		tabPane.addTab(Constants.TAB_LOCAL_TITLE, localPanel);
		
		queryPanel = new QueryPanel();
		tabPane.addTab(Constants.TAB_QUERY_TITLE, queryPanel);
		
		toolsPanel = new ToolsPanel();
		tabPane.addTab(Constants.TAB_TOOLS_TITLE, toolsPanel);
		
	}
	
	protected void setControls() {
		
		tabPane.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				Component c = tabPane.getSelectedComponent();
				if(c!=null && c instanceof PreferencePanel) {
					PreferencePanel pp = (PreferencePanel) c;
					pp.reloadValues();
				}
			}
		});
		
	}
	
}
