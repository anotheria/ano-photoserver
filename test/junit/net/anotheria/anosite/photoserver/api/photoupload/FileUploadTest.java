package net.anotheria.anosite.photoserver.api.photoupload;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import junit.framework.Assert;
import net.anotheria.anoplass.api.APIException;
import net.anotheria.anoplass.api.APIFinder;
import net.anotheria.anoplass.api.generic.login.LoginAPI;
import net.anotheria.anosite.photoserver.TestingContextInitializer;
import net.anotheria.anosite.photoserver.api.upload.PhotoUploadAPI;
import net.anotheria.anosite.photoserver.api.upload.PhotoUploader;

import org.junit.BeforeClass;
import org.junit.Test;


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
	
	@BeforeClass
	public static void deInit() {
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
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
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
		
		MockHttpServletRequest request = new MockHttpServletRequest(out.toByteArray(), CONTENT_TYPE);
	
		PhotoUploader uploader = photoUploadAPI.createMyPhotoUploader();
		uploader.doUpload(request);
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

}
