package net.anotheria.anosite.photoserver.api.upload;

import net.anotheria.anoplass.api.API;
import net.anotheria.anoplass.api.APIException;
import net.anotheria.anosite.photoserver.shared.vo.TempPhotoVO;

/**
 * This API implements features to upload photos and
 * manipulate (crop, rotate, scale) them.
 *
 * @author oliver
 */
public interface PhotoUploadAPI extends API {

	/**
	 * Creates a new PhotoWorkbench for a given photo
	 *
	 * @param photo {@link TempPhotoVO}
	 * @return {@link PhotoWorkbench}
	 */
	PhotoWorkbench createMyPhotoWorkbench(TempPhotoVO photo);

	/**
	 * Gets a reference to a user's PhotoWorkbench
	 *
	 * @param workbenchId
	 * @return {@link PhotoWorkbench}
	 */
	PhotoWorkbench getMyPhotoWorkbench(String workbenchId);

	/**
	 * Starts an upload. This has to be called before the submit of the
	 * upload form. For example by an ajax request.
	 *
	 * @return {@link PhotoUploader}
	 */
	PhotoUploader createMyPhotoUploader() throws APIException;

    /**
     * Starts an upload. This has to be called before the submit of the
     * upload form. For example by an ajax request.
     *
     * @param userId {@link String} user id
     * @throws APIException {@link APIException}
     * @return {@link PhotoUploader}
     */
    PhotoUploader createPhotoUploader(String userId) throws APIException;

	/**
	 * Gets a reference to the PhotoUploader for the current photoUpload.
	 *
	 * @param uploaderId id of current user who's uploading
	 * @return {@link PhotoUploader}
	 */
	PhotoUploader getMyPhotoUploader(String uploaderId);

	/**
	 * rotates a photo n-times by 90° clockwise and returns a new generated TempPhotoVO
	 *
	 * @param photo {@link TempPhotoVO}
	 * @param n
	 * @return {@link TempPhotoVO}
	 */
	TempPhotoVO rotatePhoto(TempPhotoVO photo, int n) throws APIException;

    /**
     * Removes workbench with given id from session.
     */
    void finishWorkbench(String workbenchId);

}
