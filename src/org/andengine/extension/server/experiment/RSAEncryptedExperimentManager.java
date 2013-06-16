package org.andengine.extension.server.experiment;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
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
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.andengine.extension.server.experiment.exception.ExperimentException;
import org.andengine.util.system.SystemUtils.SystemUtilsException;
import org.json.JSONException;
import org.json.JSONObject;

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

	private final Cipher mRSACipher;
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

		this.mRSACipher = this.createRSACipher(pPublicKeyBase64);
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

		this.mRSACipher = this.createRSACipher(pPublicKeyBase64);
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

		this.mRSACipher = this.createRSACipher(pPublicKeyBase64);
	}

	private Cipher createRSACipher(final String pPublicKeyBase64) {
		try {
			final KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			final byte[] publicKeyBase64Bytes = pPublicKeyBase64.getBytes("UTF-8");
			final byte[] publicKeyBytes = Base64.decode(publicKeyBase64Bytes, Base64.DEFAULT);
			final EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
			final PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);

			final Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			cipher.init(Cipher.UNWRAP_MODE, publicKey);

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
	protected Map<String, Experiment<?>> parseExperiments(final String pServerResponse) throws ExperimentException {
		/* UnJSONify all the things: */
		final JSONObject serverResponseJSONObject;
		final String rsaEncryptedAESKeyBase64String;
		final String aesEncryptedExperimentDataBase64String;
		final String ivBase64String;
		try {
			serverResponseJSONObject = new JSONObject(pServerResponse);

			rsaEncryptedAESKeyBase64String = serverResponseJSONObject.getString("key");
			ivBase64String = serverResponseJSONObject.getString("iv");
			aesEncryptedExperimentDataBase64String = serverResponseJSONObject.getString("data");
		} catch (final JSONException e) {
			throw new ExperimentException(e);
		}

		/* Base64 decode all the things: */
		final byte[] rsaEncryptedAESKeyBytes = RSAEncryptedExperimentManager.base64Decode(rsaEncryptedAESKeyBase64String);
		final byte[] aesEncryptedExperimentDataBytes = RSAEncryptedExperimentManager.base64Decode(aesEncryptedExperimentDataBase64String);
		final byte[] ivBytes = RSAEncryptedExperimentManager.base64Decode(ivBase64String);

		/* AES Key: */
		final Cipher aesCipher;
		this.mCipherLock.lock();
		try {
			final Key aesKey = this.mRSACipher.unwrap(rsaEncryptedAESKeyBytes, "AES/CBC/PKCS5Padding", Cipher.SECRET_KEY);
			final IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);

			aesCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			aesCipher.init(Cipher.DECRYPT_MODE, aesKey, ivSpec);
		} catch (final NoSuchAlgorithmException e) {
			throw new ExperimentException(e);
		} catch (final NoSuchPaddingException e) {
			throw new ExperimentException(e);
		} catch (final InvalidKeyException e) {
			throw new ExperimentException(e);
		} catch (final InvalidAlgorithmParameterException e) {
			throw new ExperimentException(e);
		} finally {
			this.mCipherLock.unlock();
		}

		/* Experiment Data: */
		final String experimentData;
		try {
			final byte[] experimentDataBytes = aesCipher.doFinal(aesEncryptedExperimentDataBytes);
			experimentData = new String(experimentDataBytes, "UTF-8");
		} catch (final UnsupportedEncodingException e) {
			throw new ExperimentException(e);
		} catch (final IllegalBlockSizeException e) {
			throw new ExperimentException(e);
		} catch (final BadPaddingException e) {
			throw new ExperimentException(e);
		}

		return super.parseExperiments(experimentData);
	}

	private static byte[] base64Decode(final String pBase64String) throws ExperimentException {
		final byte[] base64Bytes;
		try {
			base64Bytes = pBase64String.getBytes("UTF-8");
		} catch (final UnsupportedEncodingException e) {
			throw new ExperimentException(e);
		}
		return Base64.decode(base64Bytes, Base64.DEFAULT);
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
