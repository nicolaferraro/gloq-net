package org.ppi.gui.draw;

import org.ppi.gui.graph.VisualGraph;
import org.ppi.gui.label.LabelGenerator;
import org.ppi.gui.label.TextLabelGenerator;
import org.ppi.gui.user.UserInterface;
import org.ppi.gui.user.UserInterfaceFactory;
import org.ppi.resource.DefaultResourceManager;
import org.ppi.resource.ResourceManager;

public class DrawPanelFactory {
	
	private static DrawPanelFactory instance;
	
	public static DrawPanelFactory getInstance() {
		if(instance==null)
			instance = new DrawPanelFactory();
		return instance;
	}
	
	private DrawPanelFactory() {
	}
	
	public DrawPanel newDrawPanel() {
		
		LabelGenerator gen = new TextLabelGenerator();
		
		UserInterface ui = UserInterfaceFactory.getInstance().getDefaultUserInterface();
		
		ResourceManager resMan = new DefaultResourceManager();
		
		DrawArea p = new DrawArea(false);
		p.setLabelGenerator(gen);
		p.setUserInterface(ui);
		
		DrawAreaController c = new DrawAreaController(false, false);
		c.setDrawPanel(p);
		c.setResourceManager(resMan);
		c.init();
		
		DrawPanel dp = new DrawPanel(p, c);

		return dp;
		
	}
	
	public DrawPanel newViewPanel(VisualGraph vg) {
		LabelGenerator gen = new TextLabelGenerator();
		
		UserInterface ui = UserInterfaceFactory.getInstance().getDefaultUserInterface();
		
		ResourceManager resMan = new DefaultResourceManager();
		
		DrawArea p = new DrawArea(vg, true);
		p.setLabelGenerator(gen);
		p.setUserInterface(ui);
		
		DrawAreaController c = new DrawAreaController(true, true);
		c.setDrawPanel(p);
		c.setResourceManager(resMan);
		c.init();
		
		DrawPanel dp = new DrawPanel(p, c);

		return dp;
		
	} 
	
}
