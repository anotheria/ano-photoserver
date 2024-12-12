package net.anotheria.anosite.photoserver.api.blur;

import net.anotheria.anoplass.api.APICallContext;
import net.anotheria.anoplass.api.generic.login.LoginAPI;
import net.anotheria.anosite.photoserver.api.photo.AlbumAO;
import net.anotheria.anosite.photoserver.api.photo.PhotoAPI;
import net.anotheria.anosite.photoserver.service.blur.AlbumIsNotBlurredException;
import net.anotheria.anosite.photoserver.service.blur.BlurSettingsService;
import net.anotheria.anosite.photoserver.service.blur.persistence.AlbumIsNotBlurredPersistenceException;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;

/**
 * BlurSettingsAPI junit test.
 *
 * @author h3ll
 */
@RunWith(MockitoJUnitRunner.class)
public class BlurSettingsAPITest {


	@InjectMocks
	private BlurSettingsAPIImpl blurSettingsAPI;

	@Mock
	private BlurSettingsService blurSettingsService;

	@Mock
	private LoginAPI loginAPI;

	@Mock
	private PhotoAPI photoAPI;

	/**
	 * Errors test.
	 */
	@Test
	public void testRuntimeExceptions() {
		try {
			blurSettingsAPI.readMyBlurSettings(-1, 1);
			Assert.fail("Invalid album ID");
		} catch (Exception e) {
			Assert.assertTrue(e instanceof IllegalArgumentException);
		}

		try {
			blurSettingsAPI.readMyBlurSettings(1, null);
			Assert.fail("Invalid picture ID");
		} catch (Exception e) {
			Assert.assertTrue(e instanceof IllegalArgumentException);
		}
		try {
			List<Long> id = new ArrayList<Long>();
			blurSettingsAPI.readMyBlurSettings(1, id);
			Assert.fail("Invalid picture ID");
		} catch (Exception e) {
			Assert.assertTrue(e instanceof IllegalArgumentException);
		}

		try {
			blurSettingsAPI.blurAlbum(-1);
			Assert.fail("Invalid album ID");
		} catch (Exception e) {
			Assert.assertTrue(e instanceof IllegalArgumentException);
		}
		try {
			blurSettingsAPI.unBlurAlbum(-1);
			Assert.fail("Invalid album ID");
		} catch (Exception e) {
			Assert.assertTrue(e instanceof IllegalArgumentException);
		}

		try {
			blurSettingsAPI.blurAlbum(-1, 1+"");
			Assert.fail("Invalid album ID");
		} catch (Exception e) {
			Assert.assertTrue(e instanceof IllegalArgumentException);
		}

		try {
			blurSettingsAPI.blurAlbum(1, null);
			Assert.fail("Invalid user ID");
		} catch (Exception e) {
			Assert.assertTrue(e instanceof IllegalArgumentException);
		}
		try {
			blurSettingsAPI.unBlurAlbum(-1, 11+"");
			Assert.fail("Invalid album ID");
		} catch (Exception e) {
			Assert.assertTrue(e instanceof IllegalArgumentException);
		}
		try {
			blurSettingsAPI.unBlurAlbum(1, "");
			Assert.fail("Invalid user ID");
		} catch (Exception e) {
			Assert.assertTrue(e instanceof IllegalArgumentException);
		}

		try {
			blurSettingsAPI.blurPicture(1, -1);
			Assert.fail("Invalid picture ID");
		} catch (Exception e) {
			Assert.assertTrue(e instanceof IllegalArgumentException);
		}
		try {
			blurSettingsAPI.blurPicture(-1, 1);
			Assert.fail("Invalid album ID");
		} catch (Exception e) {
			Assert.assertTrue(e instanceof IllegalArgumentException);
		}
		try {
			blurSettingsAPI.blurUserPicture(1, -1);
			Assert.fail("Invalid picture ID");
		} catch (Exception e) {
			Assert.assertTrue(e instanceof IllegalArgumentException);
		}
		try {
			blurSettingsAPI.blurUserPicture(-1, 1);
			Assert.fail("Invalid album ID");
		} catch (Exception e) {
			Assert.assertTrue(e instanceof IllegalArgumentException);
		}
		try {
			blurSettingsAPI.unBlurPicture(1, -1);
			Assert.fail("Invalid picture ID");
		} catch (Exception e) {
			Assert.assertTrue(e instanceof IllegalArgumentException);
		}
		try {
			blurSettingsAPI.unBlurPicture(-1, 1);
			Assert.fail("Invalid album ID");
		} catch (Exception e) {
			Assert.assertTrue(e instanceof IllegalArgumentException);
		}


		//
		try {
			blurSettingsAPI.blurPicture(1, -1, 1+"");
			Assert.fail("Invalid picture ID");
		} catch (Exception e) {
			Assert.assertTrue(e instanceof IllegalArgumentException);
		}
		try {
			blurSettingsAPI.blurPicture(-1, 1, 1+"");
			Assert.fail("Invalid album ID");
		} catch (Exception e) {
			Assert.assertTrue(e instanceof IllegalArgumentException);
		}
		try {
			blurSettingsAPI.blurPicture(1, 1,null);
			Assert.fail("Invalid user ID");
		} catch (Exception e) {
			Assert.assertTrue(e instanceof IllegalArgumentException);
		}
		try {
			blurSettingsAPI.unBlurPicture(1, -1, 1+"");
			Assert.fail("Invalid picture ID");
		} catch (Exception e) {
			Assert.assertTrue(e instanceof IllegalArgumentException);
		}
		try {
			blurSettingsAPI.unBlurPicture(-1, 1, 1+"");
			Assert.fail("Invalid album ID");
		} catch (Exception e) {
			Assert.assertTrue(e instanceof IllegalArgumentException);
		}
		try {
			blurSettingsAPI.unBlurPicture(1, 1, "");
			Assert.fail("Invalid user ID");
		} catch (Exception e) {
			Assert.assertTrue(e instanceof IllegalArgumentException);
		}


		try {
			blurSettingsAPI.removeBlurSettings(-1);
			Assert.fail("Invalid album ID");
		} catch (Exception e) {
			Assert.assertTrue(e instanceof IllegalArgumentException);
		}

	}

