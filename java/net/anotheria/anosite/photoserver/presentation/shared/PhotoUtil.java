package net.anotheria.anosite.photoserver.presentation.shared;

import net.anotheria.anosite.photoserver.api.upload.PhotoUploadAPIConfig;
import net.anotheria.anosite.photoserver.presentation.delivery.BluringRadiusChoose;
import net.anotheria.anosite.photoserver.presentation.delivery.DeliveryConfig;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

/**
 * Util-Class to load, save and modify images by using only javax.imageio.* and java.awt.*
 *
 * @author oliver
 * @version $Id: $Id
 */
public class PhotoUtil {
	
	protected BufferedImage image;
	
	/**
	 * Crop a rectanguar area out of the original image.
	 *
	 * @param x a int.
	 * @param y a int.
	 * @param width a int.
	 * @param height a int.
	 */
	public void crop(int x, int y, int width, int height) {
		BufferedImage clipping = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);  
	    Graphics2D graphics = (Graphics2D) clipping.getGraphics().create();  
	    graphics.drawImage(image, 0, 0, clipping.getWidth(), clipping.getHeight(), x, y, x + clipping.getWidth(), y + clipping.getHeight(), null);  
	    graphics.dispose(); 
	    image = clipping;
	}
	
	/**
	 * Rotate the image clockwise 90°
	 */
	public void rotate() {
		 BufferedImage rotatedImage = new BufferedImage(getHeight(), getWidth(), BufferedImage.TYPE_INT_ARGB);
		 Graphics2D graphics = (Graphics2D) rotatedImage.getGraphics();
		 graphics.rotate(Math.toRadians(90.0));
		 graphics.drawImage(image, 0, -getHeight(), null);
		 graphics.dispose();
		 image = rotatedImage;
	}
	
	/**
	 * Get the height of the image
	 *
	 * @return a int.
	 */
	public int getHeight() {
	    return image.getHeight();
    }
	
	/**
	 * Get the width of the image
	 *
	 * @return a int.
	 */
	public int getWidth() {
	    return image.getWidth();
    }

	/**
	 * Read image from a File
	 *
	 * @param input a {@link java.io.File} object.
	 * @throws java.io.IOException if any.
	 */
	public void read(File input) throws IOException {
		InputStream in = new FileInputStream(input);
		read(in);
	}
	
	/**
	 * Read image from a File
	 *
	 * @param filename a {@link java.lang.String} object.
	 * @throws java.io.IOException if any.
	 */
	public void read(String filename) throws IOException {
		File input = new File(filename);
		read(input);
    }
	
	/**
	 * Read image from an InputStream
	 *
	 * @param in a {@link java.io.InputStream} object.
	 * @throws java.io.IOException if any.
	 */
	public void read(InputStream in) throws IOException {
    	read(in, getSelectedBackground());
    }
	
    /**
     * Read image from an InputStream.
     *
     * @param in a {@link java.io.InputStream} object.
     * @param bgColor background color
     * @throws java.io.IOException if any.
     */
    public void read(InputStream in, Color bgColor) throws IOException {
    	BufferedImage readImage = ImageIO.read(in);
    	
    	image =  new BufferedImage(readImage.getWidth(), readImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
    	Graphics graphics = image.getGraphics();
		graphics.drawImage(readImage, 0, 0, bgColor, null);
		graphics.dispose();
    }
	
    /**
     * Scale the image that it fits in a squared-size with the specified 'max'-width.
     * The width/height ratio of the image will be unchanged.
     *
     * @param max a int.
     */
    public void scale(int max) {
    	if(image.getWidth() > image.getHeight()) {
    		scale(max, -1);
    	} else {
    		scale(-1, max);
    	}
    }
    
	/**
	 * Scale the image to the new width and height. The width/height ratio of the image will be changed
	 *
	 * @param newX a int.
	 * @param newY a int.
	 */
	public void scale(int newX, int newY) {
		Image scaledImage  = image.getScaledInstance(newX, newY, Image.SCALE_SMOOTH);
		image = new BufferedImage(scaledImage.getWidth(null), scaledImage.getHeight(null), BufferedImage.TYPE_INT_ARGB);
		Graphics graphics = image.getGraphics().create();
		graphics.drawImage(scaledImage, 0, 0, null);
		graphics.dispose();
	}
		
	/**
	 * Blur the image
	 */
	public void blur() {
		float radius;
		if(BluringRadiusChoose.MAX.equals(DeliveryConfig.getInstance().getRadius()))
			radius = Math.max(DeliveryConfig.getInstance().getBlurMinRadius(), getWidth() * 3 / 100);
		else
			radius = Math.min(DeliveryConfig.getInstance().getBlurMinRadius(), getWidth() * 3 / 100);
		BoxBlurFilter blur = new BoxBlurFilter(radius, radius, DeliveryConfig.getInstance().getBlurIteration());
		BufferedImage blurredImage = blur.filter(image, null);
		image =  new BufferedImage(blurredImage.getWidth(), blurredImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = image.createGraphics();
		graphics.drawImage(blurredImage, 0, 0, getSelectedBackground(), null);
		graphics.dispose();	
	}
	
	/**
	 * Pixelize the image
	 */
	public void pixelize() {
		int pixelWidth = getWidth()*3/100;
		BlockFilter block = new BlockFilter(pixelWidth);
		BufferedImage blockedImage = block.filter(image, null);
		image =  new BufferedImage(blockedImage.getWidth(), blockedImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = image.createGraphics();
		graphics.drawImage(blockedImage, 0, 0, getSelectedBackground(), null);
		graphics.dispose();	
	}
	
	/**
	 * Write image with given quality.
	 * Type of the image depends on {@link net.anotheria.anosite.photoserver.api.upload.PhotoUploadAPIConfig#imageWriteFormat}.
	 *
	 * @param quality a float.
	 * @param output a {@link java.io.File} object.
	 * @throws java.io.IOException if any.
	 */
	public void write(float quality, File output) throws IOException {
		FileImageOutputStream out = new FileImageOutputStream(output);
		write(quality, out);
	}

	/**
	 * Write image with given quality.
	 * Type of the image depends on {@link net.anotheria.anosite.photoserver.api.upload.PhotoUploadAPIConfig#imageWriteFormat}.
	 *
	 * @param quality a float.
	 * @param filename a {@link java.lang.String} object.
	 * @throws java.io.IOException if any.
	 */
	public void write(float quality, String filename) throws IOException {
		write(quality, new File(filename));
	}

	/**
	 * Write image with given quality.
	 * Type of the image depends on {@link net.anotheria.anosite.photoserver.api.upload.PhotoUploadAPIConfig#imageWriteFormat}.
	 *
	 * @param quality a float.
	 * @param out a {@link javax.imageio.stream.ImageOutputStream} object.
	 * @throws java.io.IOException if any.
	 */
	public void write(float quality, ImageOutputStream out) throws IOException {
		final ImageWriteFormat imageFormat = ImageWriteFormat.getByValue(PhotoUploadAPIConfig.getInstance().getImageWriteFormat());

		BufferedImage writeImage =  new BufferedImage(image.getWidth(), image.getHeight(), getBufferedImageColorType(imageFormat));
    	Graphics graphics = writeImage.getGraphics();
		graphics.drawImage(image, 0, 0, null);
		graphics.dispose();

		Iterator<ImageWriter> iter = ImageIO.getImageWritersByFormatName(imageFormat.getValue());
		ImageWriter writer = iter.next();
		ImageWriteParam iwp = writer.getDefaultWriteParam();

		// set image writer params if required
		switch (imageFormat) {
			case PNG:
				break;
			case JPEG:
				iwp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
				iwp.setCompressionQuality(1);
			default:
				break;
		}

		writer.setOutput(out);
		IIOImage iioImage = new IIOImage(writeImage, null, null);
		writer.write(null, iioImage, iwp);
		writer.dispose();
	}

	/**
	 * Get color type of buffered image by incoming image format.
	 *
	 * @param imageFormat {@link ImageWriteFormat}
	 * @return value of the color type
	 */
	private int getBufferedImageColorType(final ImageWriteFormat imageFormat) {
		if (imageFormat == null)
			throw new IllegalArgumentException("imageFormat is null");

		switch (imageFormat) {
			case PNG:
				// supports transparency
				return BufferedImage.TYPE_INT_ARGB;
			case JPEG:
			default:
				return BufferedImage.TYPE_INT_RGB;
		}
	}

	/**
	 * Return selected background color.
	 *
	 * @return {@link Color}
	 */
	private Color getSelectedBackground() {
		boolean transparentBg = PhotoUploadAPIConfig.getInstance().isAllowTransparentBackground();
		return transparentBg ? null : Color.WHITE;
	}
	
	/**
	 * <p>main.</p>
	 *
	 * @param args an array of {@link java.lang.String} objects.
	 * @throws java.io.IOException if any.
	 */
	public static void main(String[] args) throws IOException {
		PhotoUploadAPIConfig.getInstance().setImageWriteFormat("png");
		PhotoUploadAPIConfig.getInstance().setAllowTransparentBackground(true);
		PhotoUtil photo = new PhotoUtil();
		//photo.read("/home/oliver/Desktop/102702439_55cec15215.jpg");
		photo.read("/Users/kapkan/Downloads/motorhead-band-vector-logo-400x400.png");
		//photo.read("/home/oliver/Desktop/demo1.jpg");
//		photo.crop(10, 10, 100, 100);
//		photo.pixelize();
//		photo.blur();
//		photo.write(100, "/Users/kapkan/Downloads/improvedscale.jpg");
		photo.write(100, "/Users/kapkan/Downloads/improvedscale.png");
	}
}
