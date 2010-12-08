package org.ppi.gui.user;

public class UserInterfaceFactory {

	private static UserInterfaceFactory instance;
	private UserInterface ui;
	
	private UserInterfaceFactory() {
		ui = new UserInterfaceImpl();
	}
	
	public static UserInterfaceFactory getInstance() {
		if(instance==null)
			instance = new UserInterfaceFactory();
		return instance;
	}
	
	public UserInterface getDefaultUserInterface() {
		return ui;
	}
	
}
