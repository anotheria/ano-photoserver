package net.anotheria.anosite.photoserver.api.photo.ceph;

import net.anotheria.moskito.core.dynamic.IOnDemandStatsFactory;

/**
 * {@link IOnDemandStatsFactory} for {@link PhotoCephClientStats}.
 *
 * @author ykalapusha
 */
public class PhotoCephClientStatsFactory implements IOnDemandStatsFactory<PhotoCephClientStats> {

    @Override
    public PhotoCephClientStats createStatsObject(String name) {
        return new PhotoCephClientStats(name);
    }
}
