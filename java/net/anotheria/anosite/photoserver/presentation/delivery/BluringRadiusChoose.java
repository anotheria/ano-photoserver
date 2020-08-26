package net.anotheria.anosite.photoserver.presentation.delivery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Photo cropping type.
 *
 * @author rkapushchak
 * @version $Id: $Id
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
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(BluringRadiusChoose.class);

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

    /**
     * <p>Getter for the field <code>value</code>.</p>
     *
     * @return a int.
     */
    public int getValue() {
        return value;
    }

    /**
     * Returns {@link net.anotheria.anosite.photoserver.presentation.delivery.BluringRadiusChoose} by given value. If there is no such {@link net.anotheria.anosite.photoserver.presentation.delivery.BluringRadiusChoose} with specified value - return {@code null}.
     *
     * @param value {@code int} value
     * @return corresponding {@link net.anotheria.anosite.photoserver.presentation.delivery.BluringRadiusChoose} or {@code null} if there is no such gender
     */
    public static BluringRadiusChoose valueOf(final int value) {
        for (BluringRadiusChoose gender : BluringRadiusChoose.values())
            if (gender.getValue() == value)
                return gender;

        LOGGER.warn("Method CroppingType.valueOf() called with invalid value=[" + value + "]");
        return null;
    }
}
