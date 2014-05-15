package net.anotheria.anosite.photoserver.service.storage;

import java.io.File;

import net.anotheria.anosite.photoserver.shared.vo.PhotoVO;

import org.configureme.ConfigurationManager;
import org.configureme.environments.DynamicEnvironment;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Storage utility test.
 * 
 * @author Alexandr Bolbat
 */
public class StorageUtilTest {

	@BeforeClass
	public static void init() {
		ConfigurationManager.INSTANCE.setDefaultEnvironment(new DynamicEnvironment("test"));
	}

	@Test
	public void complexTest() {
		// TODO: write me
	}

	@Test
	public void errorsTest() {
		// check wrong photo information
		try {
			StorageUtil.getPhoto(null);
			Assert.fail();
		} catch (IllegalArgumentException e) {
		} catch (StorageUtilException e) {
			Assert.fail();
		}

		// check wrong file
		try {
			StorageUtil.writePhoto(null, new PhotoVO(), true);
			Assert.fail();
		} catch (IllegalArgumentException e) {
		} catch (StorageUtilException e) {
			Assert.fail();
		}

		// check wrong photo file location
		try {
			StorageUtil.writePhoto(new File(File.separator), new PhotoVO(), true);
			Assert.fail();
		} catch (IllegalArgumentException e) {
		} catch (StorageUtilException e) {
			Assert.fail();
		}
	}

}
