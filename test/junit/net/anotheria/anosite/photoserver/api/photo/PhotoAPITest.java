package net.anotheria.anosite.photoserver.api.photo;

import net.anotheria.anoplass.api.APIException;
import net.anotheria.anoplass.api.APIFinder;
import net.anotheria.anoplass.api.generic.login.LoginAPI;
import net.anotheria.anoprise.metafactory.MetaFactory;
import net.anotheria.anoprise.metafactory.MetaFactoryException;
import net.anotheria.anosite.photoserver.TestingContextInitializer;
import net.anotheria.anosite.photoserver.service.storage.AlbumBO;
import net.anotheria.anosite.photoserver.service.storage.PhotoBO;
import net.anotheria.anosite.photoserver.service.storage.StorageService;
import net.anotheria.anosite.photoserver.service.storage.StorageServiceException;
import net.anotheria.anosite.photoserver.shared.ApprovalStatus;
import net.anotheria.anosite.photoserver.shared.PhotoServerConfig;
import net.anotheria.anosite.photoserver.shared.vo.PreviewSettingsVO;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Test for PhotoAPI.
 */
public class PhotoAPITest {


	private static PhotoAPI photoAPI;
	private static LoginAPI loginAPI;

	private static final String USER1_ID = 1L + "";
	private static final String USER2_ID = 2L + "";
	private static AlbumAO testAlbum1;
	private static AlbumAO testAlbum2;
	private static AlbumAO defaultAlbum;

	@BeforeClass
	public static void init() throws APIException {
		TestingContextInitializer.deInit();
		TestingContextInitializer.init();

		try {
			StorageService storageService = MetaFactory.get(StorageService.class);

			AlbumBO albumBO = new AlbumBO();
			albumBO.setUserId(USER2_ID);
			albumBO.setDefault(false);

			albumBO = storageService.createAlbum(albumBO);

//			PhotoBO photoBO = new PhotoBO();
//			photoBO.setAlbumId(albumBO.getId());
//			photoBO.setUserId(USER2_ID);
//			storageService.createPhoto(photoBO);

		} catch (MetaFactoryException e) {
			Assert.fail("Unexpected exception " + e.getMessage());
		} catch (StorageServiceException e) {
			Assert.fail("Unexpected exception " + e.getMessage());
		}

		photoAPI = APIFinder.findAPI(PhotoAPI.class);
		loginAPI = APIFinder.findAPI(LoginAPI.class);

		//test data creating
		testAlbum1 = new AlbumAO();
		testAlbum1.setUserId(USER1_ID);
		testAlbum1.setDescription("description");
		testAlbum1.setName("name");
		testAlbum1.setDefault(false);

		testAlbum2 = new AlbumAO();
		testAlbum2.setUserId(USER1_ID);
		testAlbum2.setDescription("description2");
		testAlbum2.setName("name2");
		testAlbum2.setDefault(false);

		defaultAlbum = new AlbumAO();
		defaultAlbum.setUserId(USER1_ID);
		defaultAlbum.setDefault(true);

	}

	@Before
	public void before() throws APIException {
		init();
	}

	@AfterClass
	public static void afterClass() {
		TestingContextInitializer.deInit();
	}

	@Test
	public void testAlbumValidationExceptions() {
		try {
			photoAPI.createAlbum(null);
			Assert.fail("IllegalArgumentException should be thrown");
		} catch (Exception e) {
			Assert.assertTrue("IllegalArgumentException should be thrown", e instanceof IllegalArgumentException);
		}

		try {
			photoAPI.updateAlbum(null);
			Assert.fail("IllegalArgumentException should be thrown");
		} catch (Exception e) {
			Assert.assertTrue("IllegalArgumentException should be thrown", e instanceof IllegalArgumentException);
		}
	}

