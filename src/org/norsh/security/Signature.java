package org.norsh.security;

import org.norsh.exceptions.ArgumentException;
import org.norsh.exceptions.InternalException;
import org.norsh.util.Converter;
import org.norsh.util.Strings;

/**
 * Service for handling cryptographic signature operations, such as verifying signatures
 * and signing data for validation purposes.
 *
 * <p>
 * This service is designed to facilitate secure operations on data, ensuring authenticity
 * and integrity through cryptographic signatures.
 * </p>
 *
 * <h2>Features:</h2>
 * <ul>
 * <li>Verification of signatures using public keys.</li>
 * <li>Creation of cryptographic signatures using private keys.</li>
 * <li>Concatenation and hashing of multiple fields for consistent signature validation.</li>
 * </ul>
 *
 * @license NCL-139
 * @author Danthur Lice
 * @since 01/2025
 * @version 1.0
 * @see <a href="https://docs.norsh.org">Norsh Documentation</a>
 */
public class Signature {
	/**
	 * Verifies the signature of a message derived from concatenated fields using the provided public key.
	 *
	 * @param publicKey the public key in Base64 or Hexadecimal format.
	 * @param signature the signature in Base64 or Hexadecimal format.
	 * @param values    the fields to be concatenated and verified.
	 * @return {@code true} if the signature is valid, {@code false} otherwise.
	 */
	public static boolean verify(String publicKey, String signature, Object... values) {
		return verifyHash(publicKey, signature, Hasher.sha256Hex(Strings.concatenate(values)));
	}

	/**
	 * Verifies the signature for a given public key and hash.
	 *
	 * @param publicKey the public key in Base64 or Hexadecimal format.
	 * @param signature the signature in Base64 or Hexadecimal format.
	 * @param hash      the pre-computed hash to verify.
	 * @return {@code true} if the signature is valid, {@code false} otherwise.
	 */
	public static boolean verifyHash(String publicKey, String signature, String hash) {
		if (publicKey == null || signature == null) {
			throw new ArgumentException("Public key and signature must not be null.");
		}

		try {
			// Convert hash, public key, and signature to byte arrays
			byte[] messageBytes = Converter.hexToBytes(hash);
			byte[] publicKeyBytes = Converter.base64OrHexToBytes(publicKey);
			byte[] signatureBytes = Converter.base64OrHexToBytes(signature);

			// Verify the signature
			Cryptography cryptography = Cryptography.valueOf(null, publicKeyBytes);
			return cryptography.verifySignature(messageBytes, signatureBytes);

		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Signs the concatenated fields using the provided private key.
	 *
	 * @param privateKey the private key in Base64 or Hexadecimal format.
	 * @param values     the fields to be concatenated and signed.
	 * @return the signature as a hexadecimal string.
	 */
	public static String sign(String privateKey, Object... values) {
		return signHash(privateKey, Hasher.sha256Hex(Strings.concatenate(values)));
	}

	/**
	 * Signs the given hash using the provided private key.
	 *
	 * @param privateKey the private key in Base64 or Hexadecimal format.
	 * @param hash       the hash to be signed.
	 * @return the signature as a hexadecimal string.
	 */
	public static String signHash(String privateKey, String hash) {
		if (privateKey == null) {
			throw new ArgumentException("Private key must not be null.");
		}

		try {
			// Initialize cryptography with the private key
			Cryptography cryptography = Cryptography.valueOf(Converter.base64OrHexToBytes(privateKey), null);

			// Sign the hash
			byte[] signatureBytes = cryptography.signData(Converter.hexToBytes(hash));
			return Converter.bytesToHex(signatureBytes);

		} catch (Exception e) {
			throw new InternalException("Failed to sign data.", e);
		}
	}
}
