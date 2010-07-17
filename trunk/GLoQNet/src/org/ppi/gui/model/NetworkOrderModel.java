package org.ppi.gui.model;

import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractListModel;

import org.ppi.common.Observer;
import org.ppi.common.manager.NetworkManager;
import org.ppi.core.graph.Graph;

public class NetworkOrderModel extends AbstractListModel implements Observer {
	
	private static final long serialVersionUID = 1L;
	
	List<Graph> orderedNetworks;
	
	public NetworkOrderModel() {
		
		orderedNetworks = new ArrayList<Graph>();
		
		NetworkManager.getInstance().addObserver(this);
	}
	
	public int moveDown(int index) {
		if(index<orderedNetworks.size()) {
			Graph temp = orderedNetworks.get(index);
			orderedNetworks.set(index, orderedNetworks.get(index + 1));
			orderedNetworks.set(index + 1, temp);
			
			fireContentsChanged(this, 0, orderedNetworks.size());
			return index + 1;
		}
		return index;
	}
	
	public int moveUp(int index) {
		if(index>0) {
			Graph temp = orderedNetworks.get(index);
			orderedNetworks.set(index, orderedNetworks.get(index - 1));
			orderedNetworks.set(index - 1, temp);
			
			fireContentsChanged(this, 0, orderedNetworks.size());
			return index - 1;
		}
		return index;
	}
	
	@Override
	public void notifyChanged(Object source) {
		orderedNetworks.clear();
		orderedNetworks.addAll(NetworkManager.getInstance().getNetworks());
		fireContentsChanged(this, 0, orderedNetworks.size());
	}
	
	@Override
	public Object getElementAt(int index) {
		return orderedNetworks.get(index);
	}

	@Override
	public int getSize() {
		return orderedNetworks.size();
	}
	
	public List<Graph> getOrderedNetworks() {
		return orderedNetworks;
	}
}
