package org.ppi.gui.draw;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import org.ppi.core.graph.Graph;
import org.ppi.core.graph.Node;
import org.ppi.core.graph.NodeFactory;
import org.ppi.gui.draw.overlay.OverlayComponent;
import org.ppi.gui.graph.VisualGraph;
import org.ppi.gui.label.LabelGenerator;
import org.ppi.gui.user.UserInterface;

public class DrawArea extends JPanel {

	private static final long serialVersionUID = 1L;
	
	public static final int MINIMUM_WIDTH = 600;
	public static final int MINIMUM_HEIGHT = 400;
	
	public static final int MODE_MOVE = 0, MODE_DRAW = 1, MODE_EDGE = 2, MODE_VIEW = 3;
	public static final int DEFAULT_MODE = MODE_DRAW;
	
	public static final float NODE_STROKE_WIDTH = 1.2f;
	public static final Color NODE_BORDER = Color.BLACK;
	public static final int NODE_MARGIN = 5;
	
	public static final Color EDGE_COLOR = Color.BLACK;
	public static final float EDGE_STROKE_WIDTH = 2.0f;
	public static final Color EDGE_CONSTRUCTION = Color.GRAY;
	
	public static final Color SPECIAL_EDGE_COLOR = new Color(150, 150, 150);
	public static final float SPECIAL_EDGE_STROKE_WIDTH = 2.0f;
	
	public static final Color GRAPH_BACKGROUND = Color.WHITE;
	public static final Color NODE_COLOR = new Color(255, 255, 210);
	public static final Color TEXT_COLOR = Color.BLACK;
	
	public static final int REPAINT_MARGIN = 4;
	public static final int EDGE_SELECT_MARGIN = 5;
	
	
	protected LabelGenerator labelGenerator;
	protected UserInterface userInterface;
	
	Lock lock;
	
	VisualGraph visualGraph;
	protected List<OverlayComponent> overlayComponents;
	protected int mode;
	protected boolean readOnly;
	
	public DrawArea(boolean readOnly) {
		this(new VisualGraph(), readOnly);
	}
	
	public DrawArea(VisualGraph visualGraph, boolean readOnly) {
		this.readOnly = readOnly;
		this.lock = new ReentrantLock(true);
		this.visualGraph = visualGraph;
		overlayComponents = new ArrayList<OverlayComponent>();
		
		this.setBackground(GRAPH_BACKGROUND);
		
		setMode((readOnly) ? MODE_MOVE : DEFAULT_MODE);
	}
	
	public boolean isReadOnly() {
		return readOnly;
	}
	
	public Graph getDrawnGraph() {
		return visualGraph.getGraph();
	}
	
	public VisualGraph getDrawnVisualGraph() {
		return visualGraph;
	}
	
	public void setLabelGenerator(LabelGenerator labelGenerator) {
		this.labelGenerator = labelGenerator;
	}
	
	public void setUserInterface(UserInterface userInterface) {
		this.userInterface = userInterface;
	}
	
	public void setMode(int mode) {
		lock.lock();
		
		MouseListener[] mls = this.getMouseListeners();
		for(MouseListener ml : mls) {
			this.removeMouseListener(ml);
		}
		
		MouseMotionListener[] mmls = this.getMouseMotionListeners();
		for(MouseMotionListener mml : mmls) {
			this.removeMouseMotionListener(mml);
		}
		
		switch(mode) {
		case MODE_DRAW:
			this.addMouseListener(new DrawMouseListener());
			this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			this.addMouseListener(new ContextMenuListener());
			break;
		case MODE_EDGE:
			EdgeMouseListener elst = new EdgeMouseListener();
			this.addMouseListener(elst);
			this.addMouseMotionListener(elst);
			this.addMouseListener(new ContextMenuListener());
			this.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
			break;
		case MODE_MOVE:
			MoveMouseListener lst = new MoveMouseListener();
			this.addMouseListener(lst);
			this.addMouseMotionListener(lst);
			this.addMouseListener(new ContextMenuListener());
			this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			break;
		case MODE_VIEW:
			this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			break;
		}
		
		this.mode = mode;
		
		lock.unlock();
	}
	
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		lock.lock();
		
