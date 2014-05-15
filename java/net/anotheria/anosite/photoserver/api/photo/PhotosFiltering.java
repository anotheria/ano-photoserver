package net.anotheria.anosite.photoserver.api.photo;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.anotheria.anosite.photoserver.shared.ApprovalStatus;
import net.anotheria.anosite.photoserver.shared.PhotoServerConfig;

/**
 * Bean, containing additional photos filtering configuration. 
 * Can be passed by PhotoAPI caller to control filtering photos by their ApprovalStatus.
 * If {@link PhotoServerConfig} is configured to ignore photo approving, this bean will be ignored. 
 * 
 * @author dzhmud
 */
public final class PhotosFiltering implements Serializable {
	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = -8170852436250130354L;
	
	/**
	 * Predefined PhotosFiltering object which disables photos filtering by ApprovalStatus.
	 */
	public static final PhotosFiltering DISABLED = new PhotosFiltering(false);
	
	/**
	 * Predefined PhotosFiltering object which enables photos filtering by ApprovalStatus.APPROVED.
	 */
	public static final PhotosFiltering DEFAULT = new PhotosFiltering(true, ApprovalStatus.APPROVED);
	
	/**
	 * Defines if PhotoAPI should filter photos.
	 */
	final boolean filteringEnabled;
	
	/**
	 * Contains allowed {@link ApprovalStatus}es. 
	 * Photos with statuses not in this list will be filtered out.
	 */
	final List<ApprovalStatus> allowedStatuses;
	
	/**
	 * Public constructor.
	 * @param enabled
	 * @param allowedStatuses
	 */
	public PhotosFiltering(boolean enabled, ApprovalStatus... allowedStatuses) {
		this.filteringEnabled = enabled;
		this.allowedStatuses = convert2List(allowedStatuses);
	}
	
	private static List<ApprovalStatus> convert2List(ApprovalStatus... allowedStatuses) {
		System.out.println(allowedStatuses);
		List<ApprovalStatus> result;
		if (allowedStatuses == null)
			result = Collections.emptyList();
		else if (allowedStatuses.length == 1)
			result = Collections.singletonList(allowedStatuses[0]);
		else
			result = Collections.unmodifiableList(Arrays.asList(allowedStatuses));
		return result;
	}

	@Override
	public String toString() {
		return "PhotosFiltering [filteringEnabled=" + filteringEnabled
				+ ", allowedStatuses=" + allowedStatuses + "]";
	}

}
