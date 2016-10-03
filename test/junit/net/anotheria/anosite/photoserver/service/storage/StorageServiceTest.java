package net.anotheria.anosite.photoserver.service.storage;

import junit.framework.Assert;
import net.anotheria.anoprise.metafactory.MetaFactory;
import net.anotheria.anoprise.metafactory.MetaFactoryException;
import net.anotheria.anosite.photoserver.TestingContextInitializer;
import net.anotheria.anosite.photoserver.shared.ApprovalStatus;
import net.anotheria.anosite.photoserver.shared.PhotoServerConfig;
import net.anotheria.anosite.photoserver.shared.vo.PreviewSettingsVO;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.*;


public class StorageServiceTest {

	/**
	 * Invoking before executing tests in this class.
	 */
	@BeforeClass
	public static void before() {
		TestingContextInitializer.deInit();
		TestingContextInitializer.init();

	}

	@Before
	public void beforeMethod() {
		TestingContextInitializer.deInit();
		TestingContextInitializer.init();

	}


	/**
	 * Invoking after executing all tests in this class.
	 */
	@AfterClass
	public static void after() {
		TestingContextInitializer.deInit();
	}

	private PhotoBO getPhotoVOforTest() {
		PhotoBO photo = new PhotoBO();
		photo.setDescription("desc");
		photo.setName("my_photo!!");
		photo.setUserId("1235456");
		photo.setExtension("PNG");
		photo.setPreviewSettings(getPreviewSettings());
		return photo;
	}

