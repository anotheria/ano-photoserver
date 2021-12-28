package net.anotheria.anosite.photoserver.api.photo;

import net.anotheria.anoplass.api.APICallContext;
import net.anotheria.anoplass.api.APIException;
import net.anotheria.anoplass.api.generic.login.LoginAPI;
import net.anotheria.anosite.photoserver.api.blur.BlurSettingsAPI;
import net.anotheria.anosite.photoserver.service.storage.AlbumBO;
import net.anotheria.anosite.photoserver.service.storage.AlbumNotFoundServiceException;
import net.anotheria.anosite.photoserver.service.storage.PhotoBO;
import net.anotheria.anosite.photoserver.service.storage.PhotoNotFoundServiceException;
import net.anotheria.anosite.photoserver.service.storage.StorageService;
import net.anotheria.anosite.photoserver.service.storage.StorageServiceException;
import net.anotheria.anosite.photoserver.shared.ApprovalStatus;
import net.anotheria.anosite.photoserver.shared.vo.PreviewSettingsVO;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Test for PhotoAPI.
 */
@RunWith(MockitoJUnitRunner.class)
public class PhotoAPITest {
	/**
	 * {@link PhotoAPIImpl} for test.
	 */
	@InjectMocks
	private PhotoAPIImpl photoAPI;
	/**
	 * Mock API: {@link LoginAPI}.
	 */
	@Mock
	private LoginAPI loginAPI;
	/**
	 * Mock API: {@link BlurSettingsAPI}.
	 */
	@Mock
	private BlurSettingsAPI blurSettingsAPI;
	/**
	 * Mock service: {@link StorageService}.
	 */
	@Mock
	private StorageService storageService;

	private static final String USER1_ID = 1L + "";
	private static final String USER2_ID = 2L + "";

	private static AlbumAO testAlbum1;
	private static AlbumBO testAlbum1_BO;
	private static AlbumAO testAlbum2;
	private static AlbumBO testAlbum2_BO;
	private static AlbumAO defaultAlbum;
	private static AlbumBO defaultAlbum_BO;

	@BeforeClass
	public static void init() throws APIException {
		//test data creating
		testAlbum1 = new AlbumAO();
		testAlbum1.setId(1L);
		testAlbum1.setUserId(USER1_ID);
		testAlbum1.setDescription("description");
		testAlbum1.setName("name");
		testAlbum1.setDefault(false);

		testAlbum1_BO = new AlbumBO();
		testAlbum1_BO.setId(1L);
		testAlbum1_BO.setUserId(USER1_ID);
		testAlbum1_BO.setDescription("description");
		testAlbum1_BO.setName("name");
		testAlbum1_BO.setDefault(false);

		testAlbum2 = new AlbumAO();
		testAlbum2.setId(2L);
		testAlbum2.setUserId(USER1_ID);
		testAlbum2.setDescription("description2");
		testAlbum2.setName("name2");
		testAlbum2.setDefault(false);

		testAlbum2_BO = new AlbumBO();
		testAlbum2_BO.setId(2L);
		testAlbum2_BO.setUserId(USER1_ID);
		testAlbum2_BO.setDescription("description2");
		testAlbum2_BO.setName("name2");
		testAlbum2_BO.setDefault(false);

		defaultAlbum = new AlbumAO();
		defaultAlbum.setId(3L);
		defaultAlbum.setUserId(USER1_ID);
		defaultAlbum.setDefault(true);

		defaultAlbum_BO = new AlbumBO();
		defaultAlbum_BO.setId(3L);
		defaultAlbum_BO.setUserId(USER1_ID);
		defaultAlbum_BO.setDefault(true);
	}

	@Test
	public void testGetAlbum() throws Exception {
		when(storageService.getAlbum(testAlbum1.getId())).thenReturn(testAlbum1_BO);
		when(loginAPI.isLogedIn()).thenReturn(true);
		when(loginAPI.getLogedUserId()).thenReturn(USER1_ID);

		AlbumAO actual = photoAPI.getAlbum(testAlbum1.getId());

		assertEquals(testAlbum1, actual);
		verify(loginAPI, atLeastOnce()).isLogedIn();
		verify(loginAPI, atLeastOnce()).getLogedUserId();
		verify(storageService, atLeastOnce()).getAlbum(testAlbum1.getId());
	}

	@Test
	public void testGetAlbumThrowError() throws Exception {
		when(storageService.getAlbum(testAlbum1.getId())).thenThrow(new AlbumNotFoundServiceException(testAlbum1.getId()));
		try {
			photoAPI.getAlbum(testAlbum1.getId());
			fail();
		} catch (AlbumNotFoundPhotoAPIException e) {
			//ok
		}
		verify(storageService, atLeastOnce()).getAlbum(testAlbum1.getId());
	}

