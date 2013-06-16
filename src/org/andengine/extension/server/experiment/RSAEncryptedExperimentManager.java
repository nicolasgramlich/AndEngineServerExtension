package org.andengine.extension.server.experiment;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.andengine.extension.server.experiment.exception.ExperimentException;
import org.andengine.util.system.SystemUtils.SystemUtilsException;

import android.content.Context;
import android.util.Base64;

/**
 * (c) 2013 Nicolas Gramlich
 * 
 * @author Nicolas Gramlich
 * @since 20:41:56 - 14.05.2013
 */
public class RSAEncryptedExperimentManager extends ExperimentManager {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	private final Cipher mCipher;
	private final Lock mCipherLock = new ReentrantLock(true);

	// ===========================================================
	// Constructors
	// ===========================================================

	/**
	 * @param pContext
	 * @param pServerURL
	 * @param pPublicKeyBase64 Format: <code>X.509</code>
	 * @throws SystemUtilsException
	 */
	public RSAEncryptedExperimentManager(final Context pContext, final String pServerURL, final String pPublicKeyBase64) throws SystemUtilsException {
		super(pContext, pServerURL);

		this.mCipher = this.initCipher(pPublicKeyBase64);
	}

	/**
	 * @param pContext
	 * @param pServerURL
	 * @param pPublicKeyBase64 Format: <code>X.509</code>
	 * @param pExperimentFactory
	 * @throws SystemUtilsException
	 */
	public RSAEncryptedExperimentManager(final Context pContext, final String pServerURL, final String pPublicKeyBase64, final IExperimentFactory pExperimentFactory) throws SystemUtilsException {
		super(pContext, pServerURL, pExperimentFactory);

		this.mCipher = this.initCipher(pPublicKeyBase64);
	}

	/**
	 * @param pContext
	 * @param pServerURL
	 * @param pPublicKeyBase64 Format: <code>X.509</code>
	 * @param pExperimentFactory
	 * @param pTimeoutMilliseconds
	 * @throws SystemUtilsException
	 */
	public RSAEncryptedExperimentManager(final Context pContext, final String pServerURL, final String pPublicKeyBase64, final IExperimentFactory pExperimentFactory, final int pTimeoutMilliseconds) throws SystemUtilsException {
		super(pContext, pServerURL, pExperimentFactory, pTimeoutMilliseconds);

		this.mCipher = this.initCipher(pPublicKeyBase64);
	}

	private Cipher initCipher(final String pPublicKeyBase64) {
		try {
			final KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			final byte[] publicKeyBase64Bytes = pPublicKeyBase64.getBytes("UTF-8");
			final byte[] publicKeyBytes = Base64.decode(publicKeyBase64Bytes, Base64.DEFAULT);
			final EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
			final PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);

			final Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			cipher.init(Cipher.DECRYPT_MODE, publicKey);

			return cipher;
		} catch (final NoSuchAlgorithmException e) {
			throw new IllegalArgumentException(e);
		} catch (final UnsupportedEncodingException e) {
			throw new IllegalArgumentException(e);
		} catch (final NoSuchPaddingException e) {
			throw new IllegalArgumentException(e);
		} catch (final InvalidKeySpecException e) {
			throw new IllegalArgumentException(e);
		} catch (final InvalidKeyException e) {
			throw new IllegalArgumentException(e);
		}
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	@Override
	protected Map<String, Experiment<?>> parseExperiments(final String pEncryptedServerResponseBase64) throws ExperimentException {
		final byte[] encryptedServerResponseBase64Bytes;
		try {
			encryptedServerResponseBase64Bytes = pEncryptedServerResponseBase64.getBytes("UTF-8");
		} catch (final UnsupportedEncodingException e) {
			throw new ExperimentException(e);
		}
		final byte[] encryptedServerResponseBytes = Base64.decode(encryptedServerResponseBase64Bytes, Base64.DEFAULT);

		this.mCipherLock.lock();
		final byte[] serverResponseBytes;
		try {
			serverResponseBytes = this.mCipher.doFinal(encryptedServerResponseBytes);
		} catch (final IllegalBlockSizeException e) {
			throw new ExperimentException(e);
		} catch (final BadPaddingException e) {
			throw new ExperimentException(e);
		} finally {
			this.mCipherLock.unlock();
		}

		final String serverResponse;
		try {
			serverResponse = new String(serverResponseBytes, "UTF-8");
		} catch (final UnsupportedEncodingException e) {
			throw new ExperimentException(e);
		}

		return super.parseExperiments(serverResponse);
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
