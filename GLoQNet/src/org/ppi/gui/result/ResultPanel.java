package org.ppi.gui.result;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.apache.log4j.Logger;
import org.ppi.common.execute.PartialResultObserver;
import org.ppi.common.result.Matching;
import org.ppi.common.store.ResultWriter;
import org.ppi.core.graph.Graph;
import org.ppi.gui.draw.DrawPanel;
import org.ppi.gui.draw.DrawPanelFactory;
import org.ppi.gui.execute.ObserverDialog;
import org.ppi.gui.file.FileChooserManager;
import org.ppi.gui.graph.GraphMerger;
import org.ppi.gui.model.ResultModel;


public class ResultPanel extends JPanel implements
		PartialResultObserver<Set<Matching>> {

	private static final long serialVersionUID = 1L;
	
	ResultModel resultModel;
	JTable resultTable;
	List<Graph> currentGraphs;
	
	JButton btnView;
	JButton btnSave;
	
	boolean canBeSaved = true;

	public ResultPanel(boolean canBeSaved) {
		this.canBeSaved = canBeSaved;
		
		setup();
		setControls();
	}
	
	public ResultPanel() {
		setup();
		setControls();
	}

	protected void setup() {
		this.setLayout(new BorderLayout());
		
		resultModel = new ResultModel();
		
		resultTable = new JTable(resultModel);
		
		this.setBorder(BorderFactory.createTitledBorder("Result: "));
		
		this.add(new JScrollPane(resultTable));
		
		JPanel btns = new JPanel();
		
		btnView = new JButton("View");
		
		btns.add(btnView);
		
		if(canBeSaved) {
			btnSave = new JButton("Save...");
			btns.add(btnSave);
		}
		
		this.add(btns, BorderLayout.SOUTH);
		
	}

	protected void setControls() {
		
		if(canBeSaved) {
			btnSave.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					
					JFileChooser chooser = FileChooserManager.getInstance().newFileChooser();
					
					int res = chooser.showSaveDialog(ResultPanel.this);
					
					if(res==JFileChooser.APPROVE_OPTION) {
						try {
							List<String[]> model = resultModel.getModel();
							
							ResultWriter.store(model, chooser.getSelectedFile());
							
						} catch(Exception ex) {
							Logger.getLogger(getClass()).error("Error while saving", ex);
							JOptionPane.showMessageDialog(ResultPanel.this, "Error while saving the file", "Error", JOptionPane.ERROR_MESSAGE);
						}
					} 
					
				}
			});
		}
		
		btnView.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				Set<Matching> mat = new HashSet<Matching>(resultModel.getAlignment());
				
				if(mat.size()==0)
					return;
				
				GraphMerger merger = new GraphMerger(currentGraphs, mat);
				
				ObserverDialog dialog = new ObserverDialog(merger);
				
				merger.launch();
				
				dialog.showProgress();
				
				if(!merger.hasCompleted()) {
					if(!merger.wasInterruptedByUser()) {
						JOptionPane.showMessageDialog(ResultPanel.this, "Error while creating the result", "Error", JOptionPane.ERROR_MESSAGE);
					}
				} else {
					
					JFrame frm = new JFrame("Result");
					DrawPanel pan = DrawPanelFactory.getInstance().newViewPanel(merger.getResult());
					
					frm.setContentPane(pan);
					frm.setExtendedState(JFrame.MAXIMIZED_BOTH);
					frm.setVisible(true);
				}
				
				
			}
		});
		
	}
	
	public void setCurrentGraphs(List<Graph> currentGraphs) {
		this.currentGraphs = currentGraphs;
	}
	
	public void clearResult() {
		resultModel.clear();
	}

	@Override
	public void partialResultComputed(Set<Matching> partialResult) {
		resultModel.partialResultComputed(partialResult);
	}
	
	public ResultModel getResultModel() {
		return resultModel;
	}
	
}
