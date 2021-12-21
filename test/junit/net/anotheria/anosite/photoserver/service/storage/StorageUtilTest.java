package net.anotheria.anosite.photoserver.service.storage;

import java.io.File;

import net.anotheria.anosite.photoserver.TestingContextInitializer;
import net.anotheria.anosite.photoserver.shared.vo.PhotoVO;

import org.configureme.ConfigurationManager;
import org.configureme.environments.DynamicEnvironment;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Storage utility test.
 * 
 * @author Alexandr Bolbat
 */
public class StorageUtilTest {

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

	@Test
	public void complexTest() {
		// TODO: write me
	}

	@Test
	public void errorsTest() {

		// check wrong file
		try {
			StorageUtil.writePhoto(new PhotoVO(), true);
			Assert.fail();
		} catch (StorageUtilException e) {
			//expected
		}

		// check wrong photo file location
		try {
			PhotoVO photoVO = new PhotoVO();
			photoVO.setTempFile(new File(File.separator));
			StorageUtil.writePhoto(photoVO, true);
			Assert.fail();
		} catch (StorageUtilException e) {
			//expected
		}
	}

}