	@Test
	public void testGetAlbumByAlbumIdAndPhotosFiltering() throws Exception {
		when(storageService.getAlbum(testAlbum1.getId())).thenReturn(testAlbum1_BO);
		when(loginAPI.isLogedIn()).thenReturn(true);
		when(loginAPI.getLogedUserId()).thenReturn(USER1_ID);

		AlbumAO actual = photoAPI.getAlbum(testAlbum1.getId(), PhotosFiltering.DEFAULT);

		assertEquals(testAlbum1, actual);
		verify(loginAPI, atLeastOnce()).isLogedIn();
		verify(loginAPI, atLeastOnce()).getLogedUserId();
		verify(storageService, atLeastOnce()).getAlbum(testAlbum1.getId());
	}

	@Test
	public void testGetAlbumByAlbumIdAndPhotosFilteringAndAuthorIdSame() throws Exception {
		when(storageService.getAlbum(testAlbum1.getId())).thenReturn(testAlbum1_BO);
		when(loginAPI.isLogedIn()).thenReturn(true);
		when(loginAPI.getLogedUserId()).thenReturn(USER1_ID);

		AlbumAO actual = photoAPI.getAlbum(testAlbum1.getId(), PhotosFiltering.DEFAULT, USER1_ID);

		assertEquals(testAlbum1, actual);
		verify(loginAPI, atLeastOnce()).isLogedIn();
		verify(loginAPI, atLeastOnce()).getLogedUserId();
		verify(storageService, atLeastOnce()).getAlbum(testAlbum1.getId());
	}

	@Test
	public void testGetAlbumByAlbumIdAndPhotosFilteringAndAuthorIdDifferent() throws Exception {
		when(storageService.getAlbum(testAlbum1.getId())).thenReturn(testAlbum1_BO);
		when(loginAPI.isLogedIn()).thenReturn(true);
		when(loginAPI.getLogedUserId()).thenReturn(USER1_ID);

		AlbumAO actual = photoAPI.getAlbum(testAlbum1.getId(), PhotosFiltering.DEFAULT, USER2_ID);

		assertEquals(testAlbum1, actual);
		verify(loginAPI, atLeastOnce()).isLogedIn();
		verify(loginAPI, atLeastOnce()).getLogedUserId();
		verify(storageService, atLeastOnce()).getAlbum(testAlbum1.getId());
	}

	@Test
	public void testGetAlbums() throws Exception {
		List<AlbumAO> expected = Arrays.asList(defaultAlbum, testAlbum1, testAlbum2);
		when(storageService.getAlbums(USER1_ID)).thenReturn(Arrays.asList(defaultAlbum_BO, testAlbum1_BO, testAlbum2_BO));
		when(loginAPI.isLogedIn()).thenReturn(true);
		when(loginAPI.getLogedUserId()).thenReturn(USER1_ID);

		List<AlbumAO> actual = photoAPI.getAlbums(USER1_ID, PhotosFiltering.DEFAULT, USER1_ID);

		assertEquals(expected.size(), actual.size());
		assertEquals(expected.get(0), actual.get(0));
		assertEquals(expected.get(1), actual.get(1));
		assertEquals(expected.get(2), actual.get(2));

		verify(loginAPI, atLeastOnce()).isLogedIn();
		verify(loginAPI, atLeastOnce()).getLogedUserId();
		verify(storageService, atLeastOnce()).getAlbums(USER1_ID);
	}

	@Test
	public void testGetAlbumsEmptyEmptyUserId() throws Exception {
		try {
			photoAPI.getAlbums(null, PhotosFiltering.DEFAULT, USER1_ID);
			fail();
		} catch (IllegalArgumentException e) {
			//ok
		}
	}

	@Test
	public void testGetDefaultAlbum() throws Exception {
		when(storageService.getDefaultAlbum(USER1_ID)).thenReturn(defaultAlbum_BO);
		when(loginAPI.isLogedIn()).thenReturn(true);
		when(loginAPI.getLogedUserId()).thenReturn(USER1_ID);

		AlbumAO actual = photoAPI.getDefaultAlbum(USER1_ID);

		assertEquals(defaultAlbum, actual);
		verify(loginAPI, atLeastOnce()).isLogedIn();
		verify(loginAPI, atLeastOnce()).getLogedUserId();
		verify(storageService, atLeastOnce()).getDefaultAlbum(USER1_ID);
	}

