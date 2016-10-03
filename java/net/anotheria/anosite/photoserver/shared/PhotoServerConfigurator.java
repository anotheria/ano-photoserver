package net.anotheria.anosite.photoserver.shared;

import net.anotheria.anoplass.api.APIFinder;
import net.anotheria.anoprise.metafactory.Extension;
import net.anotheria.anoprise.metafactory.MetaFactory;
import net.anotheria.anoprise.metafactory.Service;
import net.anotheria.anoprise.metafactory.ServiceFactory;
import net.anotheria.anosite.photoserver.api.access.AccessAPI;
import net.anotheria.anosite.photoserver.api.access.AccessAPIFactory;
import net.anotheria.anosite.photoserver.api.blur.BlurSettingsAPI;
import net.anotheria.anosite.photoserver.api.blur.BlurSettingsAPIFactory;
import net.anotheria.anosite.photoserver.api.photo.PhotoAPI;
import net.anotheria.anosite.photoserver.api.photo.PhotoAPIFactory;
import net.anotheria.anosite.photoserver.api.upload.PhotoUploadAPI;
import net.anotheria.anosite.photoserver.api.upload.PhotoUploadAPIFactory;
import net.anotheria.anosite.photoserver.service.blur.BlurSettingsService;
import net.anotheria.anosite.photoserver.service.blur.BlurSettingsServiceFactory;
import net.anotheria.anosite.photoserver.service.blur.persistence.BlurSettingsPersistenceService;
import net.anotheria.anosite.photoserver.service.blur.persistence.BlurSettingsPersistenceServiceFactory;
import net.anotheria.anosite.photoserver.service.storage.StorageService;
import net.anotheria.anosite.photoserver.service.storage.StorageServiceFactory;
import net.anotheria.anosite.photoserver.service.storage.persistence.StoragePersistenceFactory;
import net.anotheria.anosite.photoserver.service.storage.persistence.StoragePersistenceService;
import net.anotheria.anosite.photoserver.service.storage.persistence.album.AlbumPersistenceService;
import net.anotheria.anosite.photoserver.service.storage.persistence.album.AlbumPersistenceServiceFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MarkerFactory;

/**
 * Utility for configuring photo server functionality.
 *
 * @author Alexandr Bolbat
 * @version $Id: $Id
 */
public final class PhotoServerConfigurator {

	/**
	 * Logger.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(PhotoServerConfigurator.class.getName());

	/**
	 * Default constructor.
	 */
	private PhotoServerConfigurator() {
		throw new IllegalAccessError();
	}

	/**
	 * Configure photo server business and api tier's.
	 */
	public static void configure() {
		LOGGER.debug("configure() Configuring photo server tier: STARTED");

		boolean isRemoteServices = PhotoServerServicesConfig.getInstance().isRemoteServices();
		LOGGER.debug("configure() Remote services allowed[" + isRemoteServices + "].");

		LOGGER.debug("configure() Configuring local (persistence, internal, other) services.");
		MetaFactory.addFactoryClass(StoragePersistenceService.class, Extension.LOCAL, StoragePersistenceFactory.class);
		MetaFactory.addAlias(StoragePersistenceService.class, Extension.LOCAL);

		MetaFactory.addFactoryClass(AlbumPersistenceService.class, Extension.LOCAL, AlbumPersistenceServiceFactory.class);
		MetaFactory.addAlias(AlbumPersistenceService.class, Extension.LOCAL);

		MetaFactory.addFactoryClass(BlurSettingsPersistenceService.class, Extension.LOCAL, BlurSettingsPersistenceServiceFactory.class);
		MetaFactory.addAlias(BlurSettingsPersistenceService.class, Extension.LOCAL);

		LOGGER.debug("configure() Configuring services.");
		Extension servicesExtension = isRemoteServices ? Extension.REMOTE : Extension.LOCAL;
		// storage service
		MetaFactory.addFactoryClass(StorageService.class, Extension.LOCAL, StorageServiceFactory.class);
		MetaFactory.addFactoryClass(StorageService.class, Extension.REMOTE, StorageServiceFactory.class);
		if (isRemoteServices && PhotoServerServicesConfig.getInstance().isStorageServiceRemote())
			addRemoteFactory(StorageService.class, PhotoServerServicesConfig.getInstance().getStorageServiceRemoteFactory());
		MetaFactory.addAlias(StorageService.class, servicesExtension);

		// blur settings service
		MetaFactory.addFactoryClass(BlurSettingsService.class, Extension.LOCAL, BlurSettingsServiceFactory.class);
		MetaFactory.addFactoryClass(BlurSettingsService.class, Extension.REMOTE, BlurSettingsServiceFactory.class);
		if (isRemoteServices && PhotoServerServicesConfig.getInstance().isBlurSettingsServiceRemote())
			addRemoteFactory(BlurSettingsService.class, PhotoServerServicesConfig.getInstance().getBlurSettingsServiceRemoteFactory());
		MetaFactory.addAlias(BlurSettingsService.class, servicesExtension);

		LOGGER.debug("configure() Configuring API's.");
		APIFinder.addAPIFactory(PhotoAPI.class, new PhotoAPIFactory());
		APIFinder.addAPIFactory(PhotoUploadAPI.class, new PhotoUploadAPIFactory());
		APIFinder.addAPIFactory(BlurSettingsAPI.class, new BlurSettingsAPIFactory());
		APIFinder.addAPIFactory(AccessAPI.class, new AccessAPIFactory());
		LOGGER.debug("configure() Configuring photo server tier: FINISHED");
	}

	/**
	 * Register remote factory by factory class name for a given interface in {@link net.anotheria.anoprise.metafactory.MetaFactory}.
	 *
	 * @param interf
	 *            - interface
	 * @param factoryClassName
	 *            - factory class name
	 */
	public static <T extends Service> void addRemoteFactory(Class<T> interf, String factoryClassName) {
		try {
			@SuppressWarnings("unchecked")
			Class<ServiceFactory<T>> clazz = (Class<ServiceFactory<T>>) Class.forName(factoryClassName);
			MetaFactory.addFactoryClass(interf, Extension.REMOTE, clazz);
		} catch (ClassNotFoundException cnfe) {
			String message = "Couldn't load factory class[" + factoryClassName + "] for service [" + interf + "].";
			LOGGER.error(MarkerFactory.getMarker("FATAL"), message, cnfe);
			throw new RuntimeException(message, cnfe);
		}
	}

}
