package org.ppi.gui.config;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;

import org.ppi.core.graph.Graph;
import org.ppi.gui.model.NetworkOrderModel;


public class NetworkOrderPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	
	NetworkOrderModel model;
	JList netList;
	
	JButton btnUp;
	JButton btnDown;
	
	public NetworkOrderPanel() {
		setup();
		setControls();
	}

	private void setup() {
		this.setBorder(BorderFactory.createTitledBorder("Network Order: "));
		
		this.setLayout(new BorderLayout());
		
		model = new NetworkOrderModel();
		netList = new JList(model);
		
		this.add(netList);
		
		JPanel btns = new JPanel();
		
		btnUp = new JButton("Up");
		btnDown = new JButton("Down");
		
		btns.add(btnUp);
		btns.add(btnDown);
		
		this.add(btns, BorderLayout.SOUTH);
	}
	
	private void setControls() {
		
		btnUp.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				int sel = netList.getSelectedIndex();
				if(sel>=0) {
					int nPos = model.moveUp(sel);
					netList.setSelectedIndex(nPos);
				}
			}
		});
		
		btnDown.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				int sel = netList.getSelectedIndex();
				if(sel>=0) {
					int nPos = model.moveDown(sel);
					netList.setSelectedIndex(nPos);
				}
			}
		});
		
	}
	
	public List<Graph> getOrderedNetworks() {
		return model.getOrderedNetworks();
	}
	
}
