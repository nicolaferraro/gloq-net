package org.ppi.core.dictionary;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.ppi.common.Observable;
import org.ppi.common.ObservableObject;
import org.ppi.common.Observer;


public class Dictionary implements Observable {
	
	protected List<Entry> dict;
	protected ObservableObject observableDelegate;
	
	public Dictionary() {
		this.dict = new ArrayList<Entry>();
		this.observableDelegate = new ObservableObject(this);
	}
	
	public Dictionary(Dictionary copy) {
		this.dict = new ArrayList<Entry>(copy.dict);
		this.observableDelegate = new ObservableObject(this);
	}
	
	public void addEntry(String n1, String n2, double value) {
		
		Entry e = new Entry(n1, n2, value);
		
		int pos = Collections.binarySearch(dict, e);
		
		if(pos>=0) {
			
			Entry dictEntry = dict.get(pos);
			dictEntry.value = (dictEntry.value + value) / 2;
			
		} else {
			
			int ins = -pos - 1;
			dict.add(ins, e); // non efficiente, usare setEntries se possibile
			
		}
		
		observableDelegate.notifyObservers();
	}
	
	public double getSimilarity(String n1, String n2) {

		Entry e = new Entry(n1, n2, 0);
		
		int pos = Collections.binarySearch(dict, e);
		
		if(pos>=0) {
			Entry dictEntry = dict.get(pos);
			return dictEntry.value;
		}
		
		return 0;
	}
	
	public void mergeEntries(List<Entry> entries) {
		List<Entry> toAdd = new ArrayList<Entry>();
		
		for(Entry e : entries) {
			
			int pos = Collections.binarySearch(dict, e);
			
			if(pos>=0) {
				Entry dictEn = dict.get(pos);
				Logger.getLogger(this.getClass()).info("Found duplicate entry: (" + e.getNode1() + ", "+e.getNode2()+") -> "+dictEn.getValue()+"/"+e.getValue());
				dictEn.value = (dictEn.value + e.value) / 2;
			} else {
				toAdd.add(e);
			}
			
		}
		
		dict.addAll(toAdd);
		
		Collections.sort(dict);
		
		observableDelegate.notifyObservers();
	}
	
	public void mergeDictionary(Dictionary nDict) {
		mergeEntries(nDict.dict);
	}
	
	public Entry getEntryAt(int pos) {
		return dict.get(pos);
	}
	
	public int size() {
		return dict.size();
	}
	
	public void clear() {
		dict.clear();
		observableDelegate.notifyObservers();
	}
	
	public void removeEntry(String n1, String n2) {
		removeEntry(new Entry(n1, n2, 0));
	}
	
	public void removeEntry(Entry e) {
		
		int pos = Collections.binarySearch(dict, e);
		
		if(pos>=0) {
			dict.remove(pos);
		}
		
	}
	
	public void removeNode(String n) {
		
		List<Entry> lst = new LinkedList<Entry>();
		
		for(Entry e : dict) {
			if(n.equals(e.getNode1()) || n.equals(e.getNode2()))
				lst.add(e);
		}
		
		dict.removeAll(lst);
		
	}
	
	public boolean containsNode(String n) {
		for(Entry e : dict) {
			if(n.equals(e.getNode1()) || n.equals(e.getNode2()))
				return true;
		}
		return false;
	}
	
	public static class Entry implements Comparable<Entry> {
		
		String node1;
		String node2;
		double value;
		
		protected Entry(String node1, String node2, double value) {
			this.node1=node1;
			this.node2=node2;
			this.value=value;
			
			if(this.node1.compareTo(this.node2)>0) {
				this.node2 = node1;
				this.node1 = node2;
			}
		}
		
		public String getNode1() {
			return node1;
		}
		
		public String getNode2() {
			return node2;
		}
		
		public double getValue() {
			return value;
		}
		
		@Override
		public int compareTo(Entry o) {
			if(!this.node1.equals(o.node1)) {
				return this.node1.compareTo(o.node1);
			}
			
			if(!this.node2.equals(o.node2)) {
				return this.node2.compareTo(o.node2);
			}
			
			return 0;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (obj instanceof Entry) {
				Entry o = (Entry) obj;
				return this.node1.equals(o.node1) && this.node2.equals(o.node2);
			}
			return false;
		}
		
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
