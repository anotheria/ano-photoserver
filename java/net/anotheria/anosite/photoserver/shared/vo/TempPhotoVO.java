package net.anotheria.anosite.photoserver.shared.vo;

import java.io.File;

import net.anotheria.anosite.photoserver.presentation.shared.PhotoDimension;

public class TempPhotoVO {

	private File file;
	private PhotoDimension dimension;
	/**
	 * Photo type.
	 */
	private String photoType;
	
	public PhotoDimension getDimension() {
		return dimension;
	}
	
	public void setDimension(PhotoDimension dimension) {
		this.dimension = dimension;
	}
	
	public File getFile() {
		return file;
	}
	
	public void setFile(File file) {
		this.file = file;
		file.deleteOnExit();
	}

	public String getPhotoType() {
		return photoType;
	}

	public void setPhotoType(String photoType) {
		this.photoType = photoType;
	}

	@Override
	protected void finalize() throws Throwable {
		if(file != null) {
			file.delete();
		}
		super.finalize();
	}
	
}
