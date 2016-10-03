package net.anotheria.anosite.photoserver.api.upload;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import net.anotheria.anosite.photoserver.presentation.shared.PhotoUtil;
import net.anotheria.anosite.photoserver.shared.vo.TempPhotoVO;

/**
 * <p>PhotoWorkbench class.</p>
 *
 * @author another
 * @version $Id: $Id
 */
public class PhotoWorkbench {

	private static final PhotoUploadAPIConfig uploadConfig = PhotoUploadAPIConfig.getInstance();

	private TempPhotoVO photo;
	private String id;
	private File[] workbenchFile = new File[4];

	/**
	 * <p>Constructor for PhotoWorkbench.</p>
	 *
	 * @param photo a {@link net.anotheria.anosite.photoserver.shared.vo.TempPhotoVO} object.
	 * @param workbenchId a {@link java.lang.String} object.
	 */
	public PhotoWorkbench(TempPhotoVO photo, String workbenchId) {
		id = workbenchId;
		this.photo = photo;
	}

	/**
	 * <p>Getter for the field <code>id</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getId() {
		return id;
	}

	/**
	 * <p>Getter for the field <code>photo</code>.</p>
	 *
	 * @return a {@link net.anotheria.anosite.photoserver.shared.vo.TempPhotoVO} object.
	 */
	public TempPhotoVO getPhoto() {
		return photo;
	}

	/**
	 * <p>getWorkbenchImage.</p>
	 *
	 * @param rotation a int.
	 * @return a {@link java.io.InputStream} object.
	 * @throws java.io.FileNotFoundException if any.
	 * @throws java.io.IOException if any.
	 */
	public InputStream getWorkbenchImage(int rotation) throws FileNotFoundException, IOException {
		return new FileInputStream(getWorkbenchFile(rotation));
	}

	private File getWorkbenchFile(int rotation) throws IOException {
		if (workbenchFile[rotation] != null) {
			return workbenchFile[rotation];
		}

		workbenchFile[rotation] = new File(photo.getFile().getParentFile(), photo.getFile().getName() + "-" + rotation + ".jpg");

		if (rotation == 0) {
			PhotoUtil photoUtil = new PhotoUtil();
			photoUtil.read(photo.getFile());
			photoUtil.scale(uploadConfig.getWorkbenchWidth());
			photoUtil.write(uploadConfig.getJpegQuality(), workbenchFile[rotation]);
		} else {
			File prevRotatedFile = getWorkbenchFile(rotation - 1);
			PhotoUtil photoUtil = new PhotoUtil();
			photoUtil.read(prevRotatedFile);
			photoUtil.rotate();
			photoUtil.write(uploadConfig.getJpegQuality(), workbenchFile[rotation]);
		}

		return workbenchFile[rotation];
	}

}
