package org.andengine.extension.server.gcm.service;

import java.util.UUID;

import org.andengine.extension.server.gcm.util.GCMServerUtils;
import org.andengine.extension.server.gcm.util.GCMUtils;
import org.andengine.extension.server.gcm.util.constants.GCMConstants;
import org.andengine.util.debug.Debug;
import org.andengine.util.exception.IllegalClassNameException;

import android.content.Context;
import android.content.Intent;

import com.google.android.gcm.GCMBaseIntentService;
import com.google.android.gcm.GCMRegistrar;

/**
 * (c) 2013 Nicolas Gramlich
 *
 * @author Nicolas Gramlich
 * @since 20:41:56 - 20.08.2010
 */
public abstract class BaseGCMIntentService extends GCMBaseIntentService implements GCMConstants {
	// ===========================================================
	// Constants
	// ===========================================================

	private static final String GCM_INTENTSERVICE_CLASS_NAME = "GCMIntentService";

	// ===========================================================
	// Fields
	// ===========================================================

	private String mServerURL;
	private UUID mUUID;
	private String mAppID;

	// ===========================================================
	// Constructors
	// ===========================================================

	public BaseGCMIntentService() throws IllegalClassNameException {
		this((String) null);
	}

	public BaseGCMIntentService(final String pServerURL) throws IllegalClassNameException {
		this(pServerURL, (String[]) null);
	}

	public BaseGCMIntentService(final String ... pSenderIds) throws IllegalClassNameException {
		this((String) null, (String[]) null);
	}

	public BaseGCMIntentService(final String pServerURL, final String ... pSenderIds) throws IllegalClassNameException {
		this(pServerURL, null, null, pSenderIds);
	}

	public BaseGCMIntentService(final String pServerURL, final UUID pUUID, final String ... pSenderIds) throws IllegalClassNameException {
		this(pServerURL, pUUID, null, pSenderIds);
	}

	public BaseGCMIntentService(final String pServerURL, final String pAppID, final String ... pSenderIds) throws IllegalClassNameException {
		this(pServerURL, null, pAppID, pSenderIds);
	}

	public BaseGCMIntentService(final String pServerURL, final UUID pUUID, final String pAppID, final String ... pSenderIds) throws IllegalClassNameException {
		super(pSenderIds);

		this.mServerURL = pServerURL;
		this.mUUID = pUUID;
		this.mAppID = pAppID;

		this.ensureClassName();
	}

	private void ensureClassName() throws IllegalClassNameException {
		if (!this.getClass().getSimpleName().equals(GCM_INTENTSERVICE_CLASS_NAME)) {
			throw new IllegalClassNameException("This class has to be named: '" + GCM_INTENTSERVICE_CLASS_NAME + "', not '" + this.getClass().getSimpleName() + "'.");
		}
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	protected String getServerURL(final Context pContext) throws IllegalStateException {
		if (this.mServerURL == null) {
			throw new IllegalStateException("Server URL was not set in constructor!");
		} else {
			return this.mServerURL;
		}
	}

	protected UUID getUUID(final Context pContext) throws IllegalStateException {
		if (this.mUUID == null) {
			throw new IllegalStateException("UUID was not set in constructor!");
		} else {
			return this.mUUID;
		}
	}

	protected String getAppID(final Context pContext) {
		if (this.mAppID == null) {
			throw new IllegalStateException("AppID was not set in constructor!");
		} else {
			return this.mAppID;
		}
	}

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	@Override
	protected void onRegistered(final Context pContext, final String pGCMRegistrationID) {
		final String serverURL = this.getServerURL(pContext);
		final String appID = getAppID(pContext);
		final UUID uuid = this.getUUID(pContext);

		final boolean registered = GCMServerUtils.register(pContext, serverURL, uuid, appID, pGCMRegistrationID);

		/* If it failed, unregister from GCM itself, so it will be retried next startup: */
		if (!registered) {
			GCMRegistrar.unregister(pContext);
		}
	}

	@Override
	protected void onUnregistered(final Context pContext, final String pGCMRegistrationID) {
		if (GCMRegistrar.isRegisteredOnServer(pContext)) {
			final String serverURL = this.getServerURL(pContext);
			final String appID = getAppID(pContext);
			final UUID uuid = this.getUUID(pContext);

			final boolean unregistered = GCMServerUtils.unregister(pContext, serverURL, uuid, appID, pGCMRegistrationID);

			/* If it failed, pretend it succeeded, because a (potential) dupe registration is not as bad as missing one: */
			if (!unregistered) {
				GCMRegistrar.setRegisteredOnServer(pContext, false);
			}
		}
	}

	@Override
	protected void onMessage(final Context pContext, final Intent pIntent) {
		GCMUtils.broadcastGCMMessage(pContext, pIntent.getExtras());
	}

	@Override
	protected void onDeletedMessages(final Context pContext, final int pTotal) {
		// TODO

		super.onDeletedMessages(pContext, pTotal);
	}

	@Override
	protected boolean onRecoverableError(final Context pContext, final String pErrorID) {
		Debug.e(pErrorID);

		return super.onRecoverableError(pContext, pErrorID);
	}

	@Override
	protected void onError(final Context pContext, final String pErrorID) {
		Debug.e(pErrorID);
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
