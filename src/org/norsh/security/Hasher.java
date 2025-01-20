package org.norsh.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import org.norsh.exceptions.InternalException;

/**
 * Utility class for cryptographic hash generation and unique identifier creation.
 * <p>
 * Provides methods to generate SHA-256 and SHA3-256 hashes, both as byte arrays and hexadecimal strings. Also includes a method
 * for generating universally unique identifiers (UUIDs).
 * </p>
 *
 * <h2>Features:</h2>
 * <ul>
 *   <li>Support for SHA-256 and SHA3-256 hashing algorithms.</li>
 *   <li>Hash generation for both byte arrays and strings.</li>
 *   <li>Outputs hashes in hexadecimal or byte array formats.</li>
 *   <li>Convenient UUID generation.</li>
 * </ul>
 *
 * @since 1.0.0
 * @version 1.0.0
 * @author Danthur Lice
 * @ntp NTP-2
 * @see <a href="https://docs.norsh.org/ntp/ntp-2">NTP-2: Cryptography and Hash Specification in the Norsh Ecosystem</a>
 */
public class Hasher {
	/**
     * Generates a hash for the given byte array using the specified algorithm.
     *
     * @param input     the byte array to hash.
     * @param algorithm the hashing algorithm (e.g., "SHA3-256", "SHA-256").
     * @return the hash as a byte array.
     * @throws InternalException if the specified algorithm is not available.
     */
    public static byte[] hashBytes(byte[] input, String algorithm) {
    	
        try {
            MessageDigest digest = MessageDigest.getInstance(algorithm);
            return digest.digest(input);
        } catch (NoSuchAlgorithmException e) {
            throw new InternalException(algorithm + " algorithm not available", e);
        }
    }

    /**
     * Generates a hexadecimal hash for the given byte array using the specified algorithm.
     *
     * @param input     the byte array to hash.
     * @param algorithm the hashing algorithm (e.g., "SHA3-256", "SHA-256").
     * @return the hexadecimal representation of the hash.
     */
    public static String hashHex(byte[] input, String algorithm) {
        byte[] hashBytes = hashBytes(input, algorithm);
        StringBuilder hexString = new StringBuilder();
        for (byte b : hashBytes) {
            hexString.append(String.format("%02x", b));
        }
        return hexString.toString();
    }

    /**
     * Generates a SHA3-256 hash for the given byte array.
     *
     * @param input the byte array to hash.
     * @return the hash as a byte array.
     */
    public static byte[] sha3Bytes(byte[] input) {
        return hashBytes(input, "SHA3-256");
    }

    /**
     * Generates a SHA3-256 hash for the given string.
     *
     * @param input the string to hash.
     * @return the hash as a byte array.
     */
    public static byte[] sha3Bytes(String input) {
        return sha3Bytes(input.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Generates a SHA3-256 hash for the given byte array.
     *
     * @param input the byte array to hash.
     * @return the hexadecimal representation of the SHA3-256 hash.
     */
    public static String sha3Hex(byte[] input) {
        return hashHex(input, "SHA3-256");
    }

    /**
     * Generates a SHA3-256 hash for the given string.
     *
     * @param input the string to hash.
     * @return the hexadecimal representation of the SHA3-256 hash.
     */
    public static String sha3Hex(String input) {
        return sha3Hex(input.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Generates a SHA-256 hash for the given byte array.
     *
     * @param input the byte array to hash.
     * @return the hash as a byte array.
     */
    public static byte[] sha256Bytes(byte[] input) {
        return hashBytes(input, "SHA-256");
    }

    /**
     * Generates a SHA-256 hash for the given string.
     *
     * @param input the string to hash.
     * @return the hash as a byte array.
     */
    public static byte[] sha256Bytes(String input) {
        return sha256Bytes(input.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Generates a SHA-256 hash for the given byte array.
     *
     * @param input the byte array to hash.
     * @return the hexadecimal representation of the SHA-256 hash.
     */
    public static String sha256Hex(byte[] input) {
        return hashHex(input, "SHA-256");
    }

    /**
     * Generates a SHA-256 hash for the given string.
     *
     * @param input the string to hash.
     * @return the hexadecimal representation of the SHA-256 hash.
     */
    public static String sha256Hex(String input) {
        return sha256Hex(input.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Generates a universally unique identifier (UUID).
     * <p>
     * This method creates a random UUID in the standard format:
     * {@code xxxxxxxx-xxxx-Mxxx-Nxxx-xxxxxxxxxxxx}, where:
     * <ul>
     *   <li>{@code x}: A hexadecimal digit (0-9, a-f).</li>
     *   <li>{@code M}: The UUID version (always "4" for random UUIDs).</li>
     *   <li>{@code N}: The UUID variant (e.g., "8", "9", "a", or "b").</li>
     * </ul>
     * </p>
     *
     * @return a random UUID as a {@code String}.
     */
    public static String uuid() {
        return UUID.randomUUID().toString();
    }
}
