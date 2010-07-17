package org.ppi.resource;

import java.net.URL;

import javax.swing.Icon;

public interface ResourceManager {
	
	public static final int NODE_BTN=0, MOVE_BTN=1, EDGE_BTN=2;
	
	public URL getResource(int code);
	
	public Icon getResourceAsIcon(int code);
	
}
