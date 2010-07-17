package org.ppi.common.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.ppi.common.Observable;
import org.ppi.common.ObservableObject;
import org.ppi.common.Observer;
import org.ppi.core.graph.Graph;


public class NetworkManager implements Observable {
	
	protected static NetworkManager instance;
	
	List<Graph> networks;
	ObservableObject observableDelegate;
	
	protected NetworkManager() {
		this.networks = new ArrayList<Graph>();
		this.observableDelegate = new ObservableObject(this);
	}
	
	public static NetworkManager getInstance() {
		if(instance==null)
			instance = new NetworkManager();
		return instance;
	}
	
	public void addNetwork(Graph g) {
		networks.add(g);
		observableDelegate.notifyObservers();
	}
	
	public void removeNetwork(Graph g) {
		networks.remove(g);
		observableDelegate.notifyObservers();
	}
	
	public void removeNetworkAt(int pos) {
		networks.remove(pos);
		observableDelegate.notifyObservers();
	}
	
	public List<Graph> getNetworks() {
		return Collections.unmodifiableList(networks);
	}

	@Override
	public void addObserver(Observer obs) {
		observableDelegate.addObserver(obs);
	}

	@Override
	public void removeObserver(Observer obs) {
		observableDelegate.removeObserver(obs);
	}
	
}
