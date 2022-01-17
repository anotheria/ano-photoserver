package net.anotheria.anosite.photoserver.service.storage;

import junit.framework.Assert;
import net.anotheria.anoprise.metafactory.Extension;
import net.anotheria.anoprise.metafactory.MetaFactory;
import net.anotheria.anosite.photoserver.service.storage.persistence.StoragePersistenceService;
import net.anotheria.anosite.photoserver.service.storage.persistence.album.AlbumPersistenceService;
import net.anotheria.anosite.photoserver.service.storage.persistence.album.DefaultAlbumNotFoundPersistenceServiceException;
import net.anotheria.anosite.photoserver.shared.vo.PreviewSettingsVO;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.*;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class StorageServiceTest {

	private static final String USER_1 = "1";
	private static final String USER_2 = "2";
	private static final String USER_3 = "3";
	private static List<AlbumBO> albums;
	private static Map<String, List<PhotoBO>> photosMap;

	@InjectMocks
	private StorageServiceImpl storageService;

	/**
	 * Invoking before executing tests in this class.
	 */
	@BeforeClass
	public static void before() throws Exception {

		albums = new ArrayList<>();
		albums.add(createAlbumForTest(USER_1, 1L));
		albums.add(createAlbumForTest(USER_2, 2L));
		albums.add(createAlbumForTest(USER_3, 3L));

		List<PhotoBO> photos1 = new ArrayList<>();
		photos1.add(getPhotoForTest(USER_1, 10L, 1L));
		photos1.add(getPhotoForTest(USER_1, 100L, 1L));

		List<PhotoBO> photos2 = new ArrayList<>();
		photos1.add(getPhotoForTest(USER_2, 20L, 2L));
		photos1.add(getPhotoForTest(USER_2, 200L, 2L));

		List<PhotoBO> photos3 = new ArrayList<>();
		photos1.add(getPhotoForTest(USER_3, 30L, 3L));
		photos1.add(getPhotoForTest(USER_3, 300L, 3L));

		photosMap = new HashMap<>();
		photosMap.put(USER_1, photos1);
		photosMap.put(USER_2, photos2);
		photosMap.put(USER_3, photos3);

		//init mocks
		AlbumPersistenceService albumPersistenceService = mock(AlbumPersistenceService.class);
		StoragePersistenceService storagePersistenceService = mock(StoragePersistenceService.class);

		//add mocks logic
		when(albumPersistenceService.getAlbum(1L)).thenReturn(albums.get(0));
		when(albumPersistenceService.getAlbums(USER_1)).thenReturn(Collections.emptyList());
		when(albumPersistenceService.getAlbums(USER_2)).thenReturn(getAlbumsForTest(USER_2));
		when(albumPersistenceService.getDefaultAlbum(USER_1)).thenReturn(albums.get(0));
		when(albumPersistenceService.getDefaultAlbum(USER_3)).thenThrow(new DefaultAlbumNotFoundPersistenceServiceException(USER_3));
		when(albumPersistenceService.createAlbum(any(AlbumBO.class))).thenReturn(albums.get(2));
		when(albumPersistenceService.createAlbum(albums.get(0))).thenReturn(albums.get(0));
		doNothing().when(albumPersistenceService).updateAlbum(any(AlbumBO.class));
		when(albumPersistenceService.getAlbum(albums.get(1).getId())).thenReturn(albums.get(1));
		when(storagePersistenceService.getPhoto(photosMap.get(USER_1).get(0).getId())).thenReturn(photos1.get(0));
		doNothing().when(storagePersistenceService).deletePhoto(photos1.get(0).getId());
		doNothing().when(storagePersistenceService).updatePhoto(photos1.get(0), false);

		//add mocks to factory
		MetaFactory.createOnTheFlyFactory(AlbumPersistenceService.class, Extension.NONE, albumPersistenceService);
		MetaFactory.createOnTheFlyFactory(StoragePersistenceService.class, Extension.NONE, storagePersistenceService);
	}

	@Test
	public void testGetAlbum() throws Exception {
		AlbumBO expected = albums.get(0);
		AlbumBO actual = storageService.getAlbum(1L);
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void tesGetAlbumsEmptyList() throws Exception {
		List<AlbumBO> expected = Collections.emptyList();
		List<AlbumBO> actual = storageService.getAlbums(USER_1);
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void testNotEmptyAlbumsList() throws Exception {
		List<AlbumBO> expected = getAlbumsForTest(USER_2);
		List<AlbumBO> actual = storageService.getAlbums(USER_2);
		Assert.assertFalse(actual.isEmpty());
		Assert.assertEquals(expected.size(), actual.size());
		Assert.assertEquals(expected.get(1), actual.get(1));
	}


	@Test
	public void tesGetDefaultAlbum() throws Exception {
		AlbumBO expected = albums.get(0);
		AlbumBO actual = storageService.getDefaultAlbum(USER_1);
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void testDefaultAlbumNotFound() throws Exception {
		AlbumBO expected = albums.get(2);
		AlbumBO actual = storageService.getDefaultAlbum(USER_3);
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void testCreateNullAlbum() throws Exception {
		try {
			storageService.createAlbum(null);
			Assert.fail();
		} catch (IllegalArgumentException e) {
			//ok
		}
	}

	@Test
	public void testCreateDefaultAlbumError() {
		try {
			AlbumBO albumBO = new AlbumBO();
			albumBO.setDefault(true);
			storageService.createAlbum(albumBO);
			Assert.fail();
		} catch (StorageServiceException e) {
			//ok
		}
	}

	@Test
	public void testCreateAlbum() throws Exception {
		AlbumBO expected = albums.get(0);
		AlbumBO actual = storageService.createAlbum(albums.get(0));
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void testUpdateNullAlbum() throws Exception {
		try {
			storageService.updateAlbum(null);
			Assert.fail();
		} catch (IllegalArgumentException e) {
			//ok
		}
	}

	@Test
	public void testUpdateAlbum() throws Exception {
		AlbumBO expected = albums.get(1);
		AlbumBO actual = storageService.updateAlbum(expected);
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void testGetPhoto() throws Exception {
		PhotoBO expected = photosMap.get(USER_1).get(0);
		PhotoBO actual = storageService.getPhoto(photosMap.get(USER_1).get(0).getId());
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void deletePhoto() throws Exception {
		storageService.removePhoto(photosMap.get(USER_1).get(0).getId());
	}

	@Test
	public void updateNullPhoto() throws Exception {
		try {
			storageService.updatePhoto(null);
			Assert.fail();
		} catch (IllegalArgumentException e) {
			//ok
		}
	}

	@Test
	public void updatePhoto() throws Exception {
		storageService.updatePhoto(photosMap.get(USER_1).get(0));
	}



	private static PhotoBO getPhotoForTest(String userId, long photoId, long albumId) {
		PhotoBO photo = new PhotoBO();
		photo.setAlbumId(albumId);
		photo.setId(photoId);
		photo.setDescription("desc");
		photo.setName("my_photo!!");
		photo.setUserId(userId);
		photo.setExtension("PNG");
		photo.setPreviewSettings(getPreviewSettings());
		return photo;
	}

	private static AlbumBO createAlbumForTest(String userId, long id) {
		AlbumBO album = new AlbumBO();
		album.setId(id);
		album.setDefault(false);
		album.setDescription("Test Album");
		album.setName("Name Album");
		album.setUserId(userId);
		return album;
	}

	private static List<AlbumBO> getAlbumsForTest(String userId) {
		List<AlbumBO> albums = new ArrayList<>();
		for (int i = 0; i < 10; i++) {
			AlbumBO album = new AlbumBO();
			album.setDefault(false);
			album.setDescription("Test " + i);
			album.setName("Name " + i);
			album.setUserId(userId);
			albums.add(album);
		}
		return albums;
	}


	private static PreviewSettingsVO getPreviewSettings() {
		return new PreviewSettingsVO(100, 200, 100500, 4500);
	}
}
