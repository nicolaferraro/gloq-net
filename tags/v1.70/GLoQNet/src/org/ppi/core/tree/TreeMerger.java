package org.ppi.core.tree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ppi.common.execute.Executable;
import org.ppi.core.dictionary.Dictionary;
import org.ppi.core.dictionary.DictionaryHelper;
import org.ppi.core.graph.Node;
import org.ppi.preference.Preferences;

public class TreeMerger extends Executable<Set<TreeNode>> {
	
	private static final Node FAKE_NODE = new Node("FAKE");
	
	Set<? extends TreeNode> masterTrees;
	Set<SingleTreeNode> slaveTrees;
	Dictionary dict;
	HashMap<Pair, Pair> cache;
	Map<List<Node>, Node> currentAlignment;
	
	public TreeMerger(Set<? extends TreeNode> masterTrees, Set<SingleTreeNode> slaveTrees, Dictionary dict, Map<List<Node>, Node> currentAlignment) {
		this.masterTrees=masterTrees;
		this.slaveTrees=slaveTrees;
		this.dict=dict;
		this.cache = new HashMap<Pair,Pair>();
		this.currentAlignment=currentAlignment;
	}
	
	public TreeMerger(Set<? extends TreeNode> masterTrees, Set<SingleTreeNode> slaveTrees, Dictionary dict) {
		this(masterTrees, slaveTrees, dict, null);
	}
	
	@Override
	protected void execute() {
		
		FakeTreeNode base1 = new FakeTreeNode();
		for(TreeNode nt : masterTrees) {
			base1.addChild(nt);
		}
		
		FakeTreeNode base2 = new FakeTreeNode();
		for(SingleTreeNode nt : slaveTrees) {
			base2.addChild(nt);
		}
		
		TreeNode merge = match(base1, base2, 0);
		
		Set<TreeNode> mergeSet = new HashSet<TreeNode>();
		for(TreeNode nt : merge.getChildren()) {
			nt.setParent(null);
			mergeSet.add(nt);
		}
		
		checkPoint();
		
		result = mergeSet;
	}
	
	private TreeNode match(TreeNode nt1, SingleTreeNode nt2, int lev) {
		if(bestMatch(nt1, nt2, lev).getBestMatch()==0)
			throw new RuntimeException();
		
		List<TreeNode> c1 = nt1.getChildren();
		List<TreeNode> c2 = nt2.getChildren();
		
		List<Node> nmerge = new LinkedList<Node>(nt1.getNodes());
		nmerge.add(nt2.getUniqueNode());
		TreeNode bin = new TreeNode(nmerge);
		
		if(c1.size()==0 || c2.size()==0) {
			return bin;
		}
		List<Pair> values = new ArrayList<Pair>();
		
		for(TreeNode n1c : c1) {
			for(TreeNode n2c : c2) {
				Pair p = bestMatch(n1c, (SingleTreeNode) n2c, lev+1);
				int i=values.size()-1;
				while(i>=0 && i<values.size() && values.get(i).getBestMatch()<p.getBestMatch()) {
					i--;
				}
				i++;
				values.add(i, p);
				if(values.size()>Preferences.getInstance().getBranchLimit())
					values.remove(values.size()-1);
				
				checkPoint();
			}
			
			checkPoint();
		}
		
		for(Pair p : values) {
			if(p.getBestMatch()==0)
				break;
			bin.addChild(match(p.getNT1(), p.getNT2(), lev+1));
		}
		
		checkPoint();
		
		return bin;
	}
	
	private Pair bestMatch(TreeNode nt1, SingleTreeNode nt2, int lev) {
		Pair pair = new Pair(nt1, nt2);
		if((nt1 instanceof FakeTreeNode) && (nt2 instanceof FakeTreeNode)) {
			pair.setBestMatch(1);
			return pair;
		}
		if(cache.containsKey(pair)) {
			return cache.get(pair);
		}
		double match = DictionaryHelper.getInstance().getSimilarity(nt1.getNodes(), nt2.getUniqueNode(), dict);
		if(lev==1 && currentAlignment!=null) {
			
			List<Node> nl = new LinkedList<Node>(nt1.getNodes());
			nl.add(nt2.getUniqueNode());
			
			if(!currentAlignment.containsKey(nl)) {
				match=0d;
			}
		}
		
		if(match==0d) {
			pair.setBestMatch(match);
			return pair;
		}
		
		List<TreeNode> c1 = nt1.getChildren();
		List<TreeNode> c2 = nt2.getChildren();
		if(c1.size()==0 || c2.size()==0) {
			pair.setBestMatch(match);
			return pair;
		}
		
		double sum = 0;
		for(TreeNode n1c : c1) {
			double max = Double.NEGATIVE_INFINITY;
			for(TreeNode n2c : c2) {
				double val = bestMatch(n1c, (SingleTreeNode) n2c, lev+1).getBestMatch();
				max = Math.max(val, max);
			}
			sum+=max;
		}
		pair.setBestMatch(sum+match);
		cache.put(pair, pair);
		return pair;
	}
	
	static class Pair {
		
		TreeNode nT1;
		SingleTreeNode nT2;
		double bestMatch;

		public Pair(TreeNode nt1, SingleTreeNode nt2) {
			super();
			this.nT1 = nt1;
			this.nT2 = nt2;
		}

		public double getBestMatch() {
			return bestMatch;
		}
		
		public void setBestMatch(double bestMatch) {
			this.bestMatch = bestMatch;
		}
		
		public TreeNode getNT1() {
			return nT1;
		}

		public void setNT1(TreeNode nt1) {
			this.nT1 = nt1;
		}

		public SingleTreeNode getNT2() {
			return nT2;
		}

		public void setNT2(SingleTreeNode nt2) {
			this.nT2 = nt2;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (obj instanceof Pair) {
				Pair p = (Pair) obj;
				return p.getNT1().equals(this.getNT1()) && p.getNT2().equals(this.getNT2());
			}
			return false;
		}
		
		@Override
		public int hashCode() {
			return nT1.hashCode()*nT2.hashCode();
		}
		
		@Override
		public String toString() {
			return "("+nT1+","+nT2+")";
		}
		
	}
	
	class FakeTreeNode extends SingleTreeNode {
		public FakeTreeNode() {
			super(FAKE_NODE);
		}
	}
}