		// Print special edges
		g2.setColor(SPECIAL_EDGE_COLOR);
		g2.setStroke(new BasicStroke(SPECIAL_EDGE_STROKE_WIDTH, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 10.0f, new float[] {16.0f,20.0f}, 0.0f));
		for(int i=0; i<visualGraph.getzOrder().size(); i++) {
			Node n1 = visualGraph.getzOrder().get(i);
			Point p1 = visualGraph.getPositions().get(n1);
			Node n2 = visualGraph.getAdditionalLinks().get(n1);
			if(n2 != null) {
				Point p2 = visualGraph.getPositions().get(n2);
				if(p1.x!=p2.x || p1.y!=p2.y) {
					g2.drawLine(p1.x, p1.y, p2.x, p2.y);
				}
			}
		}
		
		// Print edges
		g2.setColor(EDGE_COLOR);
		g2.setStroke(new BasicStroke(EDGE_STROKE_WIDTH));
		for(int i=0; i<visualGraph.getzOrder().size(); i++) {
			Node n1 = visualGraph.getzOrder().get(i);
			Point p1 = visualGraph.getPositions().get(n1);
			for(Node n2 : visualGraph.getGraph().getAdjacent(n1)) {
				int j = visualGraph.getzOrder().indexOf(n2);
				if(j<i)
					continue; // Print edges only once
				Point p2 = visualGraph.getPositions().get(n2);
				if(p1.x!=p2.x || p1.y!=p2.y) {
					g2.drawLine(p1.x, p1.y, p2.x, p2.y);
				}
			}
		}
		
		// Print nodes
		for(Node n : visualGraph.getzOrder()) {
			Point p = visualGraph.getPositions().get(n);
			int radius = getNodeRadius(n);
			g2.setColor(NODE_COLOR);
			g2.fillOval(p.x-radius, p.y-radius, 2*radius, 2*radius);
			g2.setColor(NODE_BORDER);
			g2.setStroke(new BasicStroke(NODE_STROKE_WIDTH));
			g2.drawOval(p.x-radius, p.y-radius, 2*radius, 2*radius);
			g2.setColor(TEXT_COLOR);
			FontMetrics fm = g2.getFontMetrics();
			g2.drawString(n.getName(), p.x-fm.stringWidth(n.getName())/2, p.y-fm.getHeight()/2+fm.getAscent());
		}
		
		for(OverlayComponent o : overlayComponents) {
			o.paintOverlay(g2);
		}
		
