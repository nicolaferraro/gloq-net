package org.ppi.gui.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.swing.table.AbstractTableModel;

import org.ppi.common.execute.PartialResultObserver;
import org.ppi.common.manager.DictionaryManager;
import org.ppi.common.result.Matching;
import org.ppi.core.graph.Node;


public class ResultModel extends AbstractTableModel implements
		PartialResultObserver<Set<Matching>> {

	private static final long serialVersionUID = 1L;

	List<Matching> alignment;

	public ResultModel() {
		this.alignment = new ArrayList<Matching>();
	}

	@Override
	public void partialResultComputed(Set<Matching> partialResult) {
		int preSize = alignment.size();
		alignment.addAll(partialResult);
		if (preSize == 0)
			this.fireTableStructureChanged();

		this.fireTableDataChanged();
	}

	@Override
	public int getColumnCount() {
		if (alignment.size() == 0)
			return 0;
		return alignment.get(0).getNodeList().size() + 1;
	}

	@Override
	public int getRowCount() {
		return alignment.size();
	}

	@Override
	public String getColumnName(int column) {
		if (alignment.size()>0 && column == alignment.get(0).getNodeList().size())
			return "Average Similarity";
		return "Protein " + (column + 1);
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Matching m = alignment.get(rowIndex);

		List<Node> nodes = m.getNodeList();

		if (columnIndex < nodes.size()) {
			return nodes.get(columnIndex).getName();
		} else {

			double simSum = 0;
			int count = 0;
			for (int i = 0; i < nodes.size(); i++) {
				for (int j = i + 1; j < nodes.size(); j++) {
					Node n1 = nodes.get(i);
					Node n2 = nodes.get(j);
					double sim = DictionaryManager.getInstance().getDictionary().getSimilarity(n1.getName(), n2.getName());
					if (sim > 0) {
						simSum += sim;
						count++;
					}
				}
			}

			double totalSim = 0d;
			if (count > 0) {
				totalSim = simSum / count;
			}
			
			return "" + totalSim;
		}

	}

	public void clear() {
		this.alignment.clear();
		this.fireTableStructureChanged();
		this.fireTableDataChanged();
	}

	public Collection<Matching> getAlignment() {
		return alignment;
	}
	
	public List<String[]> getModel() {
		List<String[]> mod = new LinkedList<String[]>();
		
		for(int i=0; i<getRowCount(); i++) {
			String[] row = new String[getColumnCount()]; 
			for(int j=0; j<getColumnCount(); j++) {
				Object val = getValueAt(i, j);
				row[j] = (val==null) ? null : val.toString();
			}
			mod.add(row);
		}
		
		return mod;
	}
	
}
