package org.ppi.gui.draw;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.filechooser.FileFilter;

import org.apache.log4j.Logger;

import org.ppi.gui.file.FileChooserManager;
import org.ppi.resource.ResourceManager;

import com.sun.imageio.plugins.jpeg.JPEGImageWriter;

public class DrawAreaController extends JPanel {

	private static final long serialVersionUID = 1L;

	Logger logger = Logger.getLogger(getClass());

	protected DrawArea drawArea;
	protected ResourceManager resourceManager;

	protected static final Color BACKGROUND = Color.WHITE;

	JToggleButton moveBtn;
	JToggleButton drawBtn;
	JToggleButton edgeBtn;

	JButton exportBtn;

	boolean readOnly;
	boolean exportable;

	public DrawAreaController(boolean readOnly, boolean exportable) {
		this.readOnly = readOnly;
		this.exportable = exportable;
	}

	public boolean isReadOnly() {
		return readOnly;
	}

	public void setDrawPanel(DrawArea drawArea) {
		this.drawArea = drawArea;
	}

	public void setResourceManager(ResourceManager resourceManager) {
		this.resourceManager = resourceManager;
	}

	public void init() {

		this.setBackground(BACKGROUND);

		drawBtn = new JToggleButton(resourceManager.getResourceAsIcon(ResourceManager.NODE_BTN));
		drawBtn.setToolTipText("Draw new nodes");
		drawBtn.setEnabled(!readOnly);
		this.add(drawBtn);

		moveBtn = new JToggleButton(resourceManager.getResourceAsIcon(ResourceManager.MOVE_BTN));
		moveBtn.setToolTipText("Move nodes");
		this.add(moveBtn);

		edgeBtn = new JToggleButton(resourceManager.getResourceAsIcon(ResourceManager.EDGE_BTN));
		edgeBtn.setToolTipText("Draw edges");
		edgeBtn.setEnabled(!readOnly);
		this.add(edgeBtn);

		ButtonGroup grp = new ButtonGroup();
		grp.add(drawBtn);
		grp.add(moveBtn);
		grp.add(edgeBtn);

		switch (DrawArea.DEFAULT_MODE) {
		case DrawArea.MODE_DRAW:
			drawBtn.setSelected(true);
			break;
		case DrawArea.MODE_EDGE:
			edgeBtn.setSelected(true);
			break;
		case DrawArea.MODE_MOVE:
			moveBtn.setSelected(true);
			break;
		}

		exportBtn = new JButton(resourceManager.getResourceAsIcon(ResourceManager.SAVE));
		exportBtn.setToolTipText("Export Image...");
		if (exportable) {
			this.add(exportBtn);
		}
		setControls();
	}

	protected void setControls() {

		drawBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				drawArea.setMode(DrawArea.MODE_DRAW);
			}
		});

		moveBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				drawArea.setMode(DrawArea.MODE_MOVE);
			}
		});

		edgeBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				drawArea.setMode(DrawArea.MODE_EDGE);
			}
		});

		exportBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				JFileChooser chooser = FileChooserManager.getInstance().newFileChooser();
				chooser.setAcceptAllFileFilterUsed(false);
				chooser.resetChoosableFileFilters();

				chooser.addChoosableFileFilter(new JpegFilter());

				int res = chooser.showSaveDialog(drawArea);

				boolean err = false;

				if (res == JFileChooser.APPROVE_OPTION) {
					
					ImageOutputStream ios=null;
					
					try {
						File f = chooser.getSelectedFile();
						String fp = f.getCanonicalPath();
						if(!fp.endsWith(".jpg") && !fp.endsWith(".JPG") && !fp.endsWith(".jpeg") && !fp.endsWith(".JPEG")) {
							fp+=".jpg";
							f = new File(fp);
						}
						
						
						Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpg");
						if (writers.hasNext()) {
							JPEGImageWriter wr = (JPEGImageWriter) writers.next();
							ImageWriteParam par = wr.getDefaultWriteParam();
							par.setCompressionMode(JPEGImageWriteParam.MODE_EXPLICIT);
							par.setCompressionQuality(1);
							ios = ImageIO.createImageOutputStream(f);
							wr.setOutput(ios);
							wr.write(drawArea.exportAsImage());
							ios.flush();
							ios.close();
						} else {
							err = true;
						}
					} catch (Exception ex) {
						logger.error("Impossible to store the image", ex);
						err = true;
					} finally {
						try {
							if(ios!=null) ios.close();
						} catch(Exception ex) {}
					}
				}

				if (err) {
					JOptionPane.showMessageDialog(drawArea, "Error while saving the image", "Error", JOptionPane.ERROR_MESSAGE);
				}

			}
		});

	}

	protected static class JpegFilter extends FileFilter {

		public JpegFilter() {
		}

		@Override
		public boolean accept(File f) {
			String n = f.getName().toLowerCase();
			return n.endsWith(".jpg") || n.endsWith(".jpeg");
		}

		@Override
		public String getDescription() {
			return "JPEG Image";
		}

	}

}
