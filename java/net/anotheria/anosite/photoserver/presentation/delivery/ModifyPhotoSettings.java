package net.anotheria.anosite.photoserver.presentation.delivery;

/**
 * Photo settings holder used by {@link DeliveryServlet}.
 *
 * @author Illya Bogatyrchuk
 */
class ModifyPhotoSettings {
	/**
	 * Indicates that photo should be cropped or not.
	 */
	private boolean cropped;
	/**
	 * Indicates that photo should be blurred or not.
	 */
	private boolean blurred;
	/**
	 * Scaling size.
	 */
	private int size;
	/**
	 * Bounding area width param.
	 */
	private int boundaryWidth;
	/**
	 * Bounding area height param.
	 */
	private int boundaryHeight;
	/**
	 * {@link CroppingType}.
	 */
	private CroppingType croppingType;
	/**
	 * {@link ResizeType}.
	 */
	private ResizeType resizeType;

	/**
	 * Constructor.
	 */
	ModifyPhotoSettings() {
		this.size = -1;
		this.boundaryWidth = -1;
		this.boundaryHeight = -1;
		this.croppingType = null;
		this.resizeType = null;
	}

	/**
	 * <p>isCropped.</p>
	 *
	 * @return a boolean.
	 */
	public boolean isCropped() {
		return cropped;
	}

	/**
	 * <p>Setter for the field <code>cropped</code>.</p>
	 *
	 * @param cropped a boolean.
	 */
	public void setCropped(boolean cropped) {
		this.cropped = cropped;
	}

	/**
	 * <p>isBlurred.</p>
	 *
	 * @return a boolean.
	 */
	public boolean isBlurred() {
		return blurred;
	}

	/**
	 * <p>Setter for the field <code>blurred</code>.</p>
	 *
	 * @param blurred a boolean.
	 */
	public void setBlurred(boolean blurred) {
		this.blurred = blurred;
	}

	/**
	 * <p>Getter for the field <code>size</code>.</p>
	 *
	 * @return a int.
	 */
	public int getSize() {
		return size;
	}

	/**
	 * <p>Setter for the field <code>size</code>.</p>
	 *
	 * @param size a int.
	 */
	public void setSize(int size) {
		this.size = size;
	}

	/**
	 * <p>Getter for the field <code>boundaryWidth</code>.</p>
	 *
	 * @return a int.
	 */
	public int getBoundaryWidth() {
		return boundaryWidth;
	}

	/**
	 * <p>Setter for the field <code>boundaryWidth</code>.</p>
	 *
	 * @param boundaryWidth a int.
	 */
	public void setBoundaryWidth(int boundaryWidth) {
		this.boundaryWidth = boundaryWidth;
	}

	/**
	 * <p>Getter for the field <code>boundaryHeight</code>.</p>
	 *
	 * @return a int.
	 */
	public int getBoundaryHeight() {
		return boundaryHeight;
	}

	/**
	 * <p>Setter for the field <code>boundaryHeight</code>.</p>
	 *
	 * @param boundaryHeight a int.
	 */
	public void setBoundaryHeight(int boundaryHeight) {
		this.boundaryHeight = boundaryHeight;
	}

	/**
	 * <p>Getter for the field <code>croppingType</code>.</p>
	 *
	 * @return a {@link net.anotheria.anosite.photoserver.presentation.delivery.CroppingType} object.
	 */
	public CroppingType getCroppingType() {
		return croppingType;
	}

	/**
	 * <p>Setter for the field <code>croppingType</code>.</p>
	 *
	 * @param croppingType a {@link net.anotheria.anosite.photoserver.presentation.delivery.CroppingType} object.
	 */
	public void setCroppingType(CroppingType croppingType) {
		this.croppingType = croppingType;
	}

	/**
	 * <p>Getter for the field <code>resizeType</code>.</p>
	 *
	 * @return a {@link net.anotheria.anosite.photoserver.presentation.delivery.ResizeType} object.
	 */
	public ResizeType getResizeType() {
		return resizeType;
	}

	/**
	 * <p>Setter for the field <code>resizeType</code>.</p>
	 *
	 * @param resizeType a {@link net.anotheria.anosite.photoserver.presentation.delivery.ResizeType} object.
	 */
	public void setResizeType(ResizeType resizeType) {
		this.resizeType = resizeType;
	}

	/**
	 * Check resizing is allowed.
	 *
	 * @return {@code true} - if resize allowed, {code false} - otherwise
	 */
	public boolean isResized() {
		return size != -1 || boundaryWidth != -1 && boundaryHeight != -1;
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return "ModifyPhotoSettings{" +
				"cropped=" + cropped +
				", blurred=" + blurred +
				", size=" + size +
				", boundaryWidth=" + boundaryWidth +
				", boundaryHeight=" + boundaryHeight +
				", croppingType=" + croppingType +
				", resizeType=" + resizeType +
				'}';
	}
}
