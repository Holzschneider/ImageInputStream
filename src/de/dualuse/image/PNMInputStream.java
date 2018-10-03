package de.dualuse.image;

import java.awt.image.BufferedImage;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * Abstract Portable Anymap InputStream
 * <p/>
 * 
 * Provides the basic and generic implementation of the InputStream parsing mechanism
 * as a sub-type of a FilterInputStream.
 * <br/>
 * Also provides basic image retrieval functionality. 
 * 
 * 
 * @author holzschneider
 *
 */
public abstract class PNMInputStream extends ImageInputStream implements ImageSource {
	
	final static int INVALID = -1;
	final static int HEADER_PREFETCH = 256; //maximum expected header length in bytes
	protected byte buffer[] = new byte[HEADER_PREFETCH];
	
	protected int frameWidth, frameHeight, bitDepth, bytesPerSample, samplesPerPixel, headerSize;

	private final byte[] magic;
	protected PNMInputStream(InputStream is, String magic) { 
		super(is);
		this.magic = (magic+"\n").getBytes();
	}

	/**
	 * Implement this function to parse an individual PNM header. This function must initialize this classes 
	 * <i>frameWidth</i>, <i>frameHeight</i>,<i>bitDepth</i>, <i>bytesPerSample</i> and <i>samplePerTuple</i> fields   
	 * 
	 * @param buffer is the byte array that contains the header
	 * @param offset is the offset where the header starts (excl. the magic number)
	 * @return header size in bytes
	 * @throws IOException in case of format errors or unsupported header values
	 */
	abstract protected int readHeader(byte[] buffer, int offset) throws IOException;
	
	@Override
	public ImageSource readFrame() throws IOException {
		int head = 0, check = 0; // "head" index 
		
		// read ahead a prefetch value into buffer and advance "head" accordingly
		for (head=0;head<HEADER_PREFETCH && check>=0;head += (check=in.read(buffer, head, buffer.length-head)));

		// check magic
		for (int i=0,I=magic.length;i<I;i++)
			if (buffer[i]!=magic[i])
				throw new IOException("Unexpected Magic: "+new String(buffer,0,I-1));
		
		frameWidth = frameHeight = bitDepth = bytesPerSample = samplesPerPixel = INVALID;
		headerSize = readHeader(buffer, 3);
		
		if (frameWidth<=0 || frameHeight<=0 || bitDepth <=0 || bytesPerSample<=0 || samplesPerPixel<=0 )
			throw new IOException("Invalid header");
		
		int ppmSize = headerSize + frameWidth*frameHeight*bytesPerSample*samplesPerPixel; // the total buffer size of the pnm (header and pixels) 
		if (buffer.length<ppmSize) 
			buffer = Arrays.copyOf(buffer, ppmSize); // copy-reallocate the buffer to hold the whole ppm
		
		//read in the rest of the whole PNM (respects the already read-in bytes of the header)   
		for (;head<ppmSize && check>=0;head += (check=in.read(buffer, head, buffer.length-head)));
		
		return check<0?null:this;
	}


	@Override
	public void skipFrames(int i) throws IOException { 
		for (int j=0;j<i;j++) 
			if (readFrame() == null)
				throw new IOException();
		
		frameWidth = frameHeight = 0; //cannot be read
	}
	
	// ImageSource ////////////////////////////////////////////////////
	
	@Override
	public int getWidth() { return this.frameWidth; }

	@Override
	public int getHeight() { return this.frameHeight; }

	@Override
	public int getBitDepth() { return this.bitDepth; }

	@Override
	public int getBytesPerSample() { return this.bytesPerSample; }
	
	@Override
	public int getSamplesPerPixel() { return this.samplesPerPixel; }
	
	@Override
	public BufferedImage getImage(final BufferedImage pbi) {
		if (pbi==null) 
			return getImage();
		
		int width = pbi.getWidth(), height = pbi.getHeight();
		int pixels[] = new int[width*height];
		getPixels(width, height, pixels, 0, width);
		pbi.setRGB(0, 0, width, height, pixels, 0, width);
		
		return pbi;
	}
	
	
	@Override
	public byte[] getSamples(int transferWidth, int transferHeight, byte samples[], int offset, int bytesPerScanline) {
		int X=(this.frameWidth<transferWidth?this.frameWidth:transferWidth);
		int Y=(this.frameHeight<transferHeight?this.frameHeight:transferHeight);
		
		if (transferWidth*samplesPerPixel==bytesPerScanline && this.frameWidth == transferWidth)
			System.arraycopy(buffer, headerSize, samples, offset, bytesPerScanline*frameHeight);
		else 
			for (int y=0,o=offset,p=headerSize,
	                 r=bytesPerScanline-X*samplesPerPixel, 
	                 s=(frameWidth-X)*samplesPerPixel;
	                 y<Y; y++,o+=r, p+=s)
				for (int x=0;x<X;x++) 
					for (int i=0;i<samplesPerPixel;i++)
						samples[o++] = buffer[p++];
		
		return samples;
	}

	public ByteBuffer getSamples(int transferWidth, int transferHeight, ByteBuffer samples, int bytesPerScanline) {
		return getSamples(transferWidth, transferHeight, samples, samples.position(), bytesPerScanline);
	}
	
	@Override
	public ByteBuffer getSamples(int transferWidth, int transferHeight, ByteBuffer samples, int position, int bytesPerScanline) {
		int X=(this.frameWidth<transferWidth?this.frameWidth:transferWidth);
		int Y=(this.frameHeight<transferHeight?this.frameHeight:transferHeight);
		
		samples.position(position);
		if (transferWidth*this.samplesPerPixel==bytesPerScanline && this.frameWidth == transferWidth)
			samples.put(this.buffer, this.headerSize, bytesPerScanline*transferHeight);
		else 
			for (int y=0,p=headerSize,
	                 r=bytesPerScanline-X*samplesPerPixel, 
	                 s=(frameWidth-X)*samplesPerPixel;
	                 y<Y; y++,samples.position( samples.position()+r ), p+=s)
				for (int x=0;x<X;x++) 
					for (int i=0;i<samplesPerPixel;i++)
						samples.put( buffer[p++] );
			
		return samples;
	}

}
