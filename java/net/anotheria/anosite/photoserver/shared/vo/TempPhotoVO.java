package net.anotheria.anosite.photoserver.shared.vo;

import java.io.File;

import net.anotheria.anosite.photoserver.presentation.shared.PhotoDimension;

/**
 * <p>TempPhotoVO class.</p>
 *
 * @author another
 * @version $Id: $Id
 */
public class TempPhotoVO {

	private File file;
	private PhotoDimension dimension;
	
	/**
	 * <p>Getter for the field <code>dimension</code>.</p>
	 *
	 * @return a {@link net.anotheria.anosite.photoserver.presentation.shared.PhotoDimension} object.
	 */
	public PhotoDimension getDimension() {
		return dimension;
	}
	
	/**
	 * <p>Setter for the field <code>dimension</code>.</p>
	 *
	 * @param dimension a {@link net.anotheria.anosite.photoserver.presentation.shared.PhotoDimension} object.
	 */
	public void setDimension(PhotoDimension dimension) {
		this.dimension = dimension;
	}
	
	/**
	 * <p>Getter for the field <code>file</code>.</p>
	 *
	 * @return a {@link java.io.File} object.
	 */
	public File getFile() {
		return file;
	}
	
	/**
	 * <p>Setter for the field <code>file</code>.</p>
	 *
	 * @param file a {@link java.io.File} object.
	 */
	public void setFile(File file) {
		this.file = file;
		file.deleteOnExit();
	}
	
	/** {@inheritDoc} */
	@Override
	protected void finalize() throws Throwable {
		if(file != null) {
			file.delete();
		}
		super.finalize();
	}
	
}
