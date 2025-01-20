package org.norsh.security;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.Signature;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.Cipher;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.norsh.exceptions.InternalException;

/**
 * Utility class for cryptographic operations using ECDSA keys.
 * <p>
 * Provides functionalities for key generation, signing, encryption, decryption, and key serialization in PEM format.
 * </p>
 *
 * <h2>Features:</h2>
 * <ul>
 *   <li>Generates ECDSA key pairs using the secp256k1 curve.</li>
 *   <li>Signs and verifies data using the SHA256withECDSA algorithm.</li>
 *   <li>Encrypts and decrypts data using ECIES encryption.</li>
 *   <li>Exports keys in PEM format for interoperability.</li>
 *   <li>Supports reconstruction of keys from encoded byte arrays.</li>
 *   <li>Configurable bypass for signature verification (useful for debugging).</li>
 * </ul>
 *
 * @since 1.0.0
 * @version 1.0.0
 * @author Danthur Lice
 * @ntp NTP-2
 * @see <a href="https://docs.norsh.org/ntp/ntp-2">NTP-2: Cryptography and Hash Specification in the Norsh Ecosystem</a>
 */
public class Cryptography {
	private PrivateKey privateKey;
	private PublicKey publicKey;

	static {
		Security.addProvider(new BouncyCastleProvider());
	}
	
	/**
	 * Constructs an instance of {@code CryptographyUtil} and generates a new ECDSA key pair using secp256k1.
	 *
	 * @throws InternalException if the key generation process fails.
	 */
	public Cryptography() {
		generateKeys();
	}

	/**
	 * Regenerates the ECDSA key pair.
	 *
	 * @throws InternalException if the key generation process fails.
	 */
	public void recreate() {
		generateKeys();
	}

	/**
	 * Signs the input data using the private key.
	 *
	 * @param data the data to be signed.
	 * @return the signature as a byte array.
	 * @throws InternalException if the signing process fails.
	 */
	public byte[] signData(byte[] data) {
		try {
			Signature signature = Signature.getInstance("SHA256withECDSA", "BC");
			signature.initSign(privateKey);
			signature.update(data);
			return signature.sign();
		} catch (Exception e) {
			throw new InternalException("Error signing data", e);
		}
	}

