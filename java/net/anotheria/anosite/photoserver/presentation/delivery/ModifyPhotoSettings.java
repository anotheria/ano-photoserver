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

	public boolean isCropped() {
		return cropped;
	}

	public void setCropped(boolean cropped) {
		this.cropped = cropped;
	}

	public boolean isBlurred() {
		return blurred;
	}

	public void setBlurred(boolean blurred) {
		this.blurred = blurred;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public int getBoundaryWidth() {
		return boundaryWidth;
	}

	public void setBoundaryWidth(int boundaryWidth) {
		this.boundaryWidth = boundaryWidth;
	}

	public int getBoundaryHeight() {
		return boundaryHeight;
	}

	public void setBoundaryHeight(int boundaryHeight) {
		this.boundaryHeight = boundaryHeight;
	}

	public CroppingType getCroppingType() {
		return croppingType;
	}

	public void setCroppingType(CroppingType croppingType) {
		this.croppingType = croppingType;
	}

	public ResizeType getResizeType() {
		return resizeType;
	}

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
