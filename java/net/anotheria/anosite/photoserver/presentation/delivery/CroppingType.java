package net.anotheria.anosite.photoserver.presentation.delivery;

import org.apache.log4j.Logger;

/**
 * Photo cropping type.
 *
 * @author rkapushchak
 */
public enum CroppingType {

	/**
	 * Crop by height.
	 */
	HEIGHT(0),

    /**
     * Crop by height with natural height limit.
     */
    NATURAL_HEIGHT(1),

	/**
	 * Crop by width.
	 */
	WIDTH(2),

    /**
     * Crop by width with natural width limit.
     */
    NATURAL_WIDTH(3),

	/**
	 * Crop by the bigger parameter (height or width).
	 */
	BOTH(4),

    /**
     * Crop by the bigger parameter  with natural limit (height or width).
     */
    NATURAL_BOTH(5);

    /**
     * Value property.
     */
    private int value;

    /**
     * Constructor.
     *
     * @param value
     *            gender value
     */
    private CroppingType(final int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    /**
     * Returns {@link CroppingType} by given value. If there is no such {@link CroppingType} with specified value - return {@code null}.
     *
     * @param value {@code int} value
     * @return corresponding {@link CroppingType} or {@code null} if there is no such gender
     */
    public static CroppingType valueOf(final int value) {
        for (CroppingType gender : CroppingType.values())
            if (gender.getValue() == value)
                return gender;

        Logger.getLogger(CroppingType.class).warn("Method CroppingType.valueOf() called with invalid value=[" + value + "]");
        return null;
    }
}