	@Test
	public void testAlbumFunctionality() {

		//createAlbum()
		//creating album by not logged in user
		logoutUser();
		try {
			photoAPI.createAlbum(new AlbumAO());
			Assert.fail("NoAccessPhotoAPIException should be thrown");
		} catch (Exception e) {
			Assert.assertTrue("NoAccessPhotoAPIException should be thrown", e instanceof NoAccessPhotoAPIException);
		}

		loginUser(USER1_ID);
		try {
			photoAPI.createAlbum(defaultAlbum);
			Assert.fail("PhotoAPIException should be thrown, you can't create default album using this method");
		} catch (Exception e) {
			Assert.assertTrue("PhotoAPIException should be thrown", e instanceof PhotoAPIException);
		}

		try {
			AlbumAO album1 = photoAPI.createAlbum(testAlbum1);
			compareAlbums(album1, testAlbum1);

			AlbumAO album2 = photoAPI.createAlbum(testAlbum2);
			compareAlbums(album2, testAlbum2);

		} catch (PhotoAPIException e) {
			Assert.fail("Unexpected exception " + e.getMessage());
		}


		//getDefaultAlbum()
		try {
			AlbumAO defaultAlbum = photoAPI.getDefaultAlbum(USER1_ID);
			Assert.assertEquals("Id of retrieved default album is wrong", defaultAlbum.getId(), photoAPI.getDefaultAlbum(USER1_ID).getId());
		} catch (PhotoAPIException e) {
			Assert.fail("Unexpected exception " + e.getMessage());
		}


		//getMyDefaultAlbum()
		logoutUser();
		try {
			photoAPI.getMyDefaultAlbum();
			Assert.fail("PhotoAPIException should be thrown, user is not logged in");
		} catch (Exception e) {
			Assert.assertTrue("PhotoAPIException should be thrown, user is not logged in", e instanceof PhotoAPIException);
		}

		loginUser(USER1_ID);
		try {
			AlbumAO defaultAlbum = photoAPI.getDefaultAlbum(USER1_ID);
			Assert.assertEquals("Id of retrieved default album is wrong", defaultAlbum.getId(), photoAPI.getDefaultAlbum(USER1_ID).getId());
		} catch (PhotoAPIException e) {
			Assert.fail("Unexpected exception " + e.getMessage());
		}


		//getAlbum()
		try {
			photoAPI.getAlbum(0);
			Assert.fail("AlbumNotFoundPhotoAPIException should be thrown");
		} catch (Exception e) {
			Assert.assertTrue("AlbumNotFoundPhotoAPIException should be thrown", e instanceof AlbumNotFoundPhotoAPIException);
		}


		//getAlbums()
		try {
			List<AlbumAO> albumAOs = photoAPI.getAlbums(USER1_ID);
			Assert.assertEquals("Size of albums is wrong", 3, albumAOs.size());
		} catch (PhotoAPIException e) {
			Assert.fail("Unexpected exception " + e.getMessage());
		}


		//getMyAlbums()
		try {
			List<AlbumAO> albumAOs = photoAPI.getMyAlbums();
			Assert.assertEquals("Size of albums is wrong", photoAPI.getAlbums(USER1_ID).size(), albumAOs.size());
		} catch (PhotoAPIException e) {
			Assert.fail("Unexpected exception " + e.getMessage());
		}

		logoutUser();
		try {
			photoAPI.getMyAlbums();
			Assert.fail("PhotoAPIException should be thrown, user is not logged in");
		} catch (Exception e) {
			Assert.assertTrue("PhotoAPIException should be thrown, user is not logged in", e instanceof PhotoAPIException);
		}


		//removeAlbum()
		logoutUser();
		try {
			photoAPI.removeAlbum(testAlbum1.getId());
			Assert.fail("PhotoAPIException should be thrown, user is not logged in");
		} catch (Exception e) {
			Assert.assertTrue("PhotoAPIException should be thrown, user is not logged in", e instanceof PhotoAPIException);
		}

		loginUser(USER1_ID);
		try {
			AlbumAO removedAlbum = photoAPI.removeAlbum(photoAPI.getMyAlbums().get(0).getId());
			compareAlbums(removedAlbum, testAlbum1);

			Assert.assertEquals("Size of albums is wrong", 2, photoAPI.getAlbums(USER1_ID).size());
		} catch (PhotoAPIException e) {
			Assert.fail("Unexpected exception " + e.getMessage());
		}


		//updateAlbum()
		logoutUser();
		try {
			photoAPI.updateAlbum(new AlbumAO());
			Assert.fail("PhotoAPIException should be thrown, user is not logged in");
		} catch (Exception e) {
			Assert.assertTrue("PhotoAPIException should be thrown, user is not logged in", e instanceof PhotoAPIException);
		}

		loginUser(USER1_ID);
		try {
			AlbumAO albumToUpdate = photoAPI.getAlbum(photoAPI.getMyAlbums().get(0).getId());
			albumToUpdate.setName("new value");
			AlbumAO albumUpdated = photoAPI.updateAlbum(albumToUpdate);
			compareAlbums(albumToUpdate, albumUpdated);
		} catch (PhotoAPIException e) {
			Assert.fail("Unexpected exception " + e.getMessage());
		}

	}

