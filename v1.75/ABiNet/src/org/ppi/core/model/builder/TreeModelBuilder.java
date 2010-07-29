package org.ppi.core.model.builder;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ppi.common.execute.Executable;
import org.ppi.core.dictionary.Dictionary;
import org.ppi.core.graph.Node;
import org.ppi.core.model.EscapeState;
import org.ppi.core.model.MatchedNodeState;
import org.ppi.core.model.Model;
import org.ppi.core.model.NodeState;
import org.ppi.core.model.SpecialState;
import org.ppi.core.model.State;
import org.ppi.core.model.Symbol;
import org.ppi.core.tree.TreeNode;

import static org.ppi.core.model.TransitionType.*;

public class TreeModelBuilder extends Executable<Model> {

	Set<? extends TreeNode> trees;
	Dictionary dict;
	Map<List<Node>, Node> currentAlignment;
	
	public TreeModelBuilder(Set<? extends TreeNode> trees, Dictionary dict, Map<List<Node>, Node> currentAlignment) {
		this.trees=trees;
		this.dict=dict;
		this.currentAlignment=currentAlignment;
	}
	
	public TreeModelBuilder(Set<? extends TreeNode> trees, Dictionary dict) {
		this(trees, dict, null);
	}
	
	@Override
	protected void execute() {
		
		signalCurrentOperation("Computing the tree structure");
		
		Map<State, TreeNode> pointers = new HashMap<State, TreeNode>();
		
		Model hmm = new Model();
		
		SpecialState begin = new SpecialState("Begin");
		hmm.add(begin);
		hmm.setTransitionType(begin, begin, FAILING_TRANSITION);
		hmm.setBeginState(begin);
		
		SpecialState end = new SpecialState("End");
		hmm.add(end);
		hmm.setTransitionType(end, end, FAILING_TRANSITION);
		
		LinkedList<State> q = new LinkedList<State>();
		
		int numGround = 0;
		
		for(TreeNode nt : trees) {
			State ns = null;
			if(currentAlignment!=null) { // local
				ns = new MatchedNodeState(new Symbol(currentAlignment.get(nt.getNodes()), 0));
				numGround += nt.getChildren().size();
			} else {
				ns = new NodeState(nt.getNodes(), 0);
			}
			pointers.put(ns, nt);
			
			hmm.add(ns);
			hmm.setTransitionType(begin, ns, MATCHING_TRANSITION);
			if(currentAlignment==null) { // global
				hmm.setTransitionType(ns, end, FAILING_TRANSITION);
			}
			q.add(ns);
		}
		
		while(q.size()>0) {
			
			State ns = q.removeFirst();
			List<TreeNode> children = pointers.get(ns).getChildren();
			
			int outgoing = children.size();
			if(pointers.get(ns).getParent()!=null)
				outgoing++;
			
			int nsDepth;
			if(ns instanceof NodeState) {
				nsDepth = ((NodeState)ns).getLevel();
			} else {
				nsDepth = 0;
			}
			
			for(TreeNode c : children) {
				NodeState nsc = new NodeState(c.getNodes(), nsDepth+1);
				hmm.add(nsc);
				hmm.setTransitionType(ns, nsc, MATCHING_TRANSITION);
				
				pointers.put(nsc, c);
				
				if(numGround>0) {
					hmm.setTransitionType(nsc, end, FAILING_TRANSITION);
					numGround--;
				} else {
					hmm.setTransitionType(nsc, ns, MATCHING_TRANSITION);
				}
				
				q.add(nsc);
			}
			
			if(!(ns instanceof MatchedNodeState)) {
				EscapeState eps = new EscapeState(nsDepth+1);
				hmm.add(eps);
				hmm.setTransitionType(ns, eps, FAILING_TRANSITION);
				hmm.setTransitionType(eps, eps, FAILING_TRANSITION);
				hmm.setTransitionType(eps, ns, FAILING_TRANSITION);
			}
			
			checkPoint();
		}
		
		checkPoint();
		
		result = hmm;
	}

}
