package org.ppi.gui.model;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.table.AbstractTableModel;

import org.ppi.common.Tuple;
import org.ppi.core.graph.Graph;
import org.ppi.core.graph.Node;


public class NetworkModel extends AbstractTableModel {
	
	private static final long serialVersionUID = 1L;
	
	List<Tuple<String>> graphTable;
	
	public NetworkModel(Graph graph) {
		this.graphTable = new ArrayList<Tuple<String>>();
		
		Set<String> eqAdded = new HashSet<String>();
		
		for(Node n : graph.getNodes()) {
			for(Node n2 : graph.getAdjacent(n)) {
				if(n.getName().compareTo(n2.getName())<0 || (n.getName().compareTo(n2.getName())==0 && !eqAdded.contains(n.getName()))) {
					Tuple<String> t = new Tuple<String>(new String[]{n.getName(), n2.getName()});
					graphTable.add(t);
					if(n.getName().equals(n2.getName())) {
						eqAdded.add(n.getName());
					}
				}
			}
		}
		
		Collections.sort(graphTable);
	}
	
	@Override
	public int getColumnCount() {
		return 2;
	}
	
	@Override
	public String getColumnName(int column) {
		if(column==0) {
			return "Node 1";
		} else if(column==1) {
			return "Node 2";
		}
		return super.getColumnName(column);
	}
	
	@Override
	public int getRowCount() {
		return graphTable.size();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		return graphTable.get(rowIndex).get(columnIndex);
	}
	
	
}
