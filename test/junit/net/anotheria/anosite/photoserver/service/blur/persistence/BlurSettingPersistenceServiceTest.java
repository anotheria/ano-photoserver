package net.anotheria.anosite.photoserver.service.blur.persistence;

import junit.framework.Assert;
import net.anotheria.anoprise.metafactory.MetaFactory;
import net.anotheria.anoprise.metafactory.MetaFactoryException;
import net.anotheria.anosite.photoserver.TestingContextInitializer;
import net.anotheria.anosite.photoserver.service.blur.BlurSettingBO;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static net.anotheria.anosite.photoserver.service.blur.BlurSettingBO.*;

/**
 * JUnit test for BlurSettingsPersistenceService.
 *
 * @author h3ll
 */
public class BlurSettingPersistenceServiceTest {


	@BeforeClass
	public static void before() {
		TestingContextInitializer.deInit();
	}

	@AfterClass
	public static void after() {
		TestingContextInitializer.deInit();
	}

	@Before
	public void beforeMethod() {
		TestingContextInitializer.deInit();
		TestingContextInitializer.init();
	}


	@Test
	public void blurAlbumTestCase() {
		long albumId = 1;
		long pictureId = 2;
		String userId = "100";

		try {
			BlurSettingsPersistenceService testService = MetaFactory.get(BlurSettingsPersistenceService.class);

			try {
				BlurSettingBO result = testService.readBlurSetting(albumId, pictureId, userId);
				Assert.assertNull("Should be null", result);

				//  now Lets  Unblur not blurred album!  We shpuld got error!! SO
				try {
					testService.unBlurAlbum(albumId);
					Assert.fail("Album is not Blurred ! there is  nothing to unBlur :))");
				} catch (Exception e) {
					Assert.assertTrue(e instanceof AlbumIsNotBlurredPersistenceException);
				}

				//blurring album!!for all users
				try {
					testService.blurAlbum(albumId);

				} catch (Exception e) {
					Assert.fail("Can't happen!!! :))");
				}
				//blur already blurred Album!!!
				try {
					testService.blurAlbum(albumId);
					Assert.fail("Album is Blurred! already");
				} catch (Exception e) {
					Assert.assertTrue(e instanceof AlbumIsBlurredPersistenceException);
				}


				//reading value  againn!  it's  shoul be blurred NOW!
				result = testService.readBlurSetting(albumId, pictureId, userId);
				Assert.assertNotNull("Should not be null", result);
				Assert.assertEquals(albumId, result.getAlbumId());
				Assert.assertEquals(ALL_ALBUM_PICTURES_DEFAULT_CONSTANT, result.getPictureId());
				Assert.assertEquals(BlurSettingBO.ALL_USERS_DEFAULT_CONSTANT, result.getUserId());
				Assert.assertTrue("Should be blurred!", result.isBlurred());


				//  Now album is Blurred!
				//Lets  try  to Blur it  for  current User!!!
				try {
					testService.blurAlbum(albumId, userId);
					Assert.fail("Album is already Blurred ! there is  nothing to blur :))");
				} catch (Exception e) {
					Assert.assertTrue(e instanceof AlbumIsBlurredPersistenceException);
				}

				//lets  unblur  album  for  current user ONLy!!
				try {
					testService.unBlurAlbum(albumId, userId);
					// reading  properties  after UNBLUR\
					BlurSettingBO unbluredAlbumForUser = testService.readBlurSetting(albumId, pictureId, userId);
					Assert.assertNotNull("Should not be null", unbluredAlbumForUser);
					Assert.assertEquals(albumId, unbluredAlbumForUser.getAlbumId());
					Assert.assertEquals(ALL_ALBUM_PICTURES_DEFAULT_CONSTANT, unbluredAlbumForUser.getPictureId());
					Assert.assertEquals(userId, unbluredAlbumForUser.getUserId());
					Assert.assertFalse("Should be not blurred!", unbluredAlbumForUser.isBlurred());

					Assert.assertTrue("Should not be same ", !result.equals(unbluredAlbumForUser));

					//LEt's  try again  UnBlur nit blurred album!! We should got an error  here!!
					try {
						testService.unBlurAlbum(albumId, userId);
						Assert.fail("Fail here!");
					} catch (Exception e) {
						Assert.assertTrue(e instanceof AlbumIsNotBlurredPersistenceException);
					}

					//Let's try to  read  setting for new  user!
					final String newUserId = 500 + "";
					BlurSettingBO settingForNewUser = testService.readBlurSetting(albumId, pictureId, newUserId);
					Assert.assertNotNull("Should not be null", settingForNewUser);
					Assert.assertEquals(albumId, settingForNewUser.getAlbumId());
					Assert.assertEquals(ALL_ALBUM_PICTURES_DEFAULT_CONSTANT, settingForNewUser.getPictureId());
					Assert.assertEquals(ALL_USERS_DEFAULT_CONSTANT, settingForNewUser.getUserId());
					Assert.assertTrue("Should be blurred!", settingForNewUser.isBlurred());

					Assert.assertTrue("Should  be same ", result.equals(settingForNewUser));


					// NOW unblur  Album globally!!
					testService.unBlurAlbum(albumId);
					result = testService.readBlurSetting(albumId, pictureId, userId);
					settingForNewUser = testService.readBlurSetting(albumId, pictureId, userId);
					Assert.assertEquals(result, settingForNewUser);
					Assert.assertFalse(result.isBlurred());
					Assert.assertFalse(settingForNewUser.isBlurred());


					//  blur  album  for new  user!
					testService.blurAlbum(albumId, newUserId);
					settingForNewUser = testService.readBlurSetting(albumId, pictureId, newUserId);
					//checking expectations
					Assert.assertNotNull("Should not be null", settingForNewUser);
					Assert.assertEquals(albumId, settingForNewUser.getAlbumId());
					Assert.assertEquals(ALL_ALBUM_PICTURES_DEFAULT_CONSTANT, settingForNewUser.getPictureId());
					Assert.assertEquals(newUserId, settingForNewUser.getUserId());
					Assert.assertTrue("Should be blurred!", settingForNewUser.isBlurred());


				} catch (Exception e) {
					Assert.fail("got an ERROR!!");
				}

			} catch (BlurSettingsPersistenceServiceException e) {
				Assert.fail("Can't happen!");
			}


		} catch (MetaFactoryException e) {
			Assert.fail("MetaFactory error");
		}


	}


