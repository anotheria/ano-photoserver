package net.anotheria.anosite.photoserver;

import net.anotheria.anoplass.api.APICallContext;
import net.anotheria.anoplass.api.APIFinder;
import net.anotheria.anoplass.api.session.APISession;
import net.anotheria.anoplass.api.session.APISessionCreationException;
import net.anotheria.anoplass.api.session.APISessionManager;
import net.anotheria.anoprise.metafactory.MetaFactory;
import net.anotheria.anosite.photoserver.shared.PhotoServerConfigurator;
import net.anotheria.db.util.DBUtil;
import net.anotheria.moskito.core.registry.ProducerRegistryFactory;
import net.anotheria.util.IdCodeGenerator;

import org.configureme.ConfigurationManager;
import org.configureme.environments.DynamicEnvironment;
import org.junit.Test;

/**
 * Testing context initializer.
 * 
 * @author Alexandr Bolbat
 */
public class TestingContextInitializer {

	/**
	 * Configure testing context.
	 */
	public static void init() {
		System.out.println("######## TESTING  INITIALIZATION: START... ########");
		ConfigurationManager.INSTANCE.setDefaultEnvironment(new DynamicEnvironment("test", "junit"));
		System.setProperty("JUNITTEST", "true");

		// Common API configuration
		try {
			@SuppressWarnings("deprecation")
			APISession session = APISessionManager.getInstance().obtainSession(IdCodeGenerator.generateCode());
			APICallContext.getCallContext().setCurrentSession(session);
			APIFinder.setMockingEnabled(true);
			APIFinder.setMaskingEnabled(true);
		} catch (APISessionCreationException e) {
			throw new RuntimeException(e);
		}

		PhotoServerConfigurator.configure();
		System.out.println("######## TESTING INITIALIZATION: FINISHED! ########");
	}

	/**
	 * Clean testing context.
	 */
	public static void deInit() {
		ConfigurationManager.INSTANCE.setDefaultEnvironment(new DynamicEnvironment("test", "junit"));
		System.setProperty("JUNITTEST", "true");

		MetaFactory.reset();
		APICallContext.getCallContext().reset();
		APIFinder.cleanUp();

		DBUtil.getInstance().removeAllTables();

		ProducerRegistryFactory.reset(); // Moskito reset
	}

	@Test
	public void testInitialization() {
		init();
	}

	@Test
	public void deInitialization() {
		deInit();
	}

}