	@Test
	public void testCheckedNotLoggedInExceptions() {
		APICallContext.getCallContext().setCurrentUserId(null);
		try {
			blurSettingsAPI.blurAlbum(1);
			Assert.fail("Not logged in");
		} catch (BlurSettingsAPIException e) {
			//skip
		}
		try {
			blurSettingsAPI.unBlurAlbum(1);
			Assert.fail("Not logged in");
		} catch (BlurSettingsAPIException e) {
			//skip
		}
		try {
			blurSettingsAPI.blurAlbum(1, 1+"");
			Assert.fail("Not logged in");
		} catch (BlurSettingsAPIException e) {
			//skip
		}
		try {
			APICallContext.getCallContext().setCurrentUserId("1");
			blurSettingsAPI.blurAlbum(1, 1+"");
			Assert.fail("Can't blur Own pictures for Self!");

		} catch (BlurSettingsAPIException e) {
			APICallContext.getCallContext().setCurrentUserId(null);
		}

		try {
			blurSettingsAPI.unBlurAlbum(1, 1+"");
			Assert.fail("Not logged in");
		} catch (BlurSettingsAPIException e) {
			//skip
		}
		try {
			APICallContext.getCallContext().setCurrentUserId("1");
			blurSettingsAPI.unBlurAlbum(1, 1+"");
			Assert.fail("Can't unBlur Own pictures for Self!");

		} catch (BlurSettingsAPIException e) {
			APICallContext.getCallContext().setCurrentUserId(null);
		}
		try {
			blurSettingsAPI.removeBlurSettings(1);
			Assert.fail("Not logged in");
		} catch (BlurSettingsAPIException e) {
			//skip
		}


		try {
			blurSettingsAPI.blurPicture(1, 1);
			Assert.fail("Not logged in");
		} catch (BlurSettingsAPIException e) {
			//skip
		}
		try {
			blurSettingsAPI.unBlurPicture(1, 1);
			Assert.fail("Not logged in");
		} catch (BlurSettingsAPIException e) {
			//skip
		}


		try {
			blurSettingsAPI.blurPicture(1, 1, 1+"");
			Assert.fail("Not logged in");
		} catch (BlurSettingsAPIException e) {
			//skip
		}
		try {
			blurSettingsAPI.unBlurPicture(1, 1, 1+"");
			Assert.fail("Not logged in");
		} catch (BlurSettingsAPIException e) {
			//skip
		}


		try {
			APICallContext.getCallContext().setCurrentUserId("1");
			blurSettingsAPI.blurPicture(1, 1, 1+"");
			Assert.fail("Can't unBlur Own pictures for Self!");
		} catch (BlurSettingsAPIException e) {
			APICallContext.getCallContext().setCurrentUserId(null);
		}
		try {
			APICallContext.getCallContext().setCurrentUserId("1");
			blurSettingsAPI.unBlurPicture(1, 1, 1+"");
			Assert.fail("Can't unBlur Own pictures for Self!");
		} catch (BlurSettingsAPIException e) {
			APICallContext.getCallContext().setCurrentUserId(null);
		}
	}

	@Test
	public void testBlurAlbumPicture() throws Exception {
		String userIdStr = "1100";
		final long albumId = 1l;
		final long pictureId = 1;
		try {
			APICallContext.getCallContext().setCurrentUserId(userIdStr);
			AlbumAO albumAO = new AlbumAO();
			albumAO.setId(albumId);
			albumAO.setUserId(userIdStr);
			when(loginAPI.isLogedIn()).thenReturn(true);
			when(photoAPI.getAlbumOwnerId(albumId)).thenReturn(albumAO.getUserId());
			boolean result = blurSettingsAPI.readMyBlurSettings(albumId, pictureId);
			verify(loginAPI, atLeastOnce()).isLogedIn();
			verify(photoAPI, atLeastOnce()).getAlbumOwnerId(albumId);
			Assert.assertFalse("Should not be blurred", result);

		} catch (BlurSettingsAPIException e) {
			Assert.fail("Should not happen");
		}
	}