	@Test
	public void testGetDefaultAlbumThrowError() throws Exception {
		when(storageService.getDefaultAlbum(USER1_ID)).thenThrow(new StorageServiceException("ops"));
		try {
			photoAPI.getDefaultAlbum(USER1_ID);
			fail();
		} catch (PhotoAPIException e) {
			//ok
		}

		verify(loginAPI, never()).isLogedIn();
		verify(loginAPI, never()).getLogedUserId();
		verify(storageService, atLeastOnce()).getDefaultAlbum(USER1_ID);
	}

	@Test
	public void testGetDefaultAlbumEmptyUserId() throws Exception {
		try {
			photoAPI.getDefaultAlbum(null, PhotosFiltering.DEFAULT, USER1_ID);
			fail();
		} catch (IllegalArgumentException e) {
			//ok
		}
	}

	@Test
	public void testCreateNullAlbum() throws Exception {
		try {
			photoAPI.createAlbum(null, USER2_ID);
			fail();
		} catch (IllegalArgumentException e) {
			//ok
		}
	}

	@Test
	public void testCreateAlbum() throws Exception {
		when(loginAPI.isLogedIn()).thenReturn(true);
		when(storageService.createAlbum(any(AlbumBO.class))).thenReturn(testAlbum1_BO);

		AlbumAO actual = photoAPI.createAlbum(testAlbum1);

		assertEquals(testAlbum1, actual);
		verify(loginAPI, atLeastOnce()).isLogedIn();
		verify(storageService, atLeastOnce()).createAlbum(any(AlbumBO.class));
	}

	@Test
	public void testCreateAlbumAuthorId() throws Exception {
		when(storageService.createAlbum(any(AlbumBO.class))).thenReturn(testAlbum1_BO);

		AlbumAO actual = photoAPI.createAlbum(testAlbum1, USER1_ID);

		assertEquals(testAlbum1, actual);
		verify(storageService, atLeastOnce()).createAlbum(any(AlbumBO.class));
	}

	@Test
	public void testUpdateNullAlbum() throws Exception {
		try {
			photoAPI.updateAlbum(null, USER2_ID);
			fail();
		} catch (IllegalArgumentException e) {
			//ok
		}
	}

	@Test
	public void testUpdateAlbum() throws Exception {
		APICallContext.getCallContext().setCurrentUserId(USER1_ID);
		when(loginAPI.isLogedIn()).thenReturn(true);
		when(storageService.updateAlbum(any(AlbumBO.class))).thenReturn(testAlbum1_BO);

		AlbumAO actual = photoAPI.updateAlbum(testAlbum1);

		assertEquals(testAlbum1, actual);
		verify(loginAPI, atLeastOnce()).isLogedIn();
		verify(storageService, atLeastOnce()).updateAlbum(any(AlbumBO.class));
	}

	@Test
	public void testUpdateAlbumAuthorId() throws Exception {
		when(storageService.updateAlbum(any(AlbumBO.class))).thenReturn(testAlbum1_BO);

		AlbumAO actual = photoAPI.updateAlbum(testAlbum1, USER1_ID);

		assertEquals(testAlbum1, actual);
		verify(storageService, atLeastOnce()).updateAlbum(any(AlbumBO.class));
	}

	@Test
	public void testUpdateAlbumAnotherUser() throws Exception {
		APICallContext.getCallContext().setCurrentUserId(USER2_ID);
		when(loginAPI.isLogedIn()).thenReturn(true);

		try {
			photoAPI.updateAlbum(testAlbum1);
			fail();
		} catch (NoAccessPhotoAPIException e) {
			//ok
		}

		verify(loginAPI, atLeastOnce()).isLogedIn();
		verify(storageService, never()).updateAlbum(any(AlbumBO.class));
	}

	@Test
	public void testRemoveAlbum() throws Exception {
		when(storageService.getAlbum(testAlbum1.getId())).thenReturn(testAlbum1_BO);
		when(storageService.removeAlbum(testAlbum1.getId())).thenReturn(testAlbum1_BO);

		AlbumAO actual = photoAPI.removeAlbum(testAlbum1.getId());

		assertEquals(testAlbum1, actual);
		verify(storageService, atLeastOnce()).getAlbum(testAlbum1.getId());
		verify(storageService, atLeastOnce()).removeAlbum(testAlbum1.getId());
	}

	@Test
	public void testRemoveAlbumWithSameAuthorId() throws Exception {
		APICallContext.getCallContext().setCurrentUserId(USER1_ID);
		when(storageService.getAlbum(testAlbum1.getId())).thenReturn(testAlbum1_BO);
		when(loginAPI.isLogedIn()).thenReturn(true);
		when(storageService.removeAlbum(testAlbum1.getId())).thenReturn(testAlbum1_BO);

		AlbumAO actual = photoAPI.removeAlbum(testAlbum1.getId(), USER1_ID);

		assertEquals(testAlbum1, actual);
		verify(storageService, atLeastOnce()).getAlbum(testAlbum1.getId());
		verify(loginAPI, atLeastOnce()).isLogedIn();
		verify(storageService, atLeastOnce()).removeAlbum(testAlbum1.getId());
	}

