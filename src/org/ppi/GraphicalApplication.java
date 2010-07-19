package org.ppi;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.UIManager;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import org.ppi.gui.window.MainWindow;
import org.ppi.preference.Preferences;

public class GraphicalApplication {

	protected static Logger logger;
	
	public static void main(String[] args) {
		
		PropertyConfigurator.configure(GraphicalApplication.class.getResource("/log4j.properties"));
		
		logger = Logger.getLogger(GraphicalApplication.class);
		
		logger.info("Application Started");
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch(Exception ex) {
		}
		
		MainWindow window = new MainWindow();
		
		window.build();
		window.setVisible(true);
		
		window.addWindowListener(new WindowAdapter() {
			
			@Override
			public void windowClosing(WindowEvent e) {
				
				logger.info("Storing preferences");
				
				try {
					Preferences.getInstance().persist();					
				} catch (Exception e2) {
				}
				
				logger.info("Application Closed");
				System.exit(0);
			}
			
		});
		
	}
	
}
