package net.anotheria.anosite.photoserver.api.blur;

import net.anotheria.anoplass.api.APICallContext;
import net.anotheria.anoplass.api.APIFinder;
import net.anotheria.anosite.photoserver.TestingContextInitializer;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * BlurSettingsAPI junit test.
 *
 * @author h3ll
 */
public class BlurSettingsAPITest {


	@BeforeClass
	public static void before() {
		TestingContextInitializer.deInit();
		TestingContextInitializer.init();
	}

	@AfterClass
	public static void after() {
		TestingContextInitializer.deInit();
	}

	/**
	 * Errors test.
	 */
	@Test
	public void testRuntimeExceptions() {

		BlurSettingsAPI testAPI = APIFinder.findAPI(BlurSettingsAPI.class);


		try {
			testAPI.readMyBlurSettings(-1, 1);
			Assert.fail("Invalid album ID");
		} catch (Exception e) {
			Assert.assertTrue(e instanceof IllegalArgumentException);
		}
		try {
			testAPI.readMyBlurSettings(1, -1);
			Assert.fail("Invalid picture ID");
		} catch (Exception e) {
			Assert.assertTrue(e instanceof IllegalArgumentException);
		}

		try {
			testAPI.readMyBlurSettings(1, null);
			Assert.fail("Invalid picture ID");
		} catch (Exception e) {
			Assert.assertTrue(e instanceof IllegalArgumentException);
		}
		try {
			List<Long> id = new ArrayList<Long>();
			testAPI.readMyBlurSettings(1, id);
			Assert.fail("Invalid picture ID");
		} catch (Exception e) {
			Assert.assertTrue(e instanceof IllegalArgumentException);
		}
		try {
			List<Long> id = new ArrayList<Long>();
			id.add(-1l);
			testAPI.readMyBlurSettings(1, id);
			Assert.fail("Invalid picture ID");
		} catch (Exception e) {
			Assert.assertTrue(e instanceof IllegalArgumentException);
		}


		try {
			testAPI.blurAlbum(-1);
			Assert.fail("Invalid album ID");
		} catch (Exception e) {
			Assert.assertTrue(e instanceof IllegalArgumentException);
		}
		try {
			testAPI.unBlurAlbum(-1);
			Assert.fail("Invalid album ID");
		} catch (Exception e) {
			Assert.assertTrue(e instanceof IllegalArgumentException);
		}

		try {
			testAPI.blurAlbum(-1, 1+"");
			Assert.fail("Invalid album ID");
		} catch (Exception e) {
			Assert.assertTrue(e instanceof IllegalArgumentException);
		}

		try {
			testAPI.blurAlbum(1, null);
			Assert.fail("Invalid user ID");
		} catch (Exception e) {
			Assert.assertTrue(e instanceof IllegalArgumentException);
		}
		try {
			testAPI.unBlurAlbum(-1, 11+"");
			Assert.fail("Invalid album ID");
		} catch (Exception e) {
			Assert.assertTrue(e instanceof IllegalArgumentException);
		}
		try {
			testAPI.unBlurAlbum(1, "");
			Assert.fail("Invalid user ID");
		} catch (Exception e) {
			Assert.assertTrue(e instanceof IllegalArgumentException);
		}

		try {
			testAPI.blurPicture(1, -1);
			Assert.fail("Invalid picture ID");
		} catch (Exception e) {
			Assert.assertTrue(e instanceof IllegalArgumentException);
		}
		try {
			testAPI.blurPicture(-1, 1);
			Assert.fail("Invalid album ID");
		} catch (Exception e) {
			Assert.assertTrue(e instanceof IllegalArgumentException);
		}
		try {
			testAPI.blurUserPicture(1, -1);
			Assert.fail("Invalid picture ID");
		} catch (Exception e) {
			Assert.assertTrue(e instanceof IllegalArgumentException);
		}
		try {
			testAPI.blurUserPicture(-1, 1);
			Assert.fail("Invalid album ID");
		} catch (Exception e) {
			Assert.assertTrue(e instanceof IllegalArgumentException);
		}
		try {
			testAPI.unBlurPicture(1, -1);
			Assert.fail("Invalid picture ID");
		} catch (Exception e) {
			Assert.assertTrue(e instanceof IllegalArgumentException);
		}
		try {
			testAPI.unBlurPicture(-1, 1);
			Assert.fail("Invalid album ID");
		} catch (Exception e) {
			Assert.assertTrue(e instanceof IllegalArgumentException);
		}


		//
		try {
			testAPI.blurPicture(1, -1, 1+"");
			Assert.fail("Invalid picture ID");
		} catch (Exception e) {
			Assert.assertTrue(e instanceof IllegalArgumentException);
		}
		try {
			testAPI.blurPicture(-1, 1, 1+"");
			Assert.fail("Invalid album ID");
		} catch (Exception e) {
			Assert.assertTrue(e instanceof IllegalArgumentException);
		}
		try {
			testAPI.blurPicture(1, 1,null);
			Assert.fail("Invalid user ID");
		} catch (Exception e) {
			Assert.assertTrue(e instanceof IllegalArgumentException);
		}
		try {
			testAPI.unBlurPicture(1, -1, 1+"");
			Assert.fail("Invalid picture ID");
		} catch (Exception e) {
			Assert.assertTrue(e instanceof IllegalArgumentException);
		}
		try {
			testAPI.unBlurPicture(-1, 1, 1+"");
			Assert.fail("Invalid album ID");
		} catch (Exception e) {
			Assert.assertTrue(e instanceof IllegalArgumentException);
		}
		try {
			testAPI.unBlurPicture(1, 1, "");
			Assert.fail("Invalid user ID");
		} catch (Exception e) {
			Assert.assertTrue(e instanceof IllegalArgumentException);
		}


		try {
			testAPI.removeBlurSettings(-1);
			Assert.fail("Invalid album ID");
		} catch (Exception e) {
			Assert.assertTrue(e instanceof IllegalArgumentException);
		}

	}

