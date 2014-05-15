package net.anotheria.anosite.photoserver.service.blur;

import junit.framework.Assert;
import net.anotheria.anoprise.metafactory.MetaFactory;
import net.anotheria.anoprise.metafactory.MetaFactoryException;
import net.anotheria.anosite.photoserver.TestingContextInitializer;
import net.anotheria.anosite.photoserver.service.blur.cache.BlurSettingsCache;
import net.anotheria.anosite.photoserver.shared.vo.BlurSettingVO;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;


/**
 * Junit for Blur setting service.
 *
 * @author h3ll
 */
public class BlurSettingsServiceTest {

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
		BlurSettingsCache.getInstance().resetCache();
	}

	@Test
	public void blurAlbumTestCase() {
		long albumId = 1;
		long pictureIdLong = 2;
		String userId = "100";

		try {
			BlurSettingsService testService = MetaFactory.get(BlurSettingsService.class);
			BlurSettingsCache cache = BlurSettingsCache.getInstance();

			try {
				List<Long> pictureId = new ArrayList<Long>();
				pictureId.add(pictureIdLong);
				Map<Long, BlurSettingBO> result = testService.readBlurSettings(albumId, pictureId, userId);
				Assert.assertNotNull("Should be null", result);
				Assert.assertFalse(result.isEmpty());
				Assert.assertEquals(result.get(pictureIdLong), new BlurSettingBO(albumId, pictureIdLong, userId, false));
				Assert.assertFalse(result.get(pictureIdLong).isBlurred());
				//cached ???:)
				Assert.assertNotNull("Should be cached ", cache.getCachedSetting(albumId, pictureIdLong, userId));


				Map<Long, BlurSettingBO> defaultUser = testService.readBlurSettings(albumId, pictureId);
				Assert.assertNotNull("Should be null", defaultUser);
				Assert.assertFalse(defaultUser.isEmpty());
				Assert.assertEquals(defaultUser.get(pictureIdLong), new BlurSettingBO(albumId, pictureIdLong, BlurSettingVO.ALL_USERS_DEFAULT_CONSTANT, false));
				Assert.assertFalse(result.get(pictureIdLong).isBlurred());


				//  now Lets  Unblur not blurred album!  We shpuld got error!! SO
				try {
					testService.unBlurAlbum(albumId);
					Assert.fail("Album is not Blurred ! there is  nothing to unBlur :))");
				} catch (Exception e) {
					Assert.assertTrue(e instanceof AlbumIsNotBlurredException);
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
					Assert.assertTrue(e instanceof AlbumIsBlurredException);
				}


				//reading value  againn!  it's  shoul be blurred NOW!

				result = testService.readBlurSettings(albumId, pictureId, userId);
				Assert.assertNotNull("Should not be null", result);
				BlurSettingBO setting = result.get(pictureIdLong);
				Assert.assertNotNull("Should not be null", setting);
				Assert.assertEquals(albumId, setting.getAlbumId());
				Assert.assertEquals(BlurSettingVO.ALL_ALBUM_PICTURES_DEFAULT_CONSTANT, setting.getPictureId());
				Assert.assertEquals(BlurSettingVO.ALL_USERS_DEFAULT_CONSTANT, setting.getUserId());
				Assert.assertTrue("Should be blurred!", setting.isBlurred());
				//check cached value
				BlurSettingBO cached = cache.getCachedSetting(albumId, pictureIdLong, userId);
				Assert.assertEquals(cached, setting);


				//  Now album is Blurred!
				//Lets  try  to Blur it  for  current User!!!
				try {
					testService.blurAlbum(albumId, userId);
					Assert.fail("Album is already Blurred ! there is  nothing to blur :))");
				} catch (Exception e) {
					Assert.assertTrue(e instanceof AlbumIsBlurredException);
				}

				//lets  unblur  album  for  current user ONLy!!
				try {
					testService.unBlurAlbum(albumId, userId);
					// reading  properties  after UNBLUR\
					Map<Long, BlurSettingBO> unblurredAlbMap = testService.readBlurSettings(albumId, pictureId, userId);
					BlurSettingBO unbluredAlbumForUser = unblurredAlbMap.get(pictureIdLong);

					Assert.assertNotNull("Should not be null", unbluredAlbumForUser);
					Assert.assertEquals(albumId, unbluredAlbumForUser.getAlbumId());
					Assert.assertEquals(BlurSettingVO.ALL_ALBUM_PICTURES_DEFAULT_CONSTANT, unbluredAlbumForUser.getPictureId());
					Assert.assertEquals(userId, unbluredAlbumForUser.getUserId());
					Assert.assertFalse("Should be not blurred!", unbluredAlbumForUser.isBlurred());

					//check cached value
					cached = cache.getCachedSetting(albumId, pictureIdLong, userId);
					Assert.assertEquals(cached, unbluredAlbumForUser);

					//LEt's  try again  UnBlur nit blurred album!! We should got an error  here!!
					try {
						testService.unBlurAlbum(albumId, userId);
						Assert.fail("Fail here!");
					} catch (Exception e) {
						Assert.assertTrue(e instanceof AlbumIsNotBlurredException);
					}


					//Let's try to  read  setting for new  user!
					final String newUserIdLong = "500";

					Map<Long, BlurSettingBO> settingMapNew = testService.readBlurSettings(albumId, pictureId, newUserIdLong);
					BlurSettingBO settingForNewUser = settingMapNew.get(pictureIdLong);


					Assert.assertNotNull("Should not be null", settingForNewUser);
					Assert.assertEquals(albumId, settingForNewUser.getAlbumId());
					Assert.assertEquals(BlurSettingVO.ALL_ALBUM_PICTURES_DEFAULT_CONSTANT, settingForNewUser.getPictureId());
					Assert.assertEquals(BlurSettingVO.ALL_USERS_DEFAULT_CONSTANT, settingForNewUser.getUserId());
					Assert.assertTrue("Should be blurred!", settingForNewUser.isBlurred());

					//check cached value
					cached = cache.getCachedSetting(albumId, pictureIdLong, userId);
					Assert.assertEquals(cached, unbluredAlbumForUser);


					// NOW unblur  Album globally!!
					testService.unBlurAlbum(albumId);
					result = testService.readBlurSettings(albumId, pictureId, userId);
					settingMapNew = testService.readBlurSettings(albumId, pictureId, userId);
					Assert.assertEquals(result.get(pictureIdLong), settingMapNew.get(pictureIdLong));
					Assert.assertFalse(result.get(pictureIdLong).isBlurred());
					Assert.assertFalse(settingMapNew.get(pictureIdLong).isBlurred());


					//  blur  album  for new  user!
					testService.blurAlbum(albumId, newUserIdLong);
					settingMapNew = testService.readBlurSettings(albumId, pictureId, newUserIdLong);
					//checking expectations
					settingForNewUser = settingMapNew.get(pictureIdLong);
					Assert.assertNotNull("Should not be null", settingForNewUser);
					Assert.assertEquals(albumId, settingForNewUser.getAlbumId());
					Assert.assertEquals(BlurSettingVO.ALL_ALBUM_PICTURES_DEFAULT_CONSTANT, settingForNewUser.getPictureId());
					Assert.assertEquals(newUserIdLong, settingForNewUser.getUserId());
					Assert.assertTrue("Should be blurred!", settingForNewUser.isBlurred());


					//Remove all!!!
					testService.removeBlurSettings(albumId);


					Map<Long, BlurSettingBO> resultDeleted = testService.readBlurSettings(albumId, pictureId, userId);
					Assert.assertNotNull("Should be null", resultDeleted);
					Assert.assertFalse(resultDeleted.isEmpty());
					Assert.assertEquals(resultDeleted.get(pictureIdLong), new BlurSettingBO(albumId, pictureIdLong, userId, false));
					Assert.assertFalse(resultDeleted.get(pictureIdLong).isBlurred());


				} catch (BlurSettingsServiceException e) {
					Assert.fail("FAILURE");
				}


			} catch (BlurSettingsServiceException e) {
				Assert.fail("Can't happen!");
			}


		} catch (MetaFactoryException e) {
			Assert.fail("MetaFactory error");
		}


	}

	@Test
	public void exceptionTest() {
		try {
			BlurSettingsService service = MetaFactory.get(BlurSettingsService.class);

			//Read  illegal  params!
			try {
				service.readBlurSettings(-1, null, "-1");
				Assert.fail("Illegal album id argument!");
			} catch (Exception e) {
				Assert.assertTrue(e instanceof IllegalArgumentException);
			}
			try {
				service.readBlurSettings(10, null, "");
				Assert.fail("Illegal  userId argument!");
			} catch (Exception e) {
				Assert.assertTrue(e instanceof IllegalArgumentException);
			}
			try {
				service.readBlurSettings(10, null, "101");
				Assert.fail("Illegal picture  id Collection argument!");
			} catch (Exception e) {
				Assert.assertTrue(e instanceof IllegalArgumentException);
			}
			try {
				service.readBlurSettings(10, Collections.<Long>emptyList(), "101");
				Assert.fail("Illegal picture  id Collection argument!");
			} catch (Exception e) {
				Assert.assertTrue(e instanceof IllegalArgumentException);
			}
			try {
				List<Long> id = new ArrayList<Long>();
				id.add(-1l);

				service.readBlurSettings(10, id, "101");
				Assert.fail("Illegal picture  id Collection argument!");
			} catch (Exception e) {
				Assert.assertTrue(e instanceof IllegalArgumentException);
			}
			try {
				List<Long> id = new ArrayList<Long>();
				id.add(null);

				service.readBlurSettings(10, id, "101");
				Assert.fail("Illegal picture  id Collection argument!");
			} catch (Exception e) {
				Assert.assertTrue(e instanceof IllegalArgumentException);
			}


			//Album blur  illegal  params

			try {
				service.blurAlbum(-1);
				Assert.fail("Illegal id");
			} catch (Exception e) {
				Assert.assertTrue(e instanceof IllegalArgumentException);
			}
			try {
				service.unBlurAlbum(-1);
				Assert.fail("Illegal id");
			} catch (Exception e) {
				Assert.assertTrue(e instanceof IllegalArgumentException);
			}
			try {
				service.blurAlbum(-1, "10");
				Assert.fail("Illegal id");
			} catch (Exception e) {
				Assert.assertTrue(e instanceof IllegalArgumentException);
			}
			try {
				service.unBlurAlbum(10, null);
				Assert.fail("Illegal userId");
			} catch (Exception e) {
				Assert.assertTrue(e instanceof IllegalArgumentException);
			}
			try {
				service.blurAlbum(1, "");
				Assert.fail("Illegal userId");
			} catch (Exception e) {
				Assert.assertTrue(e instanceof IllegalArgumentException);
			}
			try {
				service.unBlurAlbum(-1, "10");
				Assert.fail("Illegal id");
			} catch (Exception e) {
				Assert.assertTrue(e instanceof IllegalArgumentException);
			}

			//photo blur

			try {
				service.blurPicture(-1, 1);
				Assert.fail("Illegal albumId");
			} catch (Exception e) {
				Assert.assertTrue(e instanceof IllegalArgumentException);
			}
			try {
				service.blurPicture(1, -1);
				Assert.fail("Illegal picture");
			} catch (Exception e) {
				Assert.assertTrue(e instanceof IllegalArgumentException);
			}
			try {
				service.unBlurPicture(-1, 1);
				Assert.fail("Illegal albumId");
			} catch (Exception e) {
				Assert.assertTrue(e instanceof IllegalArgumentException);
			}
			try {
				service.unBlurPicture(1, -1);
				Assert.fail("Illegal picture");
			} catch (Exception e) {
				Assert.assertTrue(e instanceof IllegalArgumentException);
			}

			try {
				service.blurPicture(-1, 1, "1");
				Assert.fail("Illegal albumId");
			} catch (Exception e) {
				Assert.assertTrue(e instanceof IllegalArgumentException);
			}
			try {
				service.blurPicture(1, -1, "1");
				Assert.fail("Illegal picture");
			} catch (Exception e) {
				Assert.assertTrue(e instanceof IllegalArgumentException);
			}
			try {
				service.blurPicture(1, 1, null);
				Assert.fail("Illegal user id");
			} catch (Exception e) {
				Assert.assertTrue(e instanceof IllegalArgumentException);
			}

			try {
				service.unBlurPicture(-1, 1, "1");
				Assert.fail("Illegal albumId");
			} catch (Exception e) {
				Assert.assertTrue(e instanceof IllegalArgumentException);
			}
			try {
				service.unBlurPicture(1, -1, "1");
				Assert.fail("Illegal picture");
			} catch (Exception e) {
				Assert.assertTrue(e instanceof IllegalArgumentException);
			}
			try {
				service.unBlurPicture(1, 1,"");
				Assert.fail("Illegal user id");
			} catch (Exception e) {
				Assert.assertTrue(e instanceof IllegalArgumentException);
			}

			//remove
			try {
				service.removeBlurSettings(-1);
				Assert.fail("Illegal album id");
			} catch (Exception e) {
				Assert.assertTrue(e instanceof IllegalArgumentException);
			}


		} catch (MetaFactoryException e) {
			Assert.fail("Error! ");
		}


	}


	@Test
	public void blurPictureTestCase() {
		long albumId = 8;
		long pictureIdLong = 22;
		String userId = "100";

		try {
			BlurSettingsService testService = MetaFactory.get(BlurSettingsService.class);
			BlurSettingsCache cache = BlurSettingsCache.getInstance();

			try {
				List<Long> pictureId = new ArrayList<Long>();
				pictureId.add(pictureIdLong);
				Map<Long, BlurSettingBO> result = testService.readBlurSettings(albumId, pictureId, userId);
				Assert.assertNotNull("Should be null", result);
				Assert.assertFalse(result.isEmpty());
				Assert.assertEquals(result.get(pictureIdLong), new BlurSettingBO(albumId, pictureIdLong, userId, false));
				Assert.assertFalse(result.get(pictureIdLong).isBlurred());

				//check cached value
				BlurSettingBO cached = cache.getCachedSetting(albumId, pictureIdLong, userId);
				Assert.assertEquals(cached, result.get(pictureIdLong));


				//  now Lets  UnBlur not blurred album!  We should got error!! SO
				try {
					testService.unBlurAlbum(albumId);
					Assert.fail("Album is not Blurred ! there is  nothing to unBlur :))");
				} catch (Exception e) {
					Assert.assertTrue(e instanceof AlbumIsNotBlurredException);
				}

				//  now Lets  UnBlur not blurred album!  We should got error!! SO
				try {
					testService.unBlurAlbum(albumId, userId);
					Assert.fail("Album is not Blurred ! there is  nothing to unBlur :))");
				} catch (Exception e) {
					Assert.assertTrue(e instanceof AlbumIsNotBlurredException);
				}

				//try to UnBlur  not blurred Picture!!!
				try {
					testService.unBlurPicture(albumId, pictureIdLong);
					Assert.fail("Picture is not Blurred ! there is  nothing to unBlur :))");
				} catch (Exception e) {
					Assert.assertTrue(e instanceof PictureIsNotBlurredException);
				}

				//try to UnBlur  not blurred Picture!!!
				try {
					testService.unBlurPicture(albumId, pictureIdLong, userId);
					Assert.fail("Picture is not Blurred ! there is  nothing to unBlur :))");
				} catch (Exception e) {
					Assert.assertTrue(e instanceof PictureIsNotBlurredException);
				}


				//Now lets  blur  album!   - and Try to Blur  Picture!!!
				testService.blurAlbum(albumId, userId);
				//try to blur  already blurred Picture!!!
				try {
					testService.blurPicture(albumId, pictureIdLong, userId);
					Assert.fail("Picture is Blurred ! there is  nothing to blur :))");
				} catch (Exception e) {
					Assert.assertTrue(e instanceof PictureIsBlurredException);
				}


				Map<Long, BlurSettingBO> settingMap = testService.readBlurSettings(albumId, pictureId, userId);
				BlurSettingBO setting = settingMap.get(pictureIdLong);
				Assert.assertNotNull(setting);
				Assert.assertEquals(albumId, setting.getAlbumId());
				Assert.assertEquals(BlurSettingBO.ALL_ALBUM_PICTURES_DEFAULT_CONSTANT, setting.getPictureId());
				Assert.assertEquals(userId, setting.getUserId());
				Assert.assertTrue("Should be blurred!", setting.isBlurred());

				//LEt's  unblur  picture for selected User
				testService.unBlurPicture(albumId, pictureIdLong, userId);
				settingMap = testService.readBlurSettings(albumId, pictureId, userId);
				setting = settingMap.get(pictureIdLong);
				Assert.assertNotNull(setting);
				Assert.assertEquals(albumId, setting.getAlbumId());
				Assert.assertEquals(pictureIdLong, setting.getPictureId());
				Assert.assertEquals(userId, setting.getUserId());
				Assert.assertFalse("Should not be blurred!", setting.isBlurred());


				//Now  let's blur album for all users!
				testService.blurAlbum(albumId);
				//checking ! - that   all other  settings  were  removed!
				settingMap = testService.readBlurSettings(albumId, pictureId, userId);
				setting = settingMap.get(pictureIdLong);
				Assert.assertNotNull(setting);
				Assert.assertEquals(albumId, setting.getAlbumId());
				Assert.assertEquals(BlurSettingBO.ALL_ALBUM_PICTURES_DEFAULT_CONSTANT, setting.getPictureId());
				Assert.assertEquals(BlurSettingBO.ALL_USERS_DEFAULT_CONSTANT, setting.getUserId());
				Assert.assertTrue("Should be blurred!", setting.isBlurred());


				//unblure picture  for all users!! NOW!
				testService.unBlurPicture(albumId, pictureIdLong);
				//checking!
				settingMap = testService.readBlurSettings(albumId, pictureId, userId);
				setting = settingMap.get(pictureIdLong);
				Assert.assertEquals(albumId, setting.getAlbumId());
				Assert.assertEquals(pictureIdLong, setting.getPictureId());
				Assert.assertEquals(BlurSettingBO.ALL_USERS_DEFAULT_CONSTANT, setting.getUserId());
				Assert.assertFalse("Should not be blurred!", setting.isBlurred());
				//check cached value
				cached = cache.getCachedSetting(albumId, pictureIdLong, userId);
				Assert.assertEquals(cached, setting);


				//  Blurring  this  picture now!  for 1 user
				testService.blurPicture(albumId, pictureIdLong, userId);

				settingMap = testService.readBlurSettings(albumId, pictureId, userId);
				setting = settingMap.get(pictureIdLong);
				Assert.assertNotNull(setting);
				Assert.assertEquals(albumId, setting.getAlbumId());
				Assert.assertEquals(pictureIdLong, setting.getPictureId());
				Assert.assertEquals(userId, setting.getUserId());
				Assert.assertTrue("Should not be blurred!", setting.isBlurred());
				//check cached value
				cached = cache.getCachedSetting(albumId, pictureIdLong, userId);
				Assert.assertEquals(cached, setting);


				//reblurring  for all users!
				testService.blurPicture(albumId, pictureIdLong);
				settingMap = testService.readBlurSettings(albumId, pictureId, "100000");
				setting = settingMap.get(pictureIdLong);
				Assert.assertNotNull(setting);
				Assert.assertEquals(albumId, setting.getAlbumId());
				Assert.assertEquals(pictureIdLong, setting.getPictureId());
				Assert.assertEquals(BlurSettingBO.ALL_USERS_DEFAULT_CONSTANT, setting.getUserId());
				Assert.assertTrue("Should not be blurred!", setting.isBlurred());
				//check cached value
				cached = cache.getCachedSetting(albumId, pictureIdLong, "100000");
				Assert.assertEquals(cached, setting);


			} catch (BlurSettingsServiceException e) {
				Assert.fail("Can't happen!");
			}

		} catch (MetaFactoryException e) {
			Assert.fail("MetaFactory error");
		}
	}
}
