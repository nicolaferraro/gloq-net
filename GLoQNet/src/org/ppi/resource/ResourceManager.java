package org.ppi.resource;

import java.awt.Image;
import java.net.URL;

import javax.swing.Icon;

public interface ResourceManager {
	
public static final int NODE_BTN=0, MOVE_BTN=1, EDGE_BTN=2, SAVE=3, ICON=4;
	
	public URL getResource(int code);
	
	public Icon getResourceAsIcon(int code);
	
	public Image getResourceAsImage(int code);
	
}
