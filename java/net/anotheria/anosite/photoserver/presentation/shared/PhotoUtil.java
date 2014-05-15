package net.anotheria.anosite.photoserver.presentation.shared;

import net.anotheria.anosite.photoserver.presentation.delivery.BluringRadiusChoose;
import net.anotheria.anosite.photoserver.presentation.delivery.DeliveryConfig;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

/**
 * Util-Class to load, save and modify images by using only javax.imageio.* and java.awt.*
 * 
 * 
 * @author oliver
 *
 */
public class PhotoUtil {
	
	protected BufferedImage image;
	
	/**
	 * Crop a rectanguar area out of the original image. 
	 * 
	 * @param x
	 * @param y
	 * @param width
	 * @param height
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
	 * @return
	 */
	public int getHeight() {
	    return image.getHeight();
    }
	
	/**
	 * Get the width of the image
	 * @return
	 */
	public int getWidth() {
	    return image.getWidth();
    }

	/**
	 * Read image from a File
	 * @param input
	 * @throws java.io.IOException
	 */
	public void read(File input) throws IOException {
		InputStream in = new FileInputStream(input);
		read(in);
	}
	
	/**
	 * Read image from a File
	 * @param filename
	 * @throws java.io.IOException
	 */
	public void read(String filename) throws IOException {
		File input = new File(filename);
		read(input);
    }
	
	/**
	 * Read image from an InputStream
	 * @param in
	 * @throws java.io.IOException
	 */
	public void read(InputStream in) throws IOException {
    	read(in, Color.WHITE);
    }
	
	/**
	 * Read image from an InputStream. In case the image has transparent color it will be replaced
	 * by the specified transparentColor. (Default: Color.WHITE)
	 * @param in
	 * @param transparentColor
	 * @throws java.io.IOException
	 */
    public void read(InputStream in, Color transparentColor) throws IOException {	
    	BufferedImage readImage = ImageIO.read(in);
    	
    	image =  new BufferedImage(readImage.getWidth(), readImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
    	Graphics graphics = image.getGraphics();
		graphics.drawImage(readImage, 0, 0, Color.WHITE, null);
		graphics.dispose();
    }
	
    /**
     * Scale the image that it fits in a squared-size with the specified 'max'-width.
     * The width/height ratio of the image will be unchanged.
     * @param max
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
     * @param newX
     * @param newY
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
		graphics.drawImage(blurredImage, 0, 0, Color.WHITE, null);
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
		graphics.drawImage(blockedImage, 0, 0, Color.WHITE, null);
		graphics.dispose();	
	}
	
	/**
	 * Write image as a jpeg with given quality
	 * @param quality
	 * @param output
	 * @throws java.io.IOException
	 */
	public void write(float quality, File output) throws IOException {
		FileImageOutputStream out = new FileImageOutputStream(output);
		write(quality, out);
	}

	/**
	 * Write image as a jpeg with given quality
	 * @param quality
	 * @param filename
	 * @throws java.io.IOException
	 */
	public void write(float quality, String filename) throws IOException {
		write(quality, new File(filename));
	}

	/**
	 * Write image as a jpeg with given quality
	 * @param quality
	 * @param out
	 * @throws java.io.IOException
	 */
	public void write(float quality, ImageOutputStream out) throws IOException {	
		BufferedImage writeImage =  new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
    	Graphics graphics = writeImage.getGraphics();
		graphics.drawImage(image, 0, 0, null);
		graphics.dispose();
		
		Iterator<ImageWriter> iter = ImageIO.getImageWritersByFormatName("jpeg");
		ImageWriter writer = iter.next();
		ImageWriteParam iwp = writer.getDefaultWriteParam();
		iwp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
		iwp.setCompressionQuality(1);
		
		writer.setOutput(out);
		IIOImage iioImage = new IIOImage(writeImage, null, null);
		writer.write(null, iioImage, iwp);
		writer.dispose();
	}
	
	public static void main(String[] args) throws IOException {
		PhotoUtil photo = new PhotoUtil();
		//photo.read("/home/oliver/Desktop/102702439_55cec15215.jpg");
		photo.read("/mnt/hgfs/Shared/Moses/Photos/testpng.png");
		//photo.read("/home/oliver/Desktop/demo1.jpg");
		//photo.scale(100);
		photo.pixelize();
		photo.write(100, "/home/oliver/Desktop/improvedscale.jpg");
	}
}