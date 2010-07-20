package org.ppi.gui.screen;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.log4j.Logger;

import org.ppi.preference.Preferences;
import org.ppi.preference.SimilarityStrategy;

public class PreferencePanel extends JPanel {

	private static final long serialVersionUID = 1646584294028525220L;
	
	JTextField txtSimilarityThreshold;
	JTextField txtDepth;
	JComboBox cmbSimilarityStrategy;
	JTextField txtBranchLimit;
	JTextField txtSpecialStateEmission;
	JTextField txtEscapeStateEmission;
	JTextField txtMatchingTransition;
	JTextField txtFailingTransition;
	JTextField txtQueryingSubgraphsMaxDistance;
	
	JButton btnSave;
	JButton btnRestore;
	
	public PreferencePanel() {
		setup();
		setControls();
	}
	
	private void setup() {
		
		setBorder(BorderFactory.createTitledBorder("Preferences:"));
		
		setLayout(new BorderLayout());
		
		JPanel p = new JPanel(new GridLayout(9, 3));
		
		Preferences pref = Preferences.getInstance();
		
		JLabel label;
		
		label = new JLabel("Similarity Threshold:");
		p.add(encapsulate(label));
		txtSimilarityThreshold = new JTextField(""+pref.getSimilarityThreshold());
		p.add(encapsulate(txtSimilarityThreshold));
		
		p.add(new JPanel()); // separator
		
		label = new JLabel("Levels:");
		p.add(encapsulate(label));
		txtDepth = new JTextField(""+pref.getDepth());
		p.add(encapsulate(txtDepth));
		
		p.add(new JPanel()); // separator
		
		label = new JLabel("Similarity Strategy:");
		p.add(encapsulate(label));
		cmbSimilarityStrategy = new JComboBox();
		cmbSimilarityStrategy.setModel(new DefaultComboBoxModel() {

			private static final long serialVersionUID = -890760327164834720L;

			@Override
			public Object getElementAt(int index) {
				return SimilarityStrategy.values()[index];
			}
			
			@Override
			public int getSize() {
				return SimilarityStrategy.values().length;
			}
			
		});
		cmbSimilarityStrategy.setSelectedItem(pref.getSimilarityStrategy());
		p.add(encapsulate(cmbSimilarityStrategy));
		
		p.add(new JPanel()); // separator
		
		label = new JLabel("Branch Limit:");
		p.add(encapsulate(label));
		txtBranchLimit = new JTextField(""+pref.getBranchLimit());
		p.add(encapsulate(txtBranchLimit));
		
		p.add(new JPanel()); // separator
		
		label = new JLabel("Special States Emission:");
		p.add(encapsulate(label));
		txtSpecialStateEmission = new JTextField(""+pref.getSpecialStatesEmission());
		p.add(encapsulate(txtSpecialStateEmission));
		
		p.add(new JPanel()); // separator
		
		label = new JLabel("Escape States Emission:");
		p.add(encapsulate(label));
		txtEscapeStateEmission = new JTextField(""+pref.getEscapeStatesEmission());
		p.add(encapsulate(txtEscapeStateEmission));
		
		p.add(new JPanel()); // separator
		
		label = new JLabel("Matching Transition:");
		p.add(encapsulate(label));
		txtMatchingTransition = new JTextField(""+pref.getMatchingTransition());
		p.add(encapsulate(txtMatchingTransition));
		
		p.add(new JPanel()); // separator
		
		label = new JLabel("Failing Transition:");
		p.add(encapsulate(label));
		txtFailingTransition = new JTextField(""+pref.getFailingTransition());
		p.add(encapsulate(txtFailingTransition));
		
		p.add(new JPanel()); // separator
		
		label = new JLabel("Max Subgraphs Distance (Querying):");
		p.add(encapsulate(label));
		txtQueryingSubgraphsMaxDistance = new JTextField(""+pref.getQueryingSubgraphsMaxDistance());
		p.add(encapsulate(txtQueryingSubgraphsMaxDistance));
		
		p.add(new JPanel()); // separator
		
		this.add(p);
		
		JPanel btns = new JPanel();
		
		btnRestore = new JButton("Restore Defaults");
		btns.add(btnRestore);
		
		btnSave = new JButton("Apply");
		btns.add(btnSave);
		
		this.add(btns, BorderLayout.SOUTH);
		
	}
	
