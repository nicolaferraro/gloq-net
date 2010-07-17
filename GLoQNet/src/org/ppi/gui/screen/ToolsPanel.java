package org.ppi.gui.screen;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.ppi.common.manager.NetworkManager;
import org.ppi.common.result.Matching;
import org.ppi.core.graph.Graph;
import org.ppi.core.parse.result.AbstractResultParser;
import org.ppi.core.parse.result.ResultParserFactory;
import org.ppi.core.parse.result.ResultParserResult;
import org.ppi.gui.draw.DrawPanel;
import org.ppi.gui.draw.DrawPanelFactory;
import org.ppi.gui.execute.ObserverDialog;
import org.ppi.gui.file.FileChooserManager;
import org.ppi.gui.graph.GraphMerger;
import org.ppi.gui.graph.NTGraphMerger;

public class ToolsPanel extends JPanel {
	
	private static final long serialVersionUID = 1L;

	JButton btnChoose;
	
	JList lstAlignment;
	DefaultListModel lstAlignmentModel;
	
	JButton btnShow;
	
	
	public ToolsPanel() {
		setup();
		setControls();
	}
	
	protected void setup() {
		
		this.setBorder(BorderFactory.createTitledBorder("Tools: "));
		this.setLayout(new BorderLayout());
		
		btnChoose = new JButton("Browse...");
		
		lstAlignmentModel = new DefaultListModel();
		lstAlignment = new JList(lstAlignmentModel);
		btnShow = new JButton("Show");
		
		JPanel topPane = new JPanel();
		topPane.add(btnChoose);
		
		JPanel bottomPane = new JPanel();
		bottomPane.add(btnShow);
		
		this.add(new JScrollPane(lstAlignment), BorderLayout.CENTER);
		this.add(topPane, BorderLayout.NORTH);
		this.add(bottomPane, BorderLayout.SOUTH);
		
	}
	
	protected void setControls() {
		
		this.btnChoose.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
				JOptionPane.showMessageDialog(ToolsPanel.this, "Remember to load the network files first", "Networks", JOptionPane.INFORMATION_MESSAGE);
				
				JFileChooser chooser = FileChooserManager.getInstance().newFileChooser();
				chooser.setAcceptAllFileFilterUsed(true);
				chooser.resetChoosableFileFilters();
				
				int res = chooser.showOpenDialog(ToolsPanel.this);
				
				if(res == JFileChooser.APPROVE_OPTION) {
					
					try {
						
						File f = chooser.getSelectedFile();
						
						Class<? extends AbstractResultParser> parseClass = ResultParserFactory.getInstance().getDefaultParserClass();
						
						AbstractResultParser parser = parseClass.newInstance();
						parser.setFile(f);
						
						ObserverDialog dialog = new ObserverDialog(parser);
						
						parser.launch();
						
						dialog.showProgress();
						
						if(!parser.hasCompleted()) {
							if(!parser.wasInterruptedByUser()) {
								JOptionPane.showMessageDialog(ToolsPanel.this, "Error while parsing the file", "Error", JOptionPane.ERROR_MESSAGE);
							}
						} else {
							ResultParserResult parserRes = parser.getResult();
							if(parserRes.getErrorCount()>0) {
								int c = JOptionPane.showConfirmDialog(ToolsPanel.this, "The parser found "+parserRes.getErrorCount()+" error(s) while reading the file. Continue?", "Warning", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
								if(c==JOptionPane.YES_OPTION) {
									displayResultList(parserRes);
								}
							} else {
								displayResultList(parserRes);
							}
						}
						
					} catch(Exception ex) {
						JOptionPane.showMessageDialog(ToolsPanel.this, "Generic error while parsing the file", "Error", JOptionPane.ERROR_MESSAGE);
					}
					
				}
				
			}
		});
		
		this.btnShow.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				ResultPart resPart = (ResultPart) lstAlignment.getSelectedValue();

				if(resPart==null) {
					JOptionPane.showMessageDialog(ToolsPanel.this, "Select one alignment from the list");
					return;
				}
				
				Set<Matching> mat = resPart.matching;
				displayResult(mat);
				
			}
		});
		
	}
	
	private void displayResultList(ResultParserResult res) {
		lstAlignmentModel.removeAllElements();
		
		Set<Set<Matching>> mats = res.getAlignments();
		
		int prg = 0;
		for(Set<Matching> mat : mats) {
			lstAlignmentModel.add(prg, new ResultPart(mat, prg + 1));
			prg++;
		}
	}
	
	private void displayResult(Set<Matching> matching) {
		if(matching.size()==0)
			return;
		
		List<Graph> currentGraphs = NetworkManager.getInstance().getNetworks();
		
		GraphMerger merger = new NTGraphMerger(currentGraphs, matching);
		
		ObserverDialog dialog = new ObserverDialog(merger);
		
		merger.launch();
		
		dialog.showProgress();
		
		if(!merger.hasCompleted()) {
			if(!merger.wasInterruptedByUser()) {
				JOptionPane.showMessageDialog(ToolsPanel.this, "Error while displaying the result", "Error", JOptionPane.ERROR_MESSAGE);
			}
		} else {
			
			JFrame frm = new JFrame("Result");
			DrawPanel pan = DrawPanelFactory.getInstance().newViewPanel(merger.getResult());
			
			frm.setContentPane(pan);
			frm.setExtendedState(JFrame.MAXIMIZED_BOTH);
			frm.setVisible(true);
		}
	}
	
	protected static class ResultPart {
		
		protected Set<Matching> matching;
		protected int prog;
		
		public ResultPart(Set<Matching> matching, int prog) {
			this.matching = matching;
			this.prog = prog;
		}
		
		@Override
		public String toString() {
			return "Alignment #" + prog;
		}
		
	} 
}
