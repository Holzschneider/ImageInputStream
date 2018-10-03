package de.dualuse.image;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

abstract public class ImageInputStream extends FilterInputStream {

	protected ImageInputStream(InputStream in) {
		super(in);
	}
	
	/**
	 * Reads and parses a frame from the encapsulated InputStream.
	 * 
	 * @return ImageSource instance that provides access to the frame's image content. 
	 * @throws IOException in case of format errors
	 */
	abstract public ImageSource readFrame() throws IOException;
	
	/**
	 * Skips exactly one frame
	 * @throws IOException
	 */
	public void skipFrame() throws IOException { skipFrames(1); }
	
	
	/**
	 * Skips exactly <i>i</i> frames
	 * @param i is the number of frames to skip
	 * @throws IOException if the InputStream ends before the requested number of frames could be skipped
	 */
	abstract public void skipFrames(int i) throws IOException; 

}