	private void setControls() {
		
		btnSave.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				try {
					
					Preferences pref = new Preferences();
					
					pref.setBranchLimit(Integer.parseInt(txtBranchLimit.getText()));
					pref.setDepth(Integer.parseInt(txtDepth.getText()));
					pref.setEscapeStatesEmission(Double.parseDouble(txtEscapeStateEmission.getText()));
					pref.setFailingTransition(Double.parseDouble(txtFailingTransition.getText()));
					pref.setMatchingTransition(Double.parseDouble(txtMatchingTransition.getText()));
					pref.setSimilarityStrategy((SimilarityStrategy)cmbSimilarityStrategy.getSelectedItem());
					pref.setSimilarityThreshold(Double.parseDouble(txtSimilarityThreshold.getText()));
					pref.setSpecialStatesEmission(Double.parseDouble(txtSpecialStateEmission.getText()));
					pref.setQueryingSubgraphsMaxDistance(Integer.parseInt(txtQueryingSubgraphsMaxDistance.getText()));
					
					changePreferences(pref);
					
				} catch(Exception ex) {
					Logger.getLogger(this.getClass()).error("Error while saving preferences", ex);
					JOptionPane.showMessageDialog(PreferencePanel.this, "There are errors in the form. Please find and correct them.", "Error", JOptionPane.ERROR_MESSAGE);
				}
				
				
			}
		});
		
		btnRestore.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					
					Preferences pref = new Preferences();
					
					changePreferences(pref);
					
				} catch(Exception ex) {
					JOptionPane.showMessageDialog(PreferencePanel.this, "Unable to load default preferences", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		
	}
	
	protected void changePreferences(Preferences pref) throws Exception {
		
		Preferences oldPref = Preferences.getInstance();
		
		pref.persist();
		
		Preferences.setInstance(pref);
		
		reloadValues();
		
		if(oldPref.getSimilarityThreshold()!=pref.getSimilarityThreshold()) {
			JOptionPane.showMessageDialog(PreferencePanel.this, "The similarity threshold has changed. Please reload the dictionary.", "Warning", JOptionPane.WARNING_MESSAGE);
		}
	}
	
	public void reloadValues() {
		
		Preferences pref = Preferences.getInstance();
		
		txtBranchLimit.setText(""+pref.getBranchLimit());
		txtDepth.setText(""+pref.getDepth());
		txtEscapeStateEmission.setText(""+pref.getEscapeStatesEmission());
		txtFailingTransition.setText(""+pref.getFailingTransition());
		txtMatchingTransition.setText(""+pref.getMatchingTransition());
		txtSimilarityThreshold.setText(""+pref.getSimilarityThreshold());
		txtSpecialStateEmission.setText(""+pref.getSpecialStatesEmission());
		cmbSimilarityStrategy.setSelectedItem(pref.getSimilarityStrategy());
		txtQueryingSubgraphsMaxDistance.setText("" + pref.getQueryingSubgraphsMaxDistance());
		
	}
	
	private JComponent encapsulate(JComponent comp) {
		
		boolean label = comp instanceof JLabel;
		if(comp instanceof JTextField) {
			((JTextField) comp).setColumns(30);
		}
		JPanel pan = new JPanel();
		FlowLayout lay = new FlowLayout((label) ? FlowLayout.RIGHT : FlowLayout.LEFT);
		pan.setLayout(lay);
		pan.add(comp);
		
		return pan;
		
	}
	
}
