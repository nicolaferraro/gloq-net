package org.ppi.resource;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;

public class DefaultResourceManager implements ResourceManager {

	protected static final String BASE_PATH = "/res/";

	@Override
	public URL getResource(int code) {
		switch (code) {
		case NODE_BTN:
			return this.getClass().getResource(BASE_PATH + "node.png");
		case MOVE_BTN:
			return this.getClass().getResource(BASE_PATH + "move.png");
		case EDGE_BTN:
			return this.getClass().getResource(BASE_PATH + "edge.png");
		case SAVE:
			return this.getClass().getResource(BASE_PATH + "save.png");
		case ICON:
			return this.getClass().getResource(BASE_PATH + "icon.png");
		}

		throw new IllegalArgumentException("Unknown code");
	}

	@Override
	public Icon getResourceAsIcon(int code) {
		URL res = getResource(code);
		return new ImageIcon(res);
	}

	@Override
	public Image getResourceAsImage(int code) {
		try {
			URL res = getResource(code);
			BufferedImage image = ImageIO.read(res);
			return image;
		} catch(Exception ex) {
			throw new RuntimeException("Unable to load image resources", ex);
		}
	}

}
