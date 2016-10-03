package net.anotheria.anosite.photoserver.shared.vo;

import java.io.File;

import net.anotheria.anosite.photoserver.presentation.shared.PhotoDimension;

public class TempPhotoVO {

	private File file;
	private PhotoDimension dimension;
	
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
	
	@Override
	protected void finalize() throws Throwable {
		if(file != null) {
			file.delete();
		}
		super.finalize();
	}
	
}