	@Test
	public void blurPictureTestCase() {
		long albumId = 8;
		long pictureId = 22;
		String userId = 100 + "";

		try {
			BlurSettingsPersistenceService testService = MetaFactory.get(BlurSettingsPersistenceService.class);

			try {
				BlurSettingBO result = testService.readBlurSetting(albumId, pictureId, userId);
				Assert.assertNull("Should be null", result);

				//  now Lets  Unblur not blurred album!  We shpuld got error!! SO
				try {
					testService.unBlurAlbum(albumId);
					Assert.fail("Album is not Blurred ! there is  nothing to unBlur :))");
				} catch (Exception e) {
					Assert.assertTrue(e instanceof AlbumIsNotBlurredPersistenceException);
				}

				//  now Lets  Unblur not blurred album for user!  We shpuld got error!! SO
				try {
					testService.unBlurAlbum(albumId, userId);
					Assert.fail("Album is not Blurred ! there is  nothing to unBlur :))");
				} catch (Exception e) {
					Assert.assertTrue(e instanceof AlbumIsNotBlurredPersistenceException);
				}

				//try to Unblur  not blurred Picture!!!
				try {
					testService.unBlurPicture(albumId, pictureId);
					Assert.fail("Picture is not Blurred ! there is  nothing to unBlur :))");
				} catch (Exception e) {
					Assert.assertTrue(e instanceof PictureIsNotBlurredPersistenceException);
				}

				//try to Unblur  not blurred Picture!!!
				try {
					testService.unBlurPicture(albumId, pictureId, userId);
					Assert.fail("Picture is not Blurred ! there is  nothing to unBlur :))");
				} catch (Exception e) {
					Assert.assertTrue(e instanceof PictureIsNotBlurredPersistenceException);
				}


				//Now lets  blur  album!   - and Try to Blur  Picture!!!
				testService.blurAlbum(albumId, userId);
				//try to blur  already blurred Picture!!!
				try {
					testService.blurPicture(albumId, pictureId, userId);
					Assert.fail("Picture is Blurred ! there is  nothing to blur :))");
				} catch (Exception e) {
					Assert.assertTrue(e instanceof PictureIsBlurredPersistenceException);
				}

				BlurSettingBO setting = testService.readBlurSetting(albumId, pictureId, userId);
				Assert.assertNotNull(setting);
				Assert.assertEquals(albumId, setting.getAlbumId());
				Assert.assertEquals(ALL_ALBUM_PICTURES_DEFAULT_CONSTANT, setting.getPictureId());
				Assert.assertEquals(userId, setting.getUserId());
				Assert.assertTrue("Should be blurred!", setting.isBlurred());

				//LEt's  unblur  picture for selected User
				testService.unBlurPicture(albumId, pictureId, userId);
				setting = testService.readBlurSetting(albumId, pictureId, userId);
				Assert.assertNotNull(setting);
				Assert.assertEquals(albumId, setting.getAlbumId());
				Assert.assertEquals(pictureId, setting.getPictureId());
				Assert.assertEquals(userId, setting.getUserId());
				Assert.assertFalse("Should not be blurred!", setting.isBlurred());


				//Now  let's blur album for all users!
				testService.blurAlbum(albumId);
				//checking ! - that   all other  settings  were  removed!
				setting = testService.readBlurSetting(albumId, pictureId, userId);
				Assert.assertNotNull(setting);
				Assert.assertEquals(albumId, setting.getAlbumId());
				Assert.assertEquals(ALL_ALBUM_PICTURES_DEFAULT_CONSTANT, setting.getPictureId());
				Assert.assertEquals(ALL_USERS_DEFAULT_CONSTANT, setting.getUserId());
				Assert.assertTrue("Should be blurred!", setting.isBlurred());


				//unblure picture  for all users!! NOW!
				testService.unBlurPicture(albumId, pictureId);
				//checking!
				setting = testService.readBlurSetting(albumId, pictureId, userId);
				Assert.assertNotNull(setting);
				Assert.assertEquals(albumId, setting.getAlbumId());
				Assert.assertEquals(pictureId, setting.getPictureId());
				Assert.assertEquals(ALL_USERS_DEFAULT_CONSTANT, setting.getUserId());
				Assert.assertFalse("Should not be blurred!", setting.isBlurred());


				//  Blurring  this  picture now!  for 1 user
				testService.blurPicture(albumId, pictureId, userId);
				setting = testService.readBlurSetting(albumId, pictureId, userId);
				Assert.assertNotNull(setting);
				Assert.assertEquals(albumId, setting.getAlbumId());
				Assert.assertEquals(pictureId, setting.getPictureId());
				Assert.assertEquals(userId, setting.getUserId());
				Assert.assertTrue("Should not be blurred!", setting.isBlurred());

				//reblurring  for all users!
				testService.blurPicture(albumId, pictureId);
				setting = testService.readBlurSetting(albumId, pictureId, 100000 + "");
				Assert.assertNotNull(setting);
				Assert.assertEquals(albumId, setting.getAlbumId());
				Assert.assertEquals(pictureId, setting.getPictureId());
				Assert.assertEquals(ALL_USERS_DEFAULT_CONSTANT, setting.getUserId());
				Assert.assertTrue("Should not be blurred!", setting.isBlurred());


			} catch (BlurSettingsPersistenceServiceException e) {
				Assert.fail("Can't happen!");
			}

		} catch (MetaFactoryException e) {
			Assert.fail("MetaFactory error");
		}
	}
}