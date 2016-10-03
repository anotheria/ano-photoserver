package net.anotheria.anosite.photoserver.shared.vo;

import java.io.Serializable;

/**
 * Setting's for generating preview image from photo.
 *
 * @author Alexandr Bolbat
 * @version $Id: $Id
 */
public class PreviewSettingsVO implements Serializable, Cloneable {

	/**
	 * Basic serialVersionUID variable.
	 */
	private static final long serialVersionUID = 1546095390744834142L;

	/**
	 * Start by X.
	 */
	private int x;

	/**
	 * Start by Y.
	 */
	private int y;

	/**
	 * Width.
	 */
	private int width;

	/**
	 * Height.
	 */
	private int height;

	/**
	 * Default constructor.
	 *
	 * @param settings
	 *            - settings
	 */
	public PreviewSettingsVO(PreviewSettingsVO settings) {
		this(settings.getX(), settings.getY(), settings.getWidth(), settings.getHeight());
	}

	/**
	 * Default constructor.
	 *
	 * @param aX
	 *            - start by x
	 * @param aY
	 *            - start by y
	 * @param size
	 *            - size for make squared preview
	 */
	public PreviewSettingsVO(int aX, int aY, int size) {
		this(aX, aY, size, size);
	}

	/**
	 * Default constructor.
	 *
	 * @param aX
	 *            - start by x
	 * @param aY
	 *            - start by y
	 * @param aWidth
	 *            - width
	 * @param aHeight
	 *            - height
	 */
	public PreviewSettingsVO(int aX, int aY, int aWidth, int aHeight) {
		this.x = aX;
		this.y = aY;
		this.width = aWidth;
		this.height = aHeight;
	}

	/**
	 * <p>Getter for the field <code>x</code>.</p>
	 *
	 * @return a int.
	 */
	public int getX() {
		return x;
	}

	/**
	 * <p>Setter for the field <code>x</code>.</p>
	 *
	 * @param x a int.
	 */
	public void setX(int x) {
		this.x = x;
	}

	/**
	 * <p>Getter for the field <code>y</code>.</p>
	 *
	 * @return a int.
	 */
	public int getY() {
		return y;
	}

	/**
	 * <p>Setter for the field <code>y</code>.</p>
	 *
	 * @param y a int.
	 */
	public void setY(int y) {
		this.y = y;
	}

	/**
	 * <p>Getter for the field <code>width</code>.</p>
	 *
	 * @return a int.
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * <p>Setter for the field <code>width</code>.</p>
	 *
	 * @param width a int.
	 */
	public void setWidth(int width) {
		this.width = width;
	}

	/**
	 * <p>Getter for the field <code>height</code>.</p>
	 *
	 * @return a int.
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * <p>Setter for the field <code>height</code>.</p>
	 *
	 * @param height a int.
	 */
	public void setHeight(int height) {
		this.height = height;
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return "PreviewSettingsVO [x=" + x + ", y=" + y + ", width=" + width + ", height=" + height + "]";
	}

	/** {@inheritDoc} */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + height;
		result = prime * result + width;
		result = prime * result + x;
		result = prime * result + y;
		return result;
	}

	/** {@inheritDoc} */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PreviewSettingsVO other = (PreviewSettingsVO) obj;
		if (height != other.height)
			return false;
		if (width != other.width)
			return false;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		return true;
	}

	/** {@inheritDoc} */
	@Override
	protected Object clone() {
		return new PreviewSettingsVO(this);
	}

}