	private List<AlbumBO> getAlbumsForTest(String userId) {
		List<AlbumBO> albums = new ArrayList<AlbumBO>();
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


	private PreviewSettingsVO getPreviewSettings() {
		return new PreviewSettingsVO(100, 200, 100500, 4500);
	}

	@Test
	public void testAlbumsCRUDFunctionality() {
		try {
			StorageService storageService = MetaFactory.get(StorageService.class);
			//owner id
			String userId = "1";

			// check that all is empty!
			List<AlbumBO> allAlbums = storageService.getAlbums(userId);
			Assert.assertNotNull("Can't be albums collection can't be null! !", allAlbums);
			Assert.assertTrue("Not Empty! Check!", allAlbums.isEmpty());

			//creating albums!!!
			List<AlbumBO> toCreate = getAlbumsForTest(userId);
			for (AlbumBO album : toCreate)
				storageService.createAlbum(album);

			allAlbums = storageService.getAlbums(userId);
			Assert.assertNotNull("Can't be albums collection can't be null! !", allAlbums);
			Assert.assertTrue("Is Empty!", !allAlbums.isEmpty() && allAlbums.size() == toCreate.size());

			// trying to read  one  by one!
			for (AlbumBO album : allAlbums) {
				AlbumBO read = storageService.getAlbum(album.getId());
				Assert.assertNotNull(read);
				Assert.assertEquals(album, read);
				Assert.assertNotSame("Clonnning  error somwhere! check!", album, read);

			}


			// getting default album!!!

			AlbumBO defaultAl = storageService.getDefaultAlbum(userId);
			AlbumBO defaultAlClone = storageService.getDefaultAlbum(userId);
			Assert.assertNotSame("Clone!", defaultAl, defaultAlClone);

			Assert.assertNotNull(defaultAl);
			// checking  that really new album  was created
			int oldAllAlbumsSize = allAlbums.size();

			allAlbums = storageService.getAlbums(userId);
			Assert.assertTrue(" Default album  was not created!", allAlbums.size() > oldAllAlbumsSize && allAlbums.size() - 1 == oldAllAlbumsSize);

			// update Check!
			for (AlbumBO album : allAlbums) {
				album.setName("a" + userId);
				storageService.updateAlbum(album);
			}

			//rereading
			allAlbums = storageService.getAlbums(userId);

			// update Check!
			for (AlbumBO album : allAlbums) {
				Assert.assertEquals("a" + userId, album.getName());
			}


			//remove Albums
			// update Check!
			for (AlbumBO album : allAlbums) {
				storageService.removeAlbum(album.getId());
			}

			// check that all is empty!
			allAlbums = storageService.getAlbums(userId);
			Assert.assertNotNull("Can't be albums collection can't be null! !", allAlbums);
			Assert.assertTrue("Not Empty! Check!", allAlbums.isEmpty());


		} catch (StorageServiceException e) {
			Assert.fail("StorageServiceException occured :  " + e);
		} catch (MetaFactoryException e) {
			Assert.fail("You should register StorageService in MetaFactory! " + e.getMessage());
		}

	}

	@Test
	public void albumCRUD_Errors() {
		try {
			StorageService storageService = MetaFactory.get(StorageService.class);
			AlbumBO toCreate = null;

			try {
				storageService.createAlbum(toCreate);
				Assert.fail("NULL");
			} catch (RuntimeException e) {
			}

			toCreate = new AlbumBO();
			toCreate.setDefault(true);

			try {
				storageService.createAlbum(toCreate);
				Assert.fail("Default album!");
			} catch (StorageServiceException e) {
			}

			AlbumBO toUpdate = null;
			try {
				storageService.updateAlbum(toUpdate);
				Assert.fail("Null");
			} catch (RuntimeException e) {
			}


			try {
				storageService.removeAlbum(1l);
				Assert.fail("Not exists!");
			} catch (StorageServiceException e) {

			}

			String userId = "10";
			//  album remove  with
			AlbumBO defaultAl = storageService.getDefaultAlbum(userId);
			//creating photo!
			PhotoBO photo = new PhotoBO();
			photo.setUserId(userId);
			photo.setAlbumId(defaultAl.getId());
			photo.setApprovalStatus(ApprovalStatus.APPROVED);
			photo.setExtension(".jpg");
			storageService.createPhoto(photo);

			try {
				storageService.removeAlbum(defaultAl.getId());
			} catch (AlbumWithPhotosServiceException e) {
			}


		} catch (StorageServiceException e) {
			Assert.fail("StorageServiceException occurred :  " + e);
		} catch (MetaFactoryException e) {
			Assert.fail("You should register StorageService in MetaFactory! " + e.getMessage());
		}
	}


	@Test
	public void testCRUDFunctionality() {
		try {
			StorageService storageService = MetaFactory.get(StorageService.class);
			PhotoBO photo = getPhotoVOforTest();


			AlbumBO defaultAlbum = storageService.getDefaultAlbum(photo.getUserId());

			//trying to get all photos from Default album!
			List<PhotoBO> allUserPhotos = storageService.getPhotos(photo.getUserId(), defaultAlbum.getId());
			Assert.assertNotNull(allUserPhotos);
			Assert.assertTrue(allUserPhotos.isEmpty());


			final String userId = photo.getUserId();
			//creating default album to which photo will be attached!
			photo.setAlbumId(defaultAlbum.getId());

			try {
				PhotoBO defaultPhoto = storageService.getDefaultPhoto(userId);
				Assert.fail("Fail here! - Album photos is Empty!!!!");
			} catch (StorageServiceException e) {
				Assert.assertTrue(e instanceof DefaultPhotoNotFoundServiceException);
			}
			try {
				PhotoBO defaultPhoto = storageService.getDefaultPhoto(userId, defaultAlbum.getId());
				Assert.fail("Fail here! - Album photos is Empty!!!!");
			} catch (StorageServiceException e) {
				Assert.assertTrue(e instanceof DefaultPhotoNotFoundServiceException);
			}


			//create
			PhotoBO created = storageService.createPhoto(photo);
			Assert.assertNotNull("Id was not created", created.getId());

			try {
				PhotoServerConfig.getInstance().setPhotoApprovingEnabled(true);
				PhotoBO defaultPhoto = storageService.getDefaultPhoto(userId);
				Assert.fail("Fail here! - Album does not have Approved photos!!!!!");
			} catch (StorageServiceException e) {
				Assert.assertTrue(e instanceof DefaultPhotoNotFoundServiceException);
			}

			try {
				PhotoServerConfig.getInstance().setPhotoApprovingEnabled(false);
				PhotoBO defaultPhoto = storageService.getDefaultPhoto(userId);
				Assert.assertEquals(defaultPhoto, created);
			} catch (StorageServiceException e) {
				Assert.fail("Should not happen!");
			}
			PhotoServerConfig.getInstance().setPhotoApprovingEnabled(true);

			//reading photo! - created!
			PhotoBO fromPersistence = storageService.getPhoto(created.getId());
			// data check!
			for (PhotoBO other : new PhotoBO[]{created, fromPersistence}) {
				// check originally set properties
				Assert.assertEquals(photo.getName(), other.getName());
				Assert.assertEquals(photo.getUserId(), other.getUserId());
				Assert.assertEquals(photo.getDescription(), other.getDescription());
				Assert.assertEquals(photo.getExtension(), other.getExtension());
				// check properties set by service
				Assert.assertNotNull(other.getFileLocation());
				Assert.assertEquals(1, other.getId());
				Assert.assertTrue(other.getModificationTime() > 0);
				// check PreviewSettingsVO
				Assert.assertEquals(photo.getPreviewSettings(), other.getPreviewSettings());
			}

			//reading all photos!!!
			List<PhotoBO> photos = storageService.getPhotos(userId, defaultAlbum.getId());
			List<PhotoBO> photos2 = storageService.getPhotos(userId, defaultAlbum.getId());
			Assert.assertNotNull(photos);
			Assert.assertNotNull(photos2);
			Assert.assertEquals(photos, photos2);
			Assert.assertEquals("More or less then 1 1photo!", 1, photos.size());

			//next photo create!
			storageService.createPhoto(photo);
			photos = storageService.getPhotos(photo.getUserId(), defaultAlbum.getId());

			Assert.assertNotNull(photos);
			Assert.assertEquals(2, photos.size());


			// check getting photos by userID and list of photo IDs
			photos = storageService.getPhotos(userId, Arrays.asList(photos.get(0).getId(), photos.get(1).getId()));
			Assert.assertEquals("error!", 2, photos.size());

			// basic check of photoApproval functionality
			final int amountOf = 10;
			final long idPhoto1 = photos.get(0).getId();
			final long idPhoto2 = photos.get(1).getId();
			//status  change for photo1
			Assert.assertEquals(2, storageService.getWaitingApprovalPhotosCount());
			Assert.assertEquals(2, storageService.getWaitingApprovalPhotos(amountOf).size());
			storageService.updatePhotoApprovalStatuses(Collections.singletonMap(idPhoto1, ApprovalStatus.REJECTED));

			//status  change for photo2
			Assert.assertEquals(1, storageService.getWaitingApprovalPhotosCount());
			Assert.assertEquals(1, storageService.getWaitingApprovalPhotos(amountOf).size());
			storageService.updatePhotoApprovalStatuses(Collections.singletonMap(idPhoto2, ApprovalStatus.APPROVED));

			//Now there shoul be 0
			Assert.assertEquals(0, storageService.getWaitingApprovalPhotosCount());
			Assert.assertEquals(0, storageService.getWaitingApprovalPhotos(amountOf).size());

			try {
				PhotoServerConfig.getInstance().setPhotoApprovingEnabled(true);
				PhotoBO defaultPhoto = storageService.getDefaultPhoto(userId);
				//First photo was rejected! So  default is Photo with id 2
				Assert.assertEquals(storageService.getDefaultAlbum(userId).getPhotosOrder().get(1), Long.valueOf(defaultPhoto.getId()));
			} catch (StorageServiceException e) {
				Assert.fail("Should not happen!");
			}


			Map<Long, ApprovalStatus> statuses = storageService.getAlbumPhotosApprovalStatus(photo.getAlbumId());
			// just  cache check!
			storageService.getPhotos(photo.getUserId(), photo.getAlbumId());
			Map<Long, ApprovalStatus> status2 = storageService.getAlbumPhotosApprovalStatus(photo.getAlbumId());
			Assert.assertTrue(status2.size() == statuses.size());


			Assert.assertEquals(2, statuses.size());
			Assert.assertEquals(ApprovalStatus.REJECTED, statuses.get(idPhoto1));
			Assert.assertEquals(ApprovalStatus.APPROVED, statuses.get(idPhoto2));

			photos = storageService.getPhotos(userId, Arrays.asList(idPhoto1, idPhoto2, 3l, 123l, -1l, 0l));
			Assert.assertEquals(2, photos.size());
			photos = storageService.getPhotos(userId, Arrays.asList(idPhoto2));
			Assert.assertEquals(1, photos.size());
			photos = storageService.getPhotos(userId, Arrays.asList(12l, 22l));
			Assert.assertEquals(0, photos.size());
			photos = storageService.getPhotos("4355345", Arrays.asList(12l, 22l));
			Assert.assertEquals(0, photos.size());
			try {
				photos = storageService.getPhotos(photo.getUserId(), null);
				Assert.fail();
			} catch (IllegalArgumentException e) {
				// expected
			}

			try {
				storageService.removePhoto(3);
				Assert.fail();
			} catch (PhotoNotFoundServiceException e) {
				// expected
			}
			try {
				storageService.removePhoto(2);
			} catch (StorageServiceException e) {
				Assert.fail();
			}
			try {
				storageService.getPhoto(2);
				Assert.fail();
			} catch (PhotoNotFoundServiceException e) {
				// expected
			}

			//first call!  just  put all to cache :)
			storageService.getPhotos(userId, defaultAlbum.getId());
			//real call
			photos = storageService.getPhotos(userId, defaultAlbum.getId());
			Assert.assertNotNull(photos);
			Assert.assertEquals(1, photos.size());

			PhotoBO changed = photos.get(0).clone();
			changed.setDescription("descCHANGED");
			changed.setName("my_photo!!CHANGED");
			changed.setUserId("66666"); // should not affect persisted object
			changed.setPreviewSettings(new PreviewSettingsVO(100, 20, 35, 41));

			storageService.updatePhoto(changed);
			PhotoBO updatedFromPersistence = storageService.getPhoto(changed.getId());

			// check originally set properties
			Assert.assertEquals(changed.getName(), updatedFromPersistence.getName());

			Assert.assertNotSame(changed.getUserId(), updatedFromPersistence.getUserId());

			Assert.assertEquals(changed.getDescription(), updatedFromPersistence.getDescription());

			Assert.assertEquals(changed.getPreviewSettings(), updatedFromPersistence.getPreviewSettings());

			// check properties set by service
			Assert.assertNotNull(updatedFromPersistence.getFileLocation());
			Assert.assertEquals(changed.getId(), updatedFromPersistence.getId());
			//Assert.assertTrue(updatedFromPersistence.getModificationTime() > changed.getModificationTime());

			changed.setId(555);
			try {
				storageService.updatePhoto(changed);
				Assert.fail();
			} catch (PhotoNotFoundServiceException e) {
				// expected
			}

            AlbumBO newAlbum =new AlbumBO();
            newAlbum.setUserId("10");
            newAlbum = storageService.createAlbum(newAlbum);
            PhotoBO updated  = storageService.movePhoto(photos.get(0).getId(), newAlbum.getId());
            AlbumBO updatedAlbum = storageService.getAlbum(newAlbum.getId());
            defaultAlbum = storageService.getAlbum(defaultAlbum.getId());

            Assert.assertEquals(updated.getAlbumId(), newAlbum.getId());

            try {
                storageService.movePhoto(555, newAlbum.getId());
                Assert.fail();
            } catch (PhotoNotFoundServiceException e){
                // expected
            }

            try {
                storageService.movePhoto(updated.getId(), 555);
                Assert.fail();
            } catch (AlbumNotFoundServiceException e){
                // expected
            }


		} catch (StorageServiceException e) {
			Assert.fail("StorageServiceException occured :  " + e);
		} catch (MetaFactoryException e) {
			Assert.fail("You should register StorageService in MetaFactory! " + e.getMessage());
		}
	}

	@Test
	public void testPhotoCrud_errors() {
		try {
			StorageService storageService = MetaFactory.get(StorageService.class);

			try {
				storageService.getWaitingApprovalPhotos(-100);
				Assert.fail("Wrong incoming  photo count");
			} catch (Exception e) {
				Assert.assertTrue(e instanceof RuntimeException);
			}
			try {
				storageService.createPhoto(null);
				Assert.fail("Illegal argument");
			} catch (Exception e) {
				Assert.assertTrue(e instanceof RuntimeException);
			}
			try {
				storageService.updatePhoto(null);
				Assert.fail("Illegal argument");
			} catch (Exception e) {
				Assert.assertTrue(e instanceof RuntimeException);
			}

			try {
				storageService.updatePhotoApprovalStatuses(null);
				Assert.fail("Illegal argument");
			} catch (Exception e) {
				Assert.assertTrue(e instanceof RuntimeException);
			}

		} catch (MetaFactoryException e) {
			Assert.fail("MF - init error");
		}
	}

	@Test
	public void testUnApprovedPhotosSort() {
		try {
			StorageService testService = MetaFactory.get(StorageService.class);
			final String firstUser = "1l";
			final String user2 = "2l";
			AlbumBO albumDefault1 = testService.getDefaultAlbum(firstUser);
			AlbumBO albumDefault2 = testService.getDefaultAlbum(user2);


			PhotoBO photo1 = new PhotoBO();
			photo1.setUserId(firstUser);
			photo1.setExtension(".jpg");
			photo1.setAlbumId(albumDefault1.getId());
			photo1.setName("Photo1");
			photo1 = testService.createPhoto(photo1);

			PhotoBO photo21 = new PhotoBO();
			photo21.setUserId(user2);
			photo21.setExtension(".jpg");
			photo21.setAlbumId(albumDefault2.getId());
			photo21.setName("Photo12");
			photo21 = testService.createPhoto(photo21);


			Thread.sleep(100l);

			PhotoBO photo2 = new PhotoBO();
			photo2.setUserId(firstUser);
			photo2.setExtension(".jpg");
			photo2.setAlbumId(albumDefault1.getId());
			photo2.setName("Photo2");
			photo2 = testService.createPhoto(photo2);

			PhotoBO photo22 = new PhotoBO();
			photo22.setUserId(user2);
			photo22.setExtension(".jpg");
			photo22.setAlbumId(albumDefault2.getId());
			photo22.setName("Photo22");
			photo22 = testService.createPhoto(photo22);

			List<PhotoBO> unApprovedPhotos = testService.getWaitingApprovalPhotos(4);
			Assert.assertTrue(unApprovedPhotos.size() == 4);
			// checking sorting!
			// first  ASC  sor by   Modification TIME! +  photos  should be sorted by UserIds!
			Assert.assertEquals(unApprovedPhotos.get(0), photo1);
			Assert.assertEquals(unApprovedPhotos.get(1), photo2);
			Assert.assertEquals(unApprovedPhotos.get(2), photo21);
			Assert.assertEquals(unApprovedPhotos.get(3), photo22);


		} catch (MetaFactoryException e) {
			Assert.fail("Error  - in MF init!");
		} catch (StorageServiceException e) {
			Assert.fail();
		} catch (InterruptedException e) {

		}
	}


}