	@Test
	public void testCheckedNotLoggedInExceptions() {
		BlurSettingsAPI testAPI = APIFinder.findAPI(BlurSettingsAPI.class);
		//not logged IN
		APICallContext.getCallContext().setCurrentUserId(null);


		try {
			testAPI.blurAlbum(1);
			Assert.fail("Not logged in");
		} catch (BlurSettingsAPIException e) {
			//skip
		}
		try {
			testAPI.unBlurAlbum(1);
			Assert.fail("Not logged in");
		} catch (BlurSettingsAPIException e) {
			//skip
		}
		try {
			testAPI.blurAlbum(1, 1+"");
			Assert.fail("Not logged in");
		} catch (BlurSettingsAPIException e) {
			//skip
		}
		try {
			APICallContext.getCallContext().setCurrentUserId("1");
			testAPI.blurAlbum(1, 1+"");
			Assert.fail("Can't blur Own pictures for Self!");

		} catch (BlurSettingsAPIException e) {
			APICallContext.getCallContext().setCurrentUserId(null);
		}

		try {
			testAPI.unBlurAlbum(1, 1+"");
			Assert.fail("Not logged in");
		} catch (BlurSettingsAPIException e) {
			//skip
		}
		try {
			APICallContext.getCallContext().setCurrentUserId("1");
			testAPI.unBlurAlbum(1, 1+"");
			Assert.fail("Can't unBlur Own pictures for Self!");

		} catch (BlurSettingsAPIException e) {
			APICallContext.getCallContext().setCurrentUserId(null);
		}
		try {
			testAPI.removeBlurSettings(1);
			Assert.fail("Not logged in");
		} catch (BlurSettingsAPIException e) {
			//skip
		}


		try {
			testAPI.blurPicture(1, 1);
			Assert.fail("Not logged in");
		} catch (BlurSettingsAPIException e) {
			//skip
		}
		try {
			testAPI.unBlurPicture(1, 1);
			Assert.fail("Not logged in");
		} catch (BlurSettingsAPIException e) {
			//skip
		}


		try {
			testAPI.blurPicture(1, 1, 1+"");
			Assert.fail("Not logged in");
		} catch (BlurSettingsAPIException e) {
			//skip
		}
		try {
			testAPI.unBlurPicture(1, 1, 1+"");
			Assert.fail("Not logged in");
		} catch (BlurSettingsAPIException e) {
			//skip
		}


		try {
			APICallContext.getCallContext().setCurrentUserId("1");
			testAPI.blurPicture(1, 1, 1+"");
			Assert.fail("Can't unBlur Own pictures for Self!");
		} catch (BlurSettingsAPIException e) {
			APICallContext.getCallContext().setCurrentUserId(null);
		}
		try {
			APICallContext.getCallContext().setCurrentUserId("1");
			testAPI.unBlurPicture(1, 1, 1+"");
			Assert.fail("Can't unBlur Own pictures for Self!");
		} catch (BlurSettingsAPIException e) {
			APICallContext.getCallContext().setCurrentUserId(null);
		}


	}


