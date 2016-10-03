package net.anotheria.anosite.photoserver.api.upload;

import net.anotheria.util.NumberUtils;

import org.json.simple.JSONObject;

/**
 * Status for a current upload.
 *
 * @author otoense
 * @version $Id: $Id
 */
public class UploadStatusAO {
	
	/** Constant <code>STATUS_NOT_STARTED=2</code> */
	public static final int STATUS_NOT_STARTED = 2;
	/** Constant <code>STATUS_UPLOADING=1</code> */
	public static final int STATUS_UPLOADING = 1;
	/** Constant <code>STATUS_FINISHED=0</code> */
	public static final int STATUS_FINISHED = 0;
	/** Constant <code>STATUS_ERROR_MAX_FILESIZE_EXCEEDED=-1</code> */
	public static final int STATUS_ERROR_MAX_FILESIZE_EXCEEDED = -1;
	/** Constant <code>STATUS_ERROR_UPLOADEXCEPTION=-2</code> */
	public static final int STATUS_ERROR_UPLOADEXCEPTION = -2;
	/** Constant <code>STATUS_ERROR_REJECTED=-3</code> */
	public static final int STATUS_ERROR_REJECTED = -3;
	/** Constant <code>STATUS_ERROR_NOTREGISTERED=-4</code> */
	public static final int STATUS_ERROR_NOTREGISTERED = -4;
	
	private int progress;
	private int status;
	private String size;
	private String filename;
	private String id;
	
	/**
	 * <p>Constructor for UploadStatusAO.</p>
	 *
	 * @param status a int.
	 */
	public UploadStatusAO(int status) {
		this.status = status;
	}
	
	/**
	 * <p>Getter for the field <code>progress</code>.</p>
	 *
	 * @return a int.
	 */
	public int getProgress() {
		return progress;
	}

	/**
	 * <p>Setter for the field <code>progress</code>.</p>
	 *
	 * @param progress a int.
	 */
	public void setProgress(int progress) {
		this.progress = progress;
	}

	/**
	 * <p>Getter for the field <code>status</code>.</p>
	 *
	 * @return a int.
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * <p>Setter for the field <code>status</code>.</p>
	 *
	 * @param status a int.
	 */
	public void setStatus(int status) {
		this.status = status;
	}

	/**
	 * <p>Getter for the field <code>size</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getSize() {
		return size;
	}

	/**
	 * <p>Setter for the field <code>size</code>.</p>
	 *
	 * @param size a long.
	 */
	public void setSize(long size) {
		this.size = NumberUtils.makeSizeString(size);
	}

	/**
	 * <p>Getter for the field <code>filename</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getFilename() {
		return filename;
	}

	/**
	 * <p>Setter for the field <code>filename</code>.</p>
	 *
	 * @param filename a {@link java.lang.String} object.
	 */
	public void setFilename(String filename) {
		this.filename = filename;
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
	 * <p>Setter for the field <code>id</code>.</p>
	 *
	 * @param id a {@link java.lang.String} object.
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * <p>toJSONString.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
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
