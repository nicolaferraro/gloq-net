package org.ppi.gui.model;


import javax.swing.table.AbstractTableModel;

import org.ppi.common.Observer;
import org.ppi.core.dictionary.Dictionary;
import org.ppi.core.dictionary.Dictionary.Entry;


public class DictionaryModel extends AbstractTableModel implements Observer {
	
	private static final long serialVersionUID = 1L;
	
	Dictionary dictionary;
	
	public DictionaryModel(Dictionary dict) {
		this.dictionary = dict;
		dictionary.addObserver(this);
	}
	
	@Override
	public void notifyChanged(Object source) {
		this.fireTableDataChanged();
	}

	@Override
	public int getColumnCount() {
		return 3;
	}

	@Override
	public int getRowCount() {
		return dictionary.size();
	}
	
	@Override
	public String getColumnName(int column) {
		if(column==0) {
			return "Node 1";
		} else if(column==1) {
			return "Node 2";
		} else if(column==2) {
			return "Similarity";
		}
		return super.getColumnName(column);
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Entry e = dictionary.getEntryAt(rowIndex);
		if(columnIndex==0)
			return e.getNode1();
		else if(columnIndex==1)
			return e.getNode2();
		return e.getValue();
	}
	
	
}