	@Test
	public void testUnblurAlbum() throws Exception {
		long albumId = 1L;
		try {
			when(loginAPI.isLogedIn()).thenReturn(true);
			doThrow(new AlbumIsNotBlurredException(albumId, new AlbumIsNotBlurredPersistenceException(albumId))).when(blurSettingsService).unBlurAlbum(albumId);
			blurSettingsAPI.unBlurAlbum(albumId);
			verify(loginAPI, atLeastOnce()).isLogedIn();
			verify(blurSettingsService, atLeastOnce()).unBlurAlbum(albumId);
			Assert.fail("Album is nOT  blurred ! Error");
		} catch (BlurSettingsAPIException e) {
			Assert.assertTrue(e instanceof AlbumIsNotBlurredAPIException);
		}
	}

	@Test
	public void testUnblurAlbumUserId() throws Exception {
		long albumId = 1L;
		try {
			APICallContext.getCallContext().setCurrentUserId("1100");
			doThrow(new AlbumIsNotBlurredException(albumId, new AlbumIsNotBlurredPersistenceException(albumId))).when(blurSettingsService).unBlurAlbum(albumId, "1101");
			blurSettingsAPI.unBlurAlbum(albumId, "1101");
			verify(blurSettingsService, atLeastOnce()).unBlurAlbum(albumId);
			Assert.fail("Album is nOT  blurred ! Error");
		} catch (BlurSettingsAPIException e) {
			Assert.assertTrue(e instanceof AlbumIsNotBlurredAPIException);
		}
	}

	@Ignore
	@Test
	public void testBlurFunctionalFlow() throws Exception {
		//My ID! I'm loggged IN
		String userIdStr = "1100";

		final long albumId = 1l;
		final long pictureId = 1;
		final String userId = 100+"";

		//trying to  get not blurred value

		//####UnBlur  not Blurred Stuff START ####

		try {
			blurSettingsAPI.unBlurPicture(albumId, pictureId, userId);
			Assert.fail("Album is nOT  blurred ! Error");
		} catch (BlurSettingsAPIException e) {
			Assert.assertTrue(e instanceof PictureIsNotBlurredAPIException);
		}
		try {
			blurSettingsAPI.unBlurPicture(albumId, pictureId);
			Assert.fail("Album is nOT  blurred ! Error");
		} catch (BlurSettingsAPIException e) {
			Assert.assertTrue(e instanceof PictureIsNotBlurredAPIException);
		}

		//####UnBlur  not Blurred Stuff END ####


		//#### Blur not Blurred Stuff START ####
		try {
			blurSettingsAPI.blurAlbum(albumId);
			//checking that it's blurred
			boolean result = blurSettingsAPI.readMyBlurSettings(albumId, pictureId);
			Assert.assertTrue("Should  be blurred", result);

			try {
				blurSettingsAPI.blurAlbum(albumId, userId);
				Assert.fail("Already blurred");
			} catch (Exception e) {
				Assert.assertTrue(e instanceof AlbumIsBlurredAPIException);
			}
			try {
				blurSettingsAPI.blurAlbum(albumId);
				Assert.fail("Already blurred");
			} catch (Exception e) {
				Assert.assertTrue(e instanceof AlbumIsBlurredAPIException);
			}

			try {
				blurSettingsAPI.blurPicture(albumId, pictureId);
				Assert.fail("Already blurred");
			} catch (Exception e) {
				Assert.assertTrue(e instanceof PictureIsBlurredAPIException);
			}
			try {
				blurSettingsAPI.blurPicture(albumId, pictureId, userId);
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
			blurSettingsAPI.unBlurPicture(albumId, pictureId, userId);
			APICallContext.getCallContext().setCurrentUserId(userId + "");
			boolean result = blurSettingsAPI.readMyBlurSettings(albumId, pictureId);
			Assert.assertFalse("Should not be blurred", result);
			APICallContext.getCallContext().setCurrentUserId("1100");

			//remove!
			blurSettingsAPI.removeBlurSettings(albumId);
			result = blurSettingsAPI.readMyBlurSettings(albumId, pictureId);
			Assert.assertFalse("Should not be blurred", result);


		} catch (BlurSettingsAPIException e) {
			Assert.fail("Error");
		}


		//reading for  Pictures List!
		List<Long> ids = new ArrayList<Long>(2);
		ids.add(100l);
		ids.add(123l);
		try {
			Map<Long, Boolean> map = blurSettingsAPI.readMyBlurSettings(100, ids);
			for (Long key : map.keySet()) {
				Assert.assertFalse(map.get(key));
			}
		} catch (BlurSettingsAPIException e) {
			Assert.fail("errors");
		}
	}

}