	/**
	 * Verifies if the given data and signature are valid using the public key.
	 *
	 * @param data      the original data that was signed.
	 * @param signature the signature to be verified.
	 * @return {@code true} if the signature is valid; {@code false} otherwise.
	 */
	public boolean verifySignature(byte[] data, byte[] signature) {
		try {
			Signature verifier = Signature.getInstance("SHA256withECDSA", "BC");
			verifier.initVerify(publicKey);
			verifier.update(data);
			return verifier.verify(signature);
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Encrypts data using the public key.
	 *
	 * @param data the data to be encrypted.
	 * @return the encrypted data as a byte array.
	 * @throws InternalException if the encryption process fails.
	 */
	public byte[] encrypt(byte[] data) {
		try {
			Cipher cipher = Cipher.getInstance("ECIES", "BC");
			cipher.init(Cipher.ENCRYPT_MODE, publicKey);
			return cipher.doFinal(data);
		} catch (Exception e) {
			throw new InternalException("Error encrypting data", e);
		}
	}

	/**
	 * Decrypts data using the private key.
	 *
	 * @param encryptedData the encrypted data to be decrypted.
	 * @return the decrypted data as a byte array.
	 * @throws InternalException if the decryption process fails.
	 */
	public byte[] decrypt(byte[] encryptedData) {
		try {
			Cipher cipher = Cipher.getInstance("ECIES", "BC");
			cipher.init(Cipher.DECRYPT_MODE, privateKey);
			return cipher.doFinal(encryptedData);
		} catch (Exception e) {
			throw new InternalException("Error decrypting data", e);
		}
	}

	/**
	 * Retrieves the private key in byte[] format.
	 *
	 * @return the private key as a byte array.
	 */
	public byte[] getPrivateKeyBytes() {
		return privateKey.getEncoded();
	}

	/**
	 * Retrieves the public key in byte[] format.
	 *
	 * @return the public key as a byte array.
	 */
	public byte[] getPublicKeyBytes() {
		return publicKey.getEncoded();
	}

	/**
	 * Exports the private key in PEM format as a String.
	 *
	 * @return the private key in PEM format.
	 */
	public String exportPrivateKeyToPEM() {
		return "-----BEGIN PRIVATE KEY-----\n" + Base64.getEncoder().encodeToString(privateKey.getEncoded()) + "\n-----END PRIVATE KEY-----";
	}

	/**
	 * Exports the public key in PEM format as a String.
	 *
	 * @return the public key in PEM format.
	 */
	public String exportPublicKeyToPEM() {
		return "-----BEGIN PUBLIC KEY-----\n" + Base64.getEncoder().encodeToString(publicKey.getEncoded()) + "\n-----END PUBLIC KEY-----";
	}

	/**
	 * Creates an instance of {@code CryptographyUtil} from an existing private and public key.
	 *
	 * @param privateKeyBytes the private key in byte[] format.
	 * @param publicKeyBytes  the public key in byte[] format.
	 * @return a new instance of {@code CryptographyUtil} initialized with the provided keys.
	 * @throws InternalException if the keys cannot be reconstructed.
	 */
	public static Cryptography valueOf(byte[] privateKeyBytes, byte[] publicKeyBytes) {
	    try {
	        KeyFactory keyFactory = KeyFactory.getInstance("EC", "BC");
	        Cryptography util = new Cryptography();

	        util.privateKey = generatePrivateKey(keyFactory, privateKeyBytes);
	        util.publicKey = generatePublicKey(keyFactory, publicKeyBytes);

	        return util;
	    } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
	        throw new InternalException("Error reconstructing keys", e);
	    }
	}

	/**
	 * Generates a PrivateKey from the given byte array.
	 *
	 * @param keyFactory the KeyFactory instance.
	 * @param privateKeyBytes the private key in byte[] format.
	 * @return the generated PrivateKey, or null if input is null.
	 * @throws InternalException if the key is invalid.
	 */
	private static PrivateKey generatePrivateKey(KeyFactory keyFactory, byte[] privateKeyBytes) {
	    try {
	        return (privateKeyBytes == null) ? null : keyFactory.generatePrivate(new PKCS8EncodedKeySpec(privateKeyBytes));
	    } catch (InvalidKeySpecException e) {
	        throw new InternalException("Invalid privateKey", e);
	    }
	}

	/**
	 * Generates a PublicKey from the given byte array.
	 *
	 * @param keyFactory the KeyFactory instance.
	 * @param publicKeyBytes the public key in byte[] format.
	 * @return the generated PublicKey, or null if input is null.
	 * @throws InternalException if the key is invalid.
	 */
	private static PublicKey generatePublicKey(KeyFactory keyFactory, byte[] publicKeyBytes) {
	    try {
	        return (publicKeyBytes == null) ? null : keyFactory.generatePublic(new X509EncodedKeySpec(publicKeyBytes));
	    } catch (InvalidKeySpecException e) {
	        throw new InternalException("Invalid publicKey", e);
	    }
	}


	/**
	 * Generates a new ECDSA key pair using the secp256k1 curve.
	 */
	private void generateKeys() {
		try {
			KeyPairGenerator keyGen = KeyPairGenerator.getInstance("EC", "BC");
			keyGen.initialize(new ECGenParameterSpec("secp256k1"));
			KeyPair pair = keyGen.generateKeyPair();
			this.privateKey = pair.getPrivate();
			this.publicKey = pair.getPublic();
		} catch (Exception e) {
			throw new InternalException("Error generating ECDSA keys", e);
		}
	}
}
