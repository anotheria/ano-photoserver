package net.anotheria.anosite.photoserver.api.photo;

import net.anotheria.anoplass.api.APIException;
import net.anotheria.anoplass.api.APIFinder;
import net.anotheria.anoplass.api.APIInitException;
import net.anotheria.anoplass.api.AbstractAPIImpl;
import net.anotheria.anoplass.api.NoLoggedInUserException;
import net.anotheria.anoplass.api.generic.login.LoginAPI;
import net.anotheria.anoprise.dualcrud.CrudServiceException;
import net.anotheria.anoprise.dualcrud.DualCrudConfig;
import net.anotheria.anoprise.dualcrud.DualCrudService;
import net.anotheria.anoprise.dualcrud.DualCrudServiceFactory;
import net.anotheria.anoprise.dualcrud.SaveableID;
import net.anotheria.anoprise.metafactory.MetaFactory;
import net.anotheria.anoprise.metafactory.MetaFactoryException;
import net.anotheria.anosite.photoserver.api.access.AlbumAction;
import net.anotheria.anosite.photoserver.api.access.PhotoAction;
import net.anotheria.anosite.photoserver.api.blur.BlurSettingsAPI;
import net.anotheria.anosite.photoserver.api.blur.BlurSettingsAPIException;
import net.anotheria.anosite.photoserver.api.photo.ceph.PhotoCephClientService;
import net.anotheria.anosite.photoserver.api.photo.fs.PhotoStorageFSService;
import net.anotheria.anosite.photoserver.api.upload.PhotoUploadAPIConfig;
import net.anotheria.anosite.photoserver.presentation.shared.PhotoDimension;
import net.anotheria.anosite.photoserver.presentation.shared.PhotoUtil;
import net.anotheria.anosite.photoserver.service.storage.AlbumBO;
import net.anotheria.anosite.photoserver.service.storage.AlbumNotFoundServiceException;
import net.anotheria.anosite.photoserver.service.storage.AlbumWithPhotosServiceException;
import net.anotheria.anosite.photoserver.service.storage.DefaultPhotoNotFoundServiceException;
import net.anotheria.anosite.photoserver.service.storage.PhotoBO;
import net.anotheria.anosite.photoserver.service.storage.PhotoNotFoundServiceException;
import net.anotheria.anosite.photoserver.service.storage.StorageConfig;
import net.anotheria.anosite.photoserver.service.storage.StorageService;
import net.anotheria.anosite.photoserver.service.storage.StorageServiceException;
import net.anotheria.anosite.photoserver.shared.ApprovalStatus;
import net.anotheria.anosite.photoserver.shared.CroppingType;
import net.anotheria.anosite.photoserver.shared.ModifyPhotoSettings;
import net.anotheria.anosite.photoserver.shared.PhotoServerConfig;
import net.anotheria.anosite.photoserver.shared.vo.PhotoVO;
import net.anotheria.anosite.photoserver.shared.vo.PreviewSettingsVO;
import net.anotheria.anosite.photoserver.shared.ResizeType;
import net.anotheria.moskito.aop.annotation.Accumulate;
import net.anotheria.moskito.aop.annotation.Accumulates;
import net.anotheria.moskito.aop.annotation.Monitor;
import net.anotheria.util.StringUtils;
import net.anotheria.util.concurrency.IdBasedLock;
import net.anotheria.util.concurrency.IdBasedLockManager;
import net.anotheria.util.concurrency.SafeIdBasedLockManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * {@link net.anotheria.anosite.photoserver.api.photo.PhotoAPIImpl} main implementation.
 *
 * @author Alexandr Bolbat
 * @version $Id: $Id
 */
@Monitor(producerId = "PS_PhotoAPIImpl", category = "api", subsystem = "photoserver")
@Accumulates({
        @Accumulate(valueName = "Avg", intervalName = "5m"),
        @Accumulate(valueName = "Avg", intervalName = "1h"),
        @Accumulate(valueName = "Req", intervalName = "5m"),
        @Accumulate(valueName = "Req", intervalName = "1h"),
        @Accumulate(valueName = "Err", intervalName = "5m"),
        @Accumulate(valueName = "Err", intervalName = "1h"),
        @Accumulate(valueName = "Time", intervalName = "5m"),
        @Accumulate(valueName = "Time", intervalName = "1h")
})
public class PhotoAPIImpl extends AbstractAPIImpl implements PhotoAPI {
    /**
     * Logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(PhotoAPIImpl.class);
    /**
     * {@link IdBasedLockManager} instance.
     */
    private static final IdBasedLockManager<String> LOCK_MANAGER = new SafeIdBasedLockManager<>();
    /**
     * {@link PhotoUploadAPIConfig} instance.
     */
    private static final PhotoUploadAPIConfig photoUploadAPIConfig = PhotoUploadAPIConfig.getInstance();
    /**
     * {@link PhotoAPIConfig} instance.
     */
    private static final PhotoAPIConfig photoAPIConfig = PhotoAPIConfig.getInstance();
    /**
     * StorageService instance.
     */
    private StorageService storageService;
    /**
     * {@link LoginAPI} instance.
     */
    private LoginAPI loginAPI;
    /**
     * {@link BlurSettingsAPI} instance.
     */
    private BlurSettingsAPI blurSettingsAPI;
    /**
     * {@link DualCrudService} for photos.
     */
    private DualCrudService<PhotoFileHolder> dualCrudService;

