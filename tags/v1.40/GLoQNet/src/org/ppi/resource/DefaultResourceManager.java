package org.ppi.resource;

import java.net.URL;

import javax.swing.Icon;
import javax.swing.ImageIcon;


public class DefaultResourceManager implements ResourceManager {
	
protected static final String BASE_PATH = "/res/";
	
	@Override
	public URL getResource(int code) {
		switch(code) {
		case NODE_BTN:
			return this.getClass().getResource(BASE_PATH+"node.png");
		case MOVE_BTN:
			return this.getClass().getResource(BASE_PATH+"move.png");
		case EDGE_BTN:
			return this.getClass().getResource(BASE_PATH+"edge.png");
		case SAVE:
			return this.getClass().getResource(BASE_PATH+"save.png");
		}
		
		throw new IllegalArgumentException("Unknown code");
	}
	
	@Override
	public Icon getResourceAsIcon(int code) {
		URL res = getResource(code);
		return new ImageIcon(res);
	}
}
