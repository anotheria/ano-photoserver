package net.anotheria.anosite.photoserver.presentation.shared;

import org.json.simple.JSONObject;

/**
 * Helper class to deal with the dimension of a photo.
 * 
 * @author otoense
 */
public class PhotoDimension {

	public int w;
	public int h;
	
	/**
	 * Constructs a new PhotoDimension with given width and height.
	 * 
	 * @param width
	 * @param height
	 */
	public PhotoDimension(int width, int height) {
		this.w = width;
		this.h = height;
	}
	
	/**
	 * Virtually rotate the image, that means swap width and height
	 */
	public void rotate() {
		int w1 = w;
		w = h;
		h = w1;
	}
	
	/**
	 * Returns the width 
	 * @return the width
	 */
	public int getWidth() {
		return w;
	}
	
	/**
	 * Returns the height
	 * @return the height
	 */
	public int getHeight() {
		return h;
	}
	
	/**
	 * This method gives a new PhotoDimension which has the same aspect ration within
	 * another dimension. It is usefull in the fotocutting-context:
	 * We get coordinates to cut from the client with a scaled preview image of our original
	 * photo. With this method we can calculate the coordinates for the original picture.
	 *  
	 * @param base the dimension of the 2nd system (e.g. preview-image)
	 * @param dimension the coordinates within the 2nd system
	 * @return the coordinates in this system
	 */
	public PhotoDimension getRelationTo(PhotoDimension base, PhotoDimension dimension) {
		double wRatio = (double) w / (double) base.w;
		double hRatio = (double) h / (double) base.h;
		return new PhotoDimension(
				(int) Math.floor((double) dimension.w * wRatio), 
				(int) Math.floor((double) dimension.h * hRatio)
		);
	}
	
	public String toString() {
		return w + "x" + h;
	}
	
	@SuppressWarnings("unchecked")
	public JSONObject getJSONObject() {
		JSONObject obj = new JSONObject();
		obj.put("w", w);
		obj.put("h", h);
		return obj;
	}
}
