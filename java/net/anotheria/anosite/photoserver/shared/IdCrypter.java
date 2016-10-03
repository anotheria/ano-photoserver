package net.anotheria.anosite.photoserver.shared;

import net.anotheria.util.NumberUtils;
import net.anotheria.util.StringUtils;
import net.anotheria.util.crypt.CryptTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

/**
 * Helper for encoding and decoding id's.
 *
 * @author Alexandr Bolbat
 * @version $Id: $Id
 */
public final class IdCrypter {

	/**
	 * Key constant.
	 */
	private static final String KEY = "99835f6c04afcbd529028f3fe1e21aaf";
	/**
	 * Delimiter Constant.
	 */
	private static final char DELIMITER = '-';
	/**
	 * Random base constant.
	 */
	private static final int RANDOM_BASE = 1000;

	/**
	 * Secret constant.
	 */
	private static final String SECRET_CONSTANT = "bR8";
	/**
	 * {@link CryptTool}.
	 */
	private static CryptTool crypt;
	/**
	 * {@link java.util.Random}.
	 */
	private static final Random rnd = new Random(System.currentTimeMillis());

	/**
	 * {@link Logger} instance.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(IdCrypter.class);

	/**
	 * Static initialization.
	 */
	static {
		crypt = new CryptTool(KEY);
	}

	/**
	 * Default constructor.
	 */
	private IdCrypter() {
		throw new IllegalAccessError();
	}

	/**
	 * Encode incoming long value.
	 *
	 * @param value
	 *            - original value which should be encoded as long
	 * @return {@link java.lang.String} encoded id
	 */
	public static String encode(long value) {
		return encode(String.valueOf(value));
	}

	/**
	 * Encode incoming String value.
	 *
	 * @param value
	 *            original value which should be encoded as String
	 * @return encoded id
	 */
	public static synchronized String encode(String value) {
		if (StringUtils.isEmpty(value))
			throw new IllegalArgumentException("value is not valid");
		String toEncrypt = SECRET_CONSTANT + DELIMITER + value + DELIMITER + NumberUtils.itoa(rnd.nextInt(RANDOM_BASE), 3);
		return crypt.encryptToHex(toEncrypt);
	}

	/**
	 * Decode incoming value to {@link java.lang.String}.
	 *
	 * @param value
	 *            value which should be decoded
	 * @return string decoded result
	 */
	public static synchronized String decodeToString(final String value) {
		if (StringUtils.isEmpty(value))
			throw new IllegalArgumentException("Invalid incoming data");

		String decrypted;
		// Just to avoid IllegalArgs inside!
		try {
			decrypted = crypt.decryptFromHex(value).trim();
		} catch (IllegalArgumentException e) {
			if (LOG.isDebugEnabled())
				LOG.debug(e.getMessage(), e);
			throw new IllegalArgumentException("encrypted id[" + value + "] - is not valid!");
		}

		String[] tokens = StringUtils.tokenize(decrypted, DELIMITER);

		if (!(tokens.length == 3)) {
			LOG.debug("Wrong number of tokens - " + tokens.length);
			throw new IllegalArgumentException("encrypted id[" + value + "] - is not valid!");
		}

		if (!tokens[0].equals(SECRET_CONSTANT)) {
			LOG.debug("Secret mismatch");
			throw new IllegalArgumentException("encrypted id[" + value + "] - is not valid!");
		}
		try {
			int randomPart = Integer.parseInt(tokens[2]);
			if (randomPart < 0 || randomPart > RANDOM_BASE) {
				LOG.debug("Random part has an illegal value");
				throw new IllegalArgumentException("encrypted id[" + value + "] - is not valid!");
			}
		} catch (NumberFormatException e) {
			LOG.debug("Random part is not numeric: " + e.getMessage());
			throw new IllegalArgumentException("encrypted id[" + value + "] - is not valid!");
		}
		return tokens[1];
	}

	/**
	 * Decode incoming value to {@link java.lang.Long}.
	 *
	 * @param value
	 *            value which should be decoded
	 * @return long decoded result
	 */
	public static long decodeToLong(final String value) {
		return Long.valueOf(decodeToString(value));
	}

}
