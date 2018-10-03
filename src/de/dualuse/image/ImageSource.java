package de.dualuse.image;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Abstract representation and accessor interface of an image, that is ready to be obtained in various form.
 * 
 * @author holzschneider
 *
 */
public interface ImageSource {

	/**
	 * Obtains a rectangular region of the the image's pixels as ARGB packed into ints.
	 * The destination array is defined by a pixel array layout description.
	 * 
	 * @param width is the horizontal extent of the requested region 
	 * @param height is the vertical extent of the requested region
	 * @param pixels will hold the requested pixels 
	 * @param offset of the upper-left pixel in the pixels arrays
	 * @param scan is the number of pixels per scan line in the pixel
	 * @return the destination pixel array
	 */
	abstract public int[] getPixels(int width, int height, int pixels[], int offset, int pixelsPerScanline);
	

	/**
	 * Obtains a rectangular region of the the image's pixels as interleaved read-out of the image's channels.
	 * The destination array is defined by a sample array layout description.
	 * 
	 * @param width is the horizontal extent of the requested region 
	 * @param height is the vertical extent of the requested region
	 * @param samples will hold the requested samples 
	 * @param offset of the upper-left sample in the sample arrays
	 * @param bytesPerScanline is the number of bytes between the first sample of two consecutive scan lines.   
	 * @return the destination sample array
	 */
	abstract public byte[] getSamples(int width, int height, byte samples[], int offset, int bytesPerScanline);	

	/**
	 * Obtains a rectangular region of the the image's pixels as interleaved read-out of the image's channels.
	 * The destination ByteBuffer, and the rectangular sample layout is defined by offset and scan line size.
	 * Assumes the ByteBuffer's position to point to the upper-left sample to be read
	 * 
	 * @param width is the horizontal extent of the requested region 
	 * @param height is the vertical extent of the requested region
	 * @param samples will hold the requested samples 
	 * @param bytesPerScanline is the number of bytes between the first sample of two consecutive scan lines.   
	 * @return the destination ByteBuffer
	 */
	abstract public ByteBuffer getSamples(int width, int height, ByteBuffer samples, int bytesPerScanline);
	

	/**
	 * Obtains a rectangular region of the the image's pixels as interleaved read-out of the image's channels.
	 * The destination ByteBuffer, and the rectangular sample layout is defined by offset and scan line size.
	 * 
	 * @param width is the horizontal extent of the requested region 
	 * @param height is the vertical extent of the requested region
	 * @param samples will hold the requested samples 
	 * @param offset of the upper-left sample in the sample arrays
	 * @param bytesPerScanline is the number of bytes between the first sample of two consecutive scan lines.   
	 * @return the destination ByteBuffer
	 */
	abstract public ByteBuffer getSamples(int width, int height, ByteBuffer samples, int position, int bytesPerScanline);
	
	
	/**
	 * Obtains a rectangular region of the the image's pixels.
	 * The destination is a provided BufferedImage. The destination's image size defines the extents of the obtained image region.
	 * 
	 * @param width is the horizontal extent of the requested region 
	 * @param height is the vertical extent of the requested region
	 * @param dest will hold the requested image content
	 * @return the destination image
	 */
	abstract public BufferedImage getImage(BufferedImage dest);
	
	/**
	 * Obtains the complete image. The Image is returned as newly allocated BufferedImage.
	 * 
	 * @return the destination image
	 */
	abstract public BufferedImage getImage();
	
	
	/**
	 * The height of the image in pixels
	 * 
	 * @return the destination image
	 */
	abstract public int getHeight();

	/**
	 * The width of the image in pixels
	 * 
	 * @return the destination image
	 */
	abstract public int getWidth();
	

	/**
	 * The number of bits that carry information per sample
	 * 
	 * @return the destination image
	 */
	abstract public int getBitDepth();
	
	/**
	 * The number of samples that comprise on pixel
	 * 
	 * @return the destination image
	 */
	abstract public int getSamplesPerPixel();
	
	/**
	 * The number of bytes used to store one sample
	 * 
	 * @return the destination image
	 */
	abstract public int getBytesPerSample();
	
	
}
