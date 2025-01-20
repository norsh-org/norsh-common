package org.norsh.util;

import java.io.Reader;
import java.lang.reflect.Type;
import java.util.Base64;

import org.norsh.exceptions.ArgumentException;

import com.google.gson.Gson;

/**
 * The {@code Converter} class provides utility methods for data encoding, decoding, and conversion. 
 * This is essential for operations involving cryptographic and blockchain data formats.
 * 
 * <h2>Features:</h2>
 * <ul>
 * <li>Conversion between hexadecimal, Base64, and byte array formats.</li>
 * <li>Validation of input formats for Base64 and hexadecimal strings.</li>
 * <li>Normalization of PEM keys for safe usage.</li>
 * <li>Utility methods for JSON serialization and deserialization.</li>
 * </ul>
 * 
 * @since 1.0.0
 * @version 1.0.0
 * @author Danthur Lice
 * @see <a href="https://docs.norsh.org">Norsh Documentation</a>
 */
public class Converter {
	private static final String HEX_REGEX = "[a-fA-F0-9]+";
	private static final String BASE64_REGEX = "^(?:[A-Za-z0-9+/]{4})*(?:[A-Za-z0-9+/]{2}==|[A-Za-z0-9+/]{3}=)?$";

	/**
	 * Converts a byte array to a hexadecimal string.
	 *
	 * @param byteArray the byte array to convert.
	 * @return a hexadecimal string representation of the byte array, or {@code null} if the input is {@code null}.
	 */
	public static String bytesToHex(byte[] byteArray) {
		if (byteArray == null) {
			return null;
		}
		StringBuilder hexBuilder = new StringBuilder();
		for (byte b : byteArray) {
			hexBuilder.append(String.format("%02x", b));
		}
		return hexBuilder.toString();
	}

	/**
	 * Converts a hexadecimal string to a byte array.
	 *
	 * @param hexString the hexadecimal string to convert.
	 * @return a byte array representation of the hexadecimal string.
	 * @throws ArgumentException if the input string is not a valid hexadecimal string.
	 */
	public static byte[] hexToBytes(String hexString) {
		if (hexString == null || hexString.trim().isEmpty()) {
			return null;
		}
		hexString = hexString.trim();
		if (hexString.startsWith("0x")) {
			hexString = hexString.substring(2);
		}
		if (!hexString.matches(HEX_REGEX)) {
			throw new ArgumentException("Invalid hexadecimal input: " + hexString);
		}
		int len = hexString.length();
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4) 
					+ Character.digit(hexString.charAt(i + 1), 16));
		}
		return data;
	}

	/**
	 * Converts a byte array to a Base64 string.
	 *
	 * @param byteArray the byte array to convert.
	 * @return a Base64 string representation of the byte array, or {@code null} if the input is {@code null}.
	 */
	public static String bytesToBase64(byte[] byteArray) {
		return byteArray == null ? null : Base64.getEncoder().encodeToString(byteArray);
	}

	/**
	 * Converts a Base64 string to a byte array.
	 *
	 * @param base64String the Base64 string to convert.
	 * @return a byte array representation of the Base64 string.
	 * @throws ArgumentException if the input string is not a valid Base64 string.
	 */
	public static byte[] base64ToBytes(String base64String) {
		if (base64String == null || base64String.isEmpty()) {
			return null;
		}
		try {
			return Base64.getDecoder().decode(base64String);
		} catch (IllegalArgumentException e) {
			throw new ArgumentException("Invalid Base64 input: " + base64String);
		}
	}

	/**
	 * Converts a Base64 string to a hexadecimal string.
	 *
	 * @param base64String the Base64 string to convert.
	 * @return a hexadecimal string representation of the Base64 input.
	 * @throws ArgumentException if the input string is not a valid Base64 string.
	 */
	public static String base64ToHex(String base64String) {
		byte[] bytes = base64ToBytes(base64String);
		return bytesToHex(bytes);
	}

	/**
	 * Converts a hexadecimal string to a Base64 string.
	 *
	 * @param hexString the hexadecimal string to convert.
	 * @return a Base64 string representation of the hexadecimal input.
	 * @throws ArgumentException if the input string is not a valid hexadecimal string.
	 */
	public static String hexToBase64(String hexString) {
		byte[] bytes = hexToBytes(hexString);
		return bytesToBase64(bytes);
	}

	/**
	 * Decodes a string (either Hexadecimal or Base64) into a byte array.
	 *
	 * @param input the input string to decode.
	 * @return the decoded byte array, or {@code null} if the input is {@code null} or empty.
	 * @throws ArgumentException if the input is not a valid hexadecimal or Base64 string.
	 */
	public static byte[] base64OrHexToBytes(String input) {
		if (input == null || input.trim().isEmpty()) {
			throw new ArgumentException("Invalid input: Input cannot be null or empty.");
		}

		input = parsePemKey(input);
		if (input.matches(HEX_REGEX)) {
			return hexToBytes(input);
		}
		
		if (input.matches(BASE64_REGEX)) {
			return base64ToBytes(input);
		}
		
		throw new ArgumentException("Invalid input: Not a valid Hexadecimal or Base64 string.");
	}

	/**
	 * Validates if the input string is either a valid Base64 or Hexadecimal format.
	 *
	 * @param input the input string to validate.
	 * @return {@code true} if the input is a valid Base64 or Hexadecimal string, {@code false} otherwise.
	 */
	public static boolean isBase64OrHex(String input) {
		if (input == null || input.trim().isEmpty()) {
			return false;
		}
		input = parsePemKey(input);
		return input.matches(HEX_REGEX) 
				|| input.matches(BASE64_REGEX);
	}

	/**
	 * Normalizes PEM key formats by removing headers, footers, and unnecessary characters.
	 *
	 * @param pemKey the PEM key string to normalize.
	 * @return a cleaned string containing only the key data.
	 */
	public static String parsePemKey(String pemKey) {
		return pemKey.trim().replace("-BEGIN", "").replace("-END", "")
				.replace(" PRIVATE", "").replace(" PUBLIC", "").replace("KEY-", "")
				.replace("-", "").replaceAll("\\s", "");
	}

	/**
	 * Serializes an object to a JSON string.
	 *
	 * @param object the object to serialize.
	 * @return the JSON string representation of the object.
	 */
	public static String toJson(Object object) {
		return new Gson().toJson(object);
	}

	/**
	 * Deserializes a JSON string into an object of the specified type.
	 *
	 * @param <T> the type of the object to deserialize.
	 * @param reader the reader containing the JSON string.
	 * @param type   the type of the object to deserialize.
	 * @return the deserialized object.
	 */
	public static <T> T fromJson(Reader reader, Type type) {
		return new Gson().fromJson(reader, type);
	}

	/**
	 * Deserializes a JSON string into an object of the specified class type.
	 *
	 * @param <T> the type of the object to deserialize.
	 * @param json the JSON string to deserialize.
	 * @param type the class of the object to deserialize.
	 * @return the deserialized object.
	 */
	public static <T> T fromJson(String json, Class<T> type) {
		return new Gson().fromJson(json, type);
	}

	public static <T> T convert(Object object, Class<T> type) {
		if (object == null)
			return null;

		String json = toJson(object);
		return fromJson(json, type);
	}
}
