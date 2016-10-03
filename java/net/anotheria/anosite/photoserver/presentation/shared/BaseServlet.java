package net.anotheria.anosite.photoserver.presentation.shared;

import net.anotheria.anoplass.api.APIFinder;
import net.anotheria.anoplass.api.generic.login.LoginAPI;
import net.anotheria.anosite.photoserver.api.upload.PhotoUploadAPIConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Implementation of the base functions for a photo server servlets.
 *
 * @author otoense
 * @version $Id: $Id
 */
public class BaseServlet extends HttpServlet {

	/**
	 * Basic serialVersionUID variable.
	 */
	private static final long serialVersionUID = -8745062263129544228L;

	/**
	 * Logger.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(BaseServlet.class);

	/**
	 * {@link LoginAPI} instance.
	 */
	protected static final LoginAPI loginAPI = APIFinder.findAPI(LoginAPI.class);

	/**
	 * Write headers for cache and JSON mimetype before writing the content.
	 *
	 * @param response
	 *            the HttpServletResponse
	 * @param content
	 *            a JSON String
	 * @throws java.io.IOException if any.
	 */
	protected void writeResponse(HttpServletResponse response, String content) throws IOException {
		response.setHeader("Pragma", "no-cache");
		response.setHeader("Cacheable", "false");
		response.setDateHeader("Last-Modified", System.currentTimeMillis());
		response.setDateHeader("Expires", 0);
		response.setContentType("application/json");
		LOG.debug("Send json:" + content);
		response.getWriter().write(content);
	}

	/* Write headers for cache and JSON mimetype before writing the content.
			*
			* @param response
	*            the HttpServletResponse
	* @param content
	*            a JSON String
	* @throws java.io.IOException
	*/
	/**
	 * <p>writeResponseJSONPResponse.</p>
	 *
	 * @param response a {@link javax.servlet.http.HttpServletResponse} object.
	 * @param methodCallbackName a {@link java.lang.String} object.
	 * @param content a {@link java.lang.String} object.
	 * @throws java.io.IOException if any.
	 */
	protected void writeResponseJSONPResponse(final HttpServletResponse response, final String methodCallbackName, final String content) throws IOException {
		response.setHeader("Pragma", "no-cache");
		response.setHeader("Cacheable", "false");
		response.setDateHeader("Last-Modified", System.currentTimeMillis());
		response.setDateHeader("Expires", 0);
		response.setContentType("text/javascript");
		LOG.debug("Send jsonp:" + content);
		response.getWriter().write(methodCallbackName + "("+ content + ");");
	}

	/**
	 * Splits the REST-like path of the request URI into tokens.
	 *
	 * @param request a {@link javax.servlet.http.HttpServletRequest} object.
	 * @return an array of {@link java.lang.String} objects.
	 */
	protected String[] parsePathParameters(HttpServletRequest request) {
		return request.getPathInfo().substring(1).split("/");
	}

	/**
	 * Streams a JPEG file to the response. Headers will be sent to disable caching.
	 *
	 * @param response a {@link javax.servlet.http.HttpServletResponse} object.
	 * @param anImageFile a {@link java.io.File} object.
	 * @throws java.io.IOException if any.
	 */
	protected void streamImageFile(HttpServletResponse response, File anImageFile) throws IOException {
		InputStream in = new FileInputStream(anImageFile);
		stream(response, in);
	}

	/**
	 * Streams a JPEG file to the response. Headers will be sent to disable caching.
	 *
	 * @param response a {@link javax.servlet.http.HttpServletResponse} object.
	 * @param in a {@link java.io.InputStream} object.
	 * @throws java.io.IOException if any.
	 */
	protected void stream(HttpServletResponse response, InputStream in) throws IOException {
		writeImageHeaders(response);
		byte[] buffer = new byte[65536];
		try {
			OutputStream out = response.getOutputStream();
			int len = 0;
			while ((len = in.read(buffer)) > 0) {
				out.write(buffer, 0, len);
			}
			out.flush();
		} finally {
			in.close();
		}
	}

	/**
	 * Write headers for a jpeg response
	 *
	 * @param response a {@link javax.servlet.http.HttpServletResponse} object.
	 * @throws java.io.IOException if any.
	 */
	protected void writeImageHeaders(HttpServletResponse response) throws IOException {
		response.setHeader("Pragma", "no-cache");
		response.setHeader("Cacheable", "false");
		response.setDateHeader("Last-Modified", System.currentTimeMillis());
		response.setDateHeader("Expires", 0);
		final String contentType = ImageWriteFormat.getByValue(PhotoUploadAPIConfig.getInstance().getImageWriteFormat()).getContentType();
		response.setContentType(contentType);
	}
}