	@Test
	public void testRemoveAlbumWithDifferentAuthorId() throws Exception {
		APICallContext.getCallContext().setCurrentUserId(USER2_ID);
		when(storageService.getAlbum(testAlbum1.getId())).thenReturn(testAlbum1_BO);
		when(loginAPI.isLogedIn()).thenReturn(true);

		try {
			photoAPI.removeAlbum(testAlbum1.getId(), USER1_ID);
			fail();
		} catch (NoAccessPhotoAPIException e) {
			//ok
		}

		verify(storageService, atLeastOnce()).getAlbum(testAlbum1.getId());
		verify(loginAPI, atLeastOnce()).isLogedIn();
		verify(storageService, never()).removeAlbum(testAlbum1.getId());
	}

	@Test
	public void testRemoveAlbumWithNotLoggedUser() throws Exception {
		when(storageService.getAlbum(testAlbum1.getId())).thenReturn(testAlbum1_BO);
		when(loginAPI.isLogedIn()).thenReturn(false);

		try {
			photoAPI.removeAlbum(testAlbum1.getId(), USER1_ID);
			fail();
		} catch (NoAccessPhotoAPIException e) {
			//ok
		}

		verify(storageService, atLeastOnce()).getAlbum(testAlbum1.getId());
		verify(loginAPI, atLeastOnce()).isLogedIn();
		verify(storageService, never()).removeAlbum(testAlbum1.getId());
	}

	@Test
	public void testGetMyAlbums() throws Exception {
		APICallContext.getCallContext().setCurrentUserId(USER1_ID);
		List<AlbumAO> expected = Arrays.asList(defaultAlbum, testAlbum1, testAlbum2);
		when(storageService.getAlbums(USER1_ID)).thenReturn(Arrays.asList(defaultAlbum_BO, testAlbum1_BO, testAlbum2_BO));

		List<AlbumAO> actual = photoAPI.getMyAlbums();

		assertEquals(expected.size(), actual.size());
		assertEquals(expected.get(0), actual.get(0));
		assertEquals(expected.get(1), actual.get(1));
		assertEquals(expected.get(2), actual.get(2));

		verify(loginAPI, never()).isLogedIn();
		verify(loginAPI, never()).getLogedUserId();
		verify(storageService, atLeastOnce()).getAlbums(USER1_ID);
	}

	@Test
	public void testGetMyDefaultAlbum() throws Exception {
		APICallContext.getCallContext().setCurrentUserId(USER1_ID);
		when(storageService.getDefaultAlbum(USER1_ID)).thenReturn(defaultAlbum_BO);

		AlbumAO actual = photoAPI.getMyDefaultAlbum();
		assertEquals(defaultAlbum, actual);
		verify(loginAPI, never()).isLogedIn();
		verify(loginAPI, never()).getLogedUserId();
		verify(storageService, atLeastOnce()).getDefaultAlbum(USER1_ID);
	}

	@Test
	public void testGetDefaultPhoto() throws Exception {
		PhotoBO photoBO = getPhotosForTest(1, testAlbum1.getId(), USER1_ID).get(0);
		PhotoAO expected = new PhotoAO(photoBO);
		expected.setBlurred(true);
		when(storageService.getDefaultPhoto(USER1_ID)).thenReturn(photoBO);
		when(blurSettingsAPI.readMyBlurSettings(photoBO.getAlbumId(), photoBO.getId())).thenReturn(true);

		PhotoAO actual = photoAPI.getDefaultPhoto(USER1_ID);

		assertEquals(expected.getId(), actual.getId());
		assertEquals(expected.getAlbumId(), actual.getAlbumId());
		assertEquals(expected.getUserId(), actual.getUserId());
		assertEquals(expected.isBlurred(), actual.isBlurred());
		verify(storageService, atLeastOnce()).getDefaultPhoto(USER1_ID);
		verify(blurSettingsAPI, atLeastOnce()).readMyBlurSettings(photoBO.getAlbumId(), photoBO.getId());
	}

