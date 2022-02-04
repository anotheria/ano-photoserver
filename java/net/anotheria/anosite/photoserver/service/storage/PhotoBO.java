package net.anotheria.anosite.photoserver.service.storage;

import net.anotheria.anosite.photoserver.shared.vo.PhotoVO;
import net.anotheria.anosite.photoserver.shared.vo.PreviewSettingsVO;

import java.io.File;

/**
 * User photo information.
 *
 * @author Alexandr Bolbat
 * @version $Id: $Id
 */
public class PhotoBO extends PhotoVO implements Cloneable {

	/**
	 * Basic serialVersionUID variable.
	 */
	private static final long serialVersionUID = 5752965750259757708L;

	/**
	 * Constructor.
	 */
	public PhotoBO() {
		super();
	}

	/**
	 * Constructor.
	 *
	 * @param photo {@link net.anotheria.anosite.photoserver.shared.vo.PhotoVO}
	 */
	public PhotoBO(PhotoVO photo) {
		super();
		setId(photo.getId());
		setUserId(photo.getUserId());
		setAlbumId(photo.getAlbumId());
		setFileLocation(photo.getFileLocation());
		setFileLocationCeph(photo.getFileLocationCeph());
		setExtension(photo.getExtension());
		setName(photo.getName());
		setDescription(photo.getDescription());
		setModificationTime(photo.getModificationTime());
		setPreviewSettings(new PreviewSettingsVO(photo.getPreviewSettings()));
		setApprovalStatus(photo.getApprovalStatus());
	}


	/** {@inheritDoc} */
	@Override
	public PhotoBO clone() {
		return PhotoBO.class.cast(super.clone());
	}
}
