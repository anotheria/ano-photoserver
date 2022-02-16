package net.anotheria.anosite.photoserver.api.photo.ceph;

import net.anotheria.moskito.core.decorators.DecoratorRegistryFactory;
import net.anotheria.moskito.core.predefined.Constants;
import net.anotheria.moskito.core.producers.GenericStats;
import net.anotheria.moskito.core.stats.StatValue;
import net.anotheria.moskito.core.stats.impl.StatValueFactory;

/**
 * Metrics for ceph photo storage.
 *
 * @author ykalapusha
 */
public class PhotoCephClientStats extends GenericStats {
    //register decorator for this stats
    static {
        DecoratorRegistryFactory.getDecoratorRegistry().addDecorator(PhotoCephClientStats.class, new PhotoCephClientStatsDecorator());
    }

    /**
     * Photos, which we try to add to the ceph storage.
     */
    private final StatValue photosToAdd;
    /**
     * Photos, which successful added to the ceph storage.
     */
    private final StatValue addedPhotos;
    /**
     * Photos, which we try to read from the ceph storage.
     */
    private final StatValue photosToRead;
    /**
     * Photos, which successful read from the ceph storage.
     */
    private final StatValue readPhotos;
    /**
     * Photos, which was not found in the ceph storage.
     */
    private final StatValue notFoundPhotos;
    /**
     * Photos, which we try to remove from the ceph storage.
     */
    private final StatValue photosToRemove;
    /**
     * Photos successful removed from the ceph storage.
     */
    private final StatValue removedPhotos;
    /**
     * Number of photo checks in the ceph storage.
     */
    private final StatValue isPhotoExistsCheck;
    /**
     * CRUD errors count.
     */
    private final StatValue crudErrors;

    /**
     * Constructs an instance of GenericStats.
     *
     * @param aName name of the stats object.
     */
    public PhotoCephClientStats(String aName) {
        super(aName);

        photosToAdd = StatValueFactory.createStatValue(0, "photosToAdd", Constants.getDefaultIntervals());
        addedPhotos = StatValueFactory.createStatValue(0, "addedPhotos", Constants.getDefaultIntervals());
        photosToRead = StatValueFactory.createStatValue(0, "photosToRead", Constants.getDefaultIntervals());
        readPhotos = StatValueFactory.createStatValue(0, "readPhotos", Constants.getDefaultIntervals());
        notFoundPhotos = StatValueFactory.createStatValue(0, "notFoundPhotos", Constants.getDefaultIntervals());
        photosToRemove = StatValueFactory.createStatValue(0, "photosToRemove", Constants.getDefaultIntervals());
        removedPhotos = StatValueFactory.createStatValue(0, "removedPhotos", Constants.getDefaultIntervals());
        isPhotoExistsCheck = StatValueFactory.createStatValue(0, "isPhotoExistsCheck", Constants.getDefaultIntervals());
        crudErrors = StatValueFactory.createStatValue(0, "crudErrors", Constants.getDefaultIntervals());
    }

    public void incPhotosToAdd() {
        photosToAdd.increase();
    }

    public long getPhotosToAdd(String intervalName) {
        return photosToAdd.getValueAsLong(intervalName);
    }

    public void incAddedPhotos() {
        addedPhotos.increase();
    }

    public long getAddedPhotos(String intervalName) {
        return addedPhotos.getValueAsLong(intervalName);
    }

    public void incPhotosToRead() {
        photosToRead.increase();
    }

    public long getPhotosToRead(String intervalName) {
        return photosToRead.getValueAsLong(intervalName);
    }

    public void incReadPhotos() {
        readPhotos.increase();
    }

    public long getReadPhotos(String intervalName) {
        return readPhotos.getValueAsLong(intervalName);
    }

    public void incNotFoundPhotos() {
        notFoundPhotos.increase();
    }

    public long getNotFoundPhotos(String intervalName) {
        return notFoundPhotos.getValueAsLong(intervalName);
    }

    public void incPhotosToRemove() {
        photosToRemove.increase();
    }

    public long getPhotosToRemove(String intervalName) {
        return photosToRemove.getValueAsLong(intervalName);
    }

    public void incRemovedPhotos() {
        removedPhotos.increase();
    }

    public long getRemovedPhotos(String intervalName) {
        return removedPhotos.getValueAsLong(intervalName);
    }

    public void incIsPhotoExistsCheck() {
        isPhotoExistsCheck.increase();
    }

    public long getIsPhotoExistsCheck(String intervalName) {
        return isPhotoExistsCheck.getValueAsLong(intervalName);
    }

    public void incCrudErrors() {
        crudErrors.increase();
    }

    public long getCrudErrors(String intervalName) {
        return crudErrors.getValueAsLong(intervalName);
    }

}
