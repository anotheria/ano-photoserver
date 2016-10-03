package net.anotheria.anosite.photoserver.api.upload;

import net.anotheria.anoplass.api.API;
import net.anotheria.anoplass.api.APIException;
import net.anotheria.anosite.photoserver.shared.vo.TempPhotoVO;

/**
 * This API implements features to upload photos and
 * manipulate (crop, rotate, scale) them.
 *
 * @author oliver
 * @version $Id: $Id
 */
public interface PhotoUploadAPI extends API {

	/**
	 * Creates a new PhotoWorkbench for a given photo
	 *
	 * @param photo {@link net.anotheria.anosite.photoserver.shared.vo.TempPhotoVO}
	 * @return {@link net.anotheria.anosite.photoserver.api.upload.PhotoWorkbench}
	 */
	PhotoWorkbench createMyPhotoWorkbench(TempPhotoVO photo);

	/**
	 * Gets a reference to a user's PhotoWorkbench
	 *
	 * @param workbenchId a {@link java.lang.String} object.
	 * @return {@link net.anotheria.anosite.photoserver.api.upload.PhotoWorkbench}
	 */
	PhotoWorkbench getMyPhotoWorkbench(String workbenchId);

	/**
	 * Starts an upload. This has to be called before the submit of the
	 * upload form. For example by an ajax request.
	 *
	 * @return {@link net.anotheria.anosite.photoserver.api.upload.PhotoUploader}
	 * @throws net.anotheria.anoplass.api.APIException if any.
	 */
	PhotoUploader createMyPhotoUploader() throws APIException;

    /**
     * Starts an upload. This has to be called before the submit of the
     * upload form. For example by an ajax request.
     *
     * @param userId {@link java.lang.String} user id
     * @throws net.anotheria.anoplass.api.APIException
     * @return {@link net.anotheria.anosite.photoserver.api.upload.PhotoUploader}
     */
    PhotoUploader createPhotoUploader(String userId) throws APIException;

	/**
	 * Gets a reference to the PhotoUploader for the current photoUpload.
	 *
	 * @param uploaderId id of current user who's uploading
	 * @return {@link net.anotheria.anosite.photoserver.api.upload.PhotoUploader}
	 */
	PhotoUploader getMyPhotoUploader(String uploaderId);

	/**
	 * rotates a photo n-times by 90° clockwise and returns a new generated TempPhotoVO
	 *
	 * @param photo {@link net.anotheria.anosite.photoserver.shared.vo.TempPhotoVO}
	 * @param n a int.
	 * @return {@link net.anotheria.anosite.photoserver.shared.vo.TempPhotoVO}
	 * @throws net.anotheria.anoplass.api.APIException if any.
	 */
	TempPhotoVO rotatePhoto(TempPhotoVO photo, int n) throws APIException;

    /**
     * Removes workbench with given id from session.
     *
     * @param workbenchId a {@link java.lang.String} object.
     */
    void finishWorkbench(String workbenchId);

}