    /** {@inheritDoc} */
    @Override
    public void init() throws APIInitException {
        try {
            storageService = MetaFactory.get(StorageService.class);
        } catch (MetaFactoryException e) {
            throw new APIInitException("Failed to get StorageService!", e);
        }

        loginAPI = APIFinder.findAPI(LoginAPI.class);
        blurSettingsAPI = APIFinder.findAPI(BlurSettingsAPI.class);

        PhotoStorageFSService photoStorageFSService = new PhotoStorageFSService();
        if (PhotoServerConfig.getInstance().isPhotoCephEnabled()) {
            DualCrudConfig config = DualCrudConfig.migrateOnTheFly();
            config.setDeleteUponMigration(false);
            dualCrudService = DualCrudServiceFactory.createDualCrudService(photoStorageFSService, new PhotoCephClientService(), config);
        } else {
            dualCrudService = DualCrudServiceFactory.createDualCrudService(photoStorageFSService, null, DualCrudConfig.useLeftOnly());
        }
    }

    // album related method's ---------------------------------------------------------

    /** {@inheritDoc} */
    @Override
    public AlbumAO getAlbum(long albumId) throws PhotoAPIException {
        return getAlbum(albumId, PhotosFiltering.DEFAULT);
    }

    /** {@inheritDoc} */
    @Override
    public AlbumAO getAlbum(long albumId, PhotosFiltering filtering) throws PhotoAPIException {
        return getAlbum(albumId, filtering, null);
    }

