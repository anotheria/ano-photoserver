package net.anotheria.anosite.photoserver.api.upload;

import net.anotheria.util.NumberUtils;
import org.json.simple.JSONObject;

/**
 * Status for a current upload. 
 *  
 * @author otoense
 *
 */
public class UploadStatusAO {
	
	public static final int STATUS_NOT_STARTED = 2;
	public static final int STATUS_UPLOADING = 1;
	public static final int STATUS_FINISHED = 0;
	public static final int STATUS_ERROR_MAX_FILESIZE_EXCEEDED = -1;
	public static final int STATUS_ERROR_UPLOADEXCEPTION = -2;
	public static final int STATUS_ERROR_REJECTED = -3;
	public static final int STATUS_ERROR_NOTREGISTERED = -4;
	/**
	 * Notifies that the dimensions of the uploaded image are less then required in the configuration.
	 */
	public static final int STATUS_ERROR_MIN_IMAGE_DIMENSIONS = -5;
	
	private int progress;
	private int status;
	private String size;
	private String filename;
	private String id;
	
	public UploadStatusAO(int status) {
		this.status = status;
	}
	
	public int getProgress() {
		return progress;
	}

	public void setProgress(int progress) {
		this.progress = progress;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = NumberUtils.makeSizeString(size);
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@SuppressWarnings("unchecked")
	public String toJSONString() {
		JSONObject json = new JSONObject();
		json.put("progress", progress);
		json.put("status", status);
		json.put("size", size);
		json.put("filename", filename);
		json.put("id", id);
		return json.toString();
	}
}
