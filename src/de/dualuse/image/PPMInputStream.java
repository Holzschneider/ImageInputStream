package de.dualuse.image;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

/**
 * Portable Pixmap InputStream
 * <p/>
 * 
 * Sub-classes PNMInputStream and implements the functionality to provide a PPM-parsing InputStream.
 * <br/>
 * PPMs are 3-channel RGB images (https://de.wikipedia.org/wiki/Portable_Anymap#Pixmap)
 * Only 8-bit per channel pixels with value range [0,255] are supported.  
 * 
 * @author holzschneider
 *
 */

public class PPMInputStream extends PNMInputStream {
	
	/**
	 * Creates a PPMInputStream for a given regular InputStream
	 * @param is is the wrapped InputStream
	 */
	public PPMInputStream(InputStream is) { super(is,"P6"); }

	@Override
	protected int readHeader(byte[] buffer, int offset) throws IOException {
		// manually parse width/height/bit-depth
		int p = offset-1; // skip the magic (index 0, 1, 2) 
		int w = 0, h = 0, d = 0; // width, height, depth;
		int i = 10000000, j = i, k = i; // exponent counter for digits
		
		// parse digits (width and height are separated by space 0x20, depth comes after a newline)
		for (int c=buffer[++p];c!=0x20&&c>=0;w+=(c-'0')*i, c=buffer[++p],i/=10);
		for (int c=buffer[++p];c!=0x0A&&c>=0;h+=(c-'0')*j, c=buffer[++p],j/=10);
		for (int c=buffer[++p];c!=0x0A&&c>=0;d+=(c-'0')*k, c=buffer[++p],k/=10);
		
		// normalized parsed value by remainder exponent 
		frameWidth =  w/i/10;
		frameHeight = h/j/10;
		bitDepth = d/k/10;
		
		// derive bytesPerSample
		bytesPerSample = ((32-Integer.numberOfLeadingZeros(d/k/10))/8);
		samplesPerPixel = 3;
		
		if (bytesPerSample!=1)
			throw new IOException("Unsupported sample format");

		return ++p;
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	public int[] getPixels(int transferWidth, int transferHeight, int pixels[], int offset, int pixelsPerScanline) {
		int X=(this.frameWidth<transferWidth?this.frameWidth:transferWidth);
		int Y=(this.frameHeight<transferHeight?this.frameHeight:transferHeight);
		
		if (pixelsPerScanline==frameWidth)
			for (int o=offset,O=transferWidth*(this.frameHeight<transferHeight?this.frameHeight:transferHeight),p=headerSize;o<O;)
				pixels[o++] =
					((((int)buffer[p++])&0xFF)<<16) |
					((((int)buffer[p++])&0xFF)<<8) |
					((((int)buffer[p++])&0xFF)) |
					0xFF000000;
		else 
			for (int y=0,o=offset,p=headerSize,
			         r=pixelsPerScanline-X, 
			         s=(frameWidth-X)*3;  
			         y<Y; y++,o+=r, p+=s)
				for (int x=0;x<X;x++)
					pixels[o++] = 
						((((int)buffer[p++])&0xFF)<<16) |
						((((int)buffer[p++])&0xFF)<<8) | 
						((((int)buffer[p++])&0xFF)) | 
						0xFF000000;
	
		return pixels;
	}
	
	
	@Override
	public BufferedImage getImage() {
		return getImage(new BufferedImage(frameWidth, frameHeight, BufferedImage.TYPE_INT_RGB));
	}
	
//	@Override
//	public BufferedImage getImage(final BufferedImage pbi) throws IOException {
//		if (pbi==null) 
//			return getImage();
//		
//		int width = pbi.getWidth(), height = pbi.getHeight();
//		
//		switch (pbi.getType()) {
//		case BufferedImage.TYPE_INT_RGB:
//		case BufferedImage.TYPE_INT_ARGB:
//		case BufferedImage.TYPE_INT_ARGB_PRE:
//			getPixels(
//					width, height, 
//					((DataBufferInt)pbi.getRaster().getDataBuffer()).getData(), 
//					((DataBufferInt)pbi.getRaster().getDataBuffer()).getOffset(), 
//					((SinglePixelPackedSampleModel)pbi.getRaster().getSampleModel()).getScanlineStride());
//			return pbi;	
//			
//		default:
//			return super.getImage(pbi);
//		}
//	}

}
