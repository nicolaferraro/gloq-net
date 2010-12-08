package org.ppi.gui.screen;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.filechooser.FileFilter;

import org.ppi.common.manager.NetworkManager;
import org.ppi.core.graph.Graph;
import org.ppi.core.parse.network.AbstractNetworkParser;
import org.ppi.core.parse.network.NetworkParserFactory;
import org.ppi.core.parse.network.NetworkParserResult;
import org.ppi.gui.execute.ObserverDialog;
import org.ppi.gui.file.FileChooserManager;

public class NetworkPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	
	JTabbedPane tabsPane;
	JButton btnAdd;
	

	public NetworkPanel() {
		setup();
		setControls();
	}

	protected void setup() {

		this.setLayout(new BorderLayout());
		this.setBorder(BorderFactory.createTitledBorder("Networks: "));
		
		tabsPane = new JTabbedPane();

		this.add(tabsPane, BorderLayout.CENTER);

		JPanel buttPane = new JPanel();

		BoxLayout lay = new BoxLayout(buttPane, BoxLayout.Y_AXIS);
		buttPane.setLayout(lay);
		buttPane.setPreferredSize(new Dimension(90, 150));

		btnAdd = new JButton("Add...");
		btnAdd.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		buttPane.add(Box.createVerticalStrut(15));
		buttPane.add(btnAdd);
		buttPane.add(Box.createGlue());

		this.add(buttPane, BorderLayout.EAST);

	}

	protected void setControls() {

		this.btnAdd.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = FileChooserManager.getInstance().newFileChooser();

				chooser.resetChoosableFileFilters();
				chooser.setAcceptAllFileFilterUsed(false);
				for (String n : NetworkParserFactory.getInstance().getParserNames()) {
					chooser.addChoosableFileFilter(new NetworkFilter(n));
				}

				int res = JFileChooser.CANCEL_OPTION;
				boolean go = false;

				while (!go) {
					res = chooser.showOpenDialog(NetworkPanel.this);
					FileFilter fil = chooser.getFileFilter();
					if (res == JFileChooser.APPROVE_OPTION && !(fil instanceof NetworkFilter)) {
						JOptionPane.showMessageDialog(NetworkPanel.this, "Select a graph parser", "Error", JOptionPane.ERROR_MESSAGE);
					} else {
						go = true;
					}
				}

				if (res == JFileChooser.APPROVE_OPTION) {

					try {

						File f = chooser.getSelectedFile();

						NetworkFilter fil = (NetworkFilter) chooser.getFileFilter();

						String cl = fil.getDescription();

						Class<? extends AbstractNetworkParser> parseClass = NetworkParserFactory.getInstance().getClass(cl);

						AbstractNetworkParser parser = parseClass.newInstance();
						parser.setFile(f);

						ObserverDialog dialog = new ObserverDialog(parser);
						
						parser.launch();
						
						dialog.showProgress();

						if (!parser.hasCompleted()) {
							if (!parser.wasInterruptedByUser()) {
								JOptionPane.showMessageDialog(NetworkPanel.this, "Error while parsing the file", "Error", JOptionPane.ERROR_MESSAGE);
							}
						} else {
							NetworkParserResult parserRes = parser.getResult();
							if (parserRes.getErrorCount() > 0) {
								int c = JOptionPane.showConfirmDialog(NetworkPanel.this, "The parser found " + parserRes.getErrorCount() + " error(s) while reading the file. Continue?", "Warning", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
								if (c == JOptionPane.YES_OPTION) {
									addGraph(parserRes.getGraph());
								}
							} else {
								addGraph(parserRes.getGraph());
							}
						}

					} catch (Exception ex) {
						JOptionPane.showMessageDialog(NetworkPanel.this, "Generic error while parsing the file", "Error", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});

	}
	
	private void addGraph(Graph g) {
		NetworkViewer v = new NetworkViewer(g);
		NetworkManager.getInstance().addNetwork(g);
		
		JPanel cont = new JPanel(new BorderLayout());
		cont.add(v);
		
		JPanel clsPan = new JPanel();
		
		JButton btnClose = new JButton("Remove");
		
		clsPan.add(btnClose);
		
		cont.add(clsPan, BorderLayout.SOUTH);
		
		btnClose.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				int pos = tabsPane.getSelectedIndex();
				NetworkManager.getInstance().removeNetworkAt(pos);
				tabsPane.removeTabAt(pos);
			}
		});
		
		tabsPane.addTab(g.getName(), cont);
	}
	
	protected static class NetworkFilter extends FileFilter {

		String description;

		public NetworkFilter(String descritpion) {
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
