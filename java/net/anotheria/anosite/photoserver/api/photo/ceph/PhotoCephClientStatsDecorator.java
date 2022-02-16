package net.anotheria.anosite.photoserver.api.photo.ceph;

import net.anotheria.moskito.core.decorators.AbstractDecorator;
import net.anotheria.moskito.core.decorators.value.LongValueAO;
import net.anotheria.moskito.core.decorators.value.StatValueAO;
import net.anotheria.moskito.core.stats.TimeUnit;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link AbstractDecorator} for {@link PhotoCephClientStats}.
 *
 * @author ykalapusha
 */
public class PhotoCephClientStatsDecorator extends AbstractDecorator<PhotoCephClientStats> {
    /**
     * Array with captions.
     */
    private static final String[] CAPTIONS = {
            "Photos to Add",
            "Added Photos",
            "Photos to Read",
            "Read Photos",
            "Not Found Photos",
            "Photos to Remove",
            "Removed Photos",
            "Is Photo Exists Check",
            "CRUD Errors"
    };

    /**
     * Array with explanations.
     */
    private static final String[] EXPLANATIONS = {
            "Photos, which we try to add to the ceph storage.",
            "Photos, which successful added to the ceph storage.",
            "Photos, which we try to read from the ceph storage.",
            "Photos, which successful read from the ceph storage.",
            "Photos, which was not found in the ceph storage.",
            "Photos, which we try to remove from the ceph storage.",
            "Photos successful removed from the ceph storage.",
            "Number of photo checks in the ceph storage.",
            "CRUD errors count."
    };
    /**
     * Array with short explanations (mouse over).
     */
    private static final String[] SHORT_EXPLANATIONS = CAPTIONS;

    /**
     * Constructor of a new decorator for ceph photos.
     */
    protected PhotoCephClientStatsDecorator() {
        super("CephPhoto", CAPTIONS, SHORT_EXPLANATIONS, EXPLANATIONS);
    }

    @Override
    public List<StatValueAO> getValues(PhotoCephClientStats stats, String interval, TimeUnit unit) {
        List<StatValueAO> ret = new ArrayList<>(CAPTIONS.length);
        int i = 0;
        ret.add(new LongValueAO(CAPTIONS[i++], stats.getPhotosToAdd(interval)));
        ret.add(new LongValueAO(CAPTIONS[i++], stats.getAddedPhotos(interval)));
        ret.add(new LongValueAO(CAPTIONS[i++], stats.getPhotosToRead(interval)));
        ret.add(new LongValueAO(CAPTIONS[i++], stats.getReadPhotos(interval)));
        ret.add(new LongValueAO(CAPTIONS[i++], stats.getNotFoundPhotos(interval)));
        ret.add(new LongValueAO(CAPTIONS[i++], stats.getPhotosToRemove(interval)));
        ret.add(new LongValueAO(CAPTIONS[i++], stats.getRemovedPhotos(interval)));
        ret.add(new LongValueAO(CAPTIONS[i++], stats.getIsPhotoExistsCheck(interval)));
        ret.add(new LongValueAO(CAPTIONS[i], stats.getCrudErrors(interval)));
        return ret;
    }
}
