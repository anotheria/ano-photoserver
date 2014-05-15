package net.anotheria.anosite.photoserver.service.storage;

import junit.framework.Assert;
import net.anotheria.anosite.photoserver.shared.ApprovalStatus;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Junit test!
 *
 * @author h3ll
 */
public class StorageServiceCacheTest {

	@Test
	public void albumsFuncTest() {
		StorageServiceCache testCache = new StorageServiceCache();

		final String userId = "100l";
		final long alId = 2l;

		List<AlbumBO> albums = testCache.getAllAlbums(userId);
		Assert.assertNull("Nothing is cached!", albums);

		AlbumBO album = new AlbumBO();
		album.setId(alId);
		album.setUserId("2l");

		// get album  by id!   but  nothing  is  cached ! yet!
		AlbumBO albumget = testCache.getCachedAlbumById(alId);
		Assert.assertNull("Nothing is cached!", albumget);
		// add   album to cache!
		testCache.updateItem(album);

		//read from cache
		albumget = testCache.getCachedAlbumById(alId);
		Assert.assertNotNull(albumget);
		Assert.assertEquals(album, albumget);
		Assert.assertNotSame("Clone fail", album, albumget);

		//  Update!
		album.setDescription("test!!");
		testCache.updateItem(album);
		//read from cache
		albumget = testCache.getCachedAlbumById(alId);
		Assert.assertNotNull(albumget);
		Assert.assertEquals(album, albumget);
		Assert.assertEquals(album.getName(), albumget.getName());
		Assert.assertNotSame("Clone fail", album, albumget);

		// have no   right  to fetch like that!
		List<AlbumBO> allAlbums = testCache.getAllAlbums(userId);
		Assert.assertNull("Nothing is cached!", allAlbums);


		List<AlbumBO> allUserAlbums = new ArrayList<AlbumBO>();
		allUserAlbums.add(album);

		//let's add all user albums to cache!
		testCache.cacheAlbums(userId, allUserAlbums);

		// have no   right  to fetch like that!
		allAlbums = testCache.getAllAlbums(userId);
		Assert.assertNotNull("Error", albumget);
		Assert.assertTrue("Error", !allAlbums.isEmpty());


		//remove! album!
		testCache.removeItem(album);
		//ceck that removed!
		album = testCache.getCachedAlbumById(alId);
		Assert.assertNull(album);

		//default album!
		Assert.assertNull(testCache.getDefaultAlbum(userId));


	}


	@Test
	public void testPhoto_operations() {
		final String userId = "100l";
		final long albumId = 200l;
		final long testPhotoId = 123123123l;

		StorageServiceCache cache = new StorageServiceCache();

		AlbumBO album = new AlbumBO();
		album.setId(albumId);
		album.setUserId(userId);
		album.setName("TEST");

		//album cached!
		cache.updateItem(album);

		//nothing is cached!
		PhotoBO photo = cache.getPhotoById(testPhotoId);
		Assert.assertNull(photo);

		//add to cache
		photo = new PhotoBO();
		photo.setId(testPhotoId);
		photo.setAlbumId(albumId);
		photo.setUserId(userId);

		cache.updateItem(photo);

		//read cache
		PhotoBO fromCache = cache.getPhotoById(testPhotoId);
		Assert.assertNotNull(fromCache);
		Assert.assertEquals(photo, fromCache);
		Assert.assertNotSame("Not clonned!!!", photo, fromCache);

		List<PhotoBO> allPhotos = cache.getAllAlbumPhotos(userId, albumId);
		Assert.assertNull("Permission does not exists!", allPhotos);

		//Update!
		photo.setExtension("jpg");
		cache.updateItem(photo);
		//check data
		Assert.assertEquals(photo.getExtension(), cache.getPhotoById(testPhotoId).getExtension());

		List<PhotoBO> allAlbumPhotos = new ArrayList<PhotoBO>();
		allAlbumPhotos.add(photo);

		//caching whole album!!
		cache.addAlbumPhotosToCache(userId, albumId, allAlbumPhotos);

		List<PhotoBO> photos = cache.getAllAlbumPhotos(userId, albumId);
		Assert.assertNotNull(photos);
		Assert.assertEquals(allAlbumPhotos.get(0), photos.get(0));
		Assert.assertNotSame("Cloning error", allAlbumPhotos.get(0), photos.get(0));

		//remove album!!!
		cache.removeItem(album);

		//get  should be null!!
		photos = cache.getAllAlbumPhotos(userId, albumId);
		Assert.assertNull(photos);


		//Approve Photo!
		Map<Long, ApprovalStatus> map = Collections.singletonMap(testPhotoId, ApprovalStatus.REJECTED);

		cache.updatePhotoApprovalsStatuses(map);

		PhotoBO photoReject = cache.getPhotoById(testPhotoId);
		Assert.assertNotNull(photoReject);
		Assert.assertTrue(photo.getApprovalStatus() != photoReject.getApprovalStatus());
		Assert.assertTrue(ApprovalStatus.REJECTED == photoReject.getApprovalStatus());


		//remove
		cache.removeItem(photo);
		//check that removed
		Assert.assertNull("not removed", cache.getPhotoById(testPhotoId));


	}
}