		lock.unlock();
	}
	
	protected Node getMatchingNode(int x, int y) {
		for(int i=visualGraph.getzOrder().size()-1; i>=0; i--) {
			Node n = visualGraph.getzOrder().get(i);
			Point p = visualGraph.getPositions().get(n);
			double dist = Math.sqrt(Math.pow(x-p.x, 2) + Math.pow(y-p.y, 2));
			if(dist<=getNodeRadius(n)) {
				return n;
			}
		}
		return null;
	}
	
	protected int getNodeRadius(Node n) {
		Graphics g2 = getGraphics();
		FontMetrics fm = g2.getFontMetrics();
		int w = fm.stringWidth(n.getName());
		return w/2 + 2*NODE_MARGIN;
	}
	
	protected GraphicEdge getMatchingEdge(int x, int y) {
		
		for(int i=0; i<visualGraph.getzOrder().size(); i++) {
			for(int j=i+1; j<visualGraph.getzOrder().size(); j++) {
				Node n1 = visualGraph.getzOrder().get(i);
				Node n2 = visualGraph.getzOrder().get(j);
				if(visualGraph.getGraph().areAdjacent(n1, n2)) {
					GraphicEdge e = new GraphicEdge();
					e.n1=n1;
					e.n2=n2;
					Point p1 = visualGraph.getPositions().get(n1);
					Point p2 = visualGraph.getPositions().get(n2);
					if(p1.x==p2.x) {
						if(Math.abs(x-p1.x)<=EDGE_SELECT_MARGIN && y<=Math.max(p1.y, p2.y) && y>=Math.min(p1.y, p2.y))
							return e;
					} else if(p1.y==p2.y) {
						if(Math.abs(y-p1.y)<=EDGE_SELECT_MARGIN && x<=Math.max(p1.x, p2.x) && x>=Math.min(p1.x, p2.x))
							return e;
					} else {
						if(x>=Math.min(p1.x, p2.x) && x<=Math.max(p1.x, p2.x)) {
							if(y>=Math.min(p1.y, p2.y) && y<=Math.max(p1.y, p2.y)) {
								Point left = p1.x<p2.x ? p1 : p2;
								Point right = p1.x<p2.x ? p2 : p1;
								double m = ((double)(right.y-left.y))/((double)(right.x-left.x));
								double q = left.y - m*left.x;
								double num = Math.abs(m*x-y+q);
								double den = Math.sqrt(Math.pow(m, 2) + 1);
								double dist = num/den;
								if(dist<=EDGE_SELECT_MARGIN) {
									return e;
								}
							}
						}
					}
				}
			}
		}
		
		return null;
	}
	
	class DrawMouseListener extends MouseAdapter {
		
		@Override
		public void mouseReleased(MouseEvent e) {
			if(e.getButton()!=MouseEvent.BUTTON1)
				return;
			
			lock.lock();
			
			Node n = NodeFactory.getInstance().createNode(labelGenerator.nextLabel());
			visualGraph.getGraph().addNode(n);
			visualGraph.getzOrder().add(n);
			visualGraph.getPositions().put(n, new Point(e.getX(), e.getY()));
			int radius = getNodeRadius(n);
			DrawArea.this.repaint(e.getX()-radius-REPAINT_MARGIN, e.getY()-radius-REPAINT_MARGIN,2*radius+2*REPAINT_MARGIN,2*radius+2*REPAINT_MARGIN);
			refreshSize();
			lock.unlock();
		}
		
	}
	
	class EdgeOverlay implements OverlayComponent {
		
		Point start;
		Point end;
		
		@Override
		public void paintOverlay(Graphics2D g) {
			if(start!=null && end!=null) {
				if(start.x!=end.x || start.y!=end.y) {
					g.setColor(EDGE_CONSTRUCTION);
					g.setStroke(new BasicStroke(EDGE_STROKE_WIDTH));
					g.drawLine(start.x, start.y, end.x, end.y);
				}
			}
		}
		
		public void setStart(Point start) {
			this.start = start;
		}
		
		public void setEnd(Point end) {
			this.end = end;
		}
		
	}
	
	class EdgeMouseListener extends MouseAdapter {
		
		Node startingNode;
		EdgeOverlay overlay;
		
		public EdgeMouseListener() {
			overlay = new EdgeOverlay();
		}
		
		@Override
		public synchronized void mousePressed(MouseEvent e) {
			lock.lock();
			
			startingNode = getMatchingNode(e.getX(), e.getY());
			
			if(startingNode!=null) {
				overlay.setStart(visualGraph.getPositions().get(startingNode));
				DrawArea.this.overlayComponents.add(overlay);
			}
			
			lock.unlock();
		}
		
		@Override
		public synchronized void mouseDragged(MouseEvent e) {
			if(startingNode!=null) {
				lock.lock();
				
				overlay.setEnd(new Point(e.getX(), e.getY()));
				DrawArea.this.repaint();
				
				lock.unlock();
			}
		}
		
		@Override
		public synchronized void mouseReleased(MouseEvent e) {
			if(startingNode!=null) {
				lock.lock();
				
				DrawArea.this.overlayComponents.remove(overlay);
				
				Node destNode = getMatchingNode(e.getX(), e.getY());
				if(destNode!=null && destNode!=startingNode) {
					visualGraph.getGraph().addEdge(startingNode, destNode);
				}
				startingNode = null;
				
				DrawArea.this.repaint();
				
				lock.unlock();
			}
		}
		
	}
	
	class MoveMouseListener extends MouseAdapter {
		
		Node selectedNode;
		
		@Override
		public synchronized void mousePressed(MouseEvent e) {
			lock.lock();
			
			selectedNode = getMatchingNode(e.getX(), e.getY());
			if(selectedNode!=null) {
				visualGraph.getzOrder().remove(selectedNode);
				visualGraph.getzOrder().add(selectedNode);
				DrawArea.this.repaint();
			}
			
			lock.unlock();
			
			
			
		}
		
		@Override
		public void mouseMoved(MouseEvent e) {
			lock.lock();
			
			Node n = getMatchingNode(e.getX(), e.getY());
			if(n!=null) {
				DrawArea.this.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
			} else {
				DrawArea.this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}
			lock.unlock();
		}
		
		@Override
		public synchronized void mouseDragged(MouseEvent e) {
			if(selectedNode!=null) {
				lock.lock();
				
				visualGraph.getPositions().put(selectedNode, new Point(e.getX(), e.getY()));
				DrawArea.this.repaint();
				refreshSize();
				
				lock.unlock();
			}
		}
		
		@Override
		public synchronized void mouseReleased(MouseEvent e) {
			selectedNode = null;
		}
		
	}
	
	class ContextMenuListener extends MouseAdapter {
		
		@Override
		public void mouseClicked(MouseEvent e) {
			if(readOnly)
				return;
			
			if(e.getButton()!=MouseEvent.BUTTON3)
				return;
			
			lock.lock();
			
			final Node n = getMatchingNode(e.getX(), e.getY());
			final GraphicEdge edge = getMatchingEdge(e.getX(), e.getY());
			
			final JPopupMenu menu = new JPopupMenu();
			JMenuItem delBtn = new JMenuItem("Delete");
			JMenuItem propBtn = new JMenuItem("Properties...");
			menu.add(delBtn);
			menu.addSeparator();
			menu.add(propBtn);
			
			
			if(n!=null) {	
				
				delBtn.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						if(userInterface.canDeleteNode(n, visualGraph.getGraph(), DrawArea.this)) {
							lock.lock();
							
							visualGraph.getzOrder().remove(n);
							visualGraph.getPositions().remove(n);
							visualGraph.getGraph().deleteNode(n);
							DrawArea.this.repaint(); // can have edges
							
							lock.unlock();
						}
					}
				});
				
				propBtn.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						lock.lock();
						userInterface.editNodeProperties(n, visualGraph, DrawArea.this);
						DrawArea.this.repaint();
						lock.unlock();
					}
				});
				
				
			} else if(edge != null) {
				
				delBtn.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						if(userInterface.canDeleteEdge(edge.n1, edge.n2, visualGraph.getGraph(), DrawArea.this)) {
							lock.lock();
							
							visualGraph.getGraph().deleteEdge(edge.n1, edge.n2);
							DrawArea.this.repaint();
							
							lock.unlock();
						}
					}
				});
				
				propBtn.setEnabled(false);
				
			} else {
				delBtn.setEnabled(false);
				propBtn.setEnabled(false);
			}
			
			menu.show(DrawArea.this, e.getX(), e.getY());
			
			lock.unlock();
		}
		
	}
	
	class GraphicEdge {
		Node n1;
		Node n2;
	} 
	
	protected Dimension computeDimensions() {
		lock.lock();
		try {
			
			int maxX = MINIMUM_WIDTH;
			int maxY = MINIMUM_HEIGHT;
			
			for(Point p : visualGraph.getPositions().values()) {
				maxX = Math.max(maxX, p.x + 50);
				maxY = Math.max(maxY, p.y + 50);
			}
			
			return new Dimension(maxX, maxY);
			
		} finally {
			lock.unlock();
		}
	}
	
	
	
	@Override
	public Dimension getPreferredSize() {
		return computeDimensions();
	}
	
	@Override
	public Dimension getMinimumSize() {
		return new Dimension(MINIMUM_WIDTH, MINIMUM_HEIGHT);
	}
	
	public void refreshSize() {
		invalidate();
		Container c = this;
		while(c!=null && c.getParent()!=null) {
			c = c.getParent();
		}
		
		c.validate();
	}
	
	public BufferedImage exportAsImage() {
		BufferedImage img = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics g = img.createGraphics();
		this.paintComponent(g);
		return img;
	}
	
}