	@Test
	public void testGetMyDefaultPhoto() throws Exception {
		APICallContext.getCallContext().setCurrentUserId(USER1_ID);
		PhotoBO photoBO = getPhotosForTest(1, testAlbum1.getId(), USER1_ID).get(0);
		PhotoAO expected = new PhotoAO(photoBO);
		expected.setBlurred(true);
		when(storageService.getDefaultPhoto(USER1_ID)).thenReturn(photoBO);
		when(blurSettingsAPI.readMyBlurSettings(photoBO.getAlbumId(), photoBO.getId())).thenReturn(true);

		PhotoAO actual = photoAPI.getMyDefaultPhoto();

		assertEquals(expected.getId(), actual.getId());
		assertEquals(expected.getAlbumId(), actual.getAlbumId());
		assertEquals(expected.getUserId(), actual.getUserId());
		assertEquals(expected.isBlurred(), actual.isBlurred());
		verify(storageService, atLeastOnce()).getDefaultPhoto(USER1_ID);
		verify(blurSettingsAPI, atLeastOnce()).readMyBlurSettings(photoBO.getAlbumId(), photoBO.getId());
	}

	@Test
	public void testGetDefaultPhotoByEmptyUser() throws Exception {
		try {
			photoAPI.getDefaultPhoto(null);
			fail();
		} catch (IllegalArgumentException e) {
			//ok
		}
	}

	@Test
	public void testGetDefaultPhotoWithAlbum() throws Exception {
		PhotoBO photoBO = getPhotosForTest(1, testAlbum1.getId(), USER1_ID).get(0);
		PhotoAO expected = new PhotoAO(photoBO);
		expected.setBlurred(true);
		when(storageService.getDefaultPhoto(USER1_ID, testAlbum1.getId())).thenReturn(photoBO);
		when(blurSettingsAPI.readMyBlurSettings(photoBO.getAlbumId(), photoBO.getId())).thenReturn(true);

		PhotoAO actual = photoAPI.getDefaultPhoto(USER1_ID, testAlbum1.getId());

		assertEquals(expected.getId(), actual.getId());
		assertEquals(expected.getAlbumId(), actual.getAlbumId());
		assertEquals(expected.getUserId(), actual.getUserId());
		assertEquals(expected.isBlurred(), actual.isBlurred());
		verify(storageService, atLeastOnce()).getDefaultPhoto(USER1_ID, testAlbum1.getId());
		verify(blurSettingsAPI, atLeastOnce()).readMyBlurSettings(photoBO.getAlbumId(), photoBO.getId());
	}

	@Test
	public void testGetDefaultPhotoWithAlbumAndEmptyUser() throws Exception {
		try {
			photoAPI.getDefaultPhoto(null, 0L);
			fail();
		} catch (IllegalArgumentException e) {
			//ok
		}
	}

	@Test
	public void testGetPhoto() throws Exception {
		PhotoBO photoBO = getPhotosForTest(1, testAlbum1.getId(), USER1_ID).get(0);
		PhotoAO expected = new PhotoAO(photoBO);
		expected.setBlurred(true);
		when(storageService.getPhoto(expected.getId())).thenReturn(photoBO);
		when(blurSettingsAPI.readMyBlurSettings(photoBO.getAlbumId(), photoBO.getId())).thenReturn(true);

		PhotoAO actual = photoAPI.getPhoto(expected.getId());

		assertEquals(expected.getId(), actual.getId());
		assertEquals(expected.getAlbumId(), actual.getAlbumId());
		assertEquals(expected.getUserId(), actual.getUserId());
		assertEquals(expected.isBlurred(), actual.isBlurred());
		verify(storageService, atLeastOnce()).getPhoto(photoBO.getId());
		verify(blurSettingsAPI, atLeastOnce()).readMyBlurSettings(photoBO.getAlbumId(), photoBO.getId());
	}

	@Test
	public void testGetPhotoThrowNotFoundPhotoError() throws Exception {
		when(storageService.getPhoto(0L)).thenThrow(new PhotoNotFoundServiceException(0L));

		try {
			photoAPI.getPhoto(0L);
			fail();
		} catch (PhotoNotFoundPhotoAPIException e) {
			//ok
		}

		verify(storageService, atLeastOnce()).getPhoto(0L);
	}

	@Test
	public void testGetPhotos() throws Exception {
		List<PhotoBO> photoBOS = getPhotosForTest(3, testAlbum1.getId(), USER1_ID);
		List<PhotoAO> expectedPhotos = new ArrayList<>();
		for (PhotoBO photoBO: photoBOS) {
			PhotoAO photoAO = new PhotoAO(photoBO);
			photoAO.setBlurred(true);
			expectedPhotos.add(photoAO);
		}

		Map<Long, Boolean> blurSettingMap = new HashMap<>();
		for (PhotoAO photoAO: expectedPhotos) {
			blurSettingMap.put(photoAO.getId(), true);
		}

		when(storageService.getAlbum(testAlbum1.getId())).thenReturn(testAlbum1_BO);
		when(loginAPI.isLogedIn()).thenReturn(true);
		when(loginAPI.getLogedUserId()).thenReturn(USER1_ID);
		when(storageService.getPhotos(testAlbum1.getUserId(), testAlbum1.getId())).thenReturn(photoBOS);
		when(blurSettingsAPI.readMyBlurSettings(anyLong(), anyList())).thenReturn(blurSettingMap);

		List<PhotoAO> actualPhotos = photoAPI.getPhotos(testAlbum1.getId());
		assertEquals(expectedPhotos.size(), actualPhotos.size());

		verify(loginAPI, atLeastOnce()).isLogedIn();
		verify(loginAPI, atLeastOnce()).getLogedUserId();
		verify(storageService, atLeastOnce()).getAlbum(testAlbum1.getId());
		verify(storageService, atLeastOnce()).getPhotos(testAlbum1.getUserId(), testAlbum1.getId());
		verify(blurSettingsAPI, atLeastOnce()).readMyBlurSettings(anyLong(), anyList());
	}

