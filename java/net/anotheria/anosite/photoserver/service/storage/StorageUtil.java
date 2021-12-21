package net.anotheria.anosite.photoserver.service.storage;

import net.anotheria.anoprise.dualcrud.CrudServiceException;
import net.anotheria.anoprise.dualcrud.DualCrudConfig;
import net.anotheria.anoprise.dualcrud.DualCrudService;
import net.anotheria.anoprise.dualcrud.DualCrudServiceFactory;
import net.anotheria.anoprise.dualcrud.ItemNotFoundException;
import net.anotheria.anosite.photoserver.service.storage.photolocation.ceph.PhotoCephClientService;
import net.anotheria.anosite.photoserver.service.storage.photolocation.fs.PhotoFSService;
import net.anotheria.anosite.photoserver.shared.PhotoServerConfig;
import net.anotheria.anosite.photoserver.shared.vo.PhotoVO;

/**
 * Utility for operating with photos on file system. All synchronizations based on file names.
 *
 * @author Alexandr Bolbat
 * @version $Id: $Id
 */
public final class StorageUtil {

	/**
	 * {@link DualCrudService} instance.
	 * */
	private static final DualCrudService<PhotoVO> DUAL_CRUD_SERVICE;

	static {
		if (PhotoServerConfig.getInstance().isPhotoCephStorageEnabled()) {
			DUAL_CRUD_SERVICE = DualCrudServiceFactory.createDualCrudService(new PhotoFSService(), new PhotoCephClientService(), DualCrudConfig.migrateOnTheFlyButMaintainBoth());
		} else {
			DUAL_CRUD_SERVICE = DualCrudServiceFactory.createDualCrudService(new PhotoFSService(), null, DualCrudConfig.useLeftOnly());
		}
	}

	/**
	 * Default constructor.
	 */
	private StorageUtil() {
		throw new IllegalAccessError();
	}

	/**
	 * Write photo to file system persistence. Incoming input stream will be closed inside at and this operation.
	 *
	 * InputStream, photo and extension can't be null or empty.
	 *
	 * @param photo
	 *            - photo information
	 * @param overwrite
	 *            - <code>true</code> if we can overwrite or <code>false</code>
	 * @throws net.anotheria.anosite.photoserver.service.storage.StorageUtilException if any.
	 */
	public static void writePhoto(final PhotoVO photo, final boolean overwrite) throws StorageUtilException {
		try {
			if (overwrite) {
				DUAL_CRUD_SERVICE.update(photo);
			} else {
				DUAL_CRUD_SERVICE.create(photo);
			}
		} catch (CrudServiceException e) {
			throw new StorageUtilException(e);
		}
	}

	/**
	 * Remove photo file from the file system. Photo and extension can't be null or empty.
	 *
	 * @param photo
	 *            - photo information
	 * @throws net.anotheria.anosite.photoserver.service.storage.StorageUtilException if any.
	 */
	public static void removePhoto(final PhotoVO photo) throws StorageUtilException {
		try {
			DUAL_CRUD_SERVICE.delete(photo);
		} catch (CrudServiceException e) {
			throw new StorageUtilException(e);
		}
	}
}
