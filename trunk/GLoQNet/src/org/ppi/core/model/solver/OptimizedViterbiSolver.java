package org.ppi.core.model.solver;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.ppi.common.execute.Executable;
import org.ppi.common.result.Matching;
import org.ppi.core.dictionary.Dictionary;
import org.ppi.core.graph.Node;
import org.ppi.core.model.Model;
import org.ppi.core.model.NodeState;
import org.ppi.core.model.State;
import org.ppi.core.model.Symbol;
import org.ppi.core.model.TransitionType;
import org.ppi.preference.Preferences;


public class OptimizedViterbiSolver extends Executable<Matching> {
	
	Model hmm;
	List<Symbol> tour;
	Dictionary dict;
	
	public OptimizedViterbiSolver(Model hmm, List<Symbol> tour, Dictionary dict) {
		this.hmm = hmm;
		this.tour = tour;
		this.dict = dict;
	}
	
	@Override
	protected void execute() throws Exception {
		
		signalCurrentOperation("Finding a matching");
		
		TreeMap<Configuration, PairOfDouble> states = new TreeMap<Configuration, PairOfDouble>();
		states.put(new Configuration(hmm.getBeginState(), null), new PairOfDouble(Math.log(1), Math.log(1)));
		
		int i=0;
		int n = tour.size();
		int lastPerc = 0;
		for(Symbol sym : tour) {
			if(i*100/n>lastPerc) {
				lastPerc = i*100/n;
				signalPercentExecuted(lastPerc);
			}
			i++;
			
			TreeMap<Configuration, PairOfDouble> nextStates = new TreeMap<Configuration, PairOfDouble>();
			
			for(Map.Entry<Configuration, PairOfDouble> en : states.entrySet()) {
				Configuration cf = en.getKey();
				PairOfDouble prePair = en.getValue();
				
				Map<State, TransitionType> trans = hmm.getTransitions(cf.getState());
				
				for(Map.Entry<State, TransitionType> t : trans.entrySet()) {
					boolean matchingTrans;
					double prob;
					
					switch(t.getValue()) {
					case MATCHING_TRANSITION:
						prob = Preferences.getInstance().getMatchingTransition();
						matchingTrans = true;
						break;
					case FAILING_TRANSITION:
						prob = Preferences.getInstance().getFailingTransition();
						matchingTrans = false;
						break;
					default:
						throw new RuntimeException("Unknown transition type");
					}
					
					State dest = t.getKey();
					double nextProb = prePair.value + Math.log(prob) + Math.log(dest.outputProbability(sym, dict));
					
					double nextScoringProb = prePair.scoringValue;
					if(matchingTrans) {
						nextScoringProb = prePair.scoringValue + dest.outputProbability(sym, dict);
					}
					
					if(nextProb==Double.NEGATIVE_INFINITY)
						continue;
					
					Matching chosenMatching = cf.getChosenMatching();
					if(chosenMatching == null) {
						if(dest instanceof NodeState) {
							NodeState ns = (NodeState) dest;
							List<Node> nodes = new ArrayList<Node>(ns.getNodes());
							nodes.add(sym.getNode());
							chosenMatching = new Matching(nodes);
						}
					}
					
					Configuration ncf = new Configuration(dest, chosenMatching);
					
					if(!nextStates.containsKey(ncf)) {
						nextStates.put(ncf, new PairOfDouble(nextProb, nextScoringProb));
					} else {
						double oldVal = nextStates.get(ncf).value;
						if(nextProb>oldVal) {
							nextStates.remove(ncf);
							nextStates.put(ncf, new PairOfDouble(nextProb, nextScoringProb));
						}
					}
					
					checkPoint();
				}
				
				checkPoint();				
			}
			
			states = nextStates;
			
			checkPoint();
		}
		
		Matching max = null;
		double logVal = Double.NEGATIVE_INFINITY;
		for(Map.Entry<Configuration, PairOfDouble> en : states.entrySet()) {
			if(en.getValue().value>logVal) {
				logVal = en.getValue().value;
				max = en.getKey().getChosenMatching();
				if(max!=null) {
					max.setLogScore(logVal);
					max.setMatchingTransitionsLogScore(en.getValue().scoringValue);
				}
				
				checkPoint();
			}
		}
		
		checkPoint();
		
		result = max;
	}
	
	class Configuration implements Comparable<Configuration> {
		
		State state;
		Matching chosenMatching;
		
		public Configuration(State state, Matching chosenMatching) {
			super();
			this.state = state;
			this.chosenMatching = chosenMatching;
		}

		public State getState() {
			return state;
		}

		public void setState(State state) {
			this.state = state;
		}

		public Matching getChosenMatching() {
			return chosenMatching;
		}
		
		public void setChosenMatching(Matching chosenMatching) {
			this.chosenMatching = chosenMatching;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof OptimizedViterbiSolver.Configuration) {
				Configuration c = (Configuration) obj;
				return c.state.equals(this.state);
			}
			return false;
		}
		
		@Override
		public int hashCode() {
			return state.hashCode();
		}

		@Override
		public String toString() {
			return state.toString()+"("+chosenMatching+")";
		}
		
		@Override
		public int compareTo(Configuration o) {
			if(this.hashCode()<o.hashCode())
				return -1;
			if(this.hashCode()>o.hashCode())
				return 1;
			return 0;
		}
		
	}
	
	class PairOfDouble {
		protected double value;
		protected double scoringValue;
		
		public PairOfDouble(double value, double scoringValue) {
			this.value = value;
			this.scoringValue = scoringValue;
		}
		
	}
	
}