	@Test
	public void testCreatePhotoInDefaultAlbumEmptyUser() throws Exception {
		try {
			photoAPI.createPhoto(null, new File("foo"), new PreviewSettingsVO(0,0,0), false);
			fail();
		} catch (IllegalArgumentException e) {
			//ok
		}
	}

	@Test
	public void testCreatePhotoInDefaultAlbumEmptyFile() throws Exception {
		try {
			photoAPI.createPhoto(USER1_ID, null, new PreviewSettingsVO(0,0,0));
			fail();
		} catch (IllegalArgumentException e) {
			//ok
		}
	}

	@Test
	public void testCreatePhotoInDefaultAlbum() throws Exception {
		PhotoBO expectedBO = getPhotosForTest(1, defaultAlbum.getId(), USER1_ID).get(0);
		PhotoAO expected = new PhotoAO(expectedBO);

		when(storageService.getDefaultAlbum(USER1_ID)).thenReturn(defaultAlbum_BO);
		when(storageService.getAlbum(defaultAlbum_BO.getId())).thenReturn(defaultAlbum_BO);
		when(storageService.createPhoto(expectedBO)).thenReturn(expectedBO);
		when(storageService.updateAlbum(any(AlbumBO.class))).thenReturn(defaultAlbum_BO);

		PhotoAO actual = photoAPI.createPhoto(USER1_ID, new File("foo"), new PreviewSettingsVO(0,0,0));

		assertEquals(expected.getId(), actual.getId());
		assertEquals(expected.getUserId(), actual.getUserId());
		assertEquals(expected.getAlbumId(), actual.getAlbumId());
		verify(storageService, atLeastOnce()).getDefaultAlbum(USER1_ID);
		verify(storageService, atLeastOnce()).getAlbum(defaultAlbum_BO.getId());
		verify(storageService, atLeastOnce()).createPhoto(expectedBO);
		verify(storageService, atLeastOnce()).updateAlbum(any(AlbumBO.class));
	}

	@Test
	public void testCreatePhoto() throws Exception {
		PhotoBO expectedBO = getPhotosForTest(1, testAlbum2.getId(), USER1_ID).get(0);
		PhotoAO expected = new PhotoAO(expectedBO);

		when(storageService.getAlbum(testAlbum2.getId())).thenReturn(testAlbum2_BO);
		when(storageService.createPhoto(expectedBO)).thenReturn(expectedBO);
		when(storageService.updateAlbum(any(AlbumBO.class))).thenReturn(testAlbum2_BO);

		PhotoAO actual = photoAPI.createPhoto(USER1_ID, testAlbum2.getId(), false, new File("foo"), new PreviewSettingsVO(0,0,0));

		assertEquals(expected.getId(), actual.getId());
		assertEquals(expected.getUserId(), actual.getUserId());
		assertEquals(expected.getAlbumId(), actual.getAlbumId());
		verify(storageService, atLeastOnce()).getAlbum(testAlbum2.getId());
		verify(storageService, atLeastOnce()).createPhoto(expectedBO);
		verify(storageService, atLeastOnce()).updateAlbum(any(AlbumBO.class));
	}

	@Test
	public void testCreatePhotoEmptyUser() throws Exception {
		try {
			photoAPI.createPhoto(null, 0L, false, new File("foo"), new PreviewSettingsVO(0,0,0));
			fail();
		} catch (IllegalArgumentException e) {
			//ok
		}
	}

	@Test
	public void testCreatePhotoEmptyFile() throws Exception {
		try {
			photoAPI.createPhoto(USER1_ID, 0L, false, null, new PreviewSettingsVO(0,0,0));
			fail();
		} catch (IllegalArgumentException e) {
			//ok
		}
	}

