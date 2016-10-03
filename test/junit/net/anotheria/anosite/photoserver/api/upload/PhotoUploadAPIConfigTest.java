package net.anotheria.anosite.photoserver.api.upload;

/**
 * JUnit test for {@link PhotoUploadAPIConfig}.
 *
 * @author Illya Bogatyrchuk
 */
public class PhotoUploadAPIConfigTest {
/*
	@Test
	public void testConfigWithDefaultPhotoTypeConfig() throws Exception {
		final PhotoUploadAPIConfig config = PhotoUploadAPIConfig.getInstance();
		assertNotNull(config);

		assertEquals("JPEG", config.getImageWriteFormat());
		assertEquals(".JPEG", config.getFilePrefix());
		//assertNotNull(config.getPhotoTypes());
		//assertEquals(2, config.getPhotoTypes().length);

		final PhotoTypeConfig defaultPhotoTypeConfig = config.resolvePhotoTypeConfig(null);
		assertNotNull(defaultPhotoTypeConfig);
		assertEquals(10485760, defaultPhotoTypeConfig.getMaxUploadFileSize());
		assertEquals(1024, defaultPhotoTypeConfig.getMaxWidth());
		assertEquals(1024, defaultPhotoTypeConfig.getMaxHeight());
		assertEquals(400, defaultPhotoTypeConfig.getWorkbenchWidth());
		assertEquals("image/pjpeg,image/jpeg,image/tiff,image/png,image/gif,image/x-png", defaultPhotoTypeConfig.getAllowedMimeTypes());
		assertEquals(85, defaultPhotoTypeConfig.getJpegQuality());
		assertEquals(false, defaultPhotoTypeConfig.isAllowTransparentBackground());
		assertEquals(0, defaultPhotoTypeConfig.getMinWidth());
		assertEquals(0, defaultPhotoTypeConfig.getMinHeight());
	}

	@Test
	public void testConfigWithCustomPhotoTypeConfig(){
		final PhotoUploadAPIConfig config = PhotoUploadAPIConfig.getInstance();
		assertNotNull(config);

		assertEquals("JPEG", config.getImageWriteFormat());
		assertEquals(".JPEG", config.getFilePrefix());
		assertNotNull(config.getPhotoTypes());
		assertEquals(2, config.getPhotoTypes().length);

		final PhotoTypeConfig privatePhotoConfig = config.resolvePhotoTypeConfig("privatePhoto");
		assertNotNull(privatePhotoConfig);
		assertEquals(2097152, privatePhotoConfig.getMaxUploadFileSize());
		assertEquals(1200, privatePhotoConfig.getMaxWidth());
		assertEquals(800, privatePhotoConfig.getMaxHeight());
		assertEquals(450, privatePhotoConfig.getWorkbenchWidth());
		assertEquals("image/jpeg", privatePhotoConfig.getAllowedMimeTypes());
		assertEquals(100, privatePhotoConfig.getJpegQuality());
		assertEquals(true, privatePhotoConfig.isAllowTransparentBackground());
		assertEquals(100, privatePhotoConfig.getMinWidth());
		assertEquals(50, privatePhotoConfig.getMinHeight());

		final PhotoTypeConfig coverPhotoConfig = config.resolvePhotoTypeConfig("coverPhoto");
		assertNotNull(coverPhotoConfig);
		assertEquals(6291456, coverPhotoConfig.getMaxUploadFileSize());
		assertEquals(2560, coverPhotoConfig.getMaxWidth());
		assertEquals(1440, coverPhotoConfig.getMaxHeight());
		assertEquals(355, coverPhotoConfig.getWorkbenchWidth());
		assertEquals("image/jpeg,image/png,image/gif,image/jpg", coverPhotoConfig.getAllowedMimeTypes());
		assertEquals(90, coverPhotoConfig.getJpegQuality());
		assertEquals(false, coverPhotoConfig.isAllowTransparentBackground());
		assertEquals(500, coverPhotoConfig.getMinWidth());
		assertEquals(200, coverPhotoConfig.getMinHeight());
	}
	*/
}
