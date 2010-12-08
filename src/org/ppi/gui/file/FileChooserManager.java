package org.ppi.gui.file;

import javax.swing.JFileChooser;

public class FileChooserManager {

	protected static FileChooserManager instance;
	
	protected JFileChooser lastFileChooser;
	
	public FileChooserManager() {
		
	}
	
	public static FileChooserManager getInstance() {
		if(instance==null)
			instance = new FileChooserManager();
		return instance;
	}
	
	public synchronized JFileChooser newFileChooser() {
		JFileChooser fc = new JFileChooser();
		
		if(lastFileChooser!=null) {
			fc.setCurrentDirectory(lastFileChooser.getCurrentDirectory());
		}
		
		lastFileChooser = fc;
		
		return fc;
	}
	
}
