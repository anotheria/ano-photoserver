package net.anotheria.anosite.photoserver.api.upload;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.anotheria.anoplass.api.APIException;
import net.anotheria.anoplass.api.APIFinder;
import net.anotheria.anoplass.api.AbstractAPIImpl;
import net.anotheria.anoplass.api.generic.login.LoginAPI;
import net.anotheria.anoplass.api.session.APISession;
import net.anotheria.anosite.photoserver.presentation.shared.PhotoDimension;
import net.anotheria.anosite.photoserver.presentation.shared.PhotoUtil;
import net.anotheria.anosite.photoserver.shared.vo.TempPhotoVO;
import net.anotheria.util.IdCodeGenerator;
import net.anotheria.util.StringUtils;

/**
 * 
 * @author otoense
 * 
 */
public class PhotoUploadAPIImpl extends AbstractAPIImpl implements PhotoUploadAPI {

	public static final String ATTR_UPLOADER_REGISTRY = "uploaderRegistry";
	public static final String ATTR_WORKBENCH_REGISTRY = "workbenchRegistry";

	public static final String TEMP_PHOTO_PREFIX = "uploaded-";
	public static final String TEMP_PHOTO_SUFFIX = ".jpg";

	private static final LoginAPI loginAPI = APIFinder.findAPI(LoginAPI.class);
	private static final PhotoUploadAPIConfig uploadAPIConfig = PhotoUploadAPIConfig.getInstance();

	@Override
	public PhotoWorkbench createMyPhotoWorkbench(TempPhotoVO photo) {
		String workbenchId = IdCodeGenerator.generateCode(16);
		PhotoWorkbench workbench = new PhotoWorkbench(photo, workbenchId);
		getMyWorkbenchRegistry().put(workbenchId, workbench);
		return workbench;
	}

	@Override
	public PhotoWorkbench getMyPhotoWorkbench(String workbenchId) {
		return getMyWorkbenchRegistry().get(workbenchId);
	}

	@Override
	public PhotoUploader createMyPhotoUploader() throws APIException {
		return createPhotoUploader(null);
	}

    @Override
    public PhotoUploader createPhotoUploader(final String userId) throws APIException{
        String uploaderId = IdCodeGenerator.generateCode(16);
        String uploaderUserId = userId;
        if (StringUtils.isEmpty(uploaderUserId)){
            uploaderUserId = loginAPI.isLogedIn() ? loginAPI.getLogedUserId() : UUID.randomUUID().toString();
        }
        PhotoUploader uploader = new PhotoUploader(uploaderId, uploaderUserId);
        getMyUploaderRegistry().put(uploaderId, uploader);
        return uploader;
    }

    @Override
	public PhotoUploader getMyPhotoUploader(String uploaderId) {
		return getMyUploaderRegistry().get(uploaderId);
	}

	private Map<String, PhotoUploader> getMyUploaderRegistry() {
		@SuppressWarnings("unchecked")
		Map<String, PhotoUploader> registry = (Map<String, PhotoUploader>) getSession().getAttribute(ATTR_UPLOADER_REGISTRY);
		if (registry == null) {
			registry = new HashMap<String, PhotoUploader>();
			getSession().setAttribute(ATTR_UPLOADER_REGISTRY, APISession.POLICY_LOCAL, registry);
		}
		return registry;
	}

	private Map<String, PhotoWorkbench> getMyWorkbenchRegistry() {
		@SuppressWarnings("unchecked")
		Map<String, PhotoWorkbench> registry = (Map<String, PhotoWorkbench>) getSession().getAttribute(ATTR_WORKBENCH_REGISTRY);
		if (registry == null) {
			registry = new HashMap<String, PhotoWorkbench>();
			getSession().setAttribute(ATTR_WORKBENCH_REGISTRY, APISession.POLICY_LOCAL, registry);
		}
		return registry;
	}

	@Override
	public TempPhotoVO rotatePhoto(TempPhotoVO photo, int n) throws APIException {
		try {
			TempPhotoVO result = new TempPhotoVO();
			PhotoUtil photoUtil = new PhotoUtil();
			FileInputStream in = new FileInputStream(photo.getFile());

			photoUtil.read(in);
			in.close();

			for (int i = 0; i < n; i++) {
				photoUtil.rotate();
			}
			File output = File.createTempFile(TEMP_PHOTO_PREFIX, TEMP_PHOTO_SUFFIX, photo.getFile().getParentFile());
			result.setDimension(new PhotoDimension(photoUtil.getWidth(), photoUtil.getHeight()));
			photoUtil.write(uploadAPIConfig.getJpegQuality(), output);
			result.setFile(output);
			return result;

		} catch (IOException e) {
			throw new APIException("Can't write rotated file", e);
		}
	}

    @Override
    public void finishWorkbench(final String workbenchId) {
        if (workbenchId == null || workbenchId.isEmpty())
            throw new IllegalArgumentException("Parameter workbenchId is empty.");

        @SuppressWarnings("unchecked")
        Map<String, PhotoWorkbench> registry = (Map<String, PhotoWorkbench>) getSession().getAttribute(ATTR_WORKBENCH_REGISTRY);
        if (registry != null && registry.containsKey(workbenchId)) {
            registry.remove(workbenchId);
            getSession().setAttribute(ATTR_WORKBENCH_REGISTRY, APISession.POLICY_LOCAL, registry);
        }
    }
}
