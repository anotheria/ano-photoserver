package net.anotheria.anosite.photoserver.presentation.shared;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import net.anotheria.anoplass.api.APIFinder;
import net.anotheria.anoplass.api.generic.login.LoginAPI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of the base functions for a photo server servlets.
 * 
 * @author otoense
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
	 * @throws java.io.IOException
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
	 * @param request
	 * @return
	 */
	protected String[] parsePathParameters(HttpServletRequest request) {
		return request.getPathInfo().substring(1).split("/");
	}

	/**
	 * Streams a JPEG file to the response. Headers will be sent to disable caching.
	 * 
	 * @param response
	 * @param anImageFile
	 */
	protected void streamImageFile(HttpServletResponse response, File anImageFile) throws IOException {
		InputStream in = new FileInputStream(anImageFile);
		stream(response, in);
	}

	/**
	 * Streams a JPEG file to the response. Headers will be sent to disable caching.
	 * 
	 * @param response
	 * @param in
	 */
	protected void stream(HttpServletResponse response, InputStream in) throws IOException {
		writeJpegHeaders(response);
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
	 */
	protected void writeJpegHeaders(HttpServletResponse response) throws IOException {
		response.setHeader("Pragma", "no-cache");
		response.setHeader("Cacheable", "false");
		response.setDateHeader("Last-Modified", System.currentTimeMillis());
		response.setDateHeader("Expires", 0);
		response.setContentType("image/jpeg");
	}
}