    /** {@inheritDoc} */
    @Override
    public AlbumAO getAlbum(long albumId, PhotosFiltering filtering, String authorId) throws PhotoAPIException {
        try {
            AlbumBO album = storageService.getAlbum(albumId);

            isAllowedForAction(AlbumAction.VIEW, album.getUserId(), authorId); // security check

            album.setPhotosOrder(filterNotApproved(album.getUserId(), album.getId(), album.getPhotosOrder(), filtering)); // filtering not approved photos from
            // order

            return new AlbumAO(album);
        } catch (AlbumNotFoundServiceException e) {
            throw new AlbumNotFoundPhotoAPIException(albumId);
        } catch (StorageServiceException e) {
            String message = "getAlbum(" + albumId + ") fail.";
            LOG.warn(message, e);
            throw new PhotoAPIException(message, e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public List<AlbumAO> getAlbums(String userId) throws PhotoAPIException {
        return getAlbums(userId, PhotosFiltering.DEFAULT);
    }

    /** {@inheritDoc} */
    @Override
    public List<AlbumAO> getAlbums(String userId, PhotosFiltering filtering) throws PhotoAPIException {
        return getAlbums(userId, filtering, null);
    }

    /** {@inheritDoc} */
    @Override
    public List<AlbumAO> getAlbums(String userId, PhotosFiltering filtering, String authorId) throws PhotoAPIException {
        if (StringUtils.isEmpty(userId))
            throw new IllegalArgumentException("UserId is not valid");

        isAllowedForAction(AlbumAction.VIEW, userId, authorId); // security check

        try {
            List<AlbumAO> result = new ArrayList<>();
            for (AlbumBO album : storageService.getAlbums(userId)) {
                album.setPhotosOrder(filterNotApproved(album.getUserId(), album.getId(), album.getPhotosOrder(), filtering)); // filtering not approved photos
                // from order
                result.add(new AlbumAO(album));
            }

            return result;
        } catch (StorageServiceException e) {
            String message = "getAlbums(" + userId + ") fail.";
            LOG.warn(message, e);
            throw new PhotoAPIException(message, e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public AlbumAO getDefaultAlbum(String userId) throws PhotoAPIException {
        return getDefaultAlbum(userId, PhotosFiltering.DEFAULT);
    }

    /** {@inheritDoc} */
    @Override
    public AlbumAO getDefaultAlbum(String userId, PhotosFiltering filtering) throws PhotoAPIException {
        return getDefaultAlbum(userId, filtering, null);
    }

    /** {@inheritDoc} */
    @Override
    public AlbumAO getDefaultAlbum(String userId, PhotosFiltering filtering, String authorId) throws PhotoAPIException {
        if (StringUtils.isEmpty(userId))
            throw new IllegalArgumentException("UserId is not valid");
        try {
            AlbumBO album = storageService.getDefaultAlbum(userId);

            isAllowedForAction(AlbumAction.VIEW, album.getUserId(), authorId); // security check
            album.setPhotosOrder(filterNotApproved(album.getUserId(), album.getId(), album.getPhotosOrder(), filtering)); // filtering not approved photos from
            // order

            return new AlbumAO(album);
        } catch (StorageServiceException e) {
            String message = "getDefaultAlbum(" + userId + ") fail.";
            LOG.warn(message, e);
            throw new PhotoAPIException(message, e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public AlbumAO createAlbum(AlbumAO album) throws PhotoAPIException {
        return createAlbum(album, null);
    }

    /** {@inheritDoc} */
    @Override
    public AlbumAO createAlbum(AlbumAO album, String authorId) throws PhotoAPIException {
        if (album == null)
            throw new IllegalArgumentException("Null album");

        isAllowedForAction(AlbumAction.CREATE, album.getUserId(), authorId); // security check

        try {
            return new AlbumAO(storageService.createAlbum(new AlbumBO(album)));
        } catch (StorageServiceException e) {
            String message = "createAlbum(" + album + ") fail.";
            LOG.warn(message, e);
            throw new PhotoAPIException(message, e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public AlbumAO updateAlbum(AlbumAO album) throws PhotoAPIException {
        return updateAlbum(album, null);
    }

    /** {@inheritDoc} */
    @Override
    public AlbumAO updateAlbum(AlbumAO album, String authorId) throws PhotoAPIException {
        if (album == null)
            throw new IllegalArgumentException("Null album");

        isAllowedForAction(AlbumAction.EDIT, album.getUserId(), authorId); // security check

        try {
            return new AlbumAO(storageService.updateAlbum(new AlbumBO(album)));
        } catch (StorageServiceException e) {
            String message = "updateAlbum(" + album + ") fail.";
            LOG.warn(message, e);
            throw new PhotoAPIException(message, e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public AlbumAO removeAlbum(long albumId) throws PhotoAPIException {
        return removeAlbum(albumId, null);
    }

    /** {@inheritDoc} */
    @Override
    public AlbumAO removeAlbum(long albumId, String authorId) throws PhotoAPIException {
        AlbumAO result = getAlbum(albumId, PhotosFiltering.DISABLED);

        isAllowedForAction(AlbumAction.REMOVE_PHOTO, result.getUserId(), authorId); // security check

        try {
            return new AlbumAO(storageService.removeAlbum(albumId));
        } catch (AlbumWithPhotosServiceException e) {
            throw new AlbumWithPhotosPhotoAPIException(albumId);
        } catch (StorageServiceException e) {
            String message = "removeAlbum(" + albumId + ") fail.";
            LOG.warn(message, e);
            throw new PhotoAPIException(message, e);
        }
    }

    // "my" album related method's ---------------------------------------------------------

    /** {@inheritDoc} */
    @Override
    public List<AlbumAO> getMyAlbums() throws PhotoAPIException {
        return getAlbums(getMyUserId(), PhotosFiltering.DISABLED);
    }

    /** {@inheritDoc} */
    @Override
    public AlbumAO getMyDefaultAlbum() throws PhotoAPIException {
        return getDefaultAlbum(getMyUserId(), PhotosFiltering.DISABLED);
    }

    // photo related method's ---------------------------------------------------------

    /** {@inheritDoc} */
    @Override
    public PhotoAO getMyDefaultPhoto() throws PhotoAPIException {
        return getDefaultPhoto(getMyUserId());
    }

    /** {@inheritDoc} */
    @Override
    public PhotoAO getDefaultPhoto(String userId) throws PhotoAPIException {
        if (StringUtils.isEmpty(userId))
            throw new IllegalArgumentException("UserId is not valid");

        try {
            PhotoBO photo = storageService.getDefaultPhoto(userId);
            isAllowedToMe(PhotoAction.VIEW, photo.getUserId(), userId); // security check
            PhotoAO result = new PhotoAO(photo);
            // populate Blur settings!
            result.setBlurred(blurSettingsAPI.readMyBlurSettings(photo.getAlbumId(), photo.getId()));
            return result;
        } catch (DefaultPhotoNotFoundServiceException e) {
            LOG.debug("getDefaultPhoto(" + userId + ") failed");
            throw new DefaultPhotoNotFoundAPIException(e.getMessage());
        } catch (StorageServiceException | BlurSettingsAPIException e) {
            String message = "getDefaultPhoto(" + userId + ") fail.";
            LOG.warn(message, e);
            throw new PhotoAPIException(message, e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public PhotoAO getDefaultPhoto(String userId, long albumId) throws PhotoAPIException {
        if (StringUtils.isEmpty(userId))
            throw new IllegalArgumentException("UserId is not valid");
        try {
            PhotoBO photo = storageService.getDefaultPhoto(userId, albumId);
            isAllowedToMe(PhotoAction.VIEW, photo.getUserId(), userId); // security check
            PhotoAO result = new PhotoAO(photo);
            // populate Blur settings!
            result.setBlurred(blurSettingsAPI.readMyBlurSettings(photo.getAlbumId(), photo.getId()));
            return result;
        } catch (DefaultPhotoNotFoundServiceException e) {
            LOG.error("getDefaultPhoto(" + userId + ", " + albumId + ") failed", e);
            throw new DefaultPhotoNotFoundAPIException(e.getMessage());
        } catch (StorageServiceException | BlurSettingsAPIException e) {
            String message = "getDefaultPhoto(" + userId + "," + albumId + ") fail.";
            LOG.warn(message, e);
            throw new PhotoAPIException(message, e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public PhotoAO getPhoto(long photoId) throws PhotoAPIException {
        try {
            PhotoVO photo = storageService.getPhoto(photoId);

            isAllowedToMe(PhotoAction.VIEW, photo.getUserId(), photo.getUserId()); // security check
            PhotoAO result = new PhotoAO(photo);
            result.setBlurred(blurSettingsAPI.readMyBlurSettings(photo.getAlbumId(), photo.getId()));
            return result;
        } catch (PhotoNotFoundServiceException e) {
            throw new PhotoNotFoundPhotoAPIException(photoId);
        } catch (StorageServiceException | BlurSettingsAPIException e) {
            String message = "getPhoto(" + photoId + ") fail.";
            LOG.warn(message, e);
            throw new PhotoAPIException(message, e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public List<PhotoAO> getPhotos(long albumId) throws PhotoAPIException {
        return getPhotos(albumId, PhotosFiltering.DEFAULT, false);
    }

    /** {@inheritDoc} */
    @Override
    public List<PhotoAO> getPhotos(long albumId, PhotosFiltering filtering, boolean orderByPhotosOrder) throws PhotoAPIException {

        final String defaultPhotoOwnerId = "-10"; //remove  after  refactoring - for  now  it's  actual for  failing security check.
        isAllowedToMe(PhotoAction.VIEW, defaultPhotoOwnerId, defaultPhotoOwnerId); // security check

        try {
            AlbumAO album = getAlbum(albumId, filtering);
            List<PhotoAO> photos = preparePhotos(album.getId(), storageService.getPhotos(album.getUserId(), album.getId()));
            photos = filterNotApproved(photos, filtering);
            if (orderByPhotosOrder)
                photos = orderByPhotosOrder(photos, album.getPhotosOrder());
            return photos;
        } catch (StorageServiceException | BlurSettingsAPIException e) {
            String message = "getPhotos(" + albumId + ") fail.";
            LOG.warn(message, e);
            throw new PhotoAPIException(message, e);
        }
    }

    /**
     * Method for ordering photos by photoOrder stored in Album. First goes photos by photoOrder, other photos are added in the passed order.
     *
     * @param photos - photos that need ordering.
     * @param ids    - photoOrder from Album
     * @return       - {@link List} of {@link PhotoBO}
     */
    private List<PhotoAO> orderByPhotosOrder(List<PhotoAO> photos, List<Long> ids) {
        if (photos == null)
            throw new IllegalArgumentException("Null list of photos received!");
        if (photos.isEmpty() || ids == null || ids.isEmpty())
            return photos;

        Map<Long, PhotoAO> photosMap = new LinkedHashMap<>();
        List<PhotoAO> result = new ArrayList<>();
        for (PhotoAO photo : photos)
            photosMap.put(photo.getId(), photo);

        for (Long id : ids) {
            PhotoAO photo = photosMap.remove(id);
            if (photo != null) {// list of photos can be already filtered by approval status or whatever.
                result.add(photo);
            } else {
                long albumId = photos.get(0).getAlbumId();
                LOG.warn("Album with ID=" + albumId + " contains photoID=" + id + " in photoOrder, but does not contain photo with such ID.");
            }
        }
        result.addAll(photosMap.values());
        return result;
    }

    /**
     * Create photoAO collection populated with BlurSettings, etc.
     *
     * @param albumId  id of album
     * @param photoVOs photoBO itself
     * @return {@link java.util.List<PhotoAO>}
     * @throws BlurSettingsAPIException on BlurSettingsAPI errors
     */
    private List<PhotoAO> preparePhotos(long albumId, List<PhotoBO> photoVOs) throws BlurSettingsAPIException {
        if (photoVOs.isEmpty())
            return new ArrayList<>();
        List<PhotoAO> result = new ArrayList<>(photoVOs.size());
        List<Long> ids = new ArrayList<>(photoVOs.size());
        for (PhotoBO photo : photoVOs) {
            result.add(new PhotoAO(photo));
            ids.add(photo.getId());
        }
        Map<Long, Boolean> blurSettingsMap = blurSettingsAPI.readMyBlurSettings(albumId, ids);
        for (PhotoAO photoAO : result) {
            Boolean blurred = blurSettingsMap.get(photoAO.getId());
            if (blurred != null)
                photoAO.setBlurred(blurred);
        }
        return result;

    }

    /** {@inheritDoc} */
    @Override
    public PhotoAO createPhoto(String userId, File tempFile, PreviewSettingsVO previewSettings) throws PhotoAPIException {
        return createPhoto(userId, tempFile, previewSettings, false);
    }

    /** {@inheritDoc} */
    @Override
    public PhotoAO createPhoto(String userId, File tempFile, PreviewSettingsVO previewSettings, boolean restricted) throws PhotoAPIException {
        if (StringUtils.isEmpty(userId))
            throw new IllegalArgumentException("UserId is not valid");
        if (tempFile == null)
            throw new IllegalArgumentException("Null temp file");

        isAllowedToMe(PhotoAction.ADD, userId, userId); // security check

        long albumId = getDefaultAlbum(userId, PhotosFiltering.DISABLED).getId();
        return createPhoto(userId, albumId, restricted, tempFile, previewSettings);
    }

    /** {@inheritDoc} */
    @Override
    public PhotoAO createPhoto(String userId, long albumId, File tempFile, PreviewSettingsVO previewSettings) throws PhotoAPIException {
        return createPhoto(userId, albumId, false, tempFile, previewSettings);
    }

    /** {@inheritDoc} */
    @Override
    public PhotoAO createPhoto(String userId, long albumId, boolean restricted, File tempFile, PreviewSettingsVO previewSettings) throws PhotoAPIException {
        if (StringUtils.isEmpty(userId))
            throw new IllegalArgumentException("UserId is not valid");
        if (tempFile == null)
            throw new IllegalArgumentException("Null temp file");

        isAllowedToMe(PhotoAction.ADD, userId, userId); // security check

        AlbumAO album = getAlbum(albumId, PhotosFiltering.DISABLED, userId);

        PhotoBO photo = new PhotoBO();
        photo.setUserId(userId);
        photo.setAlbumId(albumId);
        photo.setRestricted(restricted);
        photo.setExtension(PhotoUploadAPIConfig.getInstance().getFilePrefix());
        photo.setPreviewSettings(previewSettings);

        try {
            // creating photo
            photo = storageService.createPhoto(photo);

            PhotoFileHolder photoFileHolder = new PhotoFileHolder(String.valueOf(photo.getId()), photo.getExtension());
            photoFileHolder.setPhotoFileInputStream(new FileInputStream(tempFile));
            photoFileHolder.setFileLocation(photo.getFileLocation());
            dualCrudService.create(photoFileHolder);

            // updating photo album
            album.addPhotoToPhotoOrder(photo.getId());
            updateAlbum(album, userId);

            return new PhotoAO(photo);
        } catch (StorageServiceException | CrudServiceException | FileNotFoundException e) {
            String message = "createPhoto(" + userId + ", " + tempFile + ", " + previewSettings + ") fail.";
            LOG.warn(message, e);
            throw new PhotoAPIException(message, e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public PhotoAO updatePhoto(PhotoAO photo) throws PhotoAPIException {
        return updatePhoto(getMyUserId(), photo);
    }

    /** {@inheritDoc} */
    @Override
    public PhotoAO updatePhoto(String userId, PhotoAO photo) throws PhotoAPIException {
        if (photo == null)
            throw new IllegalArgumentException("Null photo");
        if (StringUtils.isEmpty(userId))
            throw new IllegalArgumentException("UserId is not valid");

        isAllowedToMe(PhotoAction.EDIT, photo.getUserId(), userId); // security check

        try {
            return new PhotoAO(storageService.updatePhoto(new PhotoBO(photo)));
        } catch (PhotoNotFoundServiceException e) {
            throw new PhotoNotFoundPhotoAPIException(photo.getId());
        } catch (StorageServiceException e) {
            String message = "updatePhoto(" + photo + ") fail.";
            LOG.warn(message, e);
            throw new PhotoAPIException(message, e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public PhotoAO removePhoto(long photoId) throws PhotoAPIException {
        return removePhoto(getMyUserId(), photoId);
    }

    /** {@inheritDoc} */
    @Override
    public PhotoAO removePhoto(String userId, long photoId) throws PhotoAPIException {
        if (StringUtils.isEmpty(userId))
            throw new IllegalArgumentException("UserId is not valid");

        PhotoAO photo = getPhoto(photoId);

        isAllowedToMe(PhotoAction.EDIT, photo.getUserId(), userId); // security check

        try {
            storageService.removePhoto(photoId);

            PhotoFileHolder photoFileHolder = new PhotoFileHolder(String.valueOf(photoId), photo.getExtension());
            photoFileHolder.setFileLocation(photo.getFileLocation());
            dualCrudService.delete(photoFileHolder);

            return photo;
        } catch (PhotoNotFoundServiceException e) {
            throw new PhotoNotFoundPhotoAPIException(photo.getId());
        } catch (StorageServiceException e) {
            String message = "removePhoto(" + photo + ") fail.";
            LOG.warn(message, e);
            throw new PhotoAPIException(message, e);
        } catch (CrudServiceException e) {
            String message = "removePhoto(" + photo + ") fail. Delete from storage fail. ";
            LOG.warn(message, e);
            throw new PhotoAPIException(message, e);
        } finally {
            AlbumAO album = getAlbum(photo.getAlbumId());
            album.removePhotofromPhotoOrder(photoId);
            try {
                storageService.updateAlbum(new AlbumBO(album));
            } catch (StorageServiceException e) {
                LOG.warn("removePhoto(" + photo + ") fail. Failed to remove photo from albums photoOrder.", e);
            }
        }
    }

    // ------------------------------------------------------------------------------------------------------------------------------------

    /**
     * Get currently logged in user id.
     *
     * @return id of currently logged in user
     */
    private String getMyUserId() throws PhotoAPIException {
        try {
            return getLoggedInUserId();
        } catch (NoLoggedInUserException e) {
            throw new PhotoAPIException("User not logged in", e);
        }
    }

    /**
     * Check is user can perform action on photo.
     *
     * @param photoAction  - action that user tries to perform.
     * @param userId       - user id that try to do action
     * @param photoOwnerId - photo owner user id
     * @throws PhotoAPIException if any errors occurs
     */
    private void isAllowedToMe(PhotoAction photoAction, String photoOwnerId, String userId) throws PhotoAPIException {
        if (PhotoAction.VIEW.equals(photoAction) || photoOwnerId.equals(userId))
            return;

        throw new NoAccessPhotoAPIException("No access.");
    }

    /**
     * Check is user can perform action on photo.
     *
     * @param albumAction  - action that user tries to perform.
     * @param albumOwnerId - album owner id
     * @throws PhotoAPIException if any errors occurs
     */
    private void isAllowedForAction(AlbumAction albumAction, String albumOwnerId, String authorId) throws PhotoAPIException {
        // TODO: fix this ugly method in future

        boolean result = false;
        switch (albumAction) {
            case VIEW:
                result = true; // all can see all photos
                break;
            case CREATE:
                result = !StringUtils.isEmpty(authorId) || loginAPI.isLogedIn(); // logged in users can add albums
                break;
            case REMOVE_PHOTO:
                result = (StringUtils.isEmpty(authorId) && albumOwnerId != null) ||//case for not logged user(some deletion job for example)
                        loginAPI.isLogedIn() && albumOwnerId != null && getMyUserId().equals(albumOwnerId); // logged in users can do anything with own albums
                break;
            default:
                result = (!StringUtils.isEmpty(authorId) && albumOwnerId != null && authorId.equals(albumOwnerId) || loginAPI.isLogedIn() && albumOwnerId != null && getMyUserId().equals(albumOwnerId)); // logged in users can do anything with own albums
                break;
        }

        if (!result)
            throw new NoAccessPhotoAPIException("No access.");
    }

    /** {@inheritDoc} */
    @Override
    public int getWaitingApprovalPhotosCount() throws PhotoAPIException {
        try {
            return storageService.getWaitingApprovalPhotosCount();
        } catch (StorageServiceException e) {
            String message = "getWaitingApprovalPhotosCount() failed.";
            LOG.warn(message, e);
            throw new PhotoAPIException(message, e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public List<PhotoAO> getWaitingApprovalPhotos(int amount) throws PhotoAPIException {
        if (amount < 0)
            throw new IllegalArgumentException("Illegal photos amount selected amount[" + amount + "]");

        if (amount == 0) {
            return new ArrayList<>();
        }

        try {
            // use same method as for bulk change of approval statuses.
            return map(storageService.getWaitingApprovalPhotos(amount));
        } catch (StorageServiceException e) {
            String message = "getWaitingApprovalPhotos(" + amount + ") failed.";
            LOG.warn(message, e);
            throw new PhotoAPIException(message, e);
        }
    }

    /**
     * Map {@link PhotoBO} collection to {@link PhotoAO} collection.
     *
     * @param waitingApprovalPhotos incoming PhotoBO collection
     * @return mapped result
     */
    private List<PhotoAO> map(List<PhotoBO> waitingApprovalPhotos) {
        List<PhotoAO> result = new ArrayList<>(waitingApprovalPhotos.size());
        for (PhotoBO photoBO : waitingApprovalPhotos)
            result.add(new PhotoAO(photoBO));
        return result;
    }

    /** {@inheritDoc} */
    @Override
    public void setApprovalStatus(long photoId, ApprovalStatus status) throws PhotoAPIException {
        if (status == null) {
            throw new IllegalArgumentException("Null ApprovalStatus!");
        }
        try {
            // using same method as for bulk change of approval statuses.
            storageService.updatePhotoApprovalStatuses(Collections.singletonMap(photoId, status));
        } catch (StorageServiceException e) {
            String message = "setApprovalStatus(" + photoId + ", " + status + ") failed.";
            LOG.warn(message, e);
            throw new PhotoAPIException(message, e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void setApprovalStatuses(Map<Long, ApprovalStatus> statuses) throws PhotoAPIException {
        if (statuses == null) {
            throw new IllegalArgumentException("Null argument received!");
        }
        try {
            storageService.updatePhotoApprovalStatuses(statuses);
        } catch (StorageServiceException e) {
            String message = "setApprovalStatuses(" + statuses + ") failed.";
            LOG.warn(message, e);
            throw new PhotoAPIException(message, e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public PhotoAO movePhoto(long photoId, long newAlbumId) throws PhotoAPIException {
        PhotoAO photo = getPhoto(photoId);
        AlbumAO album = getAlbum(newAlbumId);
        AlbumAO oldAlbum = getAlbum(photo.getAlbumId());

        if (!album.getUserId().equals(photo.getUserId()))
            throw new NoAccessPhotoAPIException("No access.");


        isAllowedToMe(PhotoAction.EDIT, photo.getUserId(), photo.getUserId()); // security check
        isAllowedForAction(AlbumAction.EDIT, album.getUserId(), photo.getUserId()); // security check

        try {
            PhotoAO updatedPhoto = new PhotoAO(storageService.movePhoto(photoId, newAlbumId));
            oldAlbum.removePhotofromPhotoOrder(photoId);
            storageService.updateAlbum(new AlbumBO(oldAlbum));
            album.addPhotoToPhotoOrder(photoId);
            storageService.updateAlbum(new AlbumBO(album));

            return updatedPhoto;
        } catch (PhotoNotFoundServiceException e) {
            throw new PhotoNotFoundPhotoAPIException(photo.getId());
        } catch (StorageServiceException e) {
            String message = "updatePhoto(" + photo + ") fail.";
            LOG.warn(message, e);
            throw new PhotoAPIException(message, e);
        }
    }

    @Override
    public InputStream getPhotoContent(PhotoAO photo) throws PhotoAPIException {
        if (photo == null)
            throw new IllegalArgumentException("Photo is null");

        try {
            return getPhotoContent(String.valueOf(photo.getId()), photo);
        } catch (CrudServiceException e) {
            String message = "Unable to read photo stream from storage: " + e.getMessage();
            LOG.error(message, e);
            throw new PhotoAPIException(message, e);
        }
    }

    @Override
    public InputStream getCachedPhotoContent(PhotoAO photoAO, ModifyPhotoSettings modifyPhotoSettings, boolean cropped, int croppingType, boolean blurred) throws PhotoAPIException {
        // preparing cached photo postfix
        String cachedFileName = String.valueOf(photoAO.getId());
        cachedFileName += cropped ? "_c_t" + croppingType : "";
        if (modifyPhotoSettings.isResized()) {
            switch (modifyPhotoSettings.getResizeType()) {
                case SIZE:
                    cachedFileName += "_s" + modifyPhotoSettings.getSize();
                    break;
                case BOUNDING_AREA:
                    cachedFileName += "_ba" + modifyPhotoSettings.getBoundaryWidth() + "_" + modifyPhotoSettings.getBoundaryHeight();
                    break;
            }
        }
        cachedFileName += blurred ? "_b" : "";

        try {
            return getPhotoContent(cachedFileName, photoAO);
        } catch (CrudServiceException e) {
            //Cached file not found, try to generate new.
        }


        // locking all incoming photo modification requests for same picture
        IdBasedLock<String> lock = LOCK_MANAGER.obtainLock(cachedFileName);
        lock.lock();
        try {
            // checking again cached photo and steaming it if exist
            try {
                return getPhotoContent(cachedFileName, photoAO);
            } catch (CrudServiceException e) {
                //Cached file not found in lock again, try to generate new.
            }

            modifyPhotoSettings.setCropped(cropped);
            modifyPhotoSettings.setBlurred(blurred);
            modifyPhotoSettings.setCroppingType(CroppingType.valueOf(croppingType));

            // modifying photo and storing to new photo file
            modifyPhoto(cachedFileName, modifyPhotoSettings, photoAO);

            //try to read cached photo again after save
            return getPhotoContent(cachedFileName, photoAO);

        } catch (IOException | CrudServiceException e) {
            String failMsg = "Unable to process cached file after save. " + e.getMessage();
            LOG.error(failMsg, e);
            throw new PhotoAPIException(failMsg);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Modify photo and store new file in storage.
     *
     * @param cachedFileName        file name
     * @param modifyPhotoSettings   {@link ModifyPhotoSettings} for photo
     * @param photoAO               {@link PhotoAO} instance
     * @throws PhotoAPIException    if errors occurs
     * @throws IOException          if errors occurs
     * @throws CrudServiceException if errors occurs
     */
    private void modifyPhoto(String cachedFileName, ModifyPhotoSettings modifyPhotoSettings, PhotoAO photoAO) throws PhotoAPIException, IOException, CrudServiceException {
        final PreviewSettingsVO pvSettings = photoAO.getPreviewSettings();

        // read photo file
        PhotoUtil putil = new PhotoUtil();
        putil.read(getPhotoContent(photoAO));

        // if blur param is present or photo should be blurred for user we have to blur image
        if (modifyPhotoSettings.isBlurred())
            putil.blur();

        // if preview param is present we have to crop image first
        if (modifyPhotoSettings.isCropped()) {
            PhotoDimension move = new PhotoDimension(pvSettings.getX(), pvSettings.getY());
            PhotoDimension crop = new PhotoDimension(pvSettings.getWidth(), pvSettings.getHeight());
            PhotoDimension originalDimension = new PhotoDimension(putil.getWidth(), putil.getHeight());
            PhotoDimension workbenchDimension;
            if (putil.getHeight() > putil.getWidth()) {
                workbenchDimension = new PhotoDimension(photoUploadAPIConfig.getWorkbenchWidth() * putil.getWidth() / putil.getHeight(),
                        photoUploadAPIConfig.getWorkbenchWidth());
            } else {
                workbenchDimension = new PhotoDimension(photoUploadAPIConfig.getWorkbenchWidth(), photoUploadAPIConfig.getWorkbenchWidth() * putil.getHeight()
                        / putil.getWidth());
            }
            PhotoDimension xy = move.getRelationTo(workbenchDimension, originalDimension);
            PhotoDimension wh = crop.getRelationTo(workbenchDimension, originalDimension);
            putil.crop(xy.w, xy.h, wh.w, wh.h);
        }

        // scale photo if needed
        if (modifyPhotoSettings.isResized()) {
            int height = putil.getHeight();
            int width = putil.getWidth();

            switch (modifyPhotoSettings.getResizeType()) {
                // scale by size
                case SIZE:
                    int size = modifyPhotoSettings.getSize();
                    switch (modifyPhotoSettings.getCroppingType()) {
                        case HEIGHT:
                            putil.scale((int) ((double) size / height * width), size);
                            break;
                        case NATURAL_HEIGHT:
                            height = height < size ? height : size;
                            width = (int) ((double) height / putil.getHeight() * width);

                            putil.scale(width, height);
                            break;
                        case WIDTH:
                            putil.scale(size, (int) ((double) size / width * height));
                            break;
                        case NATURAL_WIDTH:
                            width = width < size ? width : size;
                            height = (int) ((double) width / putil.getWidth() * height);

                            putil.scale(width, height);
                            break;
                        case BOTH:
                            putil.scale(size);
                            break;
                        case NATURAL_BOTH:
                            if (width > size && height > size)
                                putil.scale(size);
                            break;
                    }
                    break;
                // scale along the bounding area
                case BOUNDING_AREA:
                    scaleAlongBoundary(putil, width, height, modifyPhotoSettings.getBoundaryWidth(), modifyPhotoSettings.getBoundaryHeight());
                    break;
            }
        }

        File baseFolder = new File(StorageConfig.getTmpStoreFolderPath(photoAO.getUserId()));
        baseFolder.mkdirs();
        File tmpFile = new File(baseFolder, cachedFileName + photoAO.getExtension());
        putil.write(photoAPIConfig.getJpegQuality(), tmpFile);

        PhotoFileHolder photoFileHolder = new PhotoFileHolder(cachedFileName, photoAO.getExtension());
        photoFileHolder.setPhotoFileInputStream(new FileInputStream(tmpFile));
        photoFileHolder.setFileLocation(photoAO.getFileLocation());
        dualCrudService.create(photoFileHolder);
        tmpFile.delete();
    }

    private InputStream getPhotoContent(String id, PhotoAO photoAO) throws CrudServiceException {
        PhotoFileHolder photoFileHolder = new PhotoFileHolder(id, photoAO.getExtension());
        photoFileHolder.setFileLocation(photoAO.getFileLocation());

        SaveableID saveableID = new SaveableID();
        saveableID.setOwnerId(photoFileHolder.getOwnerId());
        saveableID.setSaveableId(photoFileHolder.getFilePath());

        return dualCrudService.read(saveableID).getPhotoFileInputStream();
    }

    /**
     * Scale width and height of the original image along the incoming width and height of the bounding area .
     *
     * @param photoUtil      {@link PhotoUtil}
     * @param width          original image width
     * @param height         original image height
     * @param boundaryWidth  width of the bounding area
     * @param boundaryHeight height of the bounding area
     */
    private void scaleAlongBoundary(final PhotoUtil photoUtil, int width, int height, int boundaryWidth, int boundaryHeight) {
        double boundaryAspectRatio = (double) boundaryWidth / boundaryHeight;
        double originalImageAspectRatio = (double) width / height;

        if (boundaryAspectRatio < originalImageAspectRatio) {
            photoUtil.scale(boundaryWidth, (int) ((double) boundaryWidth / width * height));
            return;
        }

        if (boundaryAspectRatio > originalImageAspectRatio) {
            photoUtil.scale((int) ((double) boundaryHeight / height * width), boundaryHeight);
            return;
        }

        // case when aspect ratio is the same
        int size = boundaryWidth >= boundaryHeight ? boundaryWidth : boundaryHeight;
        photoUtil.scale(size);
    }

    private List<PhotoAO> filterNotApproved(List<PhotoAO> photos, PhotosFiltering filtering) throws PhotoAPIException {
        if (filtering == null)
            filtering = PhotosFiltering.DEFAULT;
        if (!filtering.filteringEnabled || !PhotoServerConfig.getInstance().isPhotoApprovingEnabled())
            return photos;

        List<PhotoAO> result = new ArrayList<>();
        try {
            for (PhotoAO photo : photos) {
                if (loginAPI.isLogedIn() && loginAPI.getLogedUserId().equalsIgnoreCase(String.valueOf(photo.getUserId()))) {
                    result.add(photo);
                    continue;
                }
                if (filtering.allowedStatuses.contains(photo.getApprovalStatus()))
                    result.add(photo);
            }
        } catch (APIException e) {
            throw new PhotoAPIException("filterNotApproved(" + photos + ") fail.", e);
        }

        return result;
    }

    private List<Long> filterNotApproved(String ownerId, long albumId, List<Long> photosIds, PhotosFiltering filtering) throws PhotoAPIException {
        if (StringUtils.isEmpty(ownerId))
            throw new IllegalArgumentException("ownerId is not valid");

        if (filtering == null)
            filtering = PhotosFiltering.DEFAULT;
        if (!filtering.filteringEnabled || !PhotoServerConfig.getInstance().isPhotoApprovingEnabled())
            return photosIds;
        try {
            if (loginAPI.isLogedIn() && loginAPI.getLogedUserId().equalsIgnoreCase(ownerId))
                return photosIds;
        } catch (APIException e) {
            throw new PhotoAPIException("filterNotApproved(" + albumId + ", " + photosIds + ") fail.", e);
        }

        try {
            Map<Long, ApprovalStatus> approvalStatuses = storageService.getAlbumPhotosApprovalStatus(albumId);

            List<Long> result = new ArrayList<>();
            for (long photoId : photosIds) {
                ApprovalStatus status = approvalStatuses.get(photoId);
                if (status != null && filtering.allowedStatuses.contains(status))
                    result.add(photoId);
            }
            return result;
        } catch (StorageServiceException e) {
            throw new PhotoAPIException("filterNotApproved(" + albumId + ", " + photosIds + ") fail.", e);
        }
    }
}
