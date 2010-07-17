package org.ppi.gui.user;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.ppi.core.graph.Graph;
import org.ppi.core.graph.Node;
import org.ppi.gui.graph.VisualGraph;


public class UserInterfaceImpl implements UserInterface {

	@Override
	public boolean canDeleteNode(Node n, Graph g, Container c) {
		int res = JOptionPane.showConfirmDialog(c, "Delete the selected node ?", "Delete confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
		if(res==JOptionPane.YES_OPTION)
			return true;
		return false;
	}
	
	@Override
	public boolean canDeleteEdge(Node n1, Node n2, Graph g, Container c) {
		int res = JOptionPane.showConfirmDialog(c, "Delete the selected edge ?", "Delete confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
		if(res==JOptionPane.YES_OPTION)
			return true;
		return false;
	}
	
	@Override
	public void editNodeProperties(Node n, VisualGraph vg, Container c) {
		PropertyEditor editor = new PropertyEditor();
		editor.setNodeName(n.getName());
		editor.init();
		editor.setVisible(true);
		if(editor.isChangeConfirmed()) {
			String newName = editor.getNodeName();
			String oldName = n.getName();
			if(!newName.equals(oldName) && vg.getGraph().getNodes().contains(new Node(newName))) {
				JOptionPane.showMessageDialog(c, "Name already present in the network. Choose another one.", "Error", JOptionPane.ERROR_MESSAGE);
			} else {
				vg.changeNodeName(n, editor.getNodeName());
			}
		}
	}
	
	
	class PropertyEditor extends JDialog {
		
		private static final long serialVersionUID = 1L;
		
		JButton okBtn;
		JButton cancBtn;
		JTextField nameTxt;
		
		boolean changeConfirmed;
		
		String nodeName;
		
		public PropertyEditor() {
			
		}
		
		public void setNodeName(String nodeName) {
			this.nodeName = nodeName;
		}
		
		public String getNodeName() {
			return nodeName;
		}
		
		public void init() {
			
			changeConfirmed = false;
			
			this.setTitle("Edit Node");
			this.setModal(true);
			
			this.setLayout(new BorderLayout());
			
			JPanel propPanel = new JPanel();
			propPanel.setBorder(BorderFactory.createTitledBorder("Properties:"));
			propPanel.setLayout(new BoxLayout(propPanel, BoxLayout.Y_AXIS));
			
			Box nameBox = Box.createHorizontalBox();
			JLabel nameLab = new JLabel("Name:");
			nameTxt = new JTextField(nodeName);
			nameTxt.setMaximumSize(new Dimension(400, nameTxt.getPreferredSize().height));
			nameBox.add(nameLab);
			nameBox.add(nameTxt);
			
			propPanel.add(nameBox);
			
			propPanel.add(Box.createVerticalStrut(10));
			
			
			
			propPanel.add(Box.createGlue());
			
			this.add(propPanel);
			
			JPanel btnPanel = new JPanel();
			okBtn = new JButton("Ok");
			cancBtn = new JButton("Cancel");
			btnPanel.add(okBtn);
			btnPanel.add(cancBtn);
			
			this.add(btnPanel, BorderLayout.SOUTH);
			
			this.pack();
			Dimension sc = Toolkit.getDefaultToolkit().getScreenSize();
			Dimension s = getSize();
			this.setLocation((sc.width-s.width)/2, (sc.height-s.height)/2);
			
			setControls();
		}
		
		public boolean isChangeConfirmed() {
			return changeConfirmed;
		}
		
		protected void setControls() {
			
			okBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					PropertyEditor.this.nodeName = nameTxt.getText();
					changeConfirmed = true;
					PropertyEditor.this.setVisible(false);
				}
			});
			
			cancBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					PropertyEditor.this.setVisible(false);
				}
			});
			
		}
		
	}
}
