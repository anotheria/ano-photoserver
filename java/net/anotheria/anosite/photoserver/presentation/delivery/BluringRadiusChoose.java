package net.anotheria.anosite.photoserver.presentation.delivery;

import org.apache.log4j.Logger;

/**
 * Photo cropping type.
 *
 * @author rkapushchak
 */
public enum BluringRadiusChoose {

	/**
	 * Use min radius.
	 */
	MIN(0),

    /**
     * Use max radius.
     */
	MAX(1);


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
    private BluringRadiusChoose(final int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    /**
     * Returns {@link BluringRadiusChoose} by given value. If there is no such {@link BluringRadiusChoose} with specified value - return {@code null}.
     *
     * @param value {@code int} value
     * @return corresponding {@link BluringRadiusChoose} or {@code null} if there is no such gender
     */
    public static BluringRadiusChoose valueOf(final int value) {
        for (BluringRadiusChoose gender : BluringRadiusChoose.values())
            if (gender.getValue() == value)
                return gender;

        Logger.getLogger(BluringRadiusChoose.class).warn("Method CroppingType.valueOf() called with invalid value=[" + value + "]");
        return null;
    }
}
