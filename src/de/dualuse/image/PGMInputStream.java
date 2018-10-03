package de.dualuse.image;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;



/**
 * Portable Graymap InputStream
 * <p/>
 * 
 * Sub-classes PNMInputStream and implements the functionality to provide a PGM-parsing InputStream.
 * <br/>
 * PGMs are 1-channel Gray images (https://de.wikipedia.org/wiki/Portable_Anymap#Graymap)
 * Only 8-bit per channel samples with value range [0,255] are supported.  
 * 
 * @author holzschneider
 *
 */
public class PGMInputStream extends PNMInputStream {
	
	/**
	 * Creates a PGMInputStream for a given regular InputStream
	 * @param is is the wrapped InputStream
	 */
	public PGMInputStream(InputStream is) { super(is, "P5"); }
	
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
		samplesPerPixel = 1;

		if (bytesPerSample!=1)
			throw new IOException("Unsupported sample format");

		return ++p;
	}

	
	public int[] getPixels(int transferWidth, int transferHeight, int pixels[], int offset, int pixelsPerScanline) {
		int X=(this.frameWidth<transferWidth?this.frameWidth:transferWidth);
		int Y=(this.frameHeight<transferHeight?this.frameHeight:transferHeight);
		
		if (pixelsPerScanline==transferWidth)
			for (int o=offset,O=transferWidth*(this.frameHeight<transferHeight?this.frameHeight:transferHeight),p=headerSize,pixel=0;o<O;o++,p++)
				pixels[o] = (pixel = (((int)buffer[p])&0xFF)) | (pixel<<8) | (pixel<<16) | 0xFF000000;
		else
			for (int y=0,o=offset,p=headerSize,
			         r=pixelsPerScanline-X, 
			         s=(transferWidth-X),
			         pixel=0;
			         y<Y; y++,o+=r, p+=s)
				for (int x=0;x<X;x++,o++,p++)
					pixels[o] = (pixel = ((int)buffer[p])&0xFF) | (pixel<<8) | (pixel<<16) | 0xFF000000;
		
		return pixels;
	}


	@Override
	public BufferedImage getImage() {
		return getImage(new BufferedImage(frameWidth, frameHeight, BufferedImage.TYPE_BYTE_GRAY));
	}

	
//	@Override
//	public BufferedImage getImage(BufferedImage pbi) throws IOException {
//		if (pbi==null)
//			return getImage();
//		
//		WritableRaster raster = pbi.getRaster();
//		
//		switch (pbi.getType()) {
//		case BufferedImage.TYPE_INT_RGB:
//		case BufferedImage.TYPE_INT_ARGB:
//		case BufferedImage.TYPE_INT_ARGB_PRE: {
//			DataBufferInt buffer = (DataBufferInt)raster.getDataBuffer();
//			SinglePixelPackedSampleModel model = (SinglePixelPackedSampleModel)raster.getSampleModel(); 
//			getPixels( frameWidth, frameHeight, buffer.getData(), buffer.getOffset(), model.getScanlineStride());
//			return pbi;
//		}
//		
//		case BufferedImage.TYPE_BYTE_GRAY: {
//			DataBufferByte buffer = (DataBufferByte)raster.getDataBuffer();
//			ComponentSampleModel model = (ComponentSampleModel)raster.getSampleModel(); 
//			getSamples( frameWidth, frameHeight, buffer.getData(), buffer.getOffset(), model.getScanlineStride());
//			return pbi;
//		}
//		
//		default:
//			return super.getImage(pbi);
//		}
//
//	}
//	

}
