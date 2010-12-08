package org.ppi.shell;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class ParameterParser {
	
	protected String[] args;
	
	public ParameterParser(String[] args) {
		this.args = args;
	}
	
	public List<File> getNetworkFiles() {
		List<String> fileNames = getParameters("-n");
		List<File> files = new LinkedList<File>();
		for(String fn : fileNames) {
			File f = new File(fn);
			files.add(f);
		}
		return files;
	}
	
	public List<File> getDictionaryFiles() {
		List<String> fileNames = getParameters("-d");
		List<File> files = new LinkedList<File>();
		for(String fn : fileNames) {
			File f = new File(fn);
			files.add(f);
		}
		return files;
	}
	
	public AlignmentChoice getAlignmentChoice() {
		List<String> aligns = getParameters("-a");
		
		for(String align : aligns) {
			for(AlignmentChoice choice : AlignmentChoice.values()) {
				if(choice.name().equalsIgnoreCase(align))
					return choice;
			}
		}
		
		return null;
	}
	
	public ApplicationType getApplicationType() {
		List<String> aligns = getParameters("-t");
		
		for(String align : aligns) {
			for(ApplicationType choice : ApplicationType.values()) {
				if(choice.name().equalsIgnoreCase(align))
					return choice;
			}
		}
		
		return null;
	}
	
	protected List<String> getParameters(String optionName) {
		List<String> pars = new LinkedList<String>();
		for(int i=0; i+1<args.length; i+=2) {
			String option = args[i];
			String value = args[i+1];
			if(optionName.equalsIgnoreCase(option)) {
				pars.add(value);
			}
		}
		return pars;
	}
	
	
	
}
