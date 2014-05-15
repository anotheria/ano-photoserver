package net.anotheria.anosite.photoserver.service.storage;

import java.io.File;

import org.configureme.ConfigurationManager;
import org.configureme.environments.DynamicEnvironment;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class StorageConfigTest {

	private static final String S = File.separator;

	@BeforeClass
	public static void init() {
		ConfigurationManager.INSTANCE.setDefaultEnvironment(new DynamicEnvironment("test"));
	}

	@Test
	public void complexTest() {
		// testing root folder for user's photos
		Assert.assertEquals(S + "work" + S + "data" + S + "test" + S + "photoserver", StorageConfig.getInstance().getStorageRoot());

		// testing root folder for user's temporary photos
		Assert.assertEquals(S + "work" + S + "data" + S + "test" + S + "photoserver" + S + "tmp" + S + "", StorageConfig.getInstance().getTmpStorageRoot());

		// configuring max user id - used in code for generating right storing path
		Assert.assertEquals(10, StorageConfig.getInstance().getMaxOwnerIdLength());

		// configuring fragment length - used in code for generating right storing path
		Assert.assertEquals(2, StorageConfig.getInstance().getFragmentLegth());

		// checking folder path for user for some user id
		Assert.assertEquals("" + S + "work" + S + "data" + S + "test" + S + "photoserver" + S + "00" + S + "00" + S + "00" + S + "01" + S + "00" + S + "",
				StorageConfig.getStoreFolderPath("100"));

		// checking temporary folder path for user for some user id
		Assert.assertEquals("" + S + "work" + S + "data" + S + "test" + S + "photoserver" + S + "tmp" + S + "00" + S + "00" + S + "10" + S + "09" + S + "99"
				+ S + "", StorageConfig.getTmpStoreFolderPath("100999"));

		// checking temporary folder path for user for some user id with replaceable characters
		Assert.assertEquals("" + S + "work" + S + "data" + S + "test" + S + "photoserver" + S + "tmp" + S + "00" + S + "00" + S + "10" + S + "_9" + S + "99"
				+ S + "", StorageConfig.getTmpStoreFolderPath("10-999"));
	}

	@Test
	public void errorsTest() {
		try {
			// null user id
			StorageConfig.getStoreFolderPath(null);
			Assert.fail();
		} catch (IllegalArgumentException e) {
		}

		try {
			// empty user id
			StorageConfig.getStoreFolderPath("");
			Assert.fail();
		} catch (IllegalArgumentException e) {
		}
	}

}
