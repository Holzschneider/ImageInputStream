package de.dualuse.image;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import de.dualuse.image.ImageSource;
import de.dualuse.image.PNMInputStream;
import de.dualuse.image.PPMInputStream;

public class PPMInputStreamLab {
	public static void main(String[] args) throws IOException {
		
		String cmd = "/usr/local/bin/ffmpeg -f avfoundation -i 0 -f image2pipe -vcodec ppm -";

//		String cmd[] = { "/bin/bash", "-c", "ssh "+args[0]+" "+
//				"/usr/local/bin/ffmpeg -framerate 30 -f avfoundation -i 0 -vcodec h264 -f m4v - | "+ 
//				"/usr/local/bin/ffmpeg -i - -f image2pipe -vcodec ppm -" };

		Process p = Runtime.getRuntime().exec(cmd);

		
		try (PNMInputStream pis = new PPMInputStream(p.getInputStream())) {
			ImageSource is = pis.readFrame();
			BufferedImage pbi = is.getImage();
			
			JFrame f = new JFrame();
			f.setContentPane(new JLabel(new ImageIcon(pbi)));
			f.setLocation(1200, 200);
			f.setVisible(true);
			f.pack();
			
			for (is = pis.readFrame(); is!=null; f.getContentPane().repaint(), is = pis.readFrame() )
				is.getImage(pbi);
		}
		
	}
}