	@Test
	public void testUpdatePhoto() throws Exception {
		PhotoBO expectedBO = getPhotosForTest(1, testAlbum2.getId(), USER1_ID).get(0);
		PhotoAO expected = new PhotoAO(expectedBO);

		when(storageService.updatePhoto(expectedBO)).thenReturn(expectedBO);
		PhotoAO actual = photoAPI.updatePhoto(USER1_ID, expected);

		assertEquals(expected.getId(), actual.getId());
		assertEquals(expected.getUserId(), actual.getUserId());
		assertEquals(expected.getAlbumId(), actual.getAlbumId());
		verify(storageService, atLeastOnce()).updatePhoto(expectedBO);
	}

	@Test
	public void testUpdatePhotoEmptyUser() throws Exception {
		try {
			photoAPI.updatePhoto(null, new PhotoAO());
			fail();
		} catch (IllegalArgumentException e) {
			//ok
		}
	}

	@Test
	public void testUpdatePhotoEmptyPhoto() throws Exception {
		try {
			photoAPI.updatePhoto(USER1_ID, null);
			fail();
		} catch (IllegalArgumentException e) {
			//ok
		}
	}

	@Test
	public void testUpdatePhotoAnotherUser() throws Exception {
		try {
			PhotoAO photoAO = new PhotoAO();
			photoAO.setUserId(USER2_ID);
			photoAPI.updatePhoto(USER1_ID, photoAO);
			fail();
		} catch (NoAccessPhotoAPIException e) {
			//ok
		}
	}

	@Test
	public void testRemovePhoto() throws Exception {
		PhotoBO photoBO = getPhotosForTest(1, testAlbum1.getId(), USER1_ID).get(0);
		PhotoAO expected = new PhotoAO(photoBO);
		expected.setBlurred(true);

		when(storageService.getPhoto(expected.getId())).thenReturn(photoBO);
		when(blurSettingsAPI.readMyBlurSettings(photoBO.getAlbumId(), photoBO.getId())).thenReturn(true);
		doNothing().when(storageService).removePhoto(expected.getId());
		when(storageService.getAlbum(expected.getAlbumId())).thenReturn(testAlbum1_BO);
		when(storageService.updateAlbum(any(AlbumBO.class))).thenReturn(testAlbum1_BO);

		PhotoAO actual = photoAPI.removePhoto(USER1_ID, expected.getId());

		assertEquals(expected.getId(), actual.getId());
		assertEquals(expected.getAlbumId(), actual.getAlbumId());
		assertEquals(expected.getUserId(), actual.getUserId());
		assertEquals(expected.isBlurred(), actual.isBlurred());
		verify(storageService, atLeastOnce()).getPhoto(photoBO.getId());
		verify(blurSettingsAPI, atLeastOnce()).readMyBlurSettings(photoBO.getAlbumId(), photoBO.getId());
		verify(storageService, atLeastOnce()).removePhoto(expected.getId());
		verify(storageService, atLeastOnce()).getAlbum(expected.getAlbumId());
		verify(storageService, atLeastOnce()).updateAlbum(any(AlbumBO.class));
	}

	@Test
	public void testRemovePhotoEmptyUser() throws Exception {
		try {
			photoAPI.removePhoto(null, 0L);
			fail();
		} catch (IllegalArgumentException e) {
			//ok
		}
	}

	@Test
	public void testRemovePhotoAnotherUser() throws Exception {
		PhotoBO photoBO = getPhotosForTest(1, testAlbum1.getId(), USER2_ID).get(0);
		PhotoAO expected = new PhotoAO(photoBO);

		when(storageService.getPhoto(expected.getId())).thenReturn(photoBO);
		when(blurSettingsAPI.readMyBlurSettings(expected.getAlbumId(), expected.getId())).thenReturn(true);

		try {
			photoAPI.removePhoto(USER1_ID, expected.getId());
			fail();
		} catch (NoAccessPhotoAPIException e) {

		}

		verify(storageService, atLeastOnce()).getPhoto(expected.getId());
		verify(blurSettingsAPI, atLeastOnce()).readMyBlurSettings(expected.getAlbumId(),  expected.getId());
		verify(storageService, never()).removePhoto(anyLong());
		verify(storageService, never()).getAlbum(anyLong());
		verify(storageService, never()).updateAlbum(any(AlbumBO.class));
	}

	@Test
	public void testGetWaitingApprovalPhotosNegativeAmount() throws Exception {
		try {
			photoAPI.getWaitingApprovalPhotos(-1);
			fail();
		} catch (IllegalArgumentException e) {
			//ok
		}
	}

	@Test
	public void testGetWaitingApprovalPhotosZero() throws Exception {
		List<PhotoAO> photoAOS = photoAPI.getWaitingApprovalPhotos(0);
		assertTrue(photoAOS.isEmpty());
	}

