package net.anotheria.anosite.photoserver.api.upload;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import net.anotheria.anosite.photoserver.presentation.shared.PhotoDimension;
import net.anotheria.anosite.photoserver.presentation.shared.PhotoUtil;
import net.anotheria.anosite.photoserver.presentation.shared.PhotoUtilException;
import net.anotheria.anosite.photoserver.service.storage.StorageConfig;
import net.anotheria.anosite.photoserver.shared.vo.TempPhotoVO;
import net.anotheria.util.StringUtils;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadBase.SizeLimitExceededException;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.ProgressListener;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.BASE64Encoder;

/**
 * <p>PhotoUploader class.</p>
 *
 * @author another
 * @version $Id: $Id
 */
public class PhotoUploader implements ProgressListener {

	private static final Logger LOG = LoggerFactory.getLogger(PhotoUploader.class);
	private static final PhotoUploadAPIConfig uploadConfig = PhotoUploadAPIConfig.getInstance();

	private UploadStatusAO status;
	private TempPhotoVO photo;

	private String id;
	private String userId;

	private static final String PARAM_UPLOAD_LINK = "pLink";
	private static final String PARAM_AUTH_USERNAME = "authName";
	private static final String PARAM_AUTH_PASSWORD = "authPassword";
	private static final int CONNECTION_TIMEOUT = 5000; // millisecond

	/**
	 * <p>Constructor for PhotoUploader.</p>
	 *
	 * @param uploaderId a {@link java.lang.String} object.
	 * @param userId a {@link java.lang.String} object.
	 */
	public PhotoUploader(String uploaderId, String userId) {
		status = new UploadStatusAO(UploadStatusAO.STATUS_NOT_STARTED);
		status.setId(uploaderId);
		this.id = uploaderId;
		this.userId = userId;
	}

	/**
	 * <p>Getter for the field <code>id</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getId() {
		return id;
	}

	/**
	 * Gets status of upload (e.g. how many percent of the file is uploaded). If status is set to STATUS_FINISHED then additional infos like filetype, size and
	 * original filename are set. Also the file can be stored using the InputStream provided.
	 *
	 * @return a {@link net.anotheria.anosite.photoserver.api.upload.UploadStatusAO} object.
	 */
	public UploadStatusAO getStatus() {
		return status;
	}

	/**
	 * <p>getUploadedPhoto.</p>
	 *
	 * @return a {@link net.anotheria.anosite.photoserver.shared.vo.TempPhotoVO} object.
	 */
	public TempPhotoVO getUploadedPhoto() {
		return photo;
	}

	/**
	 * <p>doUpload.</p>
	 *
	 * @param request a {@link javax.servlet.http.HttpServletRequest} object.
	 */
	@SuppressWarnings("unchecked")
	public void doUpload(HttpServletRequest request) {
		// Create a factory for disk-based file items
		FileItemFactory factory = new DiskFileItemFactory();

		// Create a new file upload handler
		ServletFileUpload upload = new ServletFileUpload(factory);
		upload.setSizeMax(uploadConfig.getMaxUploadFileSize());
		upload.setProgressListener(this);

		try {
			List<FileItem> items = new ArrayList<FileItem>();

			if (ServletFileUpload.isMultipartContent(request))
				items.addAll(upload.parseRequest(request)); // uploading form data

			// uploading by link (if link exist)
			boolean result = uploadByLink(request, factory, items);
			if (result) // exiting from method if uploading by link fail
				return;

			for (FileItem item : items) {
				// get handle to uploaded file
				if (!item.isFormField()) {
					if (!uploadConfig.isAllowedMimeType(item.getContentType()) && !uploadConfig.isAllowedLinkEndType(item.getName())) {
						status.setStatus(UploadStatusAO.STATUS_ERROR_REJECTED);
						return;
					}

					// convert and scale uploaded file
					PhotoUtil photoUtil = new PhotoUtil();
					photoUtil.read(item.getInputStream());

					if (photoUtil.getWidth() > uploadConfig.getMaxWidth()) {
						photoUtil.scale(uploadConfig.getMaxWidth(), -1);
					}

					if (photoUtil.getHeight() > uploadConfig.getMaxHeight()) {
						photoUtil.scale(-1, uploadConfig.getMaxHeight());
					}

					File baseFolder = new File(StorageConfig.getTmpStoreFolderPath(userId));
					baseFolder.mkdirs();
					File tmpFile = new File(baseFolder, System.currentTimeMillis() + uploadConfig.getFilePrefix());
					photoUtil.write(uploadConfig.getJpegQuality(), tmpFile);

					photo = new TempPhotoVO();
					photo.setFile(tmpFile);
					photo.setDimension(new PhotoDimension(photoUtil.getWidth(), photoUtil.getHeight()));

					status.setFilename(item.getName());
					status.setSize(item.getSize());
					status.setStatus(UploadStatusAO.STATUS_FINISHED);

					item.delete();
				}
			}
		} catch (SizeLimitExceededException e) {
			status.setStatus(UploadStatusAO.STATUS_ERROR_MAX_FILESIZE_EXCEEDED);
			LOG.debug(id + ": UploadStatus=MAX_FILESIZE_EXCEEDED");
		} catch (FileUploadException | PhotoUtilException | IOException e) {
			status.setStatus(UploadStatusAO.STATUS_ERROR_UPLOADEXCEPTION);
			LOG.error(id, e);
		}
	}