	@Test
	public void testBlurFunctionalFlow() {
		//My ID! I'm loggged IN
		APICallContext.getCallContext().setCurrentUserId("1100");
		BlurSettingsAPI testApi = APIFinder.findAPI(BlurSettingsAPI.class);

		final long albumId = 1l;
		final long pictureId = 1;
		final String userId = 100+"";

		//trying to  get not blurred value
		try {
			boolean result = testApi.readMyBlurSettings(albumId, pictureId);
			Assert.assertFalse("Should not be blurred", result);

		} catch (BlurSettingsAPIException e) {
			Assert.fail("Should not happen");
		}
		//####UnBlur  not Blurred Stuff START ####
		try {
			testApi.unBlurAlbum(albumId);
			Assert.fail("Album is nOT  blurred ! Error");
		} catch (BlurSettingsAPIException e) {
			Assert.assertTrue(e instanceof AlbumIsNotBlurredAPIException);
		}
		try {
			testApi.unBlurAlbum(albumId, userId);
			Assert.fail("Album is nOT  blurred ! Error");
		} catch (BlurSettingsAPIException e) {
			Assert.assertTrue(e instanceof AlbumIsNotBlurredAPIException);
		}
		try {
			testApi.unBlurPicture(albumId, pictureId, userId);
			Assert.fail("Album is nOT  blurred ! Error");
		} catch (BlurSettingsAPIException e) {
			Assert.assertTrue(e instanceof PictureIsNotBlurredAPIException);
		}
		try {
			testApi.unBlurPicture(albumId, pictureId);
			Assert.fail("Album is nOT  blurred ! Error");
		} catch (BlurSettingsAPIException e) {
			Assert.assertTrue(e instanceof PictureIsNotBlurredAPIException);
		}

		//####UnBlur  not Blurred Stuff END ####


		//#### Blur not Blurred Stuff START ####
		try {
			testApi.blurAlbum(albumId);
			//checking that it's blurred
			boolean result = testApi.readMyBlurSettings(albumId, pictureId);
			Assert.assertTrue("Should  be blurred", result);

			try {
				testApi.blurAlbum(albumId, userId);
				Assert.fail("Already blurred");
			} catch (Exception e) {
				Assert.assertTrue(e instanceof AlbumIsBlurredAPIException);
			}
			try {
				testApi.blurAlbum(albumId);
				Assert.fail("Already blurred");
			} catch (Exception e) {
				Assert.assertTrue(e instanceof AlbumIsBlurredAPIException);
			}

			try {
				testApi.blurPicture(albumId, pictureId);
				Assert.fail("Already blurred");
			} catch (Exception e) {
				Assert.assertTrue(e instanceof PictureIsBlurredAPIException);
			}
			try {
				testApi.blurPicture(albumId, pictureId, userId);
				Assert.fail("Already blurred");
			} catch (Exception e) {
				Assert.assertTrue(e instanceof PictureIsBlurredAPIException);
			}


		} catch (BlurSettingsAPIException e) {
			Assert.fail("Error");
		}
		//#### Blur not Blurred Stuff END ####


		// Unblurring  picture  in Blurred Album now!

		// for selected User! so
		try {
			testApi.unBlurPicture(albumId, pictureId, userId);
			APICallContext.getCallContext().setCurrentUserId(userId + "");
			boolean result = testApi.readMyBlurSettings(albumId, pictureId);
			Assert.assertFalse("Should not be blurred", result);
			APICallContext.getCallContext().setCurrentUserId("1100");

			//remove!
			testApi.removeBlurSettings(albumId);
			result = testApi.readMyBlurSettings(albumId, pictureId);
			Assert.assertFalse("Should not be blurred", result);


		} catch (BlurSettingsAPIException e) {
			Assert.fail("Error");
		}


		//reading for  Pictures List!
		List<Long> ids = new ArrayList<Long>(2);
		ids.add(100l);
		ids.add(123l);
		try {
			Map<Long, Boolean> map = testApi.readMyBlurSettings(100, ids);
			for (Long key : map.keySet()) {
				Assert.assertFalse(map.get(key));
			}
		} catch (BlurSettingsAPIException e) {
			Assert.fail("errors");
		}
	}

}
