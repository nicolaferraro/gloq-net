package org.ppi.core.parse.result.parser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

import org.ppi.common.result.Matching;
import org.ppi.core.graph.Node;
import org.ppi.core.graph.NodeFactory;
import org.ppi.core.parse.result.AbstractResultParser;
import org.ppi.core.parse.result.ResultParserResult;
import org.ppi.preference.Constants;

public class DefaultResultParser extends AbstractResultParser {

	public DefaultResultParser() {
	}

	@Override
	protected void execute() throws Exception {

		BufferedReader br = new BufferedReader(new FileReader(file));

		String line;
		Set<Set<Matching>> alignment = new TreeSet<Set<Matching>>(new Comparator<Set<Matching>>() {
			@Override
			public int compare(Set<Matching> o1, Set<Matching> o2) {
				if(o1.size()!=o2.size())
					return o2.size() - o1.size();
				return o1.hashCode()-o2.hashCode();
			}
		});
		int errorCount = 0;
		Integer rowLength = null;
		
		signalCurrentOperation("Loading the file");
		
		Set<Matching> currentMatchings = new HashSet<Matching>();
		
		while ((line = br.readLine()) != null) {
			StringTokenizer st = new StringTokenizer(line, " \t");
			
			List<String> lineRes = new LinkedList<String>();

			while (st.hasMoreTokens()) {
				String s = st.nextToken();
				lineRes.add(s);
			}
			
			if(lineRes.get(0).equals(Constants.RESULT_MATCHINGS_SEPARATOR)) {
				alignment.add(currentMatchings);
				currentMatchings = new HashSet<Matching>();
				continue;
			}
			
			lineRes.remove(lineRes.size()-1); // matching value
			lineRes.remove(lineRes.size()-1); // score
			
			List<Node> nodes = new ArrayList<Node>();
			for(String s : lineRes) {
				nodes.add(NodeFactory.getInstance().createNode(s));
			}
			
			if (rowLength==null) {
				rowLength = lineRes.size();
				Matching m = new Matching(nodes);
				currentMatchings.add(m);
			} else {
				if(nodes.size()==rowLength) {
					Matching m = new Matching(nodes);
					currentMatchings.add(m);
				} else {
					errorCount++;
				}
			}
			
			checkPoint();
		}
		
		if(currentMatchings.size()>0) {
			alignment.add(currentMatchings);
		}

		result = new ResultParserResult();
		
		result.setErrorCount(errorCount);
		result.setAlignments(alignment);

	}

}