	private boolean uploadByLink(final HttpServletRequest request, final FileItemFactory factory, List<FileItem> items) {
		// trying to upload additional photo by link
		Object link = request.getAttribute(PARAM_UPLOAD_LINK);
		if (!(link instanceof String)) // if basic param null trying to get it from form data
			for (FileItem item : items)
				if (item.isFormField() && item.getFieldName().equals(PARAM_UPLOAD_LINK) && !StringUtils.isEmpty(item.getString()))
					link = item.getString();

		InputStream is = null;
		if (link instanceof String) {
			try {
				final String authName = request.getParameter(PARAM_AUTH_USERNAME);
				final String authPassword = request.getParameter(PARAM_AUTH_PASSWORD);
				URL url = new URL(String.class.cast(link));
				URLConnection conn = url.openConnection();
				conn.setConnectTimeout(CONNECTION_TIMEOUT);
				//authorizate connection is username and password present in URL
				if (authName != null && authName.length() > 0 && authPassword != null && authPassword.length() > 0) {
					String encoding = new BASE64Encoder().encode((authName + ":" + authPassword).getBytes());
					conn.setRequestProperty("Authorization", "Basic " + encoding);
				}
				conn.connect();

				// checking content type
				if (!uploadConfig.isAllowedMimeType(conn.getContentType()) && !uploadConfig.isAllowedLinkEndType(conn.getURL().getFile())) {
					status.setStatus(UploadStatusAO.STATUS_ERROR_REJECTED);
					return true;
				}

				String[] linkSegments = String.class.cast(link).split("/");
				String resourceName = "uploaded";
				if (linkSegments != null && linkSegments.length > 0)
					resourceName = linkSegments[linkSegments.length - 1];

				FileItem file = factory.createItem(PARAM_UPLOAD_LINK, conn.getContentType(), false, resourceName);
				is = conn.getInputStream();
				IOUtils.copyLarge(is, file.getOutputStream());
				file.getOutputStream().flush();

				items.add(file);
			} catch (SocketTimeoutException e) {
				status.setStatus(UploadStatusAO.STATUS_ERROR_UPLOADEXCEPTION);
				LOG.debug(id, e);
				return true;
			} catch (MalformedURLException e) {
				status.setStatus(UploadStatusAO.STATUS_ERROR_UPLOADEXCEPTION);
				LOG.debug(id, e);
				return true;
			} catch (IOException e) {
				status.setStatus(UploadStatusAO.STATUS_ERROR_UPLOADEXCEPTION);
				LOG.debug(id, e);
				return true;
			} finally {
				IOUtils.closeQuietly(is);
			}
		}

		return false;
	}

	/** {@inheritDoc} */
	@Override
	public void update(long bytesRead, long contentLength, int item) {
		status.setProgress((int) (((float) bytesRead / (float) contentLength) * 100f));
		status.setStatus(UploadStatusAO.STATUS_UPLOADING);
	}
}