	private static List<PhotoBO> getPhotosForTest(int amount, long albumID, String userId) {
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

	@Test
	public void testPhotoApprovingFunctionality() {
		List<PhotoBO> photos = new ArrayList<PhotoBO>();
		long albumId = 0;
		try {
			StorageService storageService = MetaFactory.get(StorageService.class);

			AlbumBO albumBO = new AlbumBO();
			albumBO.setUserId(USER2_ID);
			albumBO.setDefault(false);
			albumBO = storageService.createAlbum(albumBO);
			albumId = albumBO.getId();

			for (PhotoBO photo : getPhotosForTest(10, albumId, USER2_ID))
				photos.add(storageService.createPhoto(photo));

		} catch (MetaFactoryException e) {
			Assert.fail("Unexpected exception " + e.getMessage());
		} catch (StorageServiceException e) {
			Assert.fail("Unexpected exception " + e.getMessage());
		}

		photoAPI = APIFinder.findAPI(PhotoAPI.class);

		//check if photos are stored
		for (PhotoBO photo : photos) {
			try {
				PhotoAO photoAO = photoAPI.getPhoto(photo.getId());
				Assert.assertNotNull("Should not be null!", photoAO);
				Assert.assertEquals(photo.getId(), photoAO.getId());
				Assert.assertEquals(ApprovalStatus.WAITING_APPROVAL, photoAO.getApprovalStatus());
			} catch (PhotoAPIException e) {
				Assert.fail("Unexpected exception " + e.getMessage());
			}
		}
		//check if photos are stored v2
		try {
			switchOffApproving();
			List<PhotoAO> photoAOs = photoAPI.getPhotos(albumId);
			Assert.assertNotNull("photoAPI.getPhotos(albumId) should never return NULL", photoAOs);
			Assert.assertEquals(photos.size(), photoAOs.size());
			switchOnApproving();
		} catch (PhotoAPIException e) {
			Assert.fail("Unexpected exception " + e.getMessage());
		}
	}

    @Test
    public void testPhotoMovingFunctionality(){
        long photoId = 0, album1Id = 0, album2Id = 0, albumAnotherUser = 0, photoAnotherUserId = 0;
        try {
            StorageService storageService = MetaFactory.get(StorageService.class);

            AlbumBO albumBO = new AlbumBO();
            albumBO.setUserId(USER1_ID);
            albumBO.setDefault(false);
            albumBO = storageService.createAlbum(albumBO);
            album1Id = albumBO.getId();

            AlbumBO album2BO = new AlbumBO();
            album2BO.setUserId(USER1_ID);
            album2BO.setDefault(false);
            album2BO = storageService.createAlbum(album2BO);
            album2Id = album2BO.getId();

            AlbumBO albumAnotherBO = new AlbumBO();
            albumAnotherBO.setUserId(USER2_ID);
            albumAnotherBO.setDefault(false);
            albumAnotherBO = storageService.createAlbum(albumAnotherBO);
            albumAnotherUser = albumAnotherBO.getId();

            PhotoBO photo = getPhotosForTest(1, album1Id, USER1_ID).get(0);
            photoId = storageService.createPhoto(photo).getId();

            PhotoBO photoAnother = getPhotosForTest(1, albumAnotherUser, USER2_ID).get(0);
            photoAnotherUserId = storageService.createPhoto(photoAnother).getId();

            albumBO = storageService.getAlbum(album1Id);
            Assert.assertTrue(new HashSet<Long>(albumBO.getPhotosOrder()).contains(photoId));
        } catch (MetaFactoryException e){
            Assert.fail("Unexpected exception " + e.getMessage());
        } catch (StorageServiceException e){
            Assert.fail("Unexpected exception " + e.getMessage());
        }

        loginUser(USER1_ID);
        photoAPI = APIFinder.findAPI(PhotoAPI.class);
        try {
            PhotoAO photo = photoAPI.movePhoto(photoId, album2Id);
            AlbumAO album1 = photoAPI.getAlbum(album1Id);
            AlbumAO album2 = photoAPI.getAlbum(album2Id);
            Assert.assertEquals(photo.getAlbumId(), album2Id);
            Assert.assertTrue(new HashSet<Long>(album2.getPhotosOrder()).contains(photo.getId()));
            Assert.assertFalse(new HashSet<Long>(album1.getPhotosOrder()).contains(photo.getId()));

            try{
                photoAPI.movePhoto(photoId, albumAnotherUser);
                Assert.fail();
            }catch (NoAccessPhotoAPIException e){
                //expected
            }

            try{
                photoAPI.movePhoto(photoAnotherUserId, album1Id);
                Assert.fail();
            }catch (NoAccessPhotoAPIException e){
                //expected
            }
        } catch (PhotoAPIException e){
            Assert.fail("Unexpected exception " + e.getMessage());
        }
    }

	private void logoutUser() {
		try {
			loginAPI.logoutMe();
		} catch (APIException e) {
			Assert.fail("Unexpected exception " + e.getMessage());
		}
	}

	private void loginUser(String userId) {
		try {
			loginAPI.logInUser(String.valueOf(userId));
		} catch (APIException e) {
			Assert.fail("Unexpected exception " + e.getMessage());
		}
	}

	private void switchOnApproving() {
		PhotoServerConfig.getInstance().setPhotoApprovingEnabled(true);
	}

	private void switchOffApproving() {
		PhotoServerConfig.getInstance().setPhotoApprovingEnabled(false);
	}

	private void compareAlbums(AlbumAO a, AlbumAO b) {
		Assert.assertEquals("Property not equal", a.getDescription(), b.getDescription());
		Assert.assertEquals("Property not equal", a.getName(), b.getName());
		Assert.assertEquals("Property not equal", a.getPhotosOrder().size(), b.getPhotosOrder().size());
		Assert.assertEquals("Property not equal", a.getUserId(), b.getUserId());
	}

}
