package org.ppi;

import org.ppi.shell.ApplicationType;
import org.ppi.shell.ParameterParser;

public class Application {

	public static void main(String[] args) throws Exception {
		
		ParameterParser parser = new ParameterParser(args);
		
		ApplicationType type = parser.getApplicationType();
		
		if(type==ApplicationType.SHELL) {
			ShellApplication.main(args);
		} else {
			GraphicalApplication.main(args);
		}
		
	}
	
}