	@Test
	public void testGetWaitingApprovalPhotos() throws Exception {
		int photosAmount = 3;
		List<PhotoBO> expected = getPhotosForTest(photosAmount, testAlbum1.getId(), USER1_ID);
		when(storageService.getWaitingApprovalPhotos(photosAmount)).thenReturn(expected);
		List<PhotoAO> actual = photoAPI.getWaitingApprovalPhotos(photosAmount);
		assertEquals(expected.size(), actual.size());
		verify(storageService, atLeastOnce()).getWaitingApprovalPhotos(photosAmount);
	}

	@Test
	public void testGetWaitingApprovalPhotosCount() throws Exception {
		int photosAmount = 3;
		when(storageService.getWaitingApprovalPhotosCount()).thenReturn(photosAmount);
		int actual = photoAPI.getWaitingApprovalPhotosCount();
		assertEquals(photosAmount, actual);
		verify(storageService, atLeastOnce()).getWaitingApprovalPhotosCount();
	}

	@Test
	public void testSetApprovalStatus() throws Exception {
		doNothing().when(storageService).updatePhotoApprovalStatuses(anyMap());
		photoAPI.setApprovalStatus(0L, ApprovalStatus.APPROVED);
		verify(storageService, atLeastOnce()).updatePhotoApprovalStatuses(anyMap());
	}

	@Test
	public void testSetNullApprovalStatus() throws Exception {
		try {
			photoAPI.setApprovalStatus(0L, null);
			fail();
		} catch (IllegalArgumentException e) {
			//ok
		}
	}

	@Test
	public void testSetApprovalStatuses() throws Exception {
		Map<Long, ApprovalStatus> approvalStatusMap = new HashMap<>();
		approvalStatusMap.put(0L, ApprovalStatus.APPROVED);
		approvalStatusMap.put(1L, ApprovalStatus.WAITING_APPROVAL);

		doNothing().when(storageService).updatePhotoApprovalStatuses(approvalStatusMap);
		photoAPI.setApprovalStatuses(approvalStatusMap);
		verify(storageService, atLeastOnce()).updatePhotoApprovalStatuses(approvalStatusMap);
	}

	@Test
	public void testSetNullApprovalStatuses() throws Exception {
		try {
			photoAPI.setApprovalStatuses( null);
			fail();
		} catch (IllegalArgumentException e) {
			//ok
		}
	}

	@Test
	public void testMovePhoto() throws Exception {
		PhotoBO photoBO = getPhotosForTest(1, testAlbum1.getId(), USER1_ID).get(0);
		PhotoAO expected = new PhotoAO(photoBO);

		when(storageService.getPhoto(expected.getId())).thenReturn(photoBO);
		when(blurSettingsAPI.readMyBlurSettings(expected.getAlbumId(), expected.getId())).thenReturn(true);
		when(storageService.getAlbum(testAlbum1.getId())).thenReturn(testAlbum1_BO);
		when(storageService.getAlbum(testAlbum2.getId())).thenReturn(testAlbum2_BO);
		when(storageService.movePhoto(expected.getId(), testAlbum2.getId())).thenReturn(photoBO);
		when(storageService.updateAlbum(any(AlbumBO.class))).thenReturn(testAlbum1_BO);

		expected.setAlbumId(testAlbum1.getId());
		PhotoAO actual = photoAPI.movePhoto(expected.getId(), testAlbum2.getId());

		assertEquals(expected.getAlbumId(), actual.getAlbumId());
		verify(storageService, atLeastOnce()).getPhoto(expected.getId());
		verify(blurSettingsAPI, atLeastOnce()).readMyBlurSettings(expected.getAlbumId(), expected.getId());
		verify(storageService, atLeastOnce()).getAlbum(testAlbum1.getId());
		verify(storageService, atLeastOnce()).getAlbum(testAlbum2.getId());
		verify(storageService, atLeastOnce()).getPhoto(expected.getId());
		verify(storageService, atLeastOnce()).movePhoto(expected.getId(), testAlbum2.getId());
		verify(storageService, atLeast(2)).updateAlbum(any(AlbumBO.class));
	}


	private List<PhotoBO> getPhotosForTest(int amount, long albumID, String userId) {
		if (amount < 1) amount = 10;
		List<PhotoBO> result = new ArrayList<PhotoBO>();
		while (amount-- > 0) {
			PhotoBO photo = new PhotoBO();
			result.add(photo);

			photo.setDescription("desc");
			photo.setName("my_photo!!");
			photo.setUserId(userId);
			photo.setAlbumId(albumID);
			photo.setExtension("jpg");
			photo.setPreviewSettings(new PreviewSettingsVO(100, 200, 100500, 4500));
		}
		return result;
	}
}
