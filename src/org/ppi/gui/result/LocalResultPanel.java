package org.ppi.gui.result;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.apache.log4j.Logger;

import org.ppi.common.execute.PartialResultObserver;
import org.ppi.common.result.Matching;
import org.ppi.common.store.ResultWriter;
import org.ppi.core.graph.Graph;
import org.ppi.gui.file.FileChooserManager;

public class LocalResultPanel extends JPanel implements PartialResultObserver<Set<Matching>> {

	private static final long serialVersionUID = 1L;
	
	JTabbedPane tabPane;
	List<Graph> currentGraphs;
	List<ResultPanel> resultPanels;
	
	JButton btnSave;
	
	public LocalResultPanel() {
		setup();
		setControls();
	}
	
	private void setup() {
		
		resultPanels = new LinkedList<ResultPanel>();
		
		this.setBorder(BorderFactory.createTitledBorder("Local alignment results: "));
		
		this.setLayout(new BorderLayout());
		
		tabPane = new JTabbedPane();
		
		this.add(tabPane);
		
		JPanel btns = new JPanel();
		
		btnSave = new JButton("Save...");
		btns.add(btnSave);
		
		this.add(btns, BorderLayout.SOUTH);
	}


	private void setControls() {
		btnSave.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				JFileChooser chooser = FileChooserManager.getInstance().newFileChooser();
				
				int res = chooser.showSaveDialog(LocalResultPanel.this);
				
				if(res==JFileChooser.APPROVE_OPTION) {
					try {
						List<String[]> model = new ArrayList<String[]>(); 
							
						for(ResultPanel pan : resultPanels) {
							model.addAll(pan.getResultModel().getModel());
							if(model.size()>0) {
								int s = model.get(0).length;
								String[] sepRow = new String[s];
								for(int i=0; i<s; i++) {
									sepRow[i] = "---";
								}
								model.add(sepRow);
							}
						}
						
						ResultWriter.store(model, chooser.getSelectedFile());
						
					} catch(Exception ex) {
						Logger.getLogger(getClass()).error("Error while saving", ex);
						JOptionPane.showMessageDialog(LocalResultPanel.this, "Error while saving the file", "Error", JOptionPane.ERROR_MESSAGE);
					}
				} 
				
			}
		});
	}
	
	public void setCurrentGraphs(List<Graph> currentGraphs) {
		this.currentGraphs = currentGraphs;
	}

	@Override
	public void partialResultComputed(Set<Matching> partialResult) {
		
		ResultPanel pan = new ResultPanel(false);
		pan.setCurrentGraphs(currentGraphs);
		pan.partialResultComputed(partialResult);
		tabPane.addTab("Result " + (tabPane.getTabCount()+1), pan);
		int idx = tabPane.getTabCount() - 1;
		tabPane.setSelectedIndex(idx);
		resultPanels.add(pan);
	}


	public void clearResult() {
		tabPane.removeAll();
		resultPanels.clear();
	}
	
}
