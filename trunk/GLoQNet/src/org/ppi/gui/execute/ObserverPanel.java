package org.ppi.gui.execute;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import org.ppi.common.execute.Executable;
import org.ppi.common.execute.ExecutionObserver;


public class ObserverPanel extends JPanel {
	
	private static final long serialVersionUID = 1L;

	Executable<? extends Object> executable;
	
	JProgressBar progressBar;
	JButton cancelBtn;
	JLabel currentOperationLab;
	
	public ObserverPanel(Executable<? extends Object> executable) {
		this.executable = executable;
		
		setup();
		setControls();
		
	}

	protected void setup() {
		
		BoxLayout lay = new BoxLayout(this, BoxLayout.Y_AXIS);
		this.setLayout(lay);
		
		this.setBorder(BorderFactory.createTitledBorder("Progress:"));
		
		Box barBox = Box.createHorizontalBox();
		barBox.setAlignmentX(Component.LEFT_ALIGNMENT);

		progressBar = new JProgressBar(0, 100);
		progressBar.setIndeterminate(true);
		cancelBtn = new JButton("Cancel");
		barBox.add(progressBar);
		barBox.add(cancelBtn);
		
		currentOperationLab = new JLabel(" ");
		currentOperationLab.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		add(barBox);
		add(currentOperationLab);

	}
	
	protected void setControls() {
		
		this.executable.addExecutionObserver(new ExecutionObserver() {
			
			@Override
			public void currentOperation(String operation) {
				currentOperationLab.setText(operation);
			}
			
			@Override
			public void executionCompleted() {
				currentOperationLab.setText("Completed");
			}
			
			@Override
			public void executionFailed() {
			}
			
			@Override
			public void executionInterrupted() {
			}
			
			@Override
			public void executionStarted() {
			}
			
			@Override
			public void percentExecuted(int percent) {
				progressBar.setIndeterminate(false);
				progressBar.setValue(percent);
			}
			
			@Override
			public void percentageUnavailable() {
				progressBar.setIndeterminate(true);
			}
			
		});
		
		
		this.cancelBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				cancelBtn.setEnabled(false);
				executable.interrupt();
			}
			
		});
		
	}
	
}
