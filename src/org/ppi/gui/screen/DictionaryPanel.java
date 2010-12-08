package org.ppi.gui.screen;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.filechooser.FileFilter;

import org.ppi.common.manager.DictionaryManager;
import org.ppi.core.parse.dictionary.AbstractDictionaryParser;
import org.ppi.core.parse.dictionary.DictionaryParserFactory;
import org.ppi.core.parse.dictionary.DictionaryParserResult;
import org.ppi.gui.execute.ObserverDialog;
import org.ppi.gui.file.FileChooserManager;
import org.ppi.gui.model.DictionaryModel;

public class DictionaryPanel extends JPanel {
	
	private static final long serialVersionUID = 1L;

	JTable dictTable;
	DictionaryModel dictModel;
	
	JButton btnClear;
	JButton btnAdd;
	
	
	public DictionaryPanel() {
		setup();
		setControls();
	}
	
	protected void setup() {
		
		this.setBorder(BorderFactory.createTitledBorder("Dictionary: "));
		this.setLayout(new BorderLayout());
		
		
		this.dictModel = new DictionaryModel(DictionaryManager.getInstance().getDictionary());
		
		dictTable = new JTable(dictModel);
		
		this.add(new JScrollPane(dictTable), BorderLayout.CENTER);
		
		btnClear = new JButton("Clear");
		
		btnAdd = new JButton("Add...");
		
		JPanel buttonsPane = new JPanel();
		
		buttonsPane.add(btnClear);
		buttonsPane.add(btnAdd);
		
		this.add(buttonsPane, BorderLayout.SOUTH);
		
	}
	
	protected void setControls() {
		
		this.btnAdd.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
				JFileChooser chooser = FileChooserManager.getInstance().newFileChooser();
				chooser.setAcceptAllFileFilterUsed(false);
				chooser.resetChoosableFileFilters();
				
				for(String n : DictionaryParserFactory.getInstance().getParserNames()) {
					chooser.addChoosableFileFilter(new DictionaryFilter(n));
				}
				
				int res = JFileChooser.CANCEL_OPTION;
				boolean go=false;
				
				while(!go) {
					res = chooser.showOpenDialog(DictionaryPanel.this);
					FileFilter fil = chooser.getFileFilter();
					if(res == JFileChooser.APPROVE_OPTION && !(fil instanceof DictionaryFilter)) {
						JOptionPane.showMessageDialog(DictionaryPanel.this, "Select a graph parser", "Error", JOptionPane.ERROR_MESSAGE);
					} else {
						go = true;
					}
				}
				
				if(res == JFileChooser.APPROVE_OPTION) {
					
					try {
						
						File f = chooser.getSelectedFile();
						
						DictionaryFilter fil = (DictionaryFilter) chooser.getFileFilter();
						
						String cl = fil.getDescription();
						
						Class<? extends AbstractDictionaryParser> parseClass = DictionaryParserFactory.getInstance().getClass(cl);
						
						AbstractDictionaryParser parser = parseClass.newInstance();
						parser.setFile(f);
						
						ObserverDialog dialog = new ObserverDialog(parser);
						
						parser.launch();
						
						dialog.showProgress();
						
						if(!parser.hasCompleted()) {
							if(!parser.wasInterruptedByUser()) {
								JOptionPane.showMessageDialog(DictionaryPanel.this, "Error while parsing the file", "Error", JOptionPane.ERROR_MESSAGE);
							}
						} else {
							DictionaryParserResult parserRes = parser.getResult();
							if(parserRes.getErrorCount()>0) {
								int c = JOptionPane.showConfirmDialog(DictionaryPanel.this, "The parser found "+parserRes.getErrorCount()+" error(s) while reading the file. Continue?", "Warning", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
								if(c==JOptionPane.YES_OPTION) {
									DictionaryManager.getInstance().getDictionary().mergeDictionary(parserRes.getDictionary());
								}
							} else {
								DictionaryManager.getInstance().getDictionary().mergeDictionary(parserRes.getDictionary());
							}
						}
						
					} catch(Exception ex) {
						JOptionPane.showMessageDialog(DictionaryPanel.this, "Generic error while parsing the file", "Error", JOptionPane.ERROR_MESSAGE);
					}
					
				}
				
			}
		});
		
		btnClear.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				DictionaryManager.getInstance().getDictionary().clear();
			}
		});
		
	}
	
	protected static class DictionaryFilter extends FileFilter {
		
		String description;
		
		public DictionaryFilter(String descritpion) {
			this.description = descritpion;
		}
		
		@Override
		public boolean accept(File f) {
			return true;
		}

		@Override
		public String getDescription() {
			return description;
		}
		
	}
	
}
