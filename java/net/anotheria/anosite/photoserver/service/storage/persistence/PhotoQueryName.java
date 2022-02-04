package net.anotheria.anosite.photoserver.service.storage.persistence;

/**
 * Names for dual crud queries.
 *
 * @author ykalapusha
 */
public enum PhotoQueryName {
    ALL_ALBUM_PHOTOS_BY_USER_ID,
    ALL_PHOTOS_FOR_USER_BY_PHOTOS_IDS,
    PHOTOS_BY_STATUS_AND_IF_EXISTS_AMOUNT,
    UPDATE_PHOTO_APPROVAL_STATUSES,
    ALL_PHOTOS_BY_ALBUM_ID,
    MOVE_PHOTO_TO_NEW_ALBUM;
}
