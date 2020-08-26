package net.anotheria.anosite.photoserver.presentation.delivery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Photo cropping type.
 *
 * @author rkapushchak
 * @version $Id: $Id
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
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(CroppingType.class);

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

    /**
     * <p>Getter for the field <code>value</code>.</p>
     *
     * @return a int.
     */
    public int getValue() {
        return value;
    }

    /**
     * Returns {@link net.anotheria.anosite.photoserver.presentation.delivery.CroppingType} by given value. If there is no such {@link net.anotheria.anosite.photoserver.presentation.delivery.CroppingType} with specified value - return {@code null}.
     *
     * @param value {@code int} value
     * @return corresponding {@link net.anotheria.anosite.photoserver.presentation.delivery.CroppingType} or {@code null} if there is no such gender
     */
    public static CroppingType valueOf(final int value) {
        for (CroppingType gender : CroppingType.values())
            if (gender.getValue() == value)
                return gender;

        LOGGER.warn("Method CroppingType.valueOf() called with invalid value=[" + value + "]");
        return null;
    }
}
