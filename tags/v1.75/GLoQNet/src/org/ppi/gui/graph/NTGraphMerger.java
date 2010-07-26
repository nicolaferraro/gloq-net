package org.ppi.gui.graph;

import java.awt.Point;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ppi.common.result.Matching;
import org.ppi.core.graph.Graph;
import org.ppi.core.graph.Node;

public class NTGraphMerger extends GraphMerger {
	
	public NTGraphMerger(List<Graph> graphs, Set<Matching> alignment) {
		super(graphs, alignment);
	}

	private double EXPECTED_DISTANCE = 500;
	
	@Override
	protected double evaluate(Map<Node, Point> positions, VisualGraph visualGraph) {

		Set<Node> nodes = positions.keySet();
		
		List<Line2D> lines = new ArrayList<Line2D>();
		Set<Point2D> points = new HashSet<Point2D>();
		
		for(Node n1 : nodes) {
			
			Point p1 = positions.get(n1);
			Point2D p2d1 = new Point2D.Double(p1.x, p1.y);
			
			points.add(p2d1);
			
			for(Node n2 : nodes) {
				if(n1==n2 || n1.getName().compareTo(n2.getName())>0)
					continue;
				
				if(visualGraph.getGraph().areAdjacent(n1, n2)) {
					Point p2 = positions.get(n2);
					Point2D p2d2 = new Point2D.Double(p2.x, p2.y);
					Line2D l = new Line2D.Double(p2d1, p2d2);
					lines.add(l);
				}
			}
		}
		
		int intersectionCount = 0;
		for(int i=0; i<lines.size(); i++) {
			Line2D l1 = lines.get(i);
			for(int j=i+1; j<lines.size(); j++) {
				Line2D l2 = lines.get(j);
				
				if(!sameOrigin(l1, l2) && l1.intersectsLine(l2))
					intersectionCount ++;
			}
		}
		
		// compute (avg)-distance
		double distanceSum = 0;
		int counted = 0;
		for(Point2D p1 : points) {
			for(Point2D p2 : points) {
				if(p1==p2)
					continue;
				
				double d = p1.distance(p2);
				
				distanceSum += d;
				counted++;
			}
		}
		
		double distancePenality = 0;
		
		if(distanceSum>0) {
			double avgDistance = distanceSum/counted;
			double frac = avgDistance/EXPECTED_DISTANCE;
			if(frac>1) frac = 1;
			distancePenality = (1-frac) * (points.size() - 1);
		}
		
		
		double penality = intersectionCount + distancePenality;
		
		double maxDisancePoint = points.size()-1;
		double maxIntersectionCount = (lines.size()*(lines.size() - 1))/2;
		
		return maxDisancePoint + maxIntersectionCount - penality;
	}

	private boolean sameOrigin(Line2D l1, Line2D l2) {
		return l1.getP1().equals(l2.getP1()) ||
			l1.getP1().equals(l2.getP2()) ||
			l1.getP2().equals(l2.getP1()) ||
			l1.getP2().equals(l2.getP2());
	}

	
	
}
