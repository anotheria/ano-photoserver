package net.anotheria.anosite.photoserver.shared;

import org.slf4j.LoggerFactory;

/**
 * Enumeration holding all possible statuses related to photo/album approving or rejecting by responsible persons.
 * 
 * @author dzhmud
 */
public enum ApprovalStatus {

	/** Default status assigned to all uploaded photos/albums. */
	WAITING_APPROVAL(10),

	/** Status assigned to photos by responsible person. Means that photo/album can be shown to everyone. */
	APPROVED(20),

	/** Status assigned to photos by responsible person. Means that photo/album can be shown only to their owner - for fixing or removing. */
	REJECTED(30);

	/**
	 * Default approval status.
	 */
	public static final ApprovalStatus DEFAULT = WAITING_APPROVAL;

	/** Internal unique value. Used for storing in persistence. */
	private final int code;

	/**
	 * Default constructor.
	 * 
	 * @param code
	 *            - code
	 */
	private ApprovalStatus(int code) {
		this.code = code;
	}

	public int getCode() {
		return code;
	}

	@Override
	public String toString() {
		return this.name() + "(" + code + ")";
	}

	/** Get ApprovalStatus by its code. */
	public static ApprovalStatus getStatusByCode(int code) {
		for (ApprovalStatus status : values())
			if (status.code == code)
				return status;

		LoggerFactory.getLogger(ApprovalStatus.class).error("ApprovalStatus(" + code + ") not found. Relying on defaults.");
		return DEFAULT;
	}

}
