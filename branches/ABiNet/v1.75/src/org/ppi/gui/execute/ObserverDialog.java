package org.ppi.gui.execute;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import org.ppi.common.execute.Executable;
import org.ppi.common.execute.ExecutionObserver;

public class ObserverDialog extends JDialog {
	
	private static final long serialVersionUID = 1L;

	Executable<? extends Object> executable;
	
	ObserverPanel observerPanel;
	
	public ObserverDialog(Executable<? extends Object> executable) {
		this.executable = executable;
		
		setup();
		setControls();
	}
	
	
	protected void setup() {
		
		observerPanel = new ObserverPanel(executable);
		
		this.setContentPane(observerPanel);
		
		this.setTitle("Progress");
		
		this.setModal(true);
		
		this.pack();
		
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension size = getSize();
		
		this.setLocation((screen.width - size.width) / 2,
				(screen.height - size.height) / 2);
		
		this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		
	}
	
	protected void setControls() {
		
		executable.addExecutionObserver(new ExecutionObserver() {
			
			@Override
			public void currentOperation(String operation) {
			}
			
			@Override
			public void executionCompleted() {
				ObserverDialog.this.setVisible(false);
			}
			
			@Override
			public void executionFailed() {
				JOptionPane.showMessageDialog(ObserverDialog.this, "Error while executing the current operation", "Error", JOptionPane.ERROR_MESSAGE);
				ObserverDialog.this.setVisible(false);
			}
			
			@Override
			public void executionInterrupted() {
				ObserverDialog.this.setVisible(false);
			}
			
			@Override
			public void executionStarted() {
			}
			
			@Override
			public void percentExecuted(int percent) {
			}
			
			@Override
			public void percentageUnavailable() {
				
			}
			
		});
		
		this.addWindowListener(new WindowAdapter() {
			
			@Override
			public void windowClosing(WindowEvent e) {
				executable.interrupt();
			}
			
		});
		
	}
	
	
	public synchronized void showProgress() {
		this.setVisible(executable.needWaiting());
	}
	
}
