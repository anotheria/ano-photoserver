package net.anotheria.anosite.photoserver.presentation.shared;

/**
 * Based exception in photo util.
 *
 * @author ykalapusha
 */
public class PhotoUtilException extends Exception {
    /**
     * Constructor.
     *
     * @param message error message
     */
    public PhotoUtilException(String message) {
        super(message);
    }

    /**
     * Constructor.
     *
     * @param message error message
     * @param cause error cause
     */
    public PhotoUtilException(String message, Throwable cause) {
        super(message, cause);
    }
}
