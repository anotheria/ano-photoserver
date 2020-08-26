package net.anotheria.anosite.photoserver.api.photoupload;

import junit.framework.Assert;
import net.anotheria.anoplass.api.APIException;
import net.anotheria.anoplass.api.APIFinder;
import net.anotheria.anoplass.api.generic.login.LoginAPI;
import net.anotheria.anosite.photoserver.TestingContextInitializer;
import net.anotheria.anosite.photoserver.api.upload.PhotoUploadAPI;
import net.anotheria.anosite.photoserver.api.upload.PhotoUploader;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.stubbing.Answer;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FileUploadTest  {

	protected static final String BOUNDARY = "1fgdb234";

	protected static final String CONTENT_TYPE = "multipart/form-data; boundary="+ BOUNDARY;

	protected static final File GOOD_TEST_FILE = new File("test/appdata/goodTestFile.jpg");

	private static final LoginAPI loginAPI = APIFinder.findAPI(LoginAPI.class);


	@BeforeClass
	public static void init() throws APIException {
		// need do this for clearing context from previous test for be sure about clean current context
		TestingContextInitializer.deInit();
		TestingContextInitializer.init();
		loginAPI.logInUser("1234");
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
	public void addMyPhotoUploadUniqueIdTest() throws APIException {

		PhotoUploadAPI photoUploadAPI = APIFinder.findAPI(PhotoUploadAPI.class);


		PhotoUploader uploadAO1 = photoUploadAPI.createMyPhotoUploader();
		String id1 = uploadAO1.getId();

		PhotoUploader uploadAO2 = photoUploadAPI.createMyPhotoUploader();
		String id2 = uploadAO2.getId();

		Assert.assertFalse(id1.equals(id2));
	}

	@Test
	public void photoUploadTest() throws IOException, APIException {

		if(!GOOD_TEST_FILE.exists()) {
			return;
		}

		PhotoUploadAPI photoUploadAPI = APIFinder.findAPI(PhotoUploadAPI.class);

		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		out.write(getHeader("filename.jpeg").getBytes("US-ASCII"));
		InputStream in = new FileInputStream(GOOD_TEST_FILE);
		byte[] buffer = new byte[8192];
		int len;
		while((len = in.read(buffer)) > 0) {
			out.write(buffer, 0, len);
		}
		out.write(getFooter().getBytes("US-ASCII"));
		out.flush();
		in.close();

		PhotoUploader uploader = photoUploadAPI.createMyPhotoUploader();
		uploader.doUpload(getMockHttpServletRequest(out));
	}

	private String getHeader(String pField) {
        return "--" + BOUNDARY + "\r\n"
            + "Content-Disposition: form-data; name=\"file\"; filename=\"" + pField + "\"\r\n"
            + "Content-Type: image/jpeg\r\n"
            + "Content-Transfer-Encoding: binary\r\n\r\n";
    }

    private String getFooter() {
        return "\r\n--" + BOUNDARY + "--\r\n";
    }

	private HttpServletRequest getMockHttpServletRequest(final ByteArrayOutputStream out) throws IOException {
		final InputStream inputStream = new ByteArrayInputStream(out.toByteArray());

		final ServletInputStream servletInputStream = mock(ServletInputStream.class);
		when(servletInputStream.read()).thenReturn(new ByteArrayInputStream(out.toByteArray()).read());
		when(servletInputStream.read(ArgumentMatchers.any(), anyInt(), anyInt())).then((Answer<Integer>) answer -> inputStream.read(
				answer.getArgument(0),
				answer.getArgument(1),
				answer.getArgument(2)
		));

		final HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getMethod()).thenReturn("post");
		when(request.getContentType()).thenReturn(CONTENT_TYPE);
		when(request.getInputStream()).thenReturn(servletInputStream);
		when(request.getContentLength()).thenReturn(out.toByteArray().length);

		return request;
	}
}

